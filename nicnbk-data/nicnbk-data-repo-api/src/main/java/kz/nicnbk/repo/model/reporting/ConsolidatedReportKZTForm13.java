package kz.nicnbk.repo.model.reporting;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_report_form_13")
public class ConsolidatedReportKZTForm13 extends CreateUpdateBaseEntity{

    private PeriodicReport report;

    private String accountNumber;
    private String name;
    private Integer lineNumber;

    private String entityName;
    private Date startPeriod;
    private Date endPeriod;
    private String interestRate;
    private Integer interestPaymentCount;

    private Double debtStartPeriod;
    private Double interestStartPeriod;
    private Double totalStartPeriod;

    private Double debtTurnover;
    private Double interestTurnover;
    private Double totalTurnover;

    private Double debtEndPeriod;
    private Double interestEndPeriod;
    private Double totalEndPeriod;

    public ConsolidatedReportKZTForm13(){}

    public ConsolidatedReportKZTForm13(String name, int lineNumber){
        this.name = name;
        this.lineNumber = lineNumber;
    }


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

    @Column(name="entity_name")
    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @Column(name="start_period")
    public Date getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(Date startPeriod) {
        this.startPeriod = startPeriod;
    }

    @Column(name="end_period")
    public Date getEndPeriod() {
        return endPeriod;
    }

    public void setEndPeriod(Date endPeriod) {
        this.endPeriod = endPeriod;
    }

    @Column(name="interest_rate")
    public String getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(String interestRate) {
        this.interestRate = interestRate;
    }

    @Column(name="interest_payment_count")
    public Integer getInterestPaymentCount() {
        return interestPaymentCount;
    }

    public void setInterestPaymentCount(Integer interestPaymentCount) {
        this.interestPaymentCount = interestPaymentCount;
    }

    @Column(name="debt_start_period")
    public Double getDebtStartPeriod() {
        return debtStartPeriod;
    }

    public void setDebtStartPeriod(Double debtStartPeriod) {
        this.debtStartPeriod = debtStartPeriod;
    }

    @Column(name="interest_start_period")
    public Double getInterestStartPeriod() {
        return interestStartPeriod;
    }

    public void setInterestStartPeriod(Double interestStartPeriod) {
        this.interestStartPeriod = interestStartPeriod;
    }

    @Column(name="total_start_period")
    public Double getTotalStartPeriod() {
        return totalStartPeriod;
    }

    public void setTotalStartPeriod(Double totalStartPeriod) {
        this.totalStartPeriod = totalStartPeriod;
    }

    @Column(name="debt_turnover")
    public Double getDebtTurnover() {
        return debtTurnover;
    }

    public void setDebtTurnover(Double debtTurnover) {
        this.debtTurnover = debtTurnover;
    }

    @Column(name="interest_turnover")
    public Double getInterestTurnover() {
        return interestTurnover;
    }

    public void setInterestTurnover(Double interestTurnover) {
        this.interestTurnover = interestTurnover;
    }

    @Column(name="total_turnover")
    public Double getTotalTurnover() {
        return totalTurnover;
    }

    public void setTotalTurnover(Double totalTurnover) {
        this.totalTurnover = totalTurnover;
    }

    @Column(name="debt_end_period")
    public Double getDebtEndPeriod() {
        return debtEndPeriod;
    }

    public void setDebtEndPeriod(Double debtEndPeriod) {
        this.debtEndPeriod = debtEndPeriod;
    }

    @Column(name="interest_end_period")
    public Double getInterestEndPeriod() {
        return interestEndPeriod;
    }

    public void setInterestEndPeriod(Double interestEndPeriod) {
        this.interestEndPeriod = interestEndPeriod;
    }

    @Column(name="total_end_period")
    public Double getTotalEndPeriod() {
        return totalEndPeriod;
    }

    public void setTotalEndPeriod(Double totalEndPeriod) {
        this.totalEndPeriod = totalEndPeriod;
    }
}
