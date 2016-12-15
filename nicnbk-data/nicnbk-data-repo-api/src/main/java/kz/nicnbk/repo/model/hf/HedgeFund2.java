//package kz.nicnbk.repo.model.hf;
//
//import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
//import kz.nicnbk.repo.model.common.Country;
//import kz.nicnbk.repo.model.common.Currency;
//import kz.nicnbk.repo.model.common.Geography;
//import kz.nicnbk.repo.model.common.Strategy;
//
//import javax.persistence.*;
//import java.util.Date;
//import java.util.Set;
//
///**
// * Created by magzumov on 04.07.2016.
// */
//
//@Entity
//@Table(name = "hf_hedge_fund")
//public class HedgeFund2 extends CreateUpdateBaseEntity {
//
//    private String name;
//    private HFFirm managerFirm;
//    private String summary;
//    private Date inception;
//    private Double aum;
//    private Currency aumCurrency;
//    private String leverage;
//    private HedgeFundStatus status;
//
//    // fees
//    private String managementFee;
//    private String performanceFee;
//    private String redemptionFee;
//
//    private Double minInitialInvestment;
//    private Double minSubsInvestment;
//    private Currency investmentCurrency;
//
//    private SubscriptionFrequency subscriptionFrequency;
//    private RedemptionFrequency redemptionFrequency;
//    private RedemptionNotificationPeriod redemptionNoticePeriod;
//    private Boolean sidePocket;
//    private String gates;
//
//    //private  liquidity;
//
//    private Double concentrationTop5;
//    private Double concentrationTop10;
//    private Double concentrationTop20;
//
//    private Set<SeniorManager> managers;
//
//    private Set<Rating> albourneRatings;
//    //private Set<Return> returns;
//
//    @Column (name = "name")
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "firm_id")
//    public HFFirm getManager() {
//        return managerFirm;
//    }
//
//    public void setManager(HFManager manager) {
//        this.manager = manager;
//    }
//
//    public HFFirm getManagerFirm() {
//        return managerFirm;
//    }
//
//    public void setManagerFirm(HFFirm managerFirm) {
//        this.managerFirm = managerFirm;
//    }
//
//    public String getSummary() {
//        return summary;
//    }
//
//    public void setSummary(String summary) {
//        this.summary = summary;
//    }
//
//    public Date getInception() {
//        return inception;
//    }
//
//    public void setInception(Date inception) {
//        this.inception = inception;
//    }
//
//    public Double getAum() {
//        return aum;
//    }
//
//    public void setAum(Double aum) {
//        this.aum = aum;
//    }
//
//    public Currency getAumCurrency() {
//        return aumCurrency;
//    }
//
//    public void setAumCurrency(Currency aumCurrency) {
//        this.aumCurrency = aumCurrency;
//    }
//
//    public String getLeverage() {
//        return leverage;
//    }
//
//    public void setLeverage(String leverage) {
//        this.leverage = leverage;
//    }
//
//    public HedgeFundStatus getStatus() {
//        return status;
//    }
//
//    public void setStatus(HedgeFundStatus status) {
//        this.status = status;
//    }
//
//    public String getManagementFee() {
//        return managementFee;
//    }
//
//    public void setManagementFee(String managementFee) {
//        this.managementFee = managementFee;
//    }
//
//    public String getPerformanceFee() {
//        return performanceFee;
//    }
//
//    public void setPerformanceFee(String performanceFee) {
//        this.performanceFee = performanceFee;
//    }
//
//    public String getRedemptionFee() {
//        return redemptionFee;
//    }
//
//    public void setRedemptionFee(String redemptionFee) {
//        this.redemptionFee = redemptionFee;
//    }
//
//    public Double getMinInitialInvestment() {
//        return minInitialInvestment;
//    }
//
//    public void setMinInitialInvestment(Double minInitialInvestment) {
//        this.minInitialInvestment = minInitialInvestment;
//    }
//
//    public Double getMinSubsInvestment() {
//        return minSubsInvestment;
//    }
//
//    public void setMinSubsInvestment(Double minSubsInvestment) {
//        this.minSubsInvestment = minSubsInvestment;
//    }
//
//    public Currency getInvestmentCurrency() {
//        return investmentCurrency;
//    }
//
//    public void setInvestmentCurrency(Currency investmentCurrency) {
//        this.investmentCurrency = investmentCurrency;
//    }
//
//    public SubscriptionFrequency getSubscriptionFrequency() {
//        return subscriptionFrequency;
//    }
//
//    public void setSubscriptionFrequency(SubscriptionFrequency subscriptionFrequency) {
//        this.subscriptionFrequency = subscriptionFrequency;
//    }
//
//    public RedemptionFrequency getRedemptionFrequency() {
//        return redemptionFrequency;
//    }
//
//    public void setRedemptionFrequency(RedemptionFrequency redemptionFrequency) {
//        this.redemptionFrequency = redemptionFrequency;
//    }
//
//    public RedemptionNotificationPeriod getRedemptionNotificationPeriod() {
//        return redemptionNoticePeriod;
//    }
//
//    public void setRedemptionNotificationPeriod(RedemptionNotificationPeriod redemptionNoticePeriod) {
//        this.redemptionNoticePeriod = redemptionNoticePeriod;
//    }
//
//    public Boolean getSidePocket() {
//        return sidePocket;
//    }
//
//    public void setSidePocket(Boolean sidePocket) {
//        this.sidePocket = sidePocket;
//    }
//
//    public String getGates() {
//        return gates;
//    }
//
//    public void setGates(String gates) {
//        this.gates = gates;
//    }
//
//    public Double getConcentrationTop5() {
//        return concentrationTop5;
//    }
//
//    public void setConcentrationTop5(Double concentrationTop5) {
//        this.concentrationTop5 = concentrationTop5;
//    }
//
//    public Double getConcentrationTop10() {
//        return concentrationTop10;
//    }
//
//    public void setConcentrationTop10(Double concentrationTop10) {
//        this.concentrationTop10 = concentrationTop10;
//    }
//
//    public Double getConcentrationTop20() {
//        return concentrationTop20;
//    }
//
//    public void setConcentrationTop20(Double concentrationTop20) {
//        this.concentrationTop20 = concentrationTop20;
//    }
//
//    public Set<Manager> getManagers() {
//        return managers;
//    }
//
//    public void setManagers(Set<Manager> managers) {
//        this.managers = managers;
//    }
//
//    public Set<Rating> getAlbourneRatings() {
//        return albourneRatings;
//    }
//
//    public void setAlbourneRatings(Set<Rating> albourneRatings) {
//        this.albourneRatings = albourneRatings;
//    }
//
//    public Set<Return> getReturns() {
//        return returns;
//    }
//
//    public void setReturns(Set<Return> returns) {
//        this.returns = returns;
//    }
//}
