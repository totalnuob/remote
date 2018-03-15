package kz.nicnbk.service.dto.pe;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.HistoryBaseEntityDto;
import kz.nicnbk.repo.model.pe.PEFund;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by zhambyl on 15-Nov-16.
 */
public class PEFundDto extends HistoryBaseEntityDto<PEFund> {
    //FUND SUMMARY
    private String fundName;
    private String status;
    private String nicStatus;
    private String currency;
    private int vintage;
    private Double fundSize;
    private Double predecessorInvestedPct;
    private double targetSize;
    private double hardCap;
    private String targetHardCapComment;
    private double gpCommitment;
    private double managementFee;
    private String managementFeeComment;
    private double carriedInterest;
    private double hurdleRate;
    private Set<BaseDictionaryDto> industry;
    private Set<BaseDictionaryDto> strategy;
    private String strategyComment;
    private Set<BaseDictionaryDto> geography;

    private String openingSchedule;
    private Boolean suitable;
    private String nonsuitableReason;

    //KEY FUND STATISTICS
    private Integer calculationType;
//    private Boolean autoCalculation;
    private Integer numberOfInvestments;
    private Double investedAmount;
    private Double realized;
    private Double unrealized;
    private Double dpi;
    private Double netIrr;
    private Double netTvpi; // MOIC
    private Double grossIrr;
    private Double grossTvpi;
    private Date asOfDate;
    private Double benchmarkNetIrr;
    private Double benchmarkNetTvpi;
    private String benchmarkName;
    private Boolean doNotDisplayInOnePager;
    private PEFirmDto firm;

    // Descriptive data
    private String investmentPeriod;
    private double fundTerm;
    private String fundTermComment;
    private String targetInvSizeRange;
    private String targetEvRange;
    private double targetNumberOfInv1;
    private double targetNumberOfInv2;
    private double expAnnualNumberOfInv1;
    private double expAnnualNumberOfInv2;
    private String expHoldPeriodPerInvestment;

    //Targeted Closing Information
    private Date firstClose;
    private Date finalClose;
    private String firstCloseComment;
    private String finalCloseComment;

    private Date asOfDateOnePager;

    //Observations
    private String generalPartnerMerits;
    private String generalPartnerRisks;
    private String strategyStructureMerits;
    private String strategyStructureRisks;
    private String performanceMerits;
    private String performanceRisks;

    private String owner;

    //cashflows
    private List<PEGrossCashflowDto> grossCashflow;
    private List<PENetCashflowDto> netCashflow;
    private List<PECompanyPerformanceDto> companyPerformance;
    private List<PECompanyPerformanceIddDto> companyPerformanceIdd;

    private List<PEOnePagerDescriptionsDto> onePagerDescriptions;

    private List<PEFundManagementTeamDto> managementTeam;

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

    public String getNicStatus() {
        return nicStatus;
    }

