package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.Date;

/**
 * Created by magzumov on 25.10.2017.
 */
public class ConsolidatedKZTForm8RecordDto implements BaseDto {
    private String accountNumber;
    private String name;
    private Integer lineNumber;

    // Debt
    private Double debtStartPeriod;
    private Double debtEndPeriod;
    private Double debtDifference;

    private String agreementDescription;
    private Date debtStartDate;

    private Double startPeriodBalance;
    private Double endPeriodBalance;

    public ConsolidatedKZTForm8RecordDto(){}

    public ConsolidatedKZTForm8RecordDto(String name, Integer lineNumber){
        this.name = name;
        this.lineNumber = lineNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Double getDebtStartPeriod() {
        return debtStartPeriod;
    }

    public void setDebtStartPeriod(Double debtStartPeriod) {
        this.debtStartPeriod = debtStartPeriod;
    }

    public Double getDebtEndPeriod() {
        return debtEndPeriod;
    }

    public void setDebtEndPeriod(Double debtEndPeriod) {
        this.debtEndPeriod = debtEndPeriod;
    }

    public Double getDebtDifference() {
        return debtDifference;
    }

    public void setDebtDifference(Double debtDifference) {
        this.debtDifference = debtDifference;
    }

    public String getAgreementDescription() {
        return agreementDescription;
    }

    public void setAgreementDescription(String agreementDescription) {
        this.agreementDescription = agreementDescription;
    }

    public Date getDebtStartDate() {
        return debtStartDate;
    }

    public void setDebtStartDate(Date debtStartDate) {
        this.debtStartDate = debtStartDate;
    }

    public Double getStartPeriodBalance() {
        return startPeriodBalance;
    }

    public void setStartPeriodBalance(Double startPeriodBalance) {
        this.startPeriodBalance = startPeriodBalance;
    }

    public Double getEndPeriodBalance() {
        return endPeriodBalance;
    }

    public void setEndPeriodBalance(Double endPeriodBalance) {
        this.endPeriodBalance = endPeriodBalance;
    }

    public boolean isEmpty(){
        return (this.debtStartPeriod == null || this.debtStartPeriod.doubleValue() == 0.0) &&
                (this.debtEndPeriod == null || this.debtEndPeriod.doubleValue() == 0.0) &&
                (this.debtDifference == null || this.debtDifference.doubleValue() == 0.0) &&
                (this.startPeriodBalance == null || this.startPeriodBalance.doubleValue() == 0.0) &&
                (this.endPeriodBalance == null || this.endPeriodBalance.doubleValue() == 0.0);
    }
}
