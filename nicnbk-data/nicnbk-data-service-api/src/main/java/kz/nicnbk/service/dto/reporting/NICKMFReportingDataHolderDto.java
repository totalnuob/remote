package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.reporting.NICKMFReportingData;

import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public class NICKMFReportingDataHolderDto extends CreateUpdateBaseEntityDto<NICKMFReportingData> {

    private List<NICKMFReportingDataDto> records;
    private PeriodicReportDto report;


    public List<NICKMFReportingDataDto> getRecords() {
        return records;
    }

    public void setRecords(List<NICKMFReportingDataDto> records) {
        this.records = records;
    }

    public PeriodicReportDto getReport() {
        return report;
    }

    public void setReport(PeriodicReportDto report) {
        this.report = report;
    }
}
