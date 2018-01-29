package kz.nicnbk.service.impl.pe;

import kz.nicnbk.service.api.pe.PEPdfService;
import org.springframework.stereotype.Service;

//import com.itextpdf.text.Anchor;
//import com.itextpdf.text.BadElementException;
//import com.itextpdf.text.BaseColor;
//import com.itextpdf.text.Chapter;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Element;
//import com.itextpdf.text.Font;
//import com.itextpdf.text.List;
//import com.itextpdf.text.ListItem;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.Phrase;
//import com.itextpdf.text.Section;
//import com.itextpdf.text.pdf.PdfPCell;
//import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfWriter;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Pak on 24/01/2018.
 */
@Service
public class PEPdfServiceImpl implements PEPdfService {
    @Override
    public void createOnePager() {
        System.out.println("PDF");

        String DEST = "D:/temp/FirstPdf.pdf";

        File file = new File(DEST);
        file.getParentFile().mkdirs();

        try {
            PdfWriter writer = new PdfWriter(DEST);

            //Initialize PDF document
            PdfDocument pdf = new PdfDocument(writer);

            // Initialize document
            Document document = new Document(pdf);

            //Add paragraph to the document
            document.add(new Paragraph("Hello World!"));

            //Close document
            document.close();
        }catch (FileNotFoundException ex){
            ex.printStackTrace();
        }
    }
}
