package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.HistoryBaseEntityDto;
import kz.nicnbk.repo.model.hf.HFManager;

@Deprecated
/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundDto_ extends HistoryBaseEntityDto<HFManager> {

    private String name;
    private HFManagerDto manager;
    private String strategyDescription;
    private String size;
    private String status;
    private String strategy;
    private String geography;
    private String shareClassCurrency;
    private String inception;
    private String legalStructure;
    private String domicileCountry;
    private String minInvestment;
    private String investmentCurrency;
    private String subscriptionFrequency;
    private String managementFeeType;
    private String managementFee;
    private String performanceFeeType;
    private String performanceFee;
    private String performanceFeePayFrequency;
    private String redemptionFrequency;
    private String redemptionNotificationPeriod;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HFManagerDto getManager() {
        return manager;
    }

    public void setManager(HFManagerDto manager) {
        this.manager = manager;
    }

    public String getStrategyDescription() {
        return strategyDescription;
    }

    public void setStrategyDescription(String strategyDescription) {
        this.strategyDescription = strategyDescription;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getGeography() {
        return geography;
    }

    public void setGeography(String geography) {
        this.geography = geography;
    }

    public String getShareClassCurrency() {
        return shareClassCurrency;
    }

    public void setShareClassCurrency(String shareClassCurrency) {
        this.shareClassCurrency = shareClassCurrency;
    }

    public String getInception() {
        return inception;
    }

    public void setInception(String inception) {
        this.inception = inception;
    }

    public String getLegalStructure() {
        return legalStructure;
    }

    public void setLegalStructure(String legalStructure) {
        this.legalStructure = legalStructure;
    }

    public String getDomicileCountry() {
        return domicileCountry;
    }

    public void setDomicileCountry(String domicileCountry) {
        this.domicileCountry = domicileCountry;
    }

    public String getMinInvestment() {
        return minInvestment;
    }

    public void setMinInvestment(String minInvestment) {
        this.minInvestment = minInvestment;
    }

    public String getInvestmentCurrency() {
        return investmentCurrency;
    }

    public void setInvestmentCurrency(String investmentCurrency) {
        this.investmentCurrency = investmentCurrency;
    }

    public String getSubscriptionFrequency() {
        return subscriptionFrequency;
    }

    public void setSubscriptionFrequency(String subscriptionFrequency) {
        this.subscriptionFrequency = subscriptionFrequency;
    }

    public String getManagementFeeType() {
        return managementFeeType;
    }

    public void setManagementFeeType(String managementFeeType) {
        this.managementFeeType = managementFeeType;
    }

    public String getManagementFee() {
        return managementFee;
    }

    public void setManagementFee(String managementFee) {
        this.managementFee = managementFee;
    }

    public String getPerformanceFeeType() {
        return performanceFeeType;
    }

    public void setPerformanceFeeType(String performanceFeeType) {
        this.performanceFeeType = performanceFeeType;
    }

    public String getPerformanceFee() {
        return performanceFee;
    }

    public void setPerformanceFee(String performanceFee) {
        this.performanceFee = performanceFee;
    }

    public String getPerformanceFeePayFrequency() {
        return performanceFeePayFrequency;
    }

    public void setPerformanceFeePayFrequency(String performanceFeePayFrequency) {
        this.performanceFeePayFrequency = performanceFeePayFrequency;
    }

    public String getRedemptionFrequency() {
        return redemptionFrequency;
    }

    public void setRedemptionFrequency(String redemptionFrequency) {
        this.redemptionFrequency = redemptionFrequency;
    }

    public String getRedemptionNotificationPeriod() {
        return redemptionNotificationPeriod;
    }

    public void setRedemptionNotificationPeriod(String redemptionNotificationPeriod) {
        this.redemptionNotificationPeriod = redemptionNotificationPeriod;
    }
}
