package kz.nicnbk.repo.model.reporting.privateequity;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.base.DataConstraints;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.PeriodicReportType;
import kz.nicnbk.repo.model.reporting.ReportStatus;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_pe_schedule_invest")
public class ReportingPEScheduleInvestment extends CreateUpdateBaseEntity{

    // TODO: refactor as enum lookup
    public static final String TYPE_FUND_INVESTMENTS = "Fund Investments";
    public static final String TYPE_COINVESTMENTS = "Co-Investments";

    private String name;
    private Double capitalCommitments;
    private Double netCost;
    private Double fairValue;
    private PeriodicReport report;

    private PEInvestmentType type;
    private Strategy strategy;
    private Currency currency;
    private Integer tranche;
    private String description;

    @Column(name = "name", length = DataConstraints.C_TYPE_ENTITY_NAME, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "capital_commitments")
    public Double getCapitalCommitments() {
        return capitalCommitments;
    }

    public void setCapitalCommitments(Double capitalCommitments) {
        this.capitalCommitments = capitalCommitments;
    }

    @Column(name = "net_cost")
    public Double getNetCost() {
        return netCost;
    }

    public void setNetCost(Double netCost) {
        this.netCost = netCost;
    }

    @Column(name = "fair_value")
    public Double getFairValue() {
        return fairValue;
    }

    public void setFairValue(Double fairValue) {
        this.fairValue = fairValue;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "report_id", nullable = false)
    public PeriodicReport getReport() {
        return report;
    }

    public void setReport(PeriodicReport report) {
        this.report = report;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "investment_type_id"/*, nullable = false*/)
    public PEInvestmentType getType() {
        return type;
    }

    public void setType(PEInvestmentType type) {
        this.type = type;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "strategy_id"/*, nullable = false*/)
    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency_id"/*, nullable = false*/)
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Column(name = "tranche", nullable = false)
    public Integer getTranche() {
        return tranche;
    }

    public void setTranche(Integer tranche) {
        this.tranche = tranche;
    }

    @Column(name = "description", length = DataConstraints.C_TYPE_ENTITY_DESCRIPTION_SHORT)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
