package kz.nicnbk.repo.model.reporting;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;

import javax.persistence.*;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_report_form_22")
public class ConsolidatedReportKZTForm22 extends CreateUpdateBaseEntity{

    private PeriodicReport report;

    private String accountNumber;
    private String name;
    private Integer lineNumber;

    private Double currentAccountBalance;
    private Double previousAccountBalance;

    private Double turnover;


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

    @Column(name="current_account_balance")
    public Double getCurrentAccountBalance() {
        return currentAccountBalance;
    }

    public void setCurrentAccountBalance(Double currentAccountBalance) {
        this.currentAccountBalance = currentAccountBalance;
    }

    @Column(name="prev_account_balance")
    public Double getPreviousAccountBalance() {
        return previousAccountBalance;
    }

    public void setPreviousAccountBalance(Double previousAccountBalance) {
        this.previousAccountBalance = previousAccountBalance;
    }

    @Column(name="turnover")
    public Double getTurnover() {
        return turnover;
    }

    public void setTurnover(Double turnover) {
        this.turnover = turnover;
    }
}
