package kz.nicnbk.service.api.reporting;

import kz.nicnbk.service.dto.reporting.NICKMFReportingDataHolderDto;

/**
 * Created by magzumov on 18.01.2018.
 */
public interface PeriodicReportNICKMFService {

    boolean saveNICKMFReportingData(NICKMFReportingDataHolderDto dataHolderDto);

    NICKMFReportingDataHolderDto getNICKMFReportingData(Long reportId);

    NICKMFReportingDataHolderDto getNICKMFReportingDataFromPreviousMonth(Long reportId);



}
