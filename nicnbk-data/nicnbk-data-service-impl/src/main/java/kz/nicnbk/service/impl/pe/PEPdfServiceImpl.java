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
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import kz.nicnbk.service.api.pe.*;
import kz.nicnbk.service.dto.pe.PEFirmDto;
import kz.nicnbk.service.dto.pe.PEFundDto;
import kz.nicnbk.service.dto.pe.PEFundManagementTeamDto;
import kz.nicnbk.service.dto.pe.PEOnePagerDescriptionsDto;
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
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Pak on 24/01/2018.
 */
@Service
public class PEPdfServiceImpl implements PEPdfService {

    private static final Logger logger = LoggerFactory.getLogger(PEPdfServiceImpl.class);

    @Autowired
    private PEFundService fundService;

    @Autowired
    private PEIrrService irrService;

    @Autowired
    private PEOnePagerDescriptionsService descriptionsService;

    @Autowired
    private PEFundManagementTeamService managementTeamService;

    //File locations
    private static final String onePagerDest = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/OnePager.pdf";
    private static final String gpLogoDest = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/img/SilverLakeLogo.png";
    private static final String nicLogoDest = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/img/NIClogo.png";
    private static final String barChartNetIrrDest = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/img/BarChartNetIrr.jpeg";
    private static final String barChartNetMoicDest = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/img/BarChartNetMoic.jpeg";

    //Colors
    private static final Color greenColor = new DeviceCmyk(0.78f, 0, 0.81f, 0.21f);
    private static final Color whiteColor = new DeviceCmyk(0, 0, 0, 0);

    //GP's and NIC's logos
    private Image gpLogo;
    private Image nicLogo;
    private Image barChartNetIrr;
    private Image barChartNetMoic;

    private static final Float lineSpacingMultiplier = 1f;
    private static final Float lineSpacingMultiplierText = 1.3f;

