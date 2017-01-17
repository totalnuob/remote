package kz.nicnbk.service.dto.hf;

import com.fasterxml.jackson.annotation.JsonProperty;
import kz.nicnbk.common.service.model.HistoryBaseEntityDto;
import kz.nicnbk.repo.model.hf.HedgeFund;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundDto extends HistoryBaseEntityDto<HedgeFund> {

    private String name;
    private HFManagerDto manager;
    private String summary;
    private String inception;
    private Date inceptionDate;
    private Double aum;
    private String aumDigit;
    private String aumCurrency;
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
    private String redemptionNotificationPeriod;
    private String sidePocket;
    private String gates;

    private String liquidityPercent;
    private String liquidityPeriod;

    private String concentrationTop5;
    private String concentrationTop10;
    private String concentrationTop20;

    // Calculated values
    private Integer numMonths; // number of months since inception
    private Integer numPositiveMonths;
    private Integer numNegativeMonths;

    private Double returnSinceInception;
    private Double annualizedReturn;
    private Double YTD;
    private Double beta;
    private Double annualVolatility;
    private Double worstDrawdown;
    private String worstDrawdownPeriod;
    private Double sharpeRatio;
    private Double sortinoRatio;

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

    private Set<ManagerDto> managers;

    private List<SubstrategyBreakdownDto> strategyBreakdownList;
    private List<InvestorBaseDto> investorBaseList;
    private List<ReturnDto> returns;

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

    public Double getAum() {
        return aum;
    }

    public void setAum(Double aum) {
        this.aum = aum;
    }

    public String getAumDigit() {
        return aumDigit;
    }

    public void setAumDigit(String aumDigit) {
        this.aumDigit = aumDigit;
    }

    public String getAumCurrency() {
        return aumCurrency;
    }

    public void setAumCurrency(String aumCurrency) {
        this.aumCurrency = aumCurrency;
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

    public String getRedemptionNotificationPeriod() {
        return redemptionNotificationPeriod;
    }

    public void setRedemptionNotificationPeriod(String redemptionNotificationPeriod) {
        this.redemptionNotificationPeriod = redemptionNotificationPeriod;
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

    public String getAlbourneIddAnalysisAssessment() {
        return albourneIddAnalysisAssessment;
    }

    public void setAlbourneIddAnalysisAssessment(String albourneIddAnalysisAssessment) {
        this.albourneIddAnalysisAssessment = albourneIddAnalysisAssessment;
    }

    public String getAlbourneConviction() {
        return albourneConviction;
    }

    public void setAlbourneConviction(String albourneConviction) {
        this.albourneConviction = albourneConviction;
    }

    public String getAlbourneExpectedAlpha() {
        return albourneExpectedAlpha;
    }

    public void setAlbourneExpectedAlpha(String albourneExpectedAlpha) {
        this.albourneExpectedAlpha = albourneExpectedAlpha;
    }

    public String getAlbourneExpectedBeta() {
        return albourneExpectedBeta;
    }

    public void setAlbourneExpectedBeta(String albourneExpectedBeta) {
        this.albourneExpectedBeta = albourneExpectedBeta;
    }

    public String getAlalbournebExpectedRisk() {
        return alalbournebExpectedRisk;
    }

    public void setAlalbournebExpectedRisk(String alalbournebExpectedRisk) {
        this.alalbournebExpectedRisk = alalbournebExpectedRisk;
    }

    public String getAlbourneStrategyInvestmentProcess() {
        return albourneStrategyInvestmentProcess;
    }

    public void setAlbourneStrategyInvestmentProcess(String albourneStrategyInvestmentProcess) {
        this.albourneStrategyInvestmentProcess = albourneStrategyInvestmentProcess;
    }

    public String getAlbourneManagementTeam() {
        return albourneManagementTeam;
    }

    public void setAlbourneManagementTeam(String albourneManagementTeam) {
        this.albourneManagementTeam = albourneManagementTeam;
    }

    public String getAlbourneRiskProcess() {
        return albourneRiskProcess;
    }

    public void setAlbourneRiskProcess(String albourneRiskProcess) {
        this.albourneRiskProcess = albourneRiskProcess;
    }

    public Set<ManagerDto> getManagers() {
        return managers;
    }

    public void setManagers(Set<ManagerDto> managers) {
        this.managers = managers;
    }

    public List<ReturnDto> getReturns() {
        return returns;
    }

    public void setReturns(List<ReturnDto> returns) {
        this.returns = returns;
    }

    public List<SubstrategyBreakdownDto> getStrategyBreakdownList() {
        return strategyBreakdownList;
    }

    public void setStrategyBreakdownList(List<SubstrategyBreakdownDto> strategyBreakdownList) {
        this.strategyBreakdownList = strategyBreakdownList;
    }

    public List<InvestorBaseDto> getInvestorBaseList() {
        return investorBaseList;
    }

    public void setInvestorBaseList(List<InvestorBaseDto> investorBaseList) {
        this.investorBaseList = investorBaseList;
    }

    public Date getInceptionDate() {
        return inceptionDate;
    }

    public void setInceptionDate(Date inceptionDate) {
        this.inceptionDate = inceptionDate;
    }

    @JsonProperty("GCMApproved")
    public Boolean getGCMApproved() {
        return GCMApproved;
    }

    public void setGCMApproved(Boolean GCMApproved) {
        this.GCMApproved = GCMApproved;
    }

    @JsonProperty("NICApproved")
    public Boolean getNICApproved() {
        return NICApproved;
    }

    public void setNICApproved(Boolean NICApproved) {
        this.NICApproved = NICApproved;
    }

    public Integer getNumMonths() {
        return numMonths;
    }

    public void setNumMonths(Integer numMonths) {
        this.numMonths = numMonths;
    }

    public Integer getNumPositiveMonths() {
        return numPositiveMonths;
    }

    public void setNumPositiveMonths(Integer numPositiveMonths) {
        this.numPositiveMonths = numPositiveMonths;
    }

    public Integer getNumNegativeMonths() {
        return numNegativeMonths;
    }

    public void setNumNegativeMonths(Integer numNegativeMonths) {
        this.numNegativeMonths = numNegativeMonths;
    }

    public Double getReturnSinceInception() {
        return returnSinceInception;
    }

    public void setReturnSinceInception(Double returnSinceInception) {
        this.returnSinceInception = returnSinceInception;
    }

    public Double getAnnualizedReturn() {
        return annualizedReturn;
    }

    public void setAnnualizedReturn(Double annualizedReturn) {
        this.annualizedReturn = annualizedReturn;
    }

    @JsonProperty("YTD")
    public Double getYTD() {
        return YTD;
    }

    public void setYTD(Double YTD) {
        this.YTD = YTD;
    }

    public Double getBeta() {
        return beta;
    }

    public void setBeta(Double beta) {
        this.beta = beta;
    }

    public Double getAnnualVolatility() {
        return annualVolatility;
    }

    public void setAnnualVolatility(Double annualVolatility) {
        this.annualVolatility = annualVolatility;
    }

    public Double getWorstDrawdown() {
        return worstDrawdown;
    }

    public void setWorstDrawdown(Double worstDrawdown) {
        this.worstDrawdown = worstDrawdown;
    }

    public Double getSharpeRatio() {
        return sharpeRatio;
    }

    public void setSharpeRatio(Double sharpeRatio) {
        this.sharpeRatio = sharpeRatio;
    }

    public Double getSortinoRatio() {
        return sortinoRatio;
    }

    public void setSortinoRatio(Double sortinoRatio) {
        this.sortinoRatio = sortinoRatio;
    }

    public String getWorstDrawdownPeriod() {
        return worstDrawdownPeriod;
    }

    public void setWorstDrawdownPeriod(String worstDrawdownPeriod) {
        this.worstDrawdownPeriod = worstDrawdownPeriod;
    }
}
