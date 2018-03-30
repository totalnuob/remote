package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.dto.pe.PEFirmDto;
import kz.nicnbk.service.dto.pe.PEFundDto;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Pak on 24/01/2018.
 */
public interface PEPdfService {

    InputStream createOnePager(Long fundId, String tmpFolder, String onePagerDest);

    void createCharts(String firmName, List<PEFundDto> fundDtoList, String benchmark, String barChartNetIrrDest, String barChartNetMoicDest, Float width)  throws Exception;
}