    @Override
    public InputStream createOnePager(Long fundId) {
        try {
            //Margins
            Float offSet = 36f;
            Float logoMaxHeight = 24f;
            Float logoMaxWidth = 72f;
//            Float topColunmOffSet = 182f;
            Float topColunmOffSet = 160.5f;
            Float columnGap = 3f;
            Float fontSize = 8f;

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
            document.setFontSize(fontSize);

            PEFundDto fundDto = fundService.get(fundId);
            PEFirmDto firmDto = fundDto.getFirm();
            List<PEFundDto> fundDtoList = fundService.loadFirmFunds(firmDto.getId(), true);
//            List<PEOnePagerDescriptionsDto> descriptionsDtoList = descriptionsService.findByFundId(fundId);
            List<PEOnePagerDescriptionsDto> descriptionsBenchmarkDtoList = descriptionsService.findByFundIdAndType(fundId, -1);
            List<PEOnePagerDescriptionsDto> descriptionsAsOfDateDtoList = descriptionsService.findByFundIdAndType(fundId, 0);
            List<PEOnePagerDescriptionsDto> descriptionsGpMeritsDtoList = descriptionsService.findByFundIdAndType(fundId, 1);
            List<PEOnePagerDescriptionsDto> descriptionsGpRisksDtoList = descriptionsService.findByFundIdAndType(fundId, 2);
            List<PEOnePagerDescriptionsDto> descriptionsStrategyMeritsDtoList = descriptionsService.findByFundIdAndType(fundId, 3);
            List<PEOnePagerDescriptionsDto> descriptionsStrategyRisksDtoList = descriptionsService.findByFundIdAndType(fundId, 4);
            List<PEOnePagerDescriptionsDto> descriptionsPerformanceMeritsDtoList = descriptionsService.findByFundIdAndType(fundId, 5);
            List<PEOnePagerDescriptionsDto> descriptionsPerformanceRisksDtoList = descriptionsService.findByFundIdAndType(fundId, 6);
            List<PEOnePagerDescriptionsDto> descriptionsFundStrategyDtoList = descriptionsService.findByFundIdAndType(fundId, 7);
            List<PEOnePagerDescriptionsDto> descriptionsDescriptiveDataDtoList = descriptionsService.findByFundIdAndType(fundId, 8);
            List<PEOnePagerDescriptionsDto> descriptionsTargetedClosingInformationDtoList = descriptionsService.findByFundIdAndType(fundId, 9);
            List<PEOnePagerDescriptionsDto> descriptionsSeniorManagementTeamDtoList = descriptionsService.findByFundIdAndType(fundId, 10);
            List<PEFundManagementTeamDto> managementTeamDtoList = managementTeamService.findByFundId(fundId);

            //Header
            Table headerTable = new Table(new float[]{1, 1, 1});
            this.addHeader(headerTable, fundDto, ps.getWidth() - offSet * 2, logoMaxWidth, fontSize + 2);
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
                this.addWhiteTitle(keyFundStatisticsTitle,
                        unNullifierToEmptyString(firmDto.getFirmName()) +
                                " Investment Performance Data as of " +
                                (
                                        (descriptionsAsOfDateDtoList != null &&
                                                descriptionsAsOfDateDtoList.size() == 1 &&
                                                descriptionsAsOfDateDtoList.get(0) != null &&
                                                descriptionsAsOfDateDtoList.get(0).getDescription() != null &&
                                                !descriptionsAsOfDateDtoList.get(0).getDescription().equals("")
                                        )
                                                ? descriptionsAsOfDateDtoList.get(0).getDescription()
                                                : "??????"
                                ) +
                                " ($mln)",
                        ps.getWidth() - offSet * 2);
                document.add(keyFundStatisticsTitle);

                //Key Fund Statistics Table
                Table keyFundStatisticsTable = new Table(new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
                this.addKeyFundStatistics(keyFundStatisticsTable, fundDtoList, ps.getWidth() - offSet * 2);
                document.add(keyFundStatisticsTable);

//                topColunmOffSet += (2 + fundDtoList.size()) * 16.4888888f + 34f;
                topColunmOffSet += (2 + fundDtoList.size()) * 13.38f + 27.76f;
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

            if (descriptionsBenchmarkDtoList != null &&
                    descriptionsBenchmarkDtoList.size() == 1 &&
                    descriptionsBenchmarkDtoList.get(0) != null &&
                    descriptionsBenchmarkDtoList.get(0).getDescription() != null &&
                    !descriptionsBenchmarkDtoList.get(0).getDescription().equals("")
                    ) {
                //Charts
                this.createCharts(firmDto, fundDtoList, descriptionsBenchmarkDtoList.get(0).getDescription(), columnOneWidth);
                barChartNetIrr = new Image(ImageDataFactory.create(barChartNetIrrDest));
                barChartNetMoic = new Image(ImageDataFactory.create(barChartNetMoicDest));
                barChartNetIrr.setWidth(columnOneWidth / 2);
                barChartNetMoic.setWidth(columnOneWidth / 2);
                document.add(new Paragraph().add(barChartNetIrr).add(barChartNetMoic));
            }

            //Observations Title
            Table observationsTitle = new Table(new float[]{1});
            this.addGreenTitle(observationsTitle, "Observations", columnOneWidth);
            document.add(observationsTitle);

            //Merits and Risks Title
            Table meritsAndRisksTitle = new Table(new float[]{1, 1});
            this.addDoubleWhiteTitle(meritsAndRisksTitle, "Merits", "Risks", columnOneWidth);
            document.add(meritsAndRisksTitle);

            if ((descriptionsGpMeritsDtoList != null && !descriptionsGpMeritsDtoList.isEmpty()) || (descriptionsGpRisksDtoList != null && !descriptionsGpRisksDtoList.isEmpty())) {

                //General Partner Title
                Table generalPartnerTitle = new Table(new float[]{1});
                this.addWhiteTitle(generalPartnerTitle, "General Partner", columnOneWidth);
                document.add(generalPartnerTitle);

                //General Partner Table
                Table generalPartnerTable = new Table(new float[]{1, 1});
                this.addMeritsRisks(generalPartnerTable, descriptionsGpMeritsDtoList, descriptionsGpRisksDtoList, columnOneWidth, fontSize - 1);
                document.add(generalPartnerTable);
            }

            if ((descriptionsStrategyMeritsDtoList != null && !descriptionsStrategyMeritsDtoList.isEmpty()) || (descriptionsStrategyRisksDtoList != null && !descriptionsStrategyRisksDtoList.isEmpty())) {

                //Strategy and Structure Title
                Table strategyAndStructureTitle = new Table(new float[]{1});
                this.addWhiteTitle(strategyAndStructureTitle, "Strategy/Structure", columnOneWidth);
                document.add(strategyAndStructureTitle);

                //Strategy and Structure Table
                Table strategyAndStructureTable = new Table(new float[]{1, 1});
                this.addMeritsRisks(strategyAndStructureTable, descriptionsStrategyMeritsDtoList, descriptionsStrategyRisksDtoList, columnOneWidth, fontSize - 1);
                document.add(strategyAndStructureTable);
            }

            if ((descriptionsPerformanceMeritsDtoList != null && !descriptionsPerformanceMeritsDtoList.isEmpty()) || (descriptionsPerformanceRisksDtoList != null && !descriptionsPerformanceRisksDtoList.isEmpty())) {

                //Performance Title
                Table performanceTitle = new Table(new float[]{1});
                this.addWhiteTitle(performanceTitle, "Performance", columnOneWidth);
                document.add(performanceTitle);

                //Performance Table
                Table performanceTable = new Table(new float[]{1, 1});
                this.addMeritsRisks(performanceTable, descriptionsPerformanceMeritsDtoList, descriptionsPerformanceRisksDtoList, columnOneWidth, fontSize - 1);
                document.add(performanceTable);
            }

            //Second column
            //##########################################################################################################
            //Define column areas
            Rectangle[] columnTwo = {new Rectangle(offSet + columnOneWidth + columnGap, offSet, columnTwoWidth, columnHeight)};
            document.setRenderer(new ColumnDocumentRenderer(document, columnTwo));

            if (descriptionsFundStrategyDtoList != null && !descriptionsFundStrategyDtoList.isEmpty()) {

                //Fund Strategy Title
                Table fundStrategyTitle = new Table(new float[]{1});
                this.addGreenTitle(fundStrategyTitle, "Fund Strategy", columnTwoWidth);
                document.add(fundStrategyTitle);

                //Fund Strategy Table
                Table fundStrategyTable = new Table(new float[]{1});
                this.addFundStrategy(fundStrategyTable, descriptionsFundStrategyDtoList, columnTwoWidth, fontSize - 1);
                document.add(fundStrategyTable);
            }

            if (descriptionsDescriptiveDataDtoList != null && !descriptionsDescriptiveDataDtoList.isEmpty()) {

                //Descriptive Data Title
                Table descriptiveDataTitle = new Table(new float[]{1});
                this.addGreenTitle(descriptiveDataTitle, "Descriptive Data", columnTwoWidth);
                document.add(descriptiveDataTitle);

                //Descriptive Data Table
                Table descriptiveDataTable = new Table(new float[]{1, 1});
                this.addTwoColumns(descriptiveDataTable, descriptionsDescriptiveDataDtoList, columnTwoWidth);
                document.add(descriptiveDataTable);
            }

            if (descriptionsTargetedClosingInformationDtoList != null && !descriptionsTargetedClosingInformationDtoList.isEmpty()) {

                //Targeted Closing Information Title
                Table targetedClosingInformationTitle = new Table(new float[]{1});
                this.addGreenTitle(targetedClosingInformationTitle, "Targeted Closing Information", columnTwoWidth);
                document.add(targetedClosingInformationTitle);

                //Targeted Closing Information Table
                Table targetedClosingInformationTable = new Table(new float[]{1, 1});
                this.addTwoColumns(targetedClosingInformationTable, descriptionsTargetedClosingInformationDtoList, columnTwoWidth);
                document.add(targetedClosingInformationTable);
            }

            if ((descriptionsSeniorManagementTeamDtoList != null && !descriptionsSeniorManagementTeamDtoList.isEmpty()) ||
                    (managementTeamDtoList != null && !managementTeamDtoList.isEmpty())) {

                //Senior Management Team Title
                Table seniorManagementTeamTitle = new Table(new float[]{1});
                this.addGreenTitle(seniorManagementTeamTitle, "Senior Management Team", columnTwoWidth);
                document.add(seniorManagementTeamTitle);

                //Senior Management Team Table
                if (descriptionsSeniorManagementTeamDtoList != null && !descriptionsSeniorManagementTeamDtoList.isEmpty()) {
                    Table seniorManagementTeamTable = new Table(new float[]{1, 1});
                    this.addTwoColumns(seniorManagementTeamTable, descriptionsSeniorManagementTeamDtoList, columnTwoWidth);
                    document.add(seniorManagementTeamTable);
                }

                //Senior Management Team Table
                if (managementTeamDtoList != null && !managementTeamDtoList.isEmpty()) {
                    Table managementTeamTable = new Table(new float[]{1});
                    this.addManagementTeam(managementTeamTable, managementTeamDtoList, columnTwoWidth);
                    document.add(managementTeamTable);
                }
            }

            document.close();

            InputStream inputStream = new FileInputStream(onePagerDest);

            file.delete();

            return inputStream;
        } catch (Exception ex) {
            logger.error("Error creating PE fund's One Pager: " + fundId, ex);
        }
        return null;
    }

    private void createCharts(PEFirmDto firmDto, List<PEFundDto> fundDtoList, String benchmark, Float width) throws Exception {
        String gpName = firmDto.getFirmName();
        String benchmarkName = benchmark;

        DefaultCategoryDataset datasetIrr = new DefaultCategoryDataset();

        for (PEFundDto fundDto : fundDtoList) {
            datasetIrr.addValue(fundDto.getNetIrr(), gpName, fundDto.getFundName());
            datasetIrr.addValue(fundDto.getBenchmarkNetIrr(), benchmarkName, fundDto.getFundName());
        }

        JFreeChart barChartIrr = ChartFactory.createBarChart("Net IRR", "", "", datasetIrr, PlotOrientation.VERTICAL, true, false, false);
        barChartIrr.getPlot().setBackgroundPaint(java.awt.Color.WHITE);
        File BarChartIrr = new File(barChartNetIrrDest);
        ChartUtilities.saveChartAsJPEG(BarChartIrr, barChartIrr, Math.round(width), Math.round(width * 3 / 4));

        DefaultCategoryDataset datasetTvpi = new DefaultCategoryDataset();

        for (PEFundDto fundDto : fundDtoList) {
            datasetTvpi.addValue(fundDto.getNetTvpi(), gpName, fundDto.getFundName());
            datasetTvpi.addValue(fundDto.getBenchmarkNetTvpi(), benchmarkName, fundDto.getFundName());
        }

        JFreeChart barChartTvpi = ChartFactory.createBarChart("Net MOIC", "", "", datasetTvpi, PlotOrientation.VERTICAL, true, false, false);
        barChartTvpi.getPlot().setBackgroundPaint(java.awt.Color.WHITE);
        File BarChartTvpi = new File(barChartNetMoicDest);
        ChartUtilities.saveChartAsJPEG(BarChartTvpi, barChartTvpi, Math.round(width), Math.round(width * 3 / 4));
    }

    private void addHeader(Table table, PEFundDto fundDto, Float width, Float logoWidth, Float fontSize) {
        table.addCell(new Cell()
                .setWidth(logoWidth)
                .add(new Paragraph().add(gpLogo))
                .setTextAlignment(TextAlignment.LEFT));
        table.addCell(new Cell()
                .setWidth(width - logoWidth * 2)
                .add(new Paragraph(unNullifierToEmptyString(fundDto.getFundName()))
                        .setBold()
                        .setFontSize(fontSize))
                .setTextAlignment(TextAlignment.CENTER));
        table.addCell(new Cell()
                .setWidth(logoWidth)
                .add(new Paragraph().add(nicLogo))
                .setTextAlignment(TextAlignment.RIGHT));
    }

    private void addGreenTitle(Table table, String title, Float width) {
        table.addCell(new Cell()
                .setWidth(width)
                .add(new Paragraph(unNullifierToEmptyString(title)).setMultipliedLeading(lineSpacingMultiplier))
                .setBackgroundColor(greenColor)
                .setFontColor(whiteColor));
    }

    private void addWhiteTitle(Table table, String title, Float width) {
        table.addCell(new Cell()
                .setWidth(width)
                .add(new Paragraph(unNullifierToEmptyString(title)).setMultipliedLeading(lineSpacingMultiplier).setBold())
                .setTextAlignment(TextAlignment.CENTER));
    }

    private void addDoubleWhiteTitle(Table table, String title1, String title2, Float width) {
        table.addCell(new Cell()
                .setWidth(width / 2)
                .add(new Paragraph(unNullifierToEmptyString(title1)).setMultipliedLeading(lineSpacingMultiplier).setBold())
                .setTextAlignment(TextAlignment.CENTER));
        table.addCell(new Cell()
                .setWidth(width / 2)
                .add(new Paragraph(unNullifierToEmptyString(title2)).setMultipliedLeading(lineSpacingMultiplier).setBold())
                .setTextAlignment(TextAlignment.CENTER));
    }

    private void addOrganizationOverview(Table table, PEFirmDto firmDto, Float width) {
        table.setWidth(width);
        table.addCell(new Cell().add(new Paragraph("GP Name").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph(unNullifierToEmptyString(firmDto.getFirmName())).setMultipliedLeading(lineSpacingMultiplier)));
        table.addCell(new Cell().add(new Paragraph("Strategy AUM ($mln)").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph(mlnFormat(firmDto.getAum() * 1000000)).setMultipliedLeading(lineSpacingMultiplier)));
        table.addCell(new Cell().add(new Paragraph("Locations").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph(unNullifierToEmptyString(firmDto.getLocations())).setMultipliedLeading(lineSpacingMultiplier)));
        table.addCell(new Cell().add(new Paragraph("Firm Inception").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph(unNullifierToEmptyString(firmDto.getFoundedYear())).setMultipliedLeading(lineSpacingMultiplier)));
        table.addCell(new Cell().add(new Paragraph("Inv. + Oper. Team").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph((firmDto.getInvTeamSize() != null ? firmDto.getInvTeamSize() : "?") + " + " + (firmDto.getOpsTeamSize() != null ? firmDto.getOpsTeamSize() : "?")).setMultipliedLeading(lineSpacingMultiplier)));
        table.addCell(new Cell().add(new Paragraph("Peers").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph(unNullifierToEmptyString(firmDto.getPeers())).setMultipliedLeading(lineSpacingMultiplier)));
    }

    private void addFundSummary(Table table, PEFundDto fundDto, Float width) {
        table.setWidth(width);
        table.addCell(new Cell().add(new Paragraph("Fund Size ($mln)").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph(mlnFormat(fundDto.getFundSize())).setMultipliedLeading(lineSpacingMultiplier)));
        table.addCell(new Cell().add(new Paragraph("Mgt. fee").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph("Mgt. fee").setMultipliedLeading(lineSpacingMultiplier)));
        table.addCell(new Cell().add(new Paragraph("Industry").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph("Industry").setMultipliedLeading(lineSpacingMultiplier)));
        table.addCell(new Cell().add(new Paragraph("Hard cap").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph("Hard cap").setMultipliedLeading(lineSpacingMultiplier)));
        table.addCell(new Cell().add(new Paragraph("Carry").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph("Carry").setMultipliedLeading(lineSpacingMultiplier)));
        table.addCell(new Cell().add(new Paragraph("Strategy").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph("Strategy").setMultipliedLeading(lineSpacingMultiplier)));
        table.addCell(new Cell().add(new Paragraph("GP Commitment").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph("GP Commitment").setMultipliedLeading(lineSpacingMultiplier)));
        table.addCell(new Cell().add(new Paragraph("Hurdle").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph("Hurdle").setMultipliedLeading(lineSpacingMultiplier)));
        table.addCell(new Cell().add(new Paragraph("Geography").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph("Geography").setMultipliedLeading(lineSpacingMultiplier)));
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
        table.addHeaderCell(new Cell().add(new Paragraph("Vintage").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("# of Inv.").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Fund Size").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Invested").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Realized").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Unrealized").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Gross MOIC").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Gross IRR").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Net MOIC").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Net IRR").setMultipliedLeading(lineSpacingMultiplier).setBold()));

        for (PEFundDto fundDto : fundDtoList) {
            totalNumberOfInvestments += unNullifierToZero(fundDto.getNumberOfInvestments());
            totalInvested += unNullifierToZero(fundDto.getInvestedAmount());
            totalRealized += unNullifierToZero(fundDto.getRealized());
            totalUnrealized += unNullifierToZero(fundDto.getUnrealized());

            if (fundDto.getCalculationType() == null || fundDto.getCalculationType() !=2) {
                areAllKeyFundStatisticsCalculatedByGrossCF = false;
            }

            table.addCell(new Cell().add(new Paragraph(unNullifierToEmptyString(fundDto.getFundName())).setMultipliedLeading(lineSpacingMultiplier)));
            table.addCell(new Cell().add(new Paragraph(Integer.toString(fundDto.getVintage())).setMultipliedLeading(lineSpacingMultiplier)));
            table.addCell(new Cell().add(new Paragraph(unNullifierToEmptyString(fundDto.getNumberOfInvestments())).setMultipliedLeading(lineSpacingMultiplier)));
            table.addCell(new Cell().add(new Paragraph(mlnFormat(fundDto.getFundSize())).setMultipliedLeading(lineSpacingMultiplier)));
            table.addCell(new Cell().add(new Paragraph(mlnFormat(fundDto.getInvestedAmount())).setMultipliedLeading(lineSpacingMultiplier)));
            table.addCell(new Cell().add(new Paragraph(mlnFormat(fundDto.getRealized())).setMultipliedLeading(lineSpacingMultiplier)));
            table.addCell(new Cell().add(new Paragraph(mlnFormat(fundDto.getUnrealized())).setMultipliedLeading(lineSpacingMultiplier)));
            table.addCell(new Cell().add(new Paragraph(moicFormat(fundDto.getGrossTvpi())).setMultipliedLeading(lineSpacingMultiplier)));
            table.addCell(new Cell().add(new Paragraph(irrFormat(fundDto.getGrossIrr())).setMultipliedLeading(lineSpacingMultiplier)));
            table.addCell(new Cell().add(new Paragraph(moicFormat(fundDto.getNetTvpi())).setMultipliedLeading(lineSpacingMultiplier)));
            table.addCell(new Cell().add(new Paragraph(irrFormat(fundDto.getNetIrr())).setMultipliedLeading(lineSpacingMultiplier)));
        }

        if (totalInvested != 0.0) {
            totalGrossMOIC = (totalRealized + totalUnrealized) / totalInvested;
        }
        if (areAllKeyFundStatisticsCalculatedByGrossCF) {
            totalGrossIrr  = irrService.getIrrByFundList(fundDtoList);
        }

        table.addCell(new Cell().add(new Paragraph("Total").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell());
        table.addCell(new Cell().add(new Paragraph(Integer.toString(totalNumberOfInvestments)).setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph("Fund Size").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph(mlnFormat(totalInvested)).setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph(mlnFormat(totalRealized)).setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph(mlnFormat(totalUnrealized)).setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph(moicFormat(totalGrossMOIC)).setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph(irrFormat(totalGrossIrr)).setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph("Net MOIC").setMultipliedLeading(lineSpacingMultiplier).setBold()));
        table.addCell(new Cell().add(new Paragraph("Net IRR").setMultipliedLeading(lineSpacingMultiplier).setBold()));
    }

    private void addMeritsRisks(Table table, List<PEOnePagerDescriptionsDto> descriptionsDtoListMerits, List<PEOnePagerDescriptionsDto> descriptionsDtoListRisks, Float width, Float fontSize) {
        table.setFontSize(fontSize);

        Cell cellMerits = new Cell().setWidth(width / 2);
        Cell cellRisks = new Cell().setWidth(width / 2);

        if (descriptionsDtoListMerits != null) {
            for (PEOnePagerDescriptionsDto descriptionsDto : descriptionsDtoListMerits) {
                if (descriptionsDto != null) {
                    Paragraph p = new Paragraph().setMultipliedLeading(lineSpacingMultiplierText);
                    if (descriptionsDto.getDescriptionBold() != null && !descriptionsDto.getDescriptionBold().equals("")) {
                        p.add(new Text("(+) " + descriptionsDto.getDescriptionBold() + " ").setBold());
                    } else {
                        p.add(new Text("(+) ").setBold());
                    }
                    if (descriptionsDto.getDescription() != null && !descriptionsDto.getDescription().equals("")) {
                        p.add(descriptionsDto.getDescription());
                    }
                    cellMerits.add(p);
                }
            }
        }

        if (descriptionsDtoListRisks != null) {
            for (PEOnePagerDescriptionsDto descriptionsDto : descriptionsDtoListRisks) {
                if (descriptionsDto != null) {
                    Paragraph p = new Paragraph().setMultipliedLeading(lineSpacingMultiplierText);
                    if (descriptionsDto.getDescriptionBold() != null && !descriptionsDto.getDescriptionBold().equals("")) {
                        p.add(new Text("(-) " + descriptionsDto.getDescriptionBold() + " ").setBold());
                    } else {
                        p.add(new Text("(-) ").setBold());
                    }
                    if (descriptionsDto.getDescription() != null && !descriptionsDto.getDescription().equals("")) {
                        p.add(descriptionsDto.getDescription());
                    }
                    cellRisks.add(p);
                }
            }
        }

        table.addCell(cellMerits);
        table.addCell(cellRisks);
    }

    private void addFundStrategy(Table table, List<PEOnePagerDescriptionsDto> descriptionsDtoList, Float width, Float fontSize) {
        table.setFontSize(fontSize);

        Cell cell = new Cell().setWidth(width);

        if (descriptionsDtoList != null) {
            for (PEOnePagerDescriptionsDto descriptionsDto : descriptionsDtoList) {
                if (descriptionsDto != null) {
                    Paragraph p = new Paragraph().setMultipliedLeading(lineSpacingMultiplierText);
                    if (descriptionsDto.getDescriptionBold() != null && !descriptionsDto.getDescriptionBold().equals("")) {
                        p.add(new Text(descriptionsDto.getDescriptionBold() + " ").setBold());
                    }
                    if (descriptionsDto.getDescription() != null && !descriptionsDto.getDescription().equals("")) {
                        p.add(descriptionsDto.getDescription());
                    }
                    cell.add(p);
                }
            }
        }

        table.addCell(cell);
    }

    private void addTwoColumns(Table table, List<PEOnePagerDescriptionsDto> descriptionsDtoList, Float width) {
        table.setWidth(width);

        for (PEOnePagerDescriptionsDto descriptionsDto : descriptionsDtoList) {
            table.addCell(new Cell().add(new Paragraph(
                            unNullifierToEmptyString(descriptionsDto.getDescriptionBold())).setMultipliedLeading(lineSpacingMultiplier).setBold()));
            table.addCell(new Cell().add(new Paragraph(
                            unNullifierToEmptyString(descriptionsDto.getDescription())).setMultipliedLeading(lineSpacingMultiplier)));
        }
    }

    private void addManagementTeam(Table table, List<PEFundManagementTeamDto> managementTeamDtoList, Float width) {
        table.setWidth(width);

        for (PEFundManagementTeamDto managementTeamDto : managementTeamDtoList) {
            Paragraph p = new Paragraph().setMultipliedLeading(lineSpacingMultiplier);
            com.itextpdf.layout.element.List list = new com.itextpdf.layout.element.List().setSymbolIndent(4).setListSymbol("   -");

            p.add(new Text(unNullifierToEmptyString(managementTeamDto.getName())).setBold());

            if (managementTeamDto.getPosition() != null && !managementTeamDto.getPosition().equals("")) {
                p.add(", " + managementTeamDto.getPosition());
            }
            if (managementTeamDto.getAge() != null) {
                p.add(", " + managementTeamDto.getAge() + " yrs");
            }

            if (managementTeamDto.getExperience() != null && !managementTeamDto.getExperience().equals("")) {
                list.add(new ListItem(managementTeamDto.getExperience()));
            }
            if (managementTeamDto.getEducation() != null && !managementTeamDto.getEducation().equals("")) {
                list.add(new ListItem(managementTeamDto.getEducation()));
            }

            table.addCell(new Cell().add(p).add(list));
        }
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