package kz.nicnbk.service.api.pe;

import kz.nicnbk.service.dto.pe.PECashFlowDto;
import kz.nicnbk.service.dto.pe.PEGrossCashflowDto;

import java.util.List;

/**
 * Created by Pak on 13.10.2017.
 */
public interface PEIrrService {

    List<PECashFlowDto> checkAndCleanCF(List<PEGrossCashflowDto> cashflowDtoList);

    Double getNPV(List<PEGrossCashflowDto> cashflowDtoList, double dailyRate);

    Double getIRR(List<PEGrossCashflowDto> cashflowDtoList);
}
