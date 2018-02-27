package kz.nicnbk.service.impl.pe;

import com.itextpdf.io.image.ImageDataFactory;
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
import kz.nicnbk.service.dto.pe.PEFundDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * Created by Pak on 24/01/2018.
 */
@Service
public class PEPdfServiceImpl implements PEPdfService {

    private static final Logger logger = LoggerFactory.getLogger(PEFundServiceImpl.class);

    @Autowired
    private PEFundService fundService;

    @Override
    public void createOnePager(Long fundId) {
        try {
            //File locations
            String onePagerDest = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/OnePager.pdf";
            String gpLogoDest = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/img/SilverLakeLogo.png";
            String nicLogoDest = "nicnbk-data/nicnbk-data-service-impl/src/main/resources/img/NIClogo.png";

            Float offSet = 36f;
            Float logoMaxHeight = 24f;
            Float logoMaxWidth = 72f;

            //GP's and NIC's logos
            Image gpLogo = new Image(ImageDataFactory.create(gpLogoDest));
            Image nicLogo = new Image(ImageDataFactory.create(nicLogoDest));

            //Setting logo size
            if (gpLogo.getImageHeight() / gpLogo.getImageWidth() > logoMaxHeight / logoMaxWidth) {
                gpLogo.setHeight(logoMaxHeight);
            } else {
                gpLogo.setWidth(logoMaxWidth);
            }
            nicLogo.setHeight(logoMaxHeight);

            File file = new File(onePagerDest);
            file.getParentFile().mkdirs();

            //Initialize PDF document
            PdfDocument pdf = new PdfDocument(new PdfWriter(onePagerDest));
            PageSize ps = PageSize.A4;

            //Initialize document
            Document document = new Document(pdf, ps);

            PEFundDto fundDto = fundService.get(fundId);

            //Header
            Table tableHeader = new Table(new float[]{1, 1, 1});
            tableHeader.addCell(new Cell()
                    .setWidth(logoMaxWidth)
                    .add(new Paragraph().add(gpLogo))
                    .setTextAlignment(TextAlignment.LEFT));
            tableHeader.addCell(new Cell()
                    .setWidth(ps.getWidth() - offSet * 2 - logoMaxWidth * 2)
                    .add(new Paragraph(fundDto.getFundName()).setBold())
                    .setTextAlignment(TextAlignment.CENTER));
            tableHeader.addCell(new Cell()
                    .setWidth(logoMaxWidth)
                    .add(new Paragraph().add(nicLogo))
                    .setTextAlignment(TextAlignment.RIGHT));
            document.add(tableHeader);

            document.close();
        } catch (IOException ex) {
            logger.error("Error creating PE fund's One Pager: " + fundId, ex);
        }
    }
}