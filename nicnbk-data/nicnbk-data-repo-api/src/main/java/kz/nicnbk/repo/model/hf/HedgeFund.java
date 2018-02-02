package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.Strategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

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

    private Date inceptionDate;

    private String AUM;
    private String AUMDigit;
    private Currency aumCurrency;
    private Strategy strategy;

    private String leverage;

    private HedgeFundStatus status;
    private String managementFee;
    private String performanceFee;
    private String redemptionFee;
    private String minInitialInvestment;
    private String minSubsInvestment;
    private SubscriptionFrequency subscriptionFrequency;
    private RedemptionFrequency redemptionFrequency;
    private RedemptionNotificationPeriod redemptionNotificationPeriod;
    private HedgeFundSidePocket sidePocket;
    private String gates;

    private String liquidityPercent;
    private String liquidityPeriod;

    private String concentrationTop5;
    private String concentrationTop10;
    private String concentrationTop20;

    //private Set<Substrategy> strategyBreakdownList;
    //private investorBaseList;
    private Set<HFManagement> managers;
    //returns;

    // ALbourne ratings
    private String albourneIddAnalysisAssessment;
    private String albourneConviction;
    private String albourneExpectedAlpha;
    private String albourneExpectedBeta;
    private String alalbournebExpectedRisk;
    private String albourneStrategyInvestmentProcess;
    private String albourneManagementTeam;
    private String albourneRiskProcess;

    private Boolean GCMApproved;
    private Boolean NICApproved;
    private Double sum;

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


    @Column(name="inception_date")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    public Date getInceptionDate() {
        return inceptionDate;
    }

    public void setInceptionDate(Date inceptionDate) {
        this.inceptionDate = inceptionDate;
    }

    @Column (name = "aum_amount")
    public String getAUM() {
        return AUM;
    }

    public void setAUM(String AUM) {
        this.AUM = AUM;
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
    public Currency getAumCurrency() {
        return aumCurrency;
    }

    public void setAumCurrency(Currency aumCurrency) {
        this.aumCurrency = aumCurrency;
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
    public HedgeFundStatus getStatus() {
        return status;
    }

    public void setStatus(HedgeFundStatus status) {
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
    public RedemptionNotificationPeriod getRedemptionNotificationPeriod() {
        return redemptionNotificationPeriod;
    }

    public void setRedemptionNotificationPeriod(RedemptionNotificationPeriod redemptionNotificationPeriod) {
        this.redemptionNotificationPeriod = redemptionNotificationPeriod;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sidepocket_id")
    public HedgeFundSidePocket getSidePocket() {
        return sidePocket;
    }

    public void setSidePocket(HedgeFundSidePocket sidePocket) {
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
    public String getAlbourneIddAnalysisAssessment() {
        return albourneIddAnalysisAssessment;
    }

    public void setAlbourneIddAnalysisAssessment(String albourneIddAnalysisAssessment) {
        this.albourneIddAnalysisAssessment = albourneIddAnalysisAssessment;
    }

    @Column (name = "alb_conviction")
    public String getAlbourneConviction() {
        return albourneConviction;
    }

    public void setAlbourneConviction(String albourneConviction) {
        this.albourneConviction = albourneConviction;
    }

    @Column (name = "alb_exp_alpha")
    public String getAlbourneExpectedAlpha() {
        return albourneExpectedAlpha;
    }

    public void setAlbourneExpectedAlpha(String albourneExpectedAlpha) {
        this.albourneExpectedAlpha = albourneExpectedAlpha;
    }

    @Column (name = "alb_exp_beta")
    public String getAlbourneExpectedBeta() {
        return albourneExpectedBeta;
    }

    public void setAlbourneExpectedBeta(String albourneExpectedBeta) {
        this.albourneExpectedBeta = albourneExpectedBeta;
    }

    @Column (name = "alb_exp_risk")
    public String getAlalbournebExpectedRisk() {
        return alalbournebExpectedRisk;
    }

    public void setAlalbournebExpectedRisk(String alalbournebExpectedRisk) {
        this.alalbournebExpectedRisk = alalbournebExpectedRisk;
    }

    @Column (name = "alb_strategy_invest")
    public String getAlbourneStrategyInvestmentProcess() {
        return albourneStrategyInvestmentProcess;
    }

    public void setAlbourneStrategyInvestmentProcess(String albourneStrategyInvestmentProcess) {
        this.albourneStrategyInvestmentProcess = albourneStrategyInvestmentProcess;
    }

    @Column (name = "alb_mng_team")
    public String getAlbourneManagementTeam() {
        return albourneManagementTeam;
    }

    public void setAlbourneManagementTeam(String albourneManagementTeam) {
        this.albourneManagementTeam = albourneManagementTeam;
    }

    @Column (name = "alb_risk_process")
    public String getAlbourneRiskProcess() {
        return albourneRiskProcess;
    }

    public void setAlbourneRiskProcess(String albourneRiskProcess) {
        this.albourneRiskProcess = albourneRiskProcess;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinTable(
            name="hf_fund_management",
            joinColumns=
            @JoinColumn(name="fund_id", referencedColumnName="ID"),
            inverseJoinColumns=
            @JoinColumn(name="management_id", referencedColumnName="ID")
    )
    public Set<HFManagement> getManagers() {
        return managers;
    }

    public void setManagers(Set<HFManagement> managers) {
        this.managers = managers;
    }

    @Column (name = "gcm_approved")
    public Boolean getGCMApproved() {
        return GCMApproved;
    }

    public void setGCMApproved(Boolean GCMApproved) {
        this.GCMApproved = GCMApproved;
    }

    @Column (name = "nic_approved")
    public Boolean getNICApproved() {
        return NICApproved;
    }

    public void setNICApproved(Boolean NICApproved) {
        this.NICApproved = NICApproved;
    }
}