    public void setNicStatus(String nicStatus) {
        this.nicStatus = nicStatus;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getVintage() {
        return vintage;
    }

    public void setVintage(int vintage) {
        this.vintage = vintage;
    }

    public Double getFundSize() {
        return fundSize;
    }

    public void setFundSize(Double fundSize) {
        this.fundSize = fundSize;
    }

    public double getTargetSize() {
        return targetSize;
    }

    public void setTargetSize(double targetSize) {
        this.targetSize = targetSize;
    }

    public double getHardCap() {
        return hardCap;
    }

    public void setHardCap(double hardCap) {
        this.hardCap = hardCap;
    }

    public String getTargetHardCapComment() {
        return targetHardCapComment;
    }

    public void setTargetHardCapComment(String targetHardCapComment) {
        this.targetHardCapComment = targetHardCapComment;
    }

    public double getGpCommitment() {
        return gpCommitment;
    }

    public void setGpCommitment(double gpCommitment) {
        this.gpCommitment = gpCommitment;
    }

    public double getManagementFee() {
        return managementFee;
    }

    public void setManagementFee(double managementFee) {
        this.managementFee = managementFee;
    }

    public String getManagementFeeComment() {
        return managementFeeComment;
    }

    public void setManagementFeeComment(String managementFeeComment) {
        this.managementFeeComment = managementFeeComment;
    }

    public double getCarriedInterest() {
        return carriedInterest;
    }

    public void setCarriedInterest(double carriedInterest) {
        this.carriedInterest = carriedInterest;
    }

    public double getHurdleRate() {
        return hurdleRate;
    }

    public void setHurdleRate(double hurdleRate) {
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

    public Integer getCalculationType() {
        return calculationType;
    }

    public void setCalculationType(Integer calculationType) {
        this.calculationType = calculationType;
    }

//    public Boolean getAutoCalculation() {
//        return autoCalculation;
//    }
//
//    public void setAutoCalculation(Boolean autoCalculation) {
//        this.autoCalculation = autoCalculation;
//    }

    public Integer getNumberOfInvestments() {
        return numberOfInvestments;
    }

    public void setNumberOfInvestments(Integer numberOfInvestments) {
        this.numberOfInvestments = numberOfInvestments;
    }

    public Double getInvestedAmount() {
        return investedAmount;
    }

    public void setInvestedAmount(Double investedAmount) {
        this.investedAmount = investedAmount;
    }

    public Double getRealized() {
        return realized;
    }

    public void setRealized(Double realized) {
        this.realized = realized;
    }

    public Double getUnrealized() {
        return unrealized;
    }

    public void setUnrealized(Double unrealized) {
        this.unrealized = unrealized;
    }

    public Double getDpi() {
        return dpi;
    }

    public void setDpi(Double dpi) {
        this.dpi = dpi;
    }

    public Double getNetIrr() {
        return netIrr;
    }

    public void setNetIrr(Double netIrr) {
        this.netIrr = netIrr;
    }

    public Double getNetTvpi() {
        return netTvpi;
    }

    public void setNetTvpi(Double netTvpi) {
        this.netTvpi = netTvpi;
    }

    public Double getGrossIrr() {
        return grossIrr;
    }

    public void setGrossIrr(Double grossIrr) {
        this.grossIrr = grossIrr;
    }

    public Double getGrossTvpi() {
        return grossTvpi;
    }

    public void setGrossTvpi(Double grossTvpi) {
        this.grossTvpi = grossTvpi;
    }

    public Date getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(Date asOfDate) {
        this.asOfDate = asOfDate;
    }

    public Double getBenchmarkNetIrr() {
        return benchmarkNetIrr;
    }

    public void setBenchmarkNetIrr(Double benchmarkNetIrr) {
        this.benchmarkNetIrr = benchmarkNetIrr;
    }

    public Double getBenchmarkNetTvpi() {
        return benchmarkNetTvpi;
    }

    public void setBenchmarkNetTvpi(Double benchmarkNetTvpi) {
        this.benchmarkNetTvpi = benchmarkNetTvpi;
    }

    public String getBenchmarkName() {
        return benchmarkName;
    }

    public void setBenchmarkName(String benchmarkName) {
        this.benchmarkName = benchmarkName;
    }

    public Boolean getDoNotDisplayInOnePager() {
        return doNotDisplayInOnePager;
    }

    public void setDoNotDisplayInOnePager(Boolean doNotDisplayInOnePager) {
        this.doNotDisplayInOnePager = doNotDisplayInOnePager;
    }

    public PEFirmDto getFirm() {
        return firm;
    }

    public void setFirm(PEFirmDto firm) {
        this.firm = firm;
    }

    public String getInvestmentPeriod() {
        return investmentPeriod;
    }

    public void setInvestmentPeriod(String investmentPeriod) {
        this.investmentPeriod = investmentPeriod;
    }

    public double getFundTerm() {
        return fundTerm;
    }

    public void setFundTerm(double fundTerm) {
        this.fundTerm = fundTerm;
    }

    public String getFundTermComment() {
        return fundTermComment;
    }

    public void setFundTermComment(String fundTermComment) {
        this.fundTermComment = fundTermComment;
    }

    public String getTargetInvSizeRange() {
        return targetInvSizeRange;
    }

    public void setTargetInvSizeRange(String targetInvSizeRange) {
        this.targetInvSizeRange = targetInvSizeRange;
    }

    public String getTargetEvRange() {
        return targetEvRange;
    }

    public void setTargetEvRange(String targetEvRange) {
        this.targetEvRange = targetEvRange;
    }

    public double getTargetNumberOfInv1() {
        return targetNumberOfInv1;
    }

    public void setTargetNumberOfInv1(double targetNumberOfInv1) {
        this.targetNumberOfInv1 = targetNumberOfInv1;
    }

    public double getTargetNumberOfInv2() {
        return targetNumberOfInv2;
    }

    public void setTargetNumberOfInv2(double targetNumberOfInv2) {
        this.targetNumberOfInv2 = targetNumberOfInv2;
    }

    public double getExpAnnualNumberOfInv1() {
        return expAnnualNumberOfInv1;
    }

    public void setExpAnnualNumberOfInv1(double expAnnualNumberOfInv1) {
        this.expAnnualNumberOfInv1 = expAnnualNumberOfInv1;
    }

    public double getExpAnnualNumberOfInv2() {
        return expAnnualNumberOfInv2;
    }

    public void setExpAnnualNumberOfInv2(double expAnnualNumberOfInv2) {
        this.expAnnualNumberOfInv2 = expAnnualNumberOfInv2;
    }

    public String getExpHoldPeriodPerInvestment() {
        return expHoldPeriodPerInvestment;
    }

    public void setExpHoldPeriodPerInvestment(String expHoldPeriodPerInvestment) {
        this.expHoldPeriodPerInvestment = expHoldPeriodPerInvestment;
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

    public String getFirstCloseComment() {
        return firstCloseComment;
    }

    public void setFirstCloseComment(String firstCloseComment) {
        this.firstCloseComment = firstCloseComment;
    }

    public String getFinalCloseComment() {
        return finalCloseComment;
    }

    public void setFinalCloseComment(String finalCloseComment) {
        this.finalCloseComment = finalCloseComment;
    }

    public Date getAsOfDateOnePager() {
        return asOfDateOnePager;
    }

    public void setAsOfDateOnePager(Date asOfDateOnePager) {
        this.asOfDateOnePager = asOfDateOnePager;
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

    public List<PEGrossCashflowDto> getGrossCashflow() {
        return grossCashflow;
    }

    public void setGrossCashflow(List<PEGrossCashflowDto> grossCashflow) {
        this.grossCashflow = grossCashflow;
    }

    public List<PECompanyPerformanceDto> getCompanyPerformance() {
        return companyPerformance;
    }

    public void setCompanyPerformance(List<PECompanyPerformanceDto> companyPerformance) {
        this.companyPerformance = companyPerformance;
    }

    public List<PECompanyPerformanceIddDto> getCompanyPerformanceIdd() {
        return companyPerformanceIdd;
    }

    public void setCompanyPerformanceIdd(List<PECompanyPerformanceIddDto> companyPerformanceIdd) {
        this.companyPerformanceIdd = companyPerformanceIdd;
    }

    public List<PEOnePagerDescriptionsDto> getOnePagerDescriptions() {
        return onePagerDescriptions;
    }

    public void setOnePagerDescriptions(List<PEOnePagerDescriptionsDto> onePagerDescriptions) {
        this.onePagerDescriptions = onePagerDescriptions;
    }

    public List<PEFundManagementTeamDto> getManagementTeam() {
        return managementTeam;
    }

    public void setManagementTeam(List<PEFundManagementTeamDto> managementTeam) {
        this.managementTeam = managementTeam;
    }

    public List<PENetCashflowDto> getNetCashflow() {
        return netCashflow;
    }

    public void setNetCashflow(List<PENetCashflowDto> netCashflow) {
        this.netCashflow = netCashflow;
    }

    public Double getPredecessorInvestedPct() {
        return predecessorInvestedPct;
    }

    public void setPredecessorInvestedPct(Double predecessorInvestedPct) {
        this.predecessorInvestedPct = predecessorInvestedPct;
    }

    public String getOpeningSchedule() {
        return openingSchedule;
    }

    public void setOpeningSchedule(String openingSchedule) {
        this.openingSchedule = openingSchedule;
    }

    public Boolean getSuitable() {
        return suitable;
    }

    public void setSuitable(Boolean suitable) {
        this.suitable = suitable;
    }

    public String getNonsuitableReason() {
        return nonsuitableReason;
    }

    public void setNonsuitableReason(String nonsuitableReason) {
        this.nonsuitableReason = nonsuitableReason;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}