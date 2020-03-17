package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.Date;
import java.util.List;

public class SingularityITDHistoricalRecordHolderDto implements BaseDto{

    private List<SingularityITDHistoricalRecordDto> records;
    private Date reportDate;

    public List<SingularityITDHistoricalRecordDto> getRecords() {
        return records;
    }

    public void setRecords(List<SingularityITDHistoricalRecordDto> records) {
        this.records = records;
    }

    public boolean isEmpty(){
        return this.records == null || this.records.isEmpty();
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }
}
