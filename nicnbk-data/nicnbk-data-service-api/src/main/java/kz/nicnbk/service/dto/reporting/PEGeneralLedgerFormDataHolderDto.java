package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.service.dto.reporting.privateequity.PEGeneralLedgerFormDataDto;

import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public class PEGeneralLedgerFormDataHolderDto implements BaseDto{

    private List<PEGeneralLedgerFormDataDto> records;
    private PeriodicReportDto report;

    public List<PEGeneralLedgerFormDataDto> getRecords() {
        return records;
    }

    public void setRecords(List<PEGeneralLedgerFormDataDto> records) {
        this.records = records;
    }

    public PeriodicReportDto getReport() {
        return report;
    }

    public void setReport(PeriodicReportDto report) {
        this.report = report;
    }
}
