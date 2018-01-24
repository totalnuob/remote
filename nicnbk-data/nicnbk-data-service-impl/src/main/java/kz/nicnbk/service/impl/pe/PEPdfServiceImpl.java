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

/**
 * Created by Pak on 24/01/2018.
 */
@Service
public class PEPdfServiceImpl implements PEPdfService {
    @Override
    public void createOnePager() {
        System.out.println("PDF");

        String FILE = "D:/temp/FirstPdf.pdf";
//        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    }
}
