package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.common.Country;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.Geography;
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
    private String strategyDescription;
    private String size;
    private FundStatus status;
    private Strategy strategy;
    private Geography geography;
    private Currency shareClassCurrency;
    private String inception;
    private LegalStructure legalStructure;
    private Country domicileCountry;
    private String minInvestment;
    private Currency investmentCurrency;
    private SubscriptionFrequency subscriptionFrequency;
    private ManagementFeeType managementFeeType;
    private String managementFee;
    private PerformanceFeeType performanceFeeType;
    private String performanceFee;
    private PerformanceFeePayFrequencyType performanceFeePayFrequency;
    private RedemptionFrequencyType redemptionFrequency;
    private RedemptionNotificationPeriodType redemptionNotificationPeriod;

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

    @Column (name = "strategy_desc")
    public String getStrategyDescription() {
        return strategyDescription;
    }

    public void setStrategyDescription(String strategyDescription) {
        this.strategyDescription = strategyDescription;
    }

    @Column (name = "size")
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    public FundStatus getStatus() {
        return status;
    }

    public void setStatus(FundStatus status) {
        this.status = status;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id")
    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "geography_id")
    public Geography getGeography() {
        return geography;
    }

    public void setGeography(Geography geography) {
        this.geography = geography;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_class_currency_id")
    public Currency getShareClassCurrency() {
        return shareClassCurrency;
    }

    public void setShareClassCurrency(Currency shareClassCurrency) {
        this.shareClassCurrency = shareClassCurrency;
    }

    @Column(name = "inception")
    public String getInception() {
        return inception;
    }

    public void setInception(String inception) {
        this.inception = inception;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "legal_structure_id")
    public LegalStructure getLegalStructure() {
        return legalStructure;
    }

    public void setLegalStructure(LegalStructure legalStructure) {
        this.legalStructure = legalStructure;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domicile_country_id")
    public Country getDomicileCountry() {
        return domicileCountry;
    }

    public void setDomicileCountry(Country domicileCountry) {
        this.domicileCountry = domicileCountry;
    }

    @Column(name = "min_investment")
    public String getMinInvestment() {
        return minInvestment;
    }

    public void setMinInvestment(String minInvestment) {
        this.minInvestment = minInvestment;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invest_currency_id")
    public Currency getInvestmentCurrency() {
        return investmentCurrency;
    }

    public void setInvestmentCurrency(Currency investmentCurrency) {
        this.investmentCurrency = investmentCurrency;
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
    @JoinColumn(name = "mng_fee_id")
    public ManagementFeeType getManagementFeeType() {
        return managementFeeType;
    }

    public void setManagementFeeType(ManagementFeeType managementFeeType) {
        this.managementFeeType = managementFeeType;
    }

    @Column(name = "mng_fee")
    public String getManagementFee() {
        return managementFee;
    }

    public void setManagementFee(String managementFee) {
        this.managementFee = managementFee;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perform_fee_id")
    public PerformanceFeeType getPerformanceFeeType() {
        return performanceFeeType;
    }

    public void setPerformanceFeeType(PerformanceFeeType performanceFeeType) {
        this.performanceFeeType = performanceFeeType;
    }

    @Column(name = "perform_fee")
    public String getPerformanceFee() {
        return performanceFee;
    }

    public void setPerformanceFee(String performanceFee) {
        this.performanceFee = performanceFee;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perform_fee_freq_id")
    public PerformanceFeePayFrequencyType getPerformanceFeePayFrequency() {
        return performanceFeePayFrequency;
    }

    public void setPerformanceFeePayFrequency(PerformanceFeePayFrequencyType performanceFeePayFrequency) {
        this.performanceFeePayFrequency = performanceFeePayFrequency;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redemption_freq_id")
    public RedemptionFrequencyType getRedemptionFrequency() {
        return redemptionFrequency;
    }

    public void setRedemptionFrequency(RedemptionFrequencyType redemptionFrequency) {
        this.redemptionFrequency = redemptionFrequency;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redemption_notif_period_id")
    public RedemptionNotificationPeriodType getRedemptionNotificationPeriod() {
        return redemptionNotificationPeriod;
    }

    public void setRedemptionNotificationPeriod(RedemptionNotificationPeriodType redemptionNotificationPeriod) {
        this.redemptionNotificationPeriod = redemptionNotificationPeriod;
    }
}
