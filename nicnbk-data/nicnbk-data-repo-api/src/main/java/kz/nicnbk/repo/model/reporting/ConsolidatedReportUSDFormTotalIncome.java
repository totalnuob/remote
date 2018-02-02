package kz.nicnbk.repo.model.reporting;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;

import javax.persistence.*;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_report_usd_form_total_income")
public class ConsolidatedReportUSDFormTotalIncome extends CreateUpdateBaseEntity{

    private PeriodicReport report;

    private String name;
    private Integer lineNumber;
    private Integer subLineNumber;
    private Double currentAccountBalance;
    private Double previousAccountBalance;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "report_id", nullable = false)
    public PeriodicReport getReport() {
        return report;
    }

    public void setReport(PeriodicReport report) {
        this.report = report;
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

    @Column(name="subline_number")
    public Integer getSubLineNumber() {
        return subLineNumber;
    }

    public void setSubLineNumber(Integer subLineNumber) {
        this.subLineNumber = subLineNumber;
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
}
