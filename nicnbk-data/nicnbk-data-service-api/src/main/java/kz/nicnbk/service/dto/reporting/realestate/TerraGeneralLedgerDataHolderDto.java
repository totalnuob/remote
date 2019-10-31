package kz.nicnbk.service.dto.reporting.realestate;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.service.dto.reporting.PeriodicReportDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 15.06.2018.
 */
public class TerraGeneralLedgerDataHolderDto implements BaseDto {

    List<TerraGeneralLedgerBalanceRecordDto> records;
    private PeriodicReportDto report;

    public TerraGeneralLedgerDataHolderDto(){
        this.records = new ArrayList<>();
    }

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
