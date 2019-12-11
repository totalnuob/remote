package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.List;

/**
 * Created by magzumov on 30.06.2017.
 */


// TODO: change to specific
public class ConsolidatedReportRecordHolderDto implements BaseDto {


    // TODO: refactor, separate

    // Schedule of Investments
    private List<ConsolidatedReportRecordDto> trancheA;
    private List<ConsolidatedReportRecordDto> trancheB;

    private List<ScheduleInvestmentsDto> scheduleInvestments;

    // Statement of Assets, Liabilities and Partners Capital (Statement of Operations)
    private List<ConsolidatedReportRecordDto> balanceTrancheA;
    private List<ConsolidatedReportRecordDto> balanceTrancheB;

    private List<ConsolidatedReportRecordDto> operationsTrancheA;
    private List<ConsolidatedReportRecordDto> operationsTrancheB;

    // Statement of Cashflows
    private List<ConsolidatedReportRecordDto> cashflows;

    // Statement of Changes
    private List<ConsolidatedReportRecordDto> changes;

    private List<SingularityGeneralLedgerBalanceRecordDto> generalLedgerBalanceList;
    private List<SingularityNOALRecordDto> noalTrancheAList;
    private List<SingularityNOALRecordDto> noalTrancheBList;

    private List<SingularityITDRecordDto> recordsITDTrancheA;
    private List<SingularityITDRecordDto> recordsITDTrancheB;

    private List<SingularityGeneralLedgerBalanceRecordDto> realEstateGeneralLedgerBalanceList;

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

    public List<ConsolidatedReportRecordDto> getChanges() {
        return changes;
    }

    public void setChanges(List<ConsolidatedReportRecordDto> changes) {
        this.changes = changes;
    }

    public List<SingularityGeneralLedgerBalanceRecordDto> getGeneralLedgerBalanceList() {
        return generalLedgerBalanceList;
    }

    public void setGeneralLedgerBalanceList(List<SingularityGeneralLedgerBalanceRecordDto> generalLedgerBalanceList) {
        this.generalLedgerBalanceList = generalLedgerBalanceList;
    }

    public List<SingularityNOALRecordDto> getNoalTrancheAList() {
        return noalTrancheAList;
    }

    public void setNoalTrancheAList(List<SingularityNOALRecordDto> noalTrancheAList) {
        this.noalTrancheAList = noalTrancheAList;
    }

    public List<SingularityNOALRecordDto> getNoalTrancheBList() {
        return noalTrancheBList;
    }

    public void setNoalTrancheBList(List<SingularityNOALRecordDto> noalTrancheBList) {
        this.noalTrancheBList = noalTrancheBList;
    }

    public PeriodicReportDto getReport() {
        return report;
    }

    public void setReport(PeriodicReportDto report) {
        this.report = report;
    }

    public List<SingularityGeneralLedgerBalanceRecordDto> getRealEstateGeneralLedgerBalanceList() {
        return realEstateGeneralLedgerBalanceList;
    }

    public void setRealEstateGeneralLedgerBalanceList(List<SingularityGeneralLedgerBalanceRecordDto> realEstateGeneralLedgerBalanceList) {
        this.realEstateGeneralLedgerBalanceList = realEstateGeneralLedgerBalanceList;
    }

    public List<ScheduleInvestmentsDto> getScheduleInvestments() {
        return scheduleInvestments;
    }

    public void setScheduleInvestments(List<ScheduleInvestmentsDto> scheduleInvestments) {
        this.scheduleInvestments = scheduleInvestments;
    }

    public List<SingularityITDRecordDto> getRecordsITDTrancheA() {
        return recordsITDTrancheA;
    }

    public void setRecordsITDTrancheA(List<SingularityITDRecordDto> recordsITDTrancheA) {
        this.recordsITDTrancheA = recordsITDTrancheA;
    }

    public List<SingularityITDRecordDto> getRecordsITDTrancheB() {
        return recordsITDTrancheB;
    }

    public void setRecordsITDTrancheB(List<SingularityITDRecordDto> recordsITDTrancheB) {
        this.recordsITDTrancheB = recordsITDTrancheB;
    }

    public void merge(ConsolidatedReportRecordHolderDto other){
        if(other.trancheA != null && !other.trancheA.isEmpty()){
            this.trancheA = other.trancheA;
        }
        if(other.trancheB != null && !other.trancheB.isEmpty()){
            this.trancheB = other.trancheB;
        }
        if(other.scheduleInvestments != null && !other.scheduleInvestments.isEmpty()){
            this.scheduleInvestments = other.scheduleInvestments;
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
        if(other.changes != null && !other.changes.isEmpty()){
            this.changes = other.changes;
        }
        if(other.generalLedgerBalanceList != null && !other.generalLedgerBalanceList.isEmpty()){
            this.generalLedgerBalanceList = other.generalLedgerBalanceList;
        }
        if(other.noalTrancheAList != null && !other.noalTrancheAList.isEmpty()){
            this.noalTrancheAList = other.noalTrancheAList;
        }
        if(other.noalTrancheBList != null && !other.noalTrancheBList.isEmpty()){
            this.noalTrancheBList = other.noalTrancheBList;
        }
        if(other.recordsITDTrancheA != null && !other.recordsITDTrancheA.isEmpty()){
            this.recordsITDTrancheA = other.recordsITDTrancheA;
        }
        if(other.recordsITDTrancheB != null && !other.recordsITDTrancheB.isEmpty()){
            this.recordsITDTrancheB = other.recordsITDTrancheB;
        }
        if(other.realEstateGeneralLedgerBalanceList != null && !other.realEstateGeneralLedgerBalanceList.isEmpty()){
            this.realEstateGeneralLedgerBalanceList = other.realEstateGeneralLedgerBalanceList;
        }
    }
}
