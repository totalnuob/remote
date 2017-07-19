package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.List;

/**
 * Created by magzumov on 30.06.2017.
 */
public class ConsolidatedReportRecordHolderDto implements BaseDto {

    // Schedule of Investments
    private List<ConsolidatedReportRecordDto> trancheA;
    private List<ConsolidatedReportRecordDto> trancheB;

    // Statement of Assets, Liabilities and Partners Capital (Statement of Operations)
    private List<ConsolidatedReportRecordDto> balanceTrancheA;
    private List<ConsolidatedReportRecordDto> balanceTrancheB;

    private List<ConsolidatedReportRecordDto> operationsTrancheA;
    private List<ConsolidatedReportRecordDto> operationsTrancheB;

    // Statement of Cashflows
    private List<ConsolidatedReportRecordDto> cashflows;

    private PeriodicReportDto report;

    public List<ConsolidatedReportRecordDto> getTrancheA() {
        return trancheA;
    }

    public void setTrancheA(List<ConsolidatedReportRecordDto> trancheA) {
        this.trancheA = trancheA;
    }

    public List<ConsolidatedReportRecordDto> getTrancheB() {
        return trancheB;
    }

    public void setTrancheB(List<ConsolidatedReportRecordDto> trancheB) {
        this.trancheB = trancheB;
    }

    public List<ConsolidatedReportRecordDto> getBalanceTrancheA() {
        return balanceTrancheA;
    }

    public void setBalanceTrancheA(List<ConsolidatedReportRecordDto> balanceTrancheA) {
        this.balanceTrancheA = balanceTrancheA;
    }

    public List<ConsolidatedReportRecordDto> getBalanceTrancheB() {
        return balanceTrancheB;
    }

    public void setBalanceTrancheB(List<ConsolidatedReportRecordDto> balanceTrancheB) {
        this.balanceTrancheB = balanceTrancheB;
    }

    public List<ConsolidatedReportRecordDto> getOperationsTrancheA() {
        return operationsTrancheA;
    }

    public void setOperationsTrancheA(List<ConsolidatedReportRecordDto> operationsTrancheA) {
        this.operationsTrancheA = operationsTrancheA;
    }

    public List<ConsolidatedReportRecordDto> getOperationsTrancheB() {
        return operationsTrancheB;
    }

    public void setOperationsTrancheB(List<ConsolidatedReportRecordDto> operationsTrancheB) {
        this.operationsTrancheB = operationsTrancheB;
    }

    public List<ConsolidatedReportRecordDto> getCashflows() {
        return cashflows;
    }

    public void setCashflows(List<ConsolidatedReportRecordDto> cashflows) {
        this.cashflows = cashflows;
    }

    public PeriodicReportDto getReport() {
        return report;
    }

    public void setReport(PeriodicReportDto report) {
        this.report = report;
    }

    public void merge(ConsolidatedReportRecordHolderDto other){
        if(other.trancheA != null && !other.trancheA.isEmpty()){
            this.trancheA = other.trancheA;
        }
        if(other.trancheB != null && !other.trancheB.isEmpty()){
            this.trancheB = other.trancheB;
        }
        if(other.balanceTrancheA != null && !other.balanceTrancheA.isEmpty()){
            this.balanceTrancheA = other.balanceTrancheA;
        }
        if(other.balanceTrancheB != null && !other.balanceTrancheB.isEmpty()){
            this.balanceTrancheB = other.balanceTrancheB;
        }
        if(other.operationsTrancheA != null && !other.operationsTrancheA.isEmpty()){
            this.operationsTrancheA = other.operationsTrancheA;
        }
        if(other.operationsTrancheB != null && !other.operationsTrancheB.isEmpty()){
            this.operationsTrancheB = other.operationsTrancheB;
        }
        if(other.cashflows != null && !other.cashflows.isEmpty()){
            this.cashflows = other.cashflows;
        }
    }
}
