package kz.nicnbk.service.impl.pe;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import kz.nicnbk.service.api.pe.PEFundService;
import kz.nicnbk.service.api.pe.PEPdfService;
import kz.nicnbk.service.dto.pe.PEFirmDto;
import kz.nicnbk.service.dto.pe.PEFundDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Pak on 24/01/2018.
 */
@Service
public class PEPdfServiceImpl implements PEPdfService {

    private static final Logger logger = LoggerFactory.getLogger(PEFundServiceImpl.class);

    @Autowired
    private PEFundService fundService;

    //File locations
    private String onePagerDest = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/OnePager.pdf";
    private String gpLogoDest = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/img/SilverLakeLogo.png";
    private String nicLogoDest = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/img/NIClogo.png";

    //Margins
    private Float offSet = 36f;
    private Float logoMaxHeight = 24f;
    private Float logoMaxWidth = 72f;

    //Colors
    private Color greenColor = new DeviceCmyk(0.78f, 0, 0.81f, 0.21f);
    private Color whiteColor = new DeviceCmyk(0, 0, 0, 0);

    //GP's and NIC's logos
    private Image gpLogo;
    private Image nicLogo;

    @Override
    public void createOnePager(Long fundId) {
        try {
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
            this.addHeader(headerTable, fundDto, ps.getWidth() - offSet * 2);
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

            //Key Fund Statistics Title
            Table keyFundStatisticsTitle = new Table(new float[]{1});
            this.addGreenTitle(keyFundStatisticsTitle, "Key fund statistics", ps.getWidth() - offSet * 2);
            this.addWhiteTitle(keyFundStatisticsTitle, unNullifier(firmDto.getFirmName()) + " Investment Performance Data as of ?????? " + "($mln)", ps.getWidth() - offSet * 2);
            document.add(keyFundStatisticsTitle);

            //Key Fund Statistics Table
            Table keyFundStatisticsTable = new Table(new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
            this.addKeyFundStatistics(keyFundStatisticsTable, fundDtoList, ps.getWidth() - offSet * 2);
            document.add(keyFundStatisticsTable);

            document.close();
        } catch (IOException ex) {
            logger.error("Error creating PE fund's One Pager: " + fundId, ex);
        }
    }

    private void addHeader(Table table, PEFundDto fundDto, Float width) {
        table.addCell(new Cell()
                .setWidth(logoMaxWidth)
                .add(new Paragraph().add(gpLogo))
                .setTextAlignment(TextAlignment.LEFT));
        table.addCell(new Cell()
                .setWidth(width - logoMaxWidth * 2)
                .add(new Paragraph(unNullifier(fundDto.getFundName()))
                        .setBold()
                        .setFontSize(10))
                .setTextAlignment(TextAlignment.CENTER));
        table.addCell(new Cell()
                .setWidth(logoMaxWidth)
                .add(new Paragraph().add(nicLogo))
                .setTextAlignment(TextAlignment.RIGHT));
    }

    private void addGreenTitle(Table table, String title, Float width) {
        table.addCell(new Cell()
                .setWidth(width)
                .add(new Paragraph(unNullifier(title)))
                .setBackgroundColor(greenColor)
                .setFontColor(whiteColor));
    }

    private void addWhiteTitle(Table table, String title, Float width) {
        table.addCell(new Cell()
                .setWidth(width)
                .add(new Paragraph(unNullifier(title)).setBold())
                .setTextAlignment(TextAlignment.CENTER));
    }

    private void addOrganizationOverview(Table table, PEFirmDto firmDto, Float width) {
        table.setWidth(width);
        table.addCell(new Cell().add(new Paragraph("GP Name").setBold()));
        table.addCell(new Cell().add(new Paragraph(unNullifier(firmDto.getFirmName()))));
        table.addCell(new Cell().add(new Paragraph("Strategy AUM ($mln)").setBold()));
        table.addCell(new Cell().add(new Paragraph(mlnFormat(firmDto.getAum() * 1000000))));
        table.addCell(new Cell().add(new Paragraph("Locations").setBold()));
        table.addCell(new Cell().add(new Paragraph(unNullifier(firmDto.getLocations()))));
        table.addCell(new Cell().add(new Paragraph("Firm Inception").setBold()));
        table.addCell(new Cell().add(new Paragraph(unNullifier(firmDto.getFoundedYear()))));
        table.addCell(new Cell().add(new Paragraph("Inv. + Oper. Team").setBold()));
        table.addCell(new Cell().add(new Paragraph((firmDto.getInvTeamSize() != null ? firmDto.getInvTeamSize() : "?") + " + " + (firmDto.getOpsTeamSize() != null ? firmDto.getOpsTeamSize() : "?"))));
        table.addCell(new Cell().add(new Paragraph("Peers").setBold()));
        table.addCell(new Cell().add(new Paragraph(unNullifier(firmDto.getPeers()))));
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
            table.addCell(new Cell().add(new Paragraph(unNullifier(fundDto.getFundName()))));
            table.addCell(new Cell().add(new Paragraph(Integer.toString(fundDto.getVintage()))));
            table.addCell(new Cell().add(new Paragraph(unNullifier(fundDto.getNumberOfInvestments()))));
            table.addCell(new Cell().add(new Paragraph(mlnFormat(fundDto.getFundSize()))));
            table.addCell(new Cell().add(new Paragraph(mlnFormat(fundDto.getInvestedAmount()))));
            table.addCell(new Cell().add(new Paragraph(mlnFormat(fundDto.getRealized()))));
            table.addCell(new Cell().add(new Paragraph(mlnFormat(fundDto.getUnrealized()))));
            table.addCell(new Cell().add(new Paragraph(moicFormat(fundDto.getGrossTvpi()))));
            table.addCell(new Cell().add(new Paragraph(irrFormat(fundDto.getGrossIrr()))));
            table.addCell(new Cell().add(new Paragraph(moicFormat(fundDto.getNetTvpi()))));
            table.addCell(new Cell().add(new Paragraph(irrFormat(fundDto.getNetIrr()))));
        }


    }

    private String unNullifier(Object st) {
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