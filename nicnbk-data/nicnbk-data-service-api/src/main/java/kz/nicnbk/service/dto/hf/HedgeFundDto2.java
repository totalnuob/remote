package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.HistoryBaseEntityDto;
import kz.nicnbk.repo.model.hf.HedgeFund;

import java.util.Set;

/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundDto2 extends HistoryBaseEntityDto<HedgeFund> {

    private String name;
    private HFFirmDto manager;
    private String summary;
    private String inception;
    private Double AUMAmount;
    private String AUMDigit;
    private String AUMCurrency;
    private String strategy;

    private String leverage;

    private String status;
    private String managementFee;
    private String performanceFee;
    private String redemptionFee;
    private String minInitialInvestment;
    private String minSubsInvestment;
    private String subscriptionFrequency;
    private String redemptionFrequency;
    private String redemptionNoticePeriod;
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

    private Set<ManagerDto> managers;
    private Set<RatingDto> albourneRatings;
    private Set<ReturnDto> returns;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HFFirmDto getManager() {
        return manager;
    }

    public void setManager(HFFirmDto manager) {
        this.manager = manager;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getInception() {
        return inception;
    }

    public void setInception(String inception) {
        this.inception = inception;
    }

    public Double getAUMAmount() {
        return AUMAmount;
    }

    public void setAUMAmount(Double AUMAmount) {
        this.AUMAmount = AUMAmount;
    }

    public String getAUMDigit() {
        return AUMDigit;
    }

    public void setAUMDigit(String AUMDigit) {
        this.AUMDigit = AUMDigit;
    }

    public String getAUMCurrency() {
        return AUMCurrency;
    }

    public void setAUMCurrency(String AUMCurrency) {
        this.AUMCurrency = AUMCurrency;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getLeverage() {
        return leverage;
    }

    public void setLeverage(String leverage) {
        this.leverage = leverage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getManagementFee() {
        return managementFee;
    }

    public void setManagementFee(String managementFee) {
        this.managementFee = managementFee;
    }

    public String getPerformanceFee() {
        return performanceFee;
    }

    public void setPerformanceFee(String performanceFee) {
        this.performanceFee = performanceFee;
    }

    public String getRedemptionFee() {
        return redemptionFee;
    }

    public void setRedemptionFee(String redemptionFee) {
        this.redemptionFee = redemptionFee;
    }

    public String getMinInitialInvestment() {
        return minInitialInvestment;
    }

    public void setMinInitialInvestment(String minInitialInvestment) {
        this.minInitialInvestment = minInitialInvestment;
    }

    public String getMinSubsInvestment() {
        return minSubsInvestment;
    }

    public void setMinSubsInvestment(String minSubsInvestment) {
        this.minSubsInvestment = minSubsInvestment;
    }

    public String getSubscriptionFrequency() {
        return subscriptionFrequency;
    }

    public void setSubscriptionFrequency(String subscriptionFrequency) {
        this.subscriptionFrequency = subscriptionFrequency;
    }

    public String getRedemptionFrequency() {
        return redemptionFrequency;
    }

    public void setRedemptionFrequency(String redemptionFrequency) {
        this.redemptionFrequency = redemptionFrequency;
    }

    public String getRedemptionNoticePeriod() {
        return redemptionNoticePeriod;
    }

    public void setRedemptionNoticePeriod(String redemptionNoticePeriod) {
        this.redemptionNoticePeriod = redemptionNoticePeriod;
    }

    public String getSidePocket() {
        return sidePocket;
    }

    public void setSidePocket(String sidePocket) {
        this.sidePocket = sidePocket;
    }

    public String getGates() {
        return gates;
    }

    public void setGates(String gates) {
        this.gates = gates;
    }

    public String getLiquidityPercent() {
        return liquidityPercent;
    }

    public void setLiquidityPercent(String liquidityPercent) {
        this.liquidityPercent = liquidityPercent;
    }

    public String getLiquidityPeriod() {
        return liquidityPeriod;
    }

    public void setLiquidityPeriod(String liquidityPeriod) {
        this.liquidityPeriod = liquidityPeriod;
    }

    public String getConcentrationTop5() {
        return concentrationTop5;
    }

    public void setConcentrationTop5(String concentrationTop5) {
        this.concentrationTop5 = concentrationTop5;
    }

    public String getConcentrationTop10() {
        return concentrationTop10;
    }

    public void setConcentrationTop10(String concentrationTop10) {
        this.concentrationTop10 = concentrationTop10;
    }

    public String getConcentrationTop20() {
        return concentrationTop20;
    }

    public void setConcentrationTop20(String concentrationTop20) {
        this.concentrationTop20 = concentrationTop20;
    }

    public String getALBIDDAnalystAssessment() {
        return ALBIDDAnalystAssessment;
    }

    public void setALBIDDAnalystAssessment(String ALBIDDAnalystAssessment) {
        this.ALBIDDAnalystAssessment = ALBIDDAnalystAssessment;
    }

    public String getALBConviction() {
        return ALBConviction;
    }

    public void setALBConviction(String ALBConviction) {
        this.ALBConviction = ALBConviction;
    }

    public String getALBExpectedAlpha() {
        return ALBExpectedAlpha;
    }

    public void setALBExpectedAlpha(String ALBExpectedAlpha) {
        this.ALBExpectedAlpha = ALBExpectedAlpha;
    }

    public String getALBExpectedBeta() {
        return ALBExpectedBeta;
    }

    public void setALBExpectedBeta(String ALBExpectedBeta) {
        this.ALBExpectedBeta = ALBExpectedBeta;
    }

    public String getALBExpectedRisk() {
        return ALBExpectedRisk;
    }

    public void setALBExpectedRisk(String ALBExpectedRisk) {
        this.ALBExpectedRisk = ALBExpectedRisk;
    }

    public String getALBStrategyInvestmentProcess() {
        return ALBStrategyInvestmentProcess;
    }

    public void setALBStrategyInvestmentProcess(String ALBStrategyInvestmentProcess) {
        this.ALBStrategyInvestmentProcess = ALBStrategyInvestmentProcess;
    }

    public String getALBManagementTeam() {
        return ALBManagementTeam;
    }

    public void setALBManagementTeam(String ALBManagementTeam) {
        this.ALBManagementTeam = ALBManagementTeam;
    }

    public String getALBRiskProcess() {
        return ALBRiskProcess;
    }

    public void setALBRiskProcess(String ALBRiskProcess) {
        this.ALBRiskProcess = ALBRiskProcess;
    }

    public Set<ManagerDto> getManagers() {
        return managers;
    }

    public void setManagers(Set<ManagerDto> managers) {
        this.managers = managers;
    }

    public Set<RatingDto> getAlbourneRatings() {
        return albourneRatings;
    }

    public void setAlbourneRatings(Set<RatingDto> albourneRatings) {
        this.albourneRatings = albourneRatings;
    }

    public Set<ReturnDto> getReturns() {
        return returns;
    }

    public void setReturns(Set<ReturnDto> returns) {
        this.returns = returns;
    }
}
