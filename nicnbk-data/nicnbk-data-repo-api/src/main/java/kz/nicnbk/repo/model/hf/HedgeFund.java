package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.Strategy;

import javax.persistence.*;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name = "hf_hedge_fund")
public class HedgeFund extends CreateUpdateBaseEntity {
    private String name;
    private HFManager manager;
    private String summary;
    private String inception;
    private Double AUMAmount;
    private String AUMDigit;
    private Currency AUMCurrency;
    private Strategy strategy;

    private String leverage;

    private FundStatus status;
    private String managementFee;
    private String performanceFee;
    private String redemptionFee;
    private String minInitialInvestment;
    private String minSubsInvestment;
    private SubscriptionFrequency subscriptionFrequency;
    private RedemptionFrequency redemptionFrequency;
    private RedemptionNotificationPeriod redemptionNoticePeriod;
    private String sidePocket;
    private String gates;

    private String liquidityPercent;
    private String liquidityPeriod;

    private String concentrationTop5;
    private String concentrationTop10;
    private String concentrationTop20;

    //private Set<Substrategy> strategyBreakdownList;
    //private investorBaseList;
    //managerList;
    //returns;

    // ALbourne ratings
    private String ALBIDDAnalystAssessment;
    private String ALBConviction;
    private String ALBExpectedAlpha;
    private String ALBExpectedBeta;
    private String ALBExpectedRisk;
    private String ALBStrategyInvestmentProcess;
    private String ALBManagementTeam;
    private String ALBRiskProcess;


