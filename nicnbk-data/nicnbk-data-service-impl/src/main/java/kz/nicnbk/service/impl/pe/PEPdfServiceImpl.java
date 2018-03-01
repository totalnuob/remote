package kz.nicnbk.service.impl.pe;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import kz.nicnbk.service.api.pe.PEFundService;
import kz.nicnbk.service.api.pe.PEIrrService;
import kz.nicnbk.service.api.pe.PEPdfService;
import kz.nicnbk.service.dto.pe.PEFirmDto;
import kz.nicnbk.service.dto.pe.PEFundDto;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pak on 24/01/2018.
 */
@Service
public class PEPdfServiceImpl implements PEPdfService {

    private static final Logger logger = LoggerFactory.getLogger(PEFundServiceImpl.class);

    @Autowired
    private PEFundService fundService;

    @Autowired
    private PEIrrService irrService;

    //File locations
    private static final String onePagerDest = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/OnePager.pdf";
    private static final String gpLogoDest = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/img/SilverLakeLogo.png";
    private static final String nicLogoDest = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/img/NIClogo.png";
    private static final String barChartDest = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/img/BarChart.jpeg";

    //Colors
    private static final Color greenColor = new DeviceCmyk(0.78f, 0, 0.81f, 0.21f);
    private static final Color whiteColor = new DeviceCmyk(0, 0, 0, 0);

    //GP's and NIC's logos
    private Image gpLogo;
    private Image nicLogo;
    private Image barChart;

