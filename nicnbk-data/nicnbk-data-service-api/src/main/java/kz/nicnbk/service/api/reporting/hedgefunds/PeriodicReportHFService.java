package kz.nicnbk.service.api.reporting.hedgefunds;

import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.reporting.hedgefunds.ExcludeSingularityRecordDto;


/**
 * Created by magzumov on 17.01.2018.
 */
public interface PeriodicReportHFService {

    ListResponseDto getSingularGeneratedForm(Long reportId);

    boolean excludeIncludeSingularityRecord(ExcludeSingularityRecordDto excludeRecordDto, String username);

}
