package kz.nicnbk.service.dto.reporting.privateequity;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.service.dto.reporting.PeriodicReportDto;

import java.util.List;

public class TarragonStatementBalanceOperationsHolderDto implements BaseDto {
    private List<TarragonStatementBalanceOperationsTrancheDto> balanceRecords;
    private List<TarragonStatementBalanceOperationsTrancheDto> operationsRecords;
    private PeriodicReportDto report;

    public List<TarragonStatementBalanceOperationsTrancheDto> getBalanceRecords() {
        return balanceRecords;
    }

    public void setBalanceRecords(List<TarragonStatementBalanceOperationsTrancheDto> balanceRecords) {
        this.balanceRecords = balanceRecords;
    }

    public List<TarragonStatementBalanceOperationsTrancheDto> getOperationsRecords() {
        return operationsRecords;
    }

    public void setOperationsRecords(List<TarragonStatementBalanceOperationsTrancheDto> operationsRecords) {
        this.operationsRecords = operationsRecords;
    }

    public PeriodicReportDto getReport() {
        return report;
    }

    public void setReport(PeriodicReportDto report) {
        this.report = report;
    }

    public void merge(TarragonStatementBalanceOperationsHolderDto other){
        if(other.balanceRecords != null && !other.balanceRecords.isEmpty() ){
            this.balanceRecords = other.balanceRecords;
        }
        if(other.operationsRecords != null && !other.operationsRecords.isEmpty() ){
            this.operationsRecords = other.operationsRecords;
        }
    }
}