    @Override
    public void createOnePager(Long fundId) {
        try {
            //Margins
            Float offSet = 36f;
            Float logoMaxHeight = 24f;
            Float logoMaxWidth = 72f;
            Float topColunmOffSet = 182f;
            Float columnGap = 3f;

            //Logo initialization
            gpLogo = new Image(ImageDataFactory.create(gpLogoDest));
            nicLogo = new Image(ImageDataFactory.create(nicLogoDest));

            //Setting logo size
            if (gpLogo.getImageHeight() / gpLogo.getImageWidth() > logoMaxHeight / logoMaxWidth) {
                gpLogo.setHeight(logoMaxHeight);
            } else {
                gpLogo.setWidth(logoMaxWidth);
            }
            nicLogo.setHeight(logoMaxHeight);

            //Folder creation
            File file = new File(onePagerDest);
            file.getParentFile().mkdirs();

            //Initialize PDF document
            PdfDocument pdf = new PdfDocument(new PdfWriter(onePagerDest));
            PageSize ps = PageSize.A4;

            //Initialize document
            Document document = new Document(pdf, ps);
            document.setFontSize(8);

            PEFundDto fundDto = fundService.get(fundId);
            PEFirmDto firmDto = fundDto.getFirm();
            List<PEFundDto> fundDtoList = fundService.loadFirmFunds(firmDto.getId(), true);

            //Header
            Table headerTable = new Table(new float[]{1, 1, 1});
            this.addHeader(headerTable, fundDto, ps.getWidth() - offSet * 2, logoMaxWidth);
            document.add(headerTable);

            //Organization Overview Title
            Table organizationOverviewTitle = new Table(new float[]{1});
            this.addGreenTitle(organizationOverviewTitle, "Organization Overview", ps.getWidth() - offSet * 2);
            document.add(organizationOverviewTitle);

            //Organization Overview Table
            Table organizationOverviewTable = new Table(new float[]{1, 1, 1, 1, 1, 1});
            this.addOrganizationOverview(organizationOverviewTable, firmDto, ps.getWidth() - offSet * 2);
            document.add(organizationOverviewTable);

            //Fund Summary Title
            Table fundSummaryTitle = new Table(new float[]{1});
            this.addGreenTitle(fundSummaryTitle, "Fund Summary", ps.getWidth() - offSet * 2);
            document.add(fundSummaryTitle);

            //Fund Summary Table
            Table fundSummaryTable = new Table(new float[]{1, 1, 1, 1, 1, 1});
            this.addFundSummary(fundSummaryTable, fundDto, ps.getWidth() - offSet * 2);
            document.add(fundSummaryTable);

            if (fundDtoList != null && fundDtoList.size() > 0) {
                //Key Fund Statistics Title
                Table keyFundStatisticsTitle = new Table(new float[]{1});
                this.addGreenTitle(keyFundStatisticsTitle, "Key fund statistics", ps.getWidth() - offSet * 2);
                this.addWhiteTitle(keyFundStatisticsTitle, unNullifierToEmptyString(firmDto.getFirmName()) + " Investment Performance Data as of ?????? " + "($mln)", ps.getWidth() - offSet * 2);
                document.add(keyFundStatisticsTitle);

                //Key Fund Statistics Table
                Table keyFundStatisticsTable = new Table(new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
                this.addKeyFundStatistics(keyFundStatisticsTable, fundDtoList, ps.getWidth() - offSet * 2);
                document.add(keyFundStatisticsTable);

                topColunmOffSet += (2 + fundDtoList.size()) * 16.4888888f + 34f;
            }

            //Define columns' widths and heights
            Float columnOneWidth = (ps.getWidth() - offSet * 2 - columnGap) * 3 / 4;
            Float columnTwoWidth = (ps.getWidth() - offSet * 2 - columnGap) * 1 / 4;
            Float columnHeight = ps.getHeight() - offSet - topColunmOffSet;

            //First column
            //##########################################################################################################
            //Define column areas
            Rectangle[] columnOne = {new Rectangle(offSet, offSet, columnOneWidth, columnHeight)};
            document.setRenderer(new ColumnDocumentRenderer(document, columnOne));

            //IRR & TVPI Multiple Title
            Table irrAndTvpiTitle = new Table(new float[]{1});
            this.addGreenTitle(irrAndTvpiTitle, "IRR & TVPI multiple", columnOneWidth);
            document.add(irrAndTvpiTitle);

            //Charts
            this.createChart(columnOneWidth / 2);
            barChart = new Image(ImageDataFactory.create(barChartDest));
            document.add(new Paragraph().add(barChart).add(barChart));
            document.add(new Paragraph().add(barChart).add(barChart));

            document.add(new Paragraph("Ajl dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " a a a a a a a a a a a a a a a a a a a a a" +
                    " a a a a a a a a a a a a a a a a a a a a" +
                    " a a a a a a a a a a a a a a a a a a a a" +
                    " a a a a a a a a a a a a a a a a a a a a" +
                    " a a a a a a a a a a a a a a a a a a a a" +
                    " a a a a a a a a a a a a a a a a a a a a" +
                    " a a a a a a a a a a a a a a a a a a a a" +
                    " a a a a a a a a a a a a a a a a a a a a" +
                    " a a a a a a a a a a a a a a a a a a a a"));

            //Second column
            //##########################################################################################################
            //Define column areas
            Rectangle[] columnTwo = {new Rectangle(offSet + columnOneWidth + columnGap, offSet, columnTwoWidth, columnHeight)};
            document.setRenderer(new ColumnDocumentRenderer(document, columnTwo));

            //Fund Strategy Title
            Table fundStrategyTitle = new Table(new float[]{1});
            this.addGreenTitle(fundStrategyTitle, "Fund Strategy", columnTwoWidth);
            document.add(fundStrategyTitle);

            document.add(new Paragraph("Ajl dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd" +
                    " dsa dsa gfdg gfd gfd gfd gfd gfds gds gfds gsd"));

            document.close();
        } catch (IOException ex) {
            logger.error("Error creating PE fund's One Pager: " + fundId, ex);
        }
    }

    private void createChart(Float width) throws IOException {
        final String fiat = "FIAT";
        final String audi = "AUDI";
        final String ford = "FORD";
        final String speed = "Speed";
        final String millage = "Millage";
        final String userrating = "User Rating";
        final String safety = "safety";

        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1.0, fiat, speed);
        dataset.addValue(3.0, fiat, userrating);
        dataset.addValue(5.0, fiat, millage);
        dataset.addValue(5.0, fiat, safety);
        dataset.addValue(5.0, audi, speed);
        dataset.addValue(6.0, audi, userrating);
        dataset.addValue(10.0, audi, millage);
        dataset.addValue(4.0, audi, safety);
        dataset.addValue(4.0, ford, speed);
        dataset.addValue(2.0, ford, userrating);
        dataset.addValue(3.0, ford, millage);
        dataset.addValue(6.0, ford, safety);

        JFreeChart barChart = ChartFactory.createBarChart("Net IRR", "", "", dataset, PlotOrientation.VERTICAL, true, true, false);

        barChart.getPlot().setBackgroundPaint(java.awt.Color.WHITE);

//        int width = 4000;    /* Width of the image */
//        int height = 3000;   /* Height of the image */
        File BarChart = new File(barChartDest);
        ChartUtilities.saveChartAsJPEG(BarChart, barChart, Math.round(width), Math.round(width * 3 / 4));
    }

