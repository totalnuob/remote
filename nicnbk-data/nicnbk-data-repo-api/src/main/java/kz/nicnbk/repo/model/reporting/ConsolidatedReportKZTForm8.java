package kz.nicnbk.repo.model.reporting;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_report_form_8")
public class ConsolidatedReportKZTForm8 extends CreateUpdateBaseEntity{

    private PeriodicReport report;

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


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "report_id", nullable = false)
    public PeriodicReport getReport() {
        return report;
    }

    public void setReport(PeriodicReport report) {
        this.report = report;
    }

    @Column(name="account_number")
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Column(name="name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="line_number")
    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Column(name="debt_start_period")
    public Double getDebtStartPeriod() {
        return debtStartPeriod;
    }

    public void setDebtStartPeriod(Double debtStartPeriod) {
        this.debtStartPeriod = debtStartPeriod;
    }

    @Column(name="debt_end_period")
    public Double getDebtEndPeriod() {
        return debtEndPeriod;
    }

    public void setDebtEndPeriod(Double debtEndPeriod) {
        this.debtEndPeriod = debtEndPeriod;
    }

    @Column(name="debt_diff")
    public Double getDebtDifference() {
        return debtDifference;
    }

    public void setDebtDifference(Double debtDifference) {
        this.debtDifference = debtDifference;
    }

    @Column(name="agreement_desc")
    public String getAgreementDescription() {
        return agreementDescription;
    }

    public void setAgreementDescription(String agreementDescription) {
        this.agreementDescription = agreementDescription;
    }

    @Column(name="debt_start_date")
    public Date getDebtStartDate() {
        return debtStartDate;
    }

    public void setDebtStartDate(Date debtStartDate) {
        this.debtStartDate = debtStartDate;
    }

    @Column(name="balance_start_period")
    public Double getStartPeriodBalance() {
        return startPeriodBalance;
    }

    public void setStartPeriodBalance(Double startPeriodBalance) {
        this.startPeriodBalance = startPeriodBalance;
    }

    @Column(name="balance_end_period")
    public Double getEndPeriodBalance() {
        return endPeriodBalance;
    }

    public void setEndPeriodBalance(Double endPeriodBalance) {
        this.endPeriodBalance = endPeriodBalance;
    }
}
