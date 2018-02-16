package kz.nicnbk.service.api.reporting.hedgefunds;

import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.reporting.GeneratedGeneralLedgerFormDto;
import kz.nicnbk.service.dto.reporting.PEGeneralLedgerFormDataHolderDto;

import java.util.List;

/**
 * Created by magzumov on 17.01.2018.
 */
public interface PeriodicReportHFService {

    ListResponseDto getSingularGeneratedForm(Long reportId);

}
