package kz.nicnbk.repo.model.risk;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "monitoring_risk_hf_allocation_quarter")
public class MonitoringRiskHFAllocationQuarter extends CreateUpdateBaseEntity {
    private MonitoringRiskHFMonthlyReport report;

    private MonitoringRiskHFPortfolioType portfolioType;
    private Date date;
    private String fund;
    private String strategy;
    private String subStrategy;
    private Double ror;
    private Double ctr;

    public MonitoringRiskHFAllocationQuarter(){}

    public MonitoringRiskHFAllocationQuarter(MonitoringRiskHFMonthlyReport report, MonitoringRiskHFPortfolioType portfolioType, Date date, String fund,
                                             String strategy, String subStrategy, Double ror, Double ctr){
        this.report = report;
        this.portfolioType = portfolioType;
        this.date = date;
        this.fund = fund;
        this.strategy = strategy;
        this.subStrategy = subStrategy;
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

    public String getFund() {
        return fund;
    }

    public void setFund(String fund) {
        this.fund = fund;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getSubStrategy() {
        return subStrategy;
    }

    public void setSubStrategy(String subStrategy) {
        this.subStrategy = subStrategy;
    }

    public Double getRor() {
        return ror;
    }

    public void setRor(Double ror) {
        this.ror = ror;
    }

    public Double getCtr() {
        return ctr;
    }

    public void setCtr(Double ctr) {
        this.ctr = ctr;
    }
}
