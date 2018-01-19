package kz.nicnbk.service.api.reporting.privateequity;

import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.reporting.GeneratedGeneralLedgerFormDto;
import kz.nicnbk.service.dto.reporting.PEGeneralLedgerFormDataHolderDto;

import java.util.List;

/**
 * Created by magzumov on 17.01.2018.
 */
public interface PeriodicReportPEService {

    boolean savePEGeneralLedgerFormData(PEGeneralLedgerFormDataHolderDto dataHolderDto);

    boolean deletePEGeneralLedgerFormDataRecordById(Long recordId);

    List<GeneratedGeneralLedgerFormDto> getTarragonGLAddedRecordsPreviousMonth(Long reportId);

    ListResponseDto getTarragonGeneratedForm(Long reportId);
}
