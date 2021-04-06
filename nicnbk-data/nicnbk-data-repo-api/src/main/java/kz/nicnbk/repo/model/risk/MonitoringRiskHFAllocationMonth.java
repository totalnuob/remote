package kz.nicnbk.repo.model.risk;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "monitoring_risk_hf_allocation_month")
public class MonitoringRiskHFAllocationMonth extends CreateUpdateBaseEntity {
    private MonitoringRiskHFMonthlyReport report;

    private MonitoringRiskHFPortfolioType portfolioType;
    private Date date;
    private String fund;
    private String strategy;
    private String subStrategy;
    private Double endingBalance;
    private Double ror;
    private Double ctr;
    private Double gainLoss;
    private Double openingBalance;

    public MonitoringRiskHFAllocationMonth(){}

    public MonitoringRiskHFAllocationMonth(MonitoringRiskHFMonthlyReport report, MonitoringRiskHFPortfolioType portfolioType, Date date, String fund,
                                           String strategy, String subStrategy, Double openingBalance, Double gainLoss, Double endingBalance, Double ror, Double ctr){
        this.report = report;
        this.portfolioType = portfolioType;
        this.date = date;
        this.fund = fund;
        this.strategy = strategy;
        this.subStrategy = subStrategy;
        this.openingBalance = openingBalance;
        this.gainLoss = gainLoss;
        this.endingBalance = endingBalance;
        this.ror = ror;
        this.ctr = ctr;
    }


    @Column(name = "date", nullable = false)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_type_id", nullable = false)
    public MonitoringRiskHFPortfolioType getPortfolioType() {
        return portfolioType;
    }

    public void setPortfolioType(MonitoringRiskHFPortfolioType portfolioType) {
        this.portfolioType = portfolioType;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    public MonitoringRiskHFMonthlyReport getReport() {
        return report;
    }

    public void setReport(MonitoringRiskHFMonthlyReport report) {
        this.report = report;
    }

    @Column(name = "fund", nullable = false)
    public String getFund() {
        return fund;
    }

    public void setFund(String fund) {
        this.fund = fund;
    }

    @Column(name = "strategy", nullable = false)
    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    @Column(name = "sub_strategy", nullable = false)
    public String getSubStrategy() {
        return subStrategy;
    }

    public void setSubStrategy(String subStrategy) {
        this.subStrategy = subStrategy;
    }

    @Column(name = "ending_balance", nullable = false)
    public Double getEndingBalance() {
        return endingBalance;
    }

    public void setEndingBalance(Double endingBalance) {
        this.endingBalance = endingBalance;
    }

    @Column(name = "ror", nullable = false)
    public Double getRor() {
        return ror;
    }

    public void setRor(Double ror) {
        this.ror = ror;
    }

    @Column(name = "ctr", nullable = false)
    public Double getCtr() {
        return ctr;
    }

    public void setCtr(Double ctr) {
        this.ctr = ctr;
    }

    @Column(name = "gain_loss", nullable = false)
    public Double getGainLoss() {
        return gainLoss;
    }

    public void setGainLoss(Double gainLoss) {
        this.gainLoss = gainLoss;
    }

    @Column(name = "opening_balance", nullable = false)
    public Double getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(Double openingBalance) {
        this.openingBalance = openingBalance;
    }
}