    private void addHeader(Table table, PEFundDto fundDto, Float width, Float logoWidth) {
        table.addCell(new Cell()
                .setWidth(logoWidth)
                .add(new Paragraph().add(gpLogo))
                .setTextAlignment(TextAlignment.LEFT));
        table.addCell(new Cell()
                .setWidth(width - logoWidth * 2)
                .add(new Paragraph(unNullifierToEmptyString(fundDto.getFundName()))
                        .setBold()
                        .setFontSize(10))
                .setTextAlignment(TextAlignment.CENTER));
        table.addCell(new Cell()
                .setWidth(logoWidth)
                .add(new Paragraph().add(nicLogo))
                .setTextAlignment(TextAlignment.RIGHT));
    }

    private void addGreenTitle(Table table, String title, Float width) {
        table.addCell(new Cell()
                .setWidth(width)
                .add(new Paragraph(unNullifierToEmptyString(title)))
                .setBackgroundColor(greenColor)
                .setFontColor(whiteColor));
    }

    private void addWhiteTitle(Table table, String title, Float width) {
        table.addCell(new Cell()
                .setWidth(width)
                .add(new Paragraph(unNullifierToEmptyString(title)).setBold())
                .setTextAlignment(TextAlignment.CENTER));
    }

    private void addOrganizationOverview(Table table, PEFirmDto firmDto, Float width) {
        table.setWidth(width);
        table.addCell(new Cell().add(new Paragraph("GP Name").setBold()));
        table.addCell(new Cell().add(new Paragraph(unNullifierToEmptyString(firmDto.getFirmName()))));
        table.addCell(new Cell().add(new Paragraph("Strategy AUM ($mln)").setBold()));
        table.addCell(new Cell().add(new Paragraph(mlnFormat(firmDto.getAum() * 1000000))));
        table.addCell(new Cell().add(new Paragraph("Locations").setBold()));
        table.addCell(new Cell().add(new Paragraph(unNullifierToEmptyString(firmDto.getLocations()))));
        table.addCell(new Cell().add(new Paragraph("Firm Inception").setBold()));
        table.addCell(new Cell().add(new Paragraph(unNullifierToEmptyString(firmDto.getFoundedYear()))));
        table.addCell(new Cell().add(new Paragraph("Inv. + Oper. Team").setBold()));
        table.addCell(new Cell().add(new Paragraph((firmDto.getInvTeamSize() != null ? firmDto.getInvTeamSize() : "?") + " + " + (firmDto.getOpsTeamSize() != null ? firmDto.getOpsTeamSize() : "?"))));
        table.addCell(new Cell().add(new Paragraph("Peers").setBold()));
        table.addCell(new Cell().add(new Paragraph(unNullifierToEmptyString(firmDto.getPeers()))));
    }

    private void addFundSummary(Table table, PEFundDto fundDto, Float width) {
        table.setWidth(width);
        table.addCell(new Cell().add(new Paragraph("Fund Size ($mln)").setBold()));
        table.addCell(new Cell().add(new Paragraph(mlnFormat(fundDto.getFundSize()))));
        table.addCell(new Cell().add(new Paragraph("Mgt. fee").setBold()));
        table.addCell(new Cell().add(new Paragraph("Mgt. fee")));
        table.addCell(new Cell().add(new Paragraph("Industry").setBold()));
        table.addCell(new Cell().add(new Paragraph("Industry")));
        table.addCell(new Cell().add(new Paragraph("Hard cap").setBold()));
        table.addCell(new Cell().add(new Paragraph("Hard cap")));
        table.addCell(new Cell().add(new Paragraph("Carry").setBold()));
        table.addCell(new Cell().add(new Paragraph("Carry")));
        table.addCell(new Cell().add(new Paragraph("Strategy").setBold()));
        table.addCell(new Cell().add(new Paragraph("Strategy")));
        table.addCell(new Cell().add(new Paragraph("GP Commitment").setBold()));
        table.addCell(new Cell().add(new Paragraph("GP Commitment")));
        table.addCell(new Cell().add(new Paragraph("Hurdle").setBold()));
        table.addCell(new Cell().add(new Paragraph("Hurdle")));
        table.addCell(new Cell().add(new Paragraph("Geography").setBold()));
        table.addCell(new Cell().add(new Paragraph("Geography")));
    }

