package kz.nicnbk.service.dto.pe;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.HistoryBaseEntityDto;
import kz.nicnbk.repo.model.pe.Benchmark;
import kz.nicnbk.repo.model.pe.Firm;
import kz.nicnbk.repo.model.pe.Fund;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by zhambyl on 15-Nov-16.
 */
public class PeFundDto extends HistoryBaseEntityDto<Fund> {
    //FUND SUMMARY
    private String fundName;
    private String status;
    private String fundCurrency;
    private int vintage;
    private float fundSize;
    private float targetSize;
    private float hardCap;
    private String targetHardCapComment;
    private float gpCommitment;
    private float managementFee;
    private String managementFeeComment;
    private float carriedInterest;
    private float hurdleRate;
    private Set<BaseDictionaryDto> industry;
    private Set<BaseDictionaryDto> strategy;
    private String strategyComment;
    private Set<BaseDictionaryDto> geography;

    //KEY FUND STATISTICS
    private int numberOfInvestments;
    private float investedAmount;
    private float realized;
    private float unrealized;
    private float dpi;
    private float netIrr;
    private float netTvpi; // MOIC
    private float grossIrr;
    private float grossTvpi;
    private Date asOfDate;
    private Benchmark benchmark;
    private Firm firm;

    // Descriptive data
    private float investmentPeriod;
    private float fundTerm;
    private String fundTermComment;
    private float targetInvSizeRange;
    private float targetEvRange;
    private float targetNumberOfInv1;
    private float targetNumberOfInv2;
    private float expAnnualNumberOfInv1;
    private float expAnnualNumberOfInv2;

    //Targeted Closing Information
    private Date firstClose;
    private Date finalClose;

    //Observations
    private String generalPartnerMerits;
    private String generalPartnerRisks;
    private String strategyStructureMerits;
    private String strategyStructureRisks;
    private String performanceMerits;
    private String performanceRisks;

    //cashflows
    private List<PeCashflowDto> cashflow;
    private List<PeFundCompaniesPerformanceDto> fundCompanyPerformance;

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFundCurrency() {
        return fundCurrency;
    }

    public void setFundCurrency(String fundCurrency) {
        this.fundCurrency = fundCurrency;
    }

    public int getVintage() {
        return vintage;
    }

    public void setVintage(int vintage) {
        this.vintage = vintage;
    }

    public float getFundSize() {
        return fundSize;
    }

    public void setFundSize(float fundSize) {
        this.fundSize = fundSize;
    }

    public float getTargetSize() {
        return targetSize;
    }

    public void setTargetSize(float targetSize) {
        this.targetSize = targetSize;
    }

    public float getHardCap() {
        return hardCap;
    }

    public void setHardCap(float hardCap) {
        this.hardCap = hardCap;
    }

    public String getTargetHardCapComment() {
        return targetHardCapComment;
    }

    public void setTargetHardCapComment(String targetHardCapComment) {
        this.targetHardCapComment = targetHardCapComment;
    }

    public float getGpCommitment() {
        return gpCommitment;
    }

    public void setGpCommitment(float gpCommitment) {
        this.gpCommitment = gpCommitment;
    }

    public float getManagementFee() {
        return managementFee;
    }

    public void setManagementFee(float managementFee) {
        this.managementFee = managementFee;
    }

    public String getManagementFeeComment() {
        return managementFeeComment;
    }

    public void setManagementFeeComment(String managementFeeComment) {
        this.managementFeeComment = managementFeeComment;
    }

    public float getCarriedInterest() {
        return carriedInterest;
    }

    public void setCarriedInterest(float carriedInterest) {
        this.carriedInterest = carriedInterest;
    }

    public float getHurdleRate() {
        return hurdleRate;
    }

    public void setHurdleRate(float hurdleRate) {
        this.hurdleRate = hurdleRate;
    }

    public Set<BaseDictionaryDto> getIndustry() {
        return industry;
    }

    public void setIndustry(Set<BaseDictionaryDto> industry) {
        this.industry = industry;
    }

    public Set<BaseDictionaryDto> getStrategy() {
        return strategy;
    }

    public void setStrategy(Set<BaseDictionaryDto> strategy) {
        this.strategy = strategy;
    }

    public String getStrategyComment() {
        return strategyComment;
    }

    public void setStrategyComment(String strategyComment) {
        this.strategyComment = strategyComment;
    }

    public Set<BaseDictionaryDto> getGeography() {
        return geography;
    }

    public void setGeography(Set<BaseDictionaryDto> geography) {
        this.geography = geography;
    }

    public int getNumberOfInvestments() {
        return numberOfInvestments;
    }

    public void setNumberOfInvestments(int numberOfInvestments) {
        this.numberOfInvestments = numberOfInvestments;
    }

    public float getInvestedAmount() {
        return investedAmount;
    }

    public void setInvestedAmount(float investedAmount) {
        this.investedAmount = investedAmount;
    }

    public float getRealized() {
        return realized;
    }

    public void setRealized(float realized) {
        this.realized = realized;
    }

    public float getUnrealized() {
        return unrealized;
    }

