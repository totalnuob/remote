package kz.nicnbk.service.impl.pe;

import kz.nicnbk.service.api.pe.PEPdfService;
import org.springframework.stereotype.Service;

/**
 * Created by Pak on 24/01/2018.
 */
@Service
public class PEPdfServiceImpl implements PEPdfService {
    @Override
    public void createOnePager() {
        System.out.println("PDF");
    }
}
