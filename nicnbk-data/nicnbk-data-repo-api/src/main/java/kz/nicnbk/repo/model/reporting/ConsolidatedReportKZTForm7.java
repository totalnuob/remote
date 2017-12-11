package kz.nicnbk.repo.model.reporting;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_report_form_7")
public class ConsolidatedReportKZTForm7 extends CreateUpdateBaseEntity{

    private PeriodicReport report;

    private String accountNumber;
    private String name;
    private Integer lineNumber;

    private String entityName;
    private String otherName;
    private Date purchaseDate;

    private Double debtStartPeriod;
    private Double fairValueAdjustmentsStartPeriod;
    private Double totalStartPeriod;

    private Double debtTurnover;
    private Double fairValueAdjustmentsTurnoverPositive;
    private Double fairValueAdjustmentsTurnoverNegative;

    private Double debtEndPeriod;
    private Double fairValueAdjustmentsEndPeriod;
    private Double totalEndPeriod;


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

    @Column(name="other_name")
    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    @Column(name="purchase_date")
    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    @Column(name="debt_start_period")
    public Double getDebtStartPeriod() {
        return debtStartPeriod;
    }

    public void setDebtStartPeriod(Double debtStartPeriod) {
        this.debtStartPeriod = debtStartPeriod;
    }

    @Column(name="fair_value_adj_start_period")
    public Double getFairValueAdjustmentsStartPeriod() {
        return fairValueAdjustmentsStartPeriod;
    }

    public void setFairValueAdjustmentsStartPeriod(Double fairValueAdjustmentsStartPeriod) {
        this.fairValueAdjustmentsStartPeriod = fairValueAdjustmentsStartPeriod;
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

    @Column(name="fair_value_adj_turnover_pos")
    public Double getFairValueAdjustmentsTurnoverPositive() {
        return fairValueAdjustmentsTurnoverPositive;
    }

    public void setFairValueAdjustmentsTurnoverPositive(Double fairValueAdjustmentsTurnover) {
        this.fairValueAdjustmentsTurnoverPositive = fairValueAdjustmentsTurnover;
    }

    @Column(name="fair_value_adj_turnover_neg")
    public Double getFairValueAdjustmentsTurnoverNegative() {
        return fairValueAdjustmentsTurnoverNegative;
    }

    public void setFairValueAdjustmentsTurnoverNegative(Double fairValueAdjustmentsTurnoverNegative) {
        this.fairValueAdjustmentsTurnoverNegative = fairValueAdjustmentsTurnoverNegative;
    }

    @Column(name="debt_end_period")
    public Double getDebtEndPeriod() {
        return debtEndPeriod;
    }

    public void setDebtEndPeriod(Double debtEndPeriod) {
        this.debtEndPeriod = debtEndPeriod;
    }

    @Column(name="fair_value_adj_end_period")
    public Double getFairValueAdjustmentsEndPeriod() {
        return fairValueAdjustmentsEndPeriod;
    }

    public void setFairValueAdjustmentsEndPeriod(Double fairValueAdjustmentsEndPeriod) {
        this.fairValueAdjustmentsEndPeriod = fairValueAdjustmentsEndPeriod;
    }

    @Column(name="total_end_period")
    public Double getTotalEndPeriod() {
        return totalEndPeriod;
    }

    public void setTotalEndPeriod(Double totalEndPeriod) {
        this.totalEndPeriod = totalEndPeriod;
    }
}
