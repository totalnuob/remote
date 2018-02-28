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
            this.addWhiteTitle(keyFundStatisticsTitle, (firmDto.getFirmName() != null ? firmDto.getFirmName() : "") + " Investment Performance Data as of ?????? " + "($mln)", ps.getWidth() - offSet * 2);
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
                .add(new Paragraph(fundDto.getFundName())
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
                .add(new Paragraph(title))
                .setBackgroundColor(greenColor)
                .setFontColor(whiteColor));
    }

    private void addWhiteTitle(Table table, String title, Float width) {
        table.addCell(new Cell()
                .setWidth(width)
                .add(new Paragraph(title).setBold())
                .setTextAlignment(TextAlignment.CENTER));
    }

    private void addOrganizationOverview(Table table, PEFirmDto firmDto, Float width) {
        table.setWidth(width);
        table.addCell(new Cell().add(new Paragraph("GP Name").setBold()));
        table.addCell(new Cell().add(new Paragraph(firmDto.getFirmName() != null ? firmDto.getFirmName() : "")));
        table.addCell(new Cell().add(new Paragraph("Strategy AUM").setBold()));
        table.addCell(new Cell().add(new Paragraph(firmDto.getAum() != null ? String.format("%.2fm", firmDto.getAum()) : "")));
        table.addCell(new Cell().add(new Paragraph("Locations").setBold()));
        table.addCell(new Cell().add(new Paragraph(firmDto.getLocations() != null ? firmDto.getLocations() : "")));
        table.addCell(new Cell().add(new Paragraph("Firm Inception").setBold()));
        table.addCell(new Cell().add(new Paragraph(firmDto.getFoundedYear() != null ? firmDto.getFoundedYear().toString() : "")));
        table.addCell(new Cell().add(new Paragraph("Inv. + Oper. Team").setBold()));
        table.addCell(new Cell().add(new Paragraph((firmDto.getInvTeamSize() != null ? firmDto.getInvTeamSize() : "?") + " + " + (firmDto.getOpsTeamSize() != null ? firmDto.getOpsTeamSize() : "?"))));
        table.addCell(new Cell().add(new Paragraph("Peers").setBold()));
        table.addCell(new Cell().add(new Paragraph(firmDto.getPeers() != null ? firmDto.getPeers() : "")));
    }

    private void addFundSummary(Table table, PEFundDto fundDto, Float width) {
        table.setWidth(width);
        table.addCell(new Cell().add(new Paragraph("Fund Size").setBold()));
        table.addCell(new Cell().add(new Paragraph(fundDto.getFundSize() != null ? String.format("%.2fm", fundDto.getFundSize()) : "")));
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
    }
}