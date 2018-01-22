package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.dto.pe.PECashflowDto;
import kz.nicnbk.service.dto.pe.PEGrossCashflowDto;

import java.util.List;

/**
 * Created by Pak on 13.10.2017.
 */
public interface PEIrrService {

    List<PECashflowDto> checkAndCleanCF(List<PEGrossCashflowDto> cashflowDtoList);

    Double getNPV(List<PECashflowDto> cashflowDtoList, double dailyRate);

    Double getIRR(List<PEGrossCashflowDto> cashflowDtoList);
}