    private void addKeyFundStatistics(Table table, List<PEFundDto> fundDtoList, Float width) {

        int totalNumberOfInvestments = 0;
        Double totalInvested = 0.0;
        Double totalRealized = 0.0;
        Double totalUnrealized = 0.0;
        Double totalGrossMOIC = null;
        Double totalGrossIrr = null;
        boolean areAllKeyFundStatisticsCalculatedByGrossCF = true;

        table.setWidth(width);
        table.addHeaderCell(new Cell());
        table.addHeaderCell(new Cell().add(new Paragraph("Vintage").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("# of Inv.").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Fund Size").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Invested").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Realized").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Unrealized").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Gross MOIC").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Gross IRR").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Net MOIC").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Net IRR").setBold()));

        for (PEFundDto fundDto : fundDtoList) {
            totalNumberOfInvestments += unNullifierToZero(fundDto.getNumberOfInvestments());
            totalInvested += unNullifierToZero(fundDto.getInvestedAmount());
            totalRealized += unNullifierToZero(fundDto.getRealized());
            totalUnrealized += unNullifierToZero(fundDto.getUnrealized());

            if (fundDto.getCalculationType() == null || fundDto.getCalculationType() !=2) {
                areAllKeyFundStatisticsCalculatedByGrossCF = false;
            }

            table.addCell(new Cell().add(new Paragraph(unNullifierToEmptyString(fundDto.getFundName()))));
            table.addCell(new Cell().add(new Paragraph(Integer.toString(fundDto.getVintage()))));
            table.addCell(new Cell().add(new Paragraph(unNullifierToEmptyString(fundDto.getNumberOfInvestments()))));
            table.addCell(new Cell().add(new Paragraph(mlnFormat(fundDto.getFundSize()))));
            table.addCell(new Cell().add(new Paragraph(mlnFormat(fundDto.getInvestedAmount()))));
            table.addCell(new Cell().add(new Paragraph(mlnFormat(fundDto.getRealized()))));
            table.addCell(new Cell().add(new Paragraph(mlnFormat(fundDto.getUnrealized()))));
            table.addCell(new Cell().add(new Paragraph(moicFormat(fundDto.getGrossTvpi()))));
            table.addCell(new Cell().add(new Paragraph(irrFormat(fundDto.getGrossIrr()))));
            table.addCell(new Cell().add(new Paragraph(moicFormat(fundDto.getNetTvpi()))));
            table.addCell(new Cell().add(new Paragraph(irrFormat(fundDto.getNetIrr()))));
        }

        if (totalInvested != 0.0) {
            totalGrossMOIC = (totalRealized + totalUnrealized) / totalInvested;
        }
        if (areAllKeyFundStatisticsCalculatedByGrossCF) {
            totalGrossIrr  = irrService.getIrrByFundList(fundDtoList);
        }

        table.addCell(new Cell().add(new Paragraph("Total").setBold()));
        table.addCell(new Cell());
        table.addCell(new Cell().add(new Paragraph(Integer.toString(totalNumberOfInvestments)).setBold()));
        table.addCell(new Cell().add(new Paragraph("Fund Size").setBold()));
        table.addCell(new Cell().add(new Paragraph(mlnFormat(totalInvested)).setBold()));
        table.addCell(new Cell().add(new Paragraph(mlnFormat(totalRealized)).setBold()));
        table.addCell(new Cell().add(new Paragraph(mlnFormat(totalUnrealized)).setBold()));
        table.addCell(new Cell().add(new Paragraph(moicFormat(totalGrossMOIC)).setBold()));
        table.addCell(new Cell().add(new Paragraph(irrFormat(totalGrossIrr)).setBold()));
        table.addCell(new Cell().add(new Paragraph("Net MOIC").setBold()));
        table.addCell(new Cell().add(new Paragraph("Net IRR").setBold()));
    }

    private int unNullifierToZero(Integer a) {
        if (a != null) {
            return a;
        }
        return 0;
    }

    private double unNullifierToZero(Double a) {
        if (a != null) {
            return a;
        }
        return 0.0;
    }

    private String unNullifierToEmptyString(Object st) {
        if (st != null) { return st.toString(); }
        return "";
    }

    private String mlnFormat(Object amount) {
        if (amount != null && amount instanceof Float) {
            return String.format("%.0f", (float) amount / 1000000);
        }
        if (amount != null && amount instanceof Double) {
            return String.format("%.0f", (double) amount / 1000000);
        }
        return "";
    }

    private String moicFormat(Double amount) {
        if (amount != null) { return String.format("%.1fx", amount); }
        return "";
    }

    private String irrFormat(Double amount) {
        if (amount != null) { return String.format("%.0f%%", amount); }
        return "";
    }
}