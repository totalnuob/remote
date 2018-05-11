package kz.nicnbk.service.api.reporting;

import kz.nicnbk.service.dto.common.EntityListSaveResponseDto;
import kz.nicnbk.service.dto.reporting.NICKMFReportingDataCalculatedValueRequestDto;
import kz.nicnbk.service.dto.reporting.NICKMFReportingDataHolderDto;

/**
 * Created by magzumov on 18.01.2018.
 */
public interface PeriodicReportNICKMFService {

    EntityListSaveResponseDto saveNICKMFReportingData(NICKMFReportingDataHolderDto dataHolderDto);

    NICKMFReportingDataHolderDto getNICKMFReportingData(Long reportId);

    NICKMFReportingDataHolderDto getNICKMFReportingDataFromPreviousMonth(Long reportId);

    //Double getNICKMFReportingDataCalculatedValue(NICKMFReportingDataCalculatedValueRequestDto requestDto);

}
