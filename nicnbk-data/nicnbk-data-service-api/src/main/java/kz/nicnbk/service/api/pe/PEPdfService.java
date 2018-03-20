package kz.nicnbk.service.api.pe;

import java.io.InputStream;

/**
 * Created by Pak on 24/01/2018.
 */
public interface PEPdfService {

    InputStream createOnePager(Long fundId, String tmpFolder, String onePagerDest);
}
