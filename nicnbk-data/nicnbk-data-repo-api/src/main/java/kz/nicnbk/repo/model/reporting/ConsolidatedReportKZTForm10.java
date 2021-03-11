package kz.nicnbk.repo.model.reporting;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;

import javax.persistence.*;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_report_form_10")
public class ConsolidatedReportKZTForm10 extends CreateUpdateBaseEntity{

    private PeriodicReport report;

    private String accountNumber;
    private String name;
    private Integer lineNumber;

    private Double startPeriodAssets;
    private Double turnoverPurchased; // positive values
    private Double turnoverOther;
    private Double endPeriodAssets;

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

    @Column(name="assets_start_period")
    public Double getStartPeriodAssets() {
        return startPeriodAssets;
    }

    public void setStartPeriodAssets(Double startPeriodAssets) {
        this.startPeriodAssets = startPeriodAssets;
    }

    @Column(name="turnover_other")
    public Double getTurnoverOther() {
        return turnoverOther;
    }

    public void setTurnoverOther(Double turnoverOther) {
        this.turnoverOther = turnoverOther;
    }

    @Column(name="assets_end_period")
    public Double getEndPeriodAssets() {
        return endPeriodAssets;
    }

    public void setEndPeriodAssets(Double endPeriodAssets) {
        this.endPeriodAssets = endPeriodAssets;
    }

    @Column(name="turnover_purchased")
    public Double getTurnoverPurchased() {
        return turnoverPurchased;
    }

    public void setTurnoverPurchased(Double turnoverPurchased) {
        this.turnoverPurchased = turnoverPurchased;
    }
}
