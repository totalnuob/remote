package kz.nicnbk.repo.model.reporting.privateequity;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.base.DataConstraints;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.reporting.PeriodicReport;

import javax.persistence.*;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_pe_schedule_invest")
public class ReportingPEScheduleInvestment extends CreateUpdateBaseEntity{

    // TODO: refactor as enum lookup
    public static final String TYPE_FUND_INVESTMENTS = "Fund Investment";
    public static final String TYPE_COINVESTMENTS = "Co-Investment";
    public static final String TYPE_SECONDARY = "Secondary";

    private String name;
    private Double capitalCommitments;
    private Integer tranche;
    private String description;
    private Boolean isTotalSum;

    private PeriodicReport report;
    private Double netCost;
    private Double fairValue;
    private Double editedFairValue;
    private PEInvestmentType type;
    private Strategy strategy;
    private Currency currency;

    private Boolean excludeFromTarragonCalculation;

    // updated input data for Schedule of Investments
    private String securityNo;
    private String investment;
    private PETrancheType trancheType;
    private Double exchangeRateRatioUSD;
    private Double investmentCommitment;
    private Double unfundedCommitment;
    private Double investmentCommitmentUSD;
    private Double unfundedCommitmentUSD;
    private Double contributionsUSD;
    private Double returnOfCapitalDistributionsUSD;
    private Double unrealizedGainLossUSD;
    private Double realizedGainLossUSD;
    private String operatingCompany;
    private String ownershipDetails;

    @Column(name = "name", length = DataConstraints.C_TYPE_ENTITY_NAME)
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

    @Column(name = "tranche")
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

    @Column(name="is_total_sum")
    public Boolean getTotalSum() {
        return isTotalSum;
    }

    public void setTotalSum(Boolean totalSum) {
        isTotalSum = totalSum;
    }

    @Column(name="edited_fair_value")
    public Double getEditedFairValue() {
        return editedFairValue;
    }

    public void setEditedFairValue(Double editedFairValue) {
        this.editedFairValue = editedFairValue;
    }

    @Column(name="exclude_from_tarragon_calc")
    public Boolean getExcludeFromTarragonCalculation() {
        return excludeFromTarragonCalculation;
    }

    public void setExcludeFromTarragonCalculation(Boolean excludeFromTarragonCalculation) {
        this.excludeFromTarragonCalculation = excludeFromTarragonCalculation;
    }

    @Column(name="security_no")
    public String getSecurityNo() {
        return securityNo;
    }

    public void setSecurityNo(String securityNo) {
        this.securityNo = securityNo;
    }

    @Column(name="investment")
    public String getInvestment() {
        return investment;
    }

    public void setInvestment(String investment) {
        this.investment = investment;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pe_tranche_type_id"/*, nullable = false*/)
    public PETrancheType getTrancheType() {
        return trancheType;
    }

    public void setTrancheType(PETrancheType trancheType) {
        this.trancheType = trancheType;
    }

    @Column(name="exchange_rate_ratio_usd")
    public Double getExchangeRateRatioUSD() {
        return exchangeRateRatioUSD;
    }

    public void setExchangeRateRatioUSD(Double exchangeRateRatioUSD) {
        this.exchangeRateRatioUSD = exchangeRateRatioUSD;
    }

    @Column(name="investment_commitment")
    public Double getInvestmentCommitment() {
        return investmentCommitment;
    }

    public void setInvestmentCommitment(Double investmentCommitment) {
        this.investmentCommitment = investmentCommitment;
    }

    @Column(name="unfunded_commitment")
    public Double getUnfundedCommitment() {
        return unfundedCommitment;
    }

    public void setUnfundedCommitment(Double unfundedCommitment) {
        this.unfundedCommitment = unfundedCommitment;
    }

    @Column(name="investment_commitment_usd")
    public Double getInvestmentCommitmentUSD() {
        return investmentCommitmentUSD;
    }

    public void setInvestmentCommitmentUSD(Double investmentCommitmentUSD) {
        this.investmentCommitmentUSD = investmentCommitmentUSD;
    }

    @Column(name="unfunded_commitment_usd")
    public Double getUnfundedCommitmentUSD() {
        return unfundedCommitmentUSD;
    }

    public void setUnfundedCommitmentUSD(Double unfundedCommitmentUSD) {
        this.unfundedCommitmentUSD = unfundedCommitmentUSD;
    }

    @Column(name="contributions_usd")
    public Double getContributionsUSD() {
        return contributionsUSD;
    }

    public void setContributionsUSD(Double contributionsUSD) {
        this.contributionsUSD = contributionsUSD;
    }

    @Column(name="return_of_capital_distr_usd")
    public Double getReturnOfCapitalDistributionsUSD() {
        return returnOfCapitalDistributionsUSD;
    }

    public void setReturnOfCapitalDistributionsUSD(Double returnOfCapitalDistributionsUSD) {
        this.returnOfCapitalDistributionsUSD = returnOfCapitalDistributionsUSD;
    }

    @Column(name="unrealized_gain_loss_usd")
    public Double getUnrealizedGainLossUSD() {
        return unrealizedGainLossUSD;
    }

    public void setUnrealizedGainLossUSD(Double unrealizedGainLossUSD) {
        this.unrealizedGainLossUSD = unrealizedGainLossUSD;
    }

    @Column(name="realized_gain_loss_usd")
    public Double getRealizedGainLossUSD() {
        return realizedGainLossUSD;
    }

    public void setRealizedGainLossUSD(Double realizedGainLossUSD) {
        this.realizedGainLossUSD = realizedGainLossUSD;
    }

    @Column(name="operating_company")
    public String getOperatingCompany() {
        return operatingCompany;
    }

    public void setOperatingCompany(String operatingCompany) {
        this.operatingCompany = operatingCompany;
    }

    @Column(name="ownership_details")
    public String getOwnershipDetails() {
        return ownershipDetails;
    }

    public void setOwnershipDetails(String ownershipDetails) {
        this.ownershipDetails = ownershipDetails;
    }
}
