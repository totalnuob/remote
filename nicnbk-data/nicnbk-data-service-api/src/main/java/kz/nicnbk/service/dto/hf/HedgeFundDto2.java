package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.HistoryBaseEntityDto;
import kz.nicnbk.repo.model.hf.HedgeFund;

import java.util.Date;
import java.util.Set;

/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundDto2 extends HistoryBaseEntityDto<HedgeFund> {

    private String name;
    private HFFirmDto firm;
    private String summary;
    private Date inception;
    private Double aum;
    private String aumCurrency;
    private String leverage;
    private String status;

    // fees
    private String managementFee;
    private String performanceFee;
    private String redemptionFee;

    private Double minInitialInvestment;
    private Double minSubsInvestment;
    private String investmentCurrency;

    private String subscriptionFrequency;
    private String redemptionFrequency;
    private String redemptionNoticePeriod;
    private String sidePocket;
    private String gates;

    //private  liquidity;

    private Double concentrationTop5;
    private Double concentrationTop10;
    private Double concentrationTop20;

    private Set<ManagerDto> managers;

    private Set<RatingDto> albourneRatings;
    private Set<ReturnDto> returns;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HFFirmDto getFirm() {
        return firm;
    }

    public void setFirm(HFFirmDto firm) {
        this.firm = firm;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Date getInception() {
        return inception;
    }

    public void setInception(Date inception) {
        this.inception = inception;
    }

    public Double getAum() {
        return aum;
    }

    public void setAum(Double aum) {
        this.aum = aum;
    }

    public String getAumCurrency() {
        return aumCurrency;
    }

    public void setAumCurrency(String aumCurrency) {
        this.aumCurrency = aumCurrency;
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

    public Double getMinInitialInvestment() {
        return minInitialInvestment;
    }

    public void setMinInitialInvestment(Double minInitialInvestment) {
        this.minInitialInvestment = minInitialInvestment;
    }

    public Double getMinSubsInvestment() {
        return minSubsInvestment;
    }

    public void setMinSubsInvestment(Double minSubsInvestment) {
        this.minSubsInvestment = minSubsInvestment;
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

    public Double getConcentrationTop5() {
        return concentrationTop5;
    }

    public void setConcentrationTop5(Double concentrationTop5) {
        this.concentrationTop5 = concentrationTop5;
    }

    public Double getConcentrationTop10() {
        return concentrationTop10;
    }

    public void setConcentrationTop10(Double concentrationTop10) {
        this.concentrationTop10 = concentrationTop10;
    }

    public Double getConcentrationTop20() {
        return concentrationTop20;
    }

    public void setConcentrationTop20(Double concentrationTop20) {
        this.concentrationTop20 = concentrationTop20;
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
