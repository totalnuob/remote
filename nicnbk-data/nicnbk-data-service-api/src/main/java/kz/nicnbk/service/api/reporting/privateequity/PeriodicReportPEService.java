package kz.nicnbk.service.api.reporting.privateequity;

import kz.nicnbk.service.dto.common.EntityListSaveResponseDto;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.reporting.GeneratedGeneralLedgerFormDto;
import kz.nicnbk.service.dto.reporting.PEGeneralLedgerFormDataHolderDto;
import kz.nicnbk.service.dto.reporting.privateequity.ExcludeTarragonRecordDto;
import kz.nicnbk.service.dto.reporting.privateequity.TarragonGeneratedGeneralLedgerFormDto;

import java.util.List;

/**
 * Created by magzumov on 17.01.2018.
 */
public interface PeriodicReportPEService {

    EntityListSaveResponseDto savePEGeneralLedgerFormData(PEGeneralLedgerFormDataHolderDto dataHolderDto);

    boolean deletePEGeneralLedgerFormDataRecordById(Long recordId);

    List<TarragonGeneratedGeneralLedgerFormDto> getTarragonGLAddedRecordsPreviousMonth(Long reportId);

    ListResponseDto getTarragonGeneratedFormWithExcluded(Long reportId);

    ListResponseDto getTarragonGeneratedFormWithoutExcluded(Long reportId);

    boolean excludeIncludeTarragonRecord(ExcludeTarragonRecordDto recordDto, String username);
}
