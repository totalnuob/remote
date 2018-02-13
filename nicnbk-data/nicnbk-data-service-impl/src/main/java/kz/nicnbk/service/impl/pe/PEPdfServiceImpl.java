package kz.nicnbk.service.impl.pe;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import kz.nicnbk.service.api.pe.PECompanyPerformanceIddService;
import kz.nicnbk.service.api.pe.PEPdfService;
import kz.nicnbk.service.dto.pe.PECompanyPerformanceIddDto;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Pak on 24/01/2018.
 */
@Service
public class PEPdfServiceImpl implements PEPdfService {

    private static final Logger logger = LoggerFactory.getLogger(PEFundServiceImpl.class);

    @Autowired
    private PECompanyPerformanceIddService performanceIddService;

    @Override
    public void createOnePager(Long fundId) {

        try {

            final String fiat = "FIAT";
            final String audi = "AUDI";
            final String ford = "FORD";
            final String speed = "Speed";
            final String millage = "Millage";
            final String userrating = "User Rating";
            final String safety = "safety";

            final DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
            dataset.addValue( 1.0 , fiat , speed );
            dataset.addValue( 3.0 , fiat , userrating );
            dataset.addValue( 5.0 , fiat , millage );
            dataset.addValue( 5.0 , fiat , safety );

            dataset.addValue( 5.0 , audi , speed );
            dataset.addValue( 6.0 , audi , userrating );
            dataset.addValue( 10.0 , audi , millage );
            dataset.addValue( 4.0 , audi , safety );

            dataset.addValue( 4.0 , ford , speed );
            dataset.addValue( 2.0 , ford , userrating );
            dataset.addValue( 3.0 , ford , millage );
            dataset.addValue( 6.0 , ford , safety );

            JFreeChart barChart = ChartFactory.createBarChart(
                    "CAR USAGE STATIStICS",
                    "Category", "Score",
                    dataset, PlotOrientation.VERTICAL,
                    true, true, false);

            barChart.getPlot().setBackgroundPaint(Color.WHITE);

            int width = 256;    /* Width of the image */
            int height = 192;   /* Height of the image */
            File BarChart = new File( "nicnbk-data/nicnbk-data-service-impl/src/main/resources/img/BarChart.jpeg" );
            ChartUtilities.saveChartAsJPEG( BarChart , barChart , width , height );

//            ##########################################################################################

            String DEST = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/hello_world.pdf";
            String DOG = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/img/dog.bmp";
//            String FOX = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/img/fox.bmp";
            String FOX = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/img/BarChart.jpeg";

            File file = new File(DEST);
            file.getParentFile().mkdirs();

            PdfWriter writer = new PdfWriter(DEST);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont fontTimes = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
            PdfFont bold = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);

            document.add(new Paragraph("Hello World!"));
            document.add(new Paragraph("iText is:").setFont(fontTimes));

            List list = new List()
                    .setSymbolIndent(12)
                    .setListSymbol("\u2022")
                    .setFont(fontTimes);
            list.add(new ListItem("Never gonna give you up"))
                    .add(new ListItem("Never gonna let you down"))
                    .add(new ListItem("Never gonna run around and desert you"))
                    .add(new ListItem("Never gonna make you cry"))
                    .add(new ListItem("Never gonna say goodbye"))
                    .add(new ListItem("Never gonna tell a lie and hurt you"));
            document.add(list);

            Image fox = new Image(ImageDataFactory.create(FOX));
            Image dog = new Image(ImageDataFactory.create(DOG));
            Paragraph p = new Paragraph("The quick brown ")
                    .add(fox)
                    .add(" jumps over the lazy ")
                    .add(dog);
            document.add(p);

            java.util.List<PECompanyPerformanceIddDto> performanceIddDtoList = performanceIddService.findByFundId(fundId);

            Table table = new Table(new float[]{3, 2, 2, 2, 2, 1, 1});

            addHeader(table, bold);
            for (PECompanyPerformanceIddDto performanceIddDto : performanceIddDtoList) {
                addRow(table, performanceIddDto);
            }

            document.add(table);

            document.close();
        } catch (IOException ex) {
            logger.error("Error creating PE fund's One Pager: " + fundId, ex);
        }
    }

    public void addHeader(Table table, PdfFont font) {
        table.addHeaderCell(new Cell().add(new Paragraph("Company name").setFont(font)));
        table.addHeaderCell(new Cell().add(new Paragraph("Invested").setFont(font)));
        table.addHeaderCell(new Cell().add(new Paragraph("Realized").setFont(font)));
        table.addHeaderCell(new Cell().add(new Paragraph("Unrealized").setFont(font)));
        table.addHeaderCell(new Cell().add(new Paragraph("Total value").setFont(font)));
        table.addHeaderCell(new Cell().add(new Paragraph("Multiple").setFont(font)));
        table.addHeaderCell(new Cell().add(new Paragraph("Gross IRR").setFont(font)));
    }

    public void addRow(Table table, PECompanyPerformanceIddDto performanceIddDto) {
        table.addCell(new Cell().add(new Paragraph(performanceIddDto.getCompanyName() != null ? performanceIddDto.getCompanyName() : "")));
        table.addCell(new Cell().add(new Paragraph(performanceIddDto.getInvested() != null ? String.format("%.2fm", performanceIddDto.getInvested() / 1000000) : "")));
        table.addCell(new Cell().add(new Paragraph(performanceIddDto.getRealized() != null ? String.format("%.2fm", performanceIddDto.getRealized() / 1000000) : "")));
        table.addCell(new Cell().add(new Paragraph(performanceIddDto.getUnrealized() != null ? String.format("%.2fm", performanceIddDto.getUnrealized() / 1000000) : "")));
        table.addCell(new Cell().add(new Paragraph(performanceIddDto.getTotalValue() != null ? String.format("%.2fm", performanceIddDto.getTotalValue() / 1000000) : "")));
        table.addCell(new Cell().add(new Paragraph(performanceIddDto.getMultiple() != null ? String.format("%.1fx", performanceIddDto.getMultiple()) : "")));
        table.addCell(new Cell().add(new Paragraph(performanceIddDto.getGrossIrr() != null ? String.format("%.2f%%", performanceIddDto.getGrossIrr()) : "")));
    }
}
