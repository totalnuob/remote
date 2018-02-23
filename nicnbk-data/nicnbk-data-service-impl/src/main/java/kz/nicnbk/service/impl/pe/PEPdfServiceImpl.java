package kz.nicnbk.service.impl.pe;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import kz.nicnbk.service.api.pe.PECompanyPerformanceIddService;
import kz.nicnbk.service.api.pe.PEFundService;
import kz.nicnbk.service.api.pe.PEPdfService;
import kz.nicnbk.service.dto.pe.PECompanyPerformanceIddDto;
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

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Pak on 24/01/2018.
 */
@Service
public class PEPdfServiceImpl implements PEPdfService {

    private static final Logger logger = LoggerFactory.getLogger(PEFundServiceImpl.class);

    @Autowired
    private PECompanyPerformanceIddService performanceIddService;

    @Autowired
    private PEFundService fundService;

    static PdfFont timesNewRoman = null;
    static PdfFont timesNewRomanBold = null;

    @Override
    public void createOnePager(Long fundId) {

        try {

            String onePagerDest = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/OnePager.pdf";

            String gpLogoDest = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/img/SilverLakeLogo.png";
            String nicLogoDest = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/img/NIClogo.png";
            String barChartDest = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/img/BarChart.jpeg";
            String dogImageDest = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/img/dog.bmp";
            String foxImageDest = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/img/fox.bmp";

            String barChart_TXT = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/txt/barChart.txt";

            timesNewRoman = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
            timesNewRomanBold = PdfFontFactory.createFont(FontConstants.TIMES_BOLD);
            PdfFont helvetica = PdfFontFactory.createFont(FontConstants.HELVETICA);
            PdfFont helveticaBold = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);

//            ##########################################################################################
//            Chart creation
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

            JFreeChart barChart = ChartFactory.createBarChart("CAR USAGE STATIStICS", "Category", "Score", dataset, PlotOrientation.VERTICAL, true, true, false);

            barChart.getPlot().setBackgroundPaint(Color.WHITE);

            int width = 256;    /* Width of the image */
            int height = 192;   /* Height of the image */
            File BarChart = new File(barChartDest);
            ChartUtilities.saveChartAsJPEG( BarChart , barChart , width , height );

//            ##########################################################################################

            File file = new File(onePagerDest);
            file.getParentFile().mkdirs();

            //Initialize PDF document
            PdfDocument pdf = new PdfDocument(new PdfWriter(onePagerDest));
            PageSize ps = PageSize.A4;

            // Initialize document
            Document document = new Document(pdf, ps);

            PEFundDto fundDto = fundService.get(fundId);
            String fundName = fundDto.getFundName();

            document.add(new Paragraph(fundName));

            Image fox = new Image(ImageDataFactory.create(foxImageDest));
            Image dog = new Image(ImageDataFactory.create(dogImageDest));
            Paragraph p = new Paragraph("The quick brown ")
                    .add(fox)
                    .add(" jumps over the lazy ")
                    .add(dog);
            document.add(p);

            //Set column parameters
            int offSet = 36;
            int columnWidth = (int) (ps.getWidth() - offSet * 2 - 10) / 3;
            int columnHeight = (int) (ps.getHeight() - offSet * 2);

            //Define column areas
            Rectangle[] columns = {new Rectangle(offSet - 5, offSet + 300, columnWidth, columnHeight - 500),
                    new Rectangle(offSet + columnWidth, offSet + 300, columnWidth, columnHeight - 500),
                    new Rectangle(offSet + columnWidth * 2 + 5, offSet + 300, columnWidth, columnHeight - 500)};
            document.setRenderer(new ColumnDocumentRenderer(document, columns));

            Image apple = new Image(ImageDataFactory.create(barChartDest)).setWidth(columnWidth);
            String articleApple = new String(Files.readAllBytes(Paths.get(barChart_TXT)), StandardCharsets.UTF_8);
            this.addArticle(document, "Apple Encryption Engineers, if Ordered to Unlock iPhone, Might Resist", "By JOHN MARKOFF MARCH 18, 2016", apple, articleApple);
            Image facebook = new Image(ImageDataFactory.create(dogImageDest)).setWidth(columnWidth);
            String articleFB = new String(Files.readAllBytes(Paths.get(barChart_TXT)), StandardCharsets.UTF_8);
            this.addArticle(document, "With \"Smog Jog\" Through Beijing, Zuckerberg Stirs Debate on Air Pollution", "By PAUL MOZUR MARCH 18, 2016", facebook, articleFB);
            Image inst = new Image(ImageDataFactory.create(foxImageDest)).setWidth(columnWidth);
            String articleInstagram = new String(Files.readAllBytes(Paths.get(barChart_TXT)), StandardCharsets.UTF_8);
            this.addArticle(document, "Instagram May Change Your Feed, Personalizing It With an Algorithm","By MIKE ISAAC MARCH 15, 2016", inst, articleInstagram);

            Rectangle[] columnsDefault = {new Rectangle(offSet, offSet, ps.getWidth() - offSet * 2, 300)};
            document.setRenderer(new ColumnDocumentRenderer(document, columnsDefault));

            document.add(new Paragraph("Hello World!"));
            document.add(new Paragraph("iText is:").setFont(timesNewRoman));

            List list = new List()
                    .setSymbolIndent(12)
                    .setListSymbol("\u2022")
                    .setFont(timesNewRoman);
            list.add(new ListItem("Never gonna give you up"))
                    .add(new ListItem("Never gonna let you down"))
                    .add(new ListItem("Never gonna run around and desert you"))
                    .add(new ListItem("Never gonna make you cry"))
                    .add(new ListItem("Never gonna say goodbye"))
                    .add(new ListItem("Never gonna tell a lie and hurt you"));
            document.add(list);

            java.util.List<PECompanyPerformanceIddDto> performanceIddDtoList = performanceIddService.findByFundId(fundId);
            Table table = new Table(new float[]{3, 2, 2, 2, 2, 1, 1});
            this.addHeader(table, helveticaBold);
            for (PECompanyPerformanceIddDto performanceIddDto : performanceIddDtoList) {
                this.addRow(table, performanceIddDto);
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

    public void addArticle(Document doc, String title, String author, Image img, String text) throws IOException {
        Paragraph p1 = new Paragraph(title)
                .setFont(timesNewRomanBold)
                .setFontSize(14);
        doc.add(p1);
        doc.add(img);
        Paragraph p2 = new Paragraph()
                .setFont(timesNewRoman)
                .setFontSize(7)
                .add(author);
        doc.add(p2);
        Paragraph p3 = new Paragraph()
                .setFont(timesNewRoman)
                .setFontSize(10)
                .add(text);
        doc.add(p3);
    }
}