    public void setUnrealized(float unrealized) {
        this.unrealized = unrealized;
    }

    public float getDpi() {
        return dpi;
    }

    public void setDpi(float dpi) {
        this.dpi = dpi;
    }

    public float getNetIrr() {
        return netIrr;
    }

    public void setNetIrr(float netIrr) {
        this.netIrr = netIrr;
    }

    public float getNetTvpi() {
        return netTvpi;
    }

    public void setNetTvpi(float netTvpi) {
        this.netTvpi = netTvpi;
    }

    public float getGrossIrr() {
        return grossIrr;
    }

    public void setGrossIrr(float grossIrr) {
        this.grossIrr = grossIrr;
    }

    public float getGrossTvpi() {
        return grossTvpi;
    }

    public void setGrossTvpi(float grossTvpi) {
        this.grossTvpi = grossTvpi;
    }

    public Date getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(Date asOfDate) {
        this.asOfDate = asOfDate;
    }

    public Benchmark getBenchmark() {
        return benchmark;
    }

    public void setBenchmark(Benchmark benchmark) {
        this.benchmark = benchmark;
    }

    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }

    public float getInvestmentPeriod() {
        return investmentPeriod;
    }

    public void setInvestmentPeriod(float investmentPeriod) {
        this.investmentPeriod = investmentPeriod;
    }

    public float getFundTerm() {
        return fundTerm;
    }

    public void setFundTerm(float fundTerm) {
        this.fundTerm = fundTerm;
    }

    public String getFundTermComment() {
        return fundTermComment;
    }

    public void setFundTermComment(String fundTermComment) {
        this.fundTermComment = fundTermComment;
    }

    public float getTargetInvSizeRange() {
        return targetInvSizeRange;
    }

    public void setTargetInvSizeRange(float targetInvSizeRange) {
        this.targetInvSizeRange = targetInvSizeRange;
    }

    public float getTargetEvRange() {
        return targetEvRange;
    }

    public void setTargetEvRange(float targetEvRange) {
        this.targetEvRange = targetEvRange;
    }

    public float getTargetNumberOfInv1() {
        return targetNumberOfInv1;
    }

    public void setTargetNumberOfInv1(float targetNumberOfInv1) {
        this.targetNumberOfInv1 = targetNumberOfInv1;
    }

    public float getTargetNumberOfInv2() {
        return targetNumberOfInv2;
    }

    public void setTargetNumberOfInv2(float targetNumberOfInv2) {
        this.targetNumberOfInv2 = targetNumberOfInv2;
    }

    public float getExpAnnualNumberOfInv1() {
        return expAnnualNumberOfInv1;
    }

    public void setExpAnnualNumberOfInv1(float expAnnualNumberOfInv1) {
        this.expAnnualNumberOfInv1 = expAnnualNumberOfInv1;
    }

    public float getExpAnnualNumberOfInv2() {
        return expAnnualNumberOfInv2;
    }

    public void setExpAnnualNumberOfInv2(float expAnnualNumberOfInv2) {
        this.expAnnualNumberOfInv2 = expAnnualNumberOfInv2;
    }

    public Date getFirstClose() {
        return firstClose;
    }

    public void setFirstClose(Date firstClose) {
        this.firstClose = firstClose;
    }

    public Date getFinalClose() {
        return finalClose;
    }

    public void setFinalClose(Date finalClose) {
        this.finalClose = finalClose;
    }

    public String getGeneralPartnerMerits() {
        return generalPartnerMerits;
    }

    public void setGeneralPartnerMerits(String generalPartnerMerits) {
        this.generalPartnerMerits = generalPartnerMerits;
    }

    public String getGeneralPartnerRisks() {
        return generalPartnerRisks;
    }

    public void setGeneralPartnerRisks(String generalPartnerRisks) {
        this.generalPartnerRisks = generalPartnerRisks;
    }

    public String getStrategyStructureMerits() {
        return strategyStructureMerits;
    }

    public void setStrategyStructureMerits(String strategyStructureMerits) {
        this.strategyStructureMerits = strategyStructureMerits;
    }

    public String getStrategyStructureRisks() {
        return strategyStructureRisks;
    }

    public void setStrategyStructureRisks(String strategyStructureRisks) {
        this.strategyStructureRisks = strategyStructureRisks;
    }

    public String getPerformanceMerits() {
        return performanceMerits;
    }

    public void setPerformanceMerits(String performanceMerits) {
        this.performanceMerits = performanceMerits;
    }

    public String getPerformanceRisks() {
        return performanceRisks;
    }

    public void setPerformanceRisks(String performanceRisks) {
        this.performanceRisks = performanceRisks;
    }

    public List<PeCashflowDto> getCashflow() {
        return cashflow;
    }

    public void setCashflow(List<PeCashflowDto> cashflow) {
        this.cashflow = cashflow;
    }

    public List<PeFundCompaniesPerformanceDto> getFundCompanyPerformance() {
        return fundCompanyPerformance;
    }

    public void setFundCompanyPerformance(List<PeFundCompaniesPerformanceDto> fundCompanyPerformance) {
        this.fundCompanyPerformance = fundCompanyPerformance;
    }

}
