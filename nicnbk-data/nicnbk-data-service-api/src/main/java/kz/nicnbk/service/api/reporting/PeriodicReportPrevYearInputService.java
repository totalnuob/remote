package kz.nicnbk.service.api.reporting;

import kz.nicnbk.service.dto.common.EntityListSaveResponseDto;
import kz.nicnbk.service.dto.reporting.PreviousYearInputDataDto;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by magzumov on 17.01.2018.
 */
public interface PeriodicReportPrevYearInputService {

    List<PreviousYearInputDataDto> getPreviousYearInputData(Long reportId);

    List<PreviousYearInputDataDto> getPreviousYearInputDataFromPreviousMonth(Long reportId);

    EntityListSaveResponseDto savePreviousYearInputData(List<PreviousYearInputDataDto> records, Long reportId);
    
}
