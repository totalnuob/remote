package kz.nicnbk.service.dto.reporting.realestate;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.service.dto.reporting.PeriodicReportDto;

import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public class TerraGeneralLedgerFormDataHolderDto implements BaseDto{

    private List<TerraGeneralLedgerBalanceRecordDto> records;
    private PeriodicReportDto report;

    public List<TerraGeneralLedgerBalanceRecordDto> getRecords() {
        return records;
    }

    public void setRecords(List<TerraGeneralLedgerBalanceRecordDto> records) {
        this.records = records;
    }

    public PeriodicReportDto getReport() {
        return report;
    }

    public void setReport(PeriodicReportDto report) {
        this.report = report;
    }
}