    @Column (name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manager_id")
    public HFManager getManager() {
        return manager;
    }

    public void setManager(HFManager manager) {
        this.manager = manager;
    }

    @Column (name = "summary", columnDefinition = "TEXT")
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Column (name = "inception")
    public String getInception() {
        return inception;
    }

    public void setInception(String inception) {
        this.inception = inception;
    }

    @Column (name = "aum_amount")
    public Double getAUMAmount() {
        return AUMAmount;
    }

    public void setAUMAmount(Double AUMAmount) {
        this.AUMAmount = AUMAmount;
    }

    @Column (name = "aum_digit")
    public String getAUMDigit() {
        return AUMDigit;
    }

    public void setAUMDigit(String AUMDigit) {
        this.AUMDigit = AUMDigit;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aum_currency_id")
    public Currency getAUMCurrency() {
        return AUMCurrency;
    }

    public void setAUMCurrency(Currency AUMCurrency) {
        this.AUMCurrency = AUMCurrency;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id")
    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    @Column (name = "leverage")
    public String getLeverage() {
        return leverage;
    }

    public void setLeverage(String leverage) {
        this.leverage = leverage;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    public FundStatus getStatus() {
        return status;
    }

    public void setStatus(FundStatus status) {
        this.status = status;
    }

    @Column (name = "mng_fee")
    public String getManagementFee() {
        return managementFee;
    }

    public void setManagementFee(String managementFee) {
        this.managementFee = managementFee;
    }

    @Column (name = "perform_fee")
    public String getPerformanceFee() {
        return performanceFee;
    }

    public void setPerformanceFee(String performanceFee) {
        this.performanceFee = performanceFee;
    }

    @Column (name = "redemption_fee")
    public String getRedemptionFee() {
        return redemptionFee;
    }

    public void setRedemptionFee(String redemptionFee) {
        this.redemptionFee = redemptionFee;
    }

    @Column (name = "min_init_investment")
    public String getMinInitialInvestment() {
        return minInitialInvestment;
    }

    public void setMinInitialInvestment(String minInitialInvestment) {
        this.minInitialInvestment = minInitialInvestment;
    }

    @Column (name = "min_subs_investment")
    public String getMinSubsInvestment() {
        return minSubsInvestment;
    }

    public void setMinSubsInvestment(String minSubsInvestment) {
        this.minSubsInvestment = minSubsInvestment;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_freq_id")
    public SubscriptionFrequency getSubscriptionFrequency() {
        return subscriptionFrequency;
    }

    public void setSubscriptionFrequency(SubscriptionFrequency subscriptionFrequency) {
        this.subscriptionFrequency = subscriptionFrequency;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redemption_freq_id")
    public RedemptionFrequency getRedemptionFrequency() {
        return redemptionFrequency;
    }

    public void setRedemptionFrequency(RedemptionFrequency redemptionFrequency) {
        this.redemptionFrequency = redemptionFrequency;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redemption_notice_id")
    public RedemptionNotificationPeriod getRedemptionNoticePeriod() {
        return redemptionNoticePeriod;
    }

    public void setRedemptionNoticePeriod(RedemptionNotificationPeriod redemptionNoticePeriod) {
        this.redemptionNoticePeriod = redemptionNoticePeriod;
    }

    @Column (name = "sidepocket")
    public String getSidePocket() {
        return sidePocket;
    }

    public void setSidePocket(String sidePocket) {
        this.sidePocket = sidePocket;
    }

    @Column (name = "gates")
    public String getGates() {
        return gates;
    }

    public void setGates(String gates) {
        this.gates = gates;
    }

    @Column (name = "liquidity_percent")
    public String getLiquidityPercent() {
        return liquidityPercent;
    }

    public void setLiquidityPercent(String liquidityPercent) {
        this.liquidityPercent = liquidityPercent;
    }

    @Column (name = "liquidity_period")
    public String getLiquidityPeriod() {
        return liquidityPeriod;
    }

    public void setLiquidityPeriod(String liquidityPeriod) {
        this.liquidityPeriod = liquidityPeriod;
    }

    @Column (name = "concentration_top5")
    public String getConcentrationTop5() {
        return concentrationTop5;
    }

    public void setConcentrationTop5(String concentrationTop5) {
        this.concentrationTop5 = concentrationTop5;
    }

    @Column (name = "concentration_top10")
    public String getConcentrationTop10() {
        return concentrationTop10;
    }

    public void setConcentrationTop10(String concentrationTop10) {
        this.concentrationTop10 = concentrationTop10;
    }

    @Column (name = "concentration_top20")
    public String getConcentrationTop20() {
        return concentrationTop20;
    }

    public void setConcentrationTop20(String concentrationTop20) {
        this.concentrationTop20 = concentrationTop20;
    }

    @Column (name = "alb_idd_aa")
    public String getALBIDDAnalystAssessment() {
        return ALBIDDAnalystAssessment;
    }

    public void setALBIDDAnalystAssessment(String ALBIDDAnalystAssessment) {
        this.ALBIDDAnalystAssessment = ALBIDDAnalystAssessment;
    }

    @Column (name = "alb_conviction")
    public String getALBConviction() {
        return ALBConviction;
    }

    public void setALBConviction(String ALBConviction) {
        this.ALBConviction = ALBConviction;
    }

    @Column (name = "alb_exp_alpha")
    public String getALBExpectedAlpha() {
        return ALBExpectedAlpha;
    }

    public void setALBExpectedAlpha(String ALBExpectedAlpha) {
        this.ALBExpectedAlpha = ALBExpectedAlpha;
    }

    @Column (name = "alb_exp_beta")
    public String getALBExpectedBeta() {
        return ALBExpectedBeta;
    }

    public void setALBExpectedBeta(String ALBExpectedBeta) {
        this.ALBExpectedBeta = ALBExpectedBeta;
    }

    @Column (name = "alb_exp_risk")
    public String getALBExpectedRisk() {
        return ALBExpectedRisk;
    }

    public void setALBExpectedRisk(String ALBExpectedRisk) {
        this.ALBExpectedRisk = ALBExpectedRisk;
    }

    @Column (name = "alb_strategy_invest")
    public String getALBStrategyInvestmentProcess() {
        return ALBStrategyInvestmentProcess;
    }

    public void setALBStrategyInvestmentProcess(String ALBStrategyInvestmentProcess) {
        this.ALBStrategyInvestmentProcess = ALBStrategyInvestmentProcess;
    }

    @Column (name = "alb_mng_team")
    public String getALBManagementTeam() {
        return ALBManagementTeam;
    }

    public void setALBManagementTeam(String ALBManagementTeam) {
        this.ALBManagementTeam = ALBManagementTeam;
    }

    @Column (name = "alb_risk_process")
    public String getALBRiskProcess() {
        return ALBRiskProcess;
    }

    public void setALBRiskProcess(String ALBRiskProcess) {
        this.ALBRiskProcess = ALBRiskProcess;
    }
}
