package kz.nicnbk.repo.model.pe;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.base.DataConstraints;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.Geography;
import kz.nicnbk.repo.model.common.Strategy;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by zhambyl on 15-Sep-16.
 */

@Entity
@Table(name = "pe_fund")
public class PEFund extends CreateUpdateBaseEntity {

    //FUND SUMMARY
    private String fundName;
    private String status;
    private String nicStatus;
    private Currency fundCurrency;
    private int vintage;
    private double fundSize;
    private Double predecessorInvestedPct;
    private double targetSize;
    private double hardCap;
    private String targetHardCapComment;
    private double gpCommitment;
    private double managementFee;
    private String managementFeeComment;
    private double carriedInterest;
    private double hurdleRate;
    private Set<PEIndustry> industry;
    private Set<Strategy> strategy;
    private String strategyComment;
    private Set<Geography> geography;

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
    private PEFirm firm;

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

    public PEFund() {}

    public PEFund(Long id) {
        this.setId(id);
    }

    @Column(name = "fund_name")
    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "nic_status")
    public String getNicStatus() {
        return nicStatus;
    }

    public void setNicStatus(String nicStatus) {
        this.nicStatus = nicStatus;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fund_currency_id")
    public Currency getFundCurrency() {
        return fundCurrency;
    }

    public void setFundCurrency(Currency fundCurrency) {
        this.fundCurrency = fundCurrency;
    }

    @Column(name = "vintage")
    public int getVintage() {
        return vintage;
    }

    public void setVintage(int vintage) {
        this.vintage = vintage;
    }

    @Column(name = "fund_size")
    public double getFundSize() {
        return fundSize;
    }

    public void setFundSize(double fundSize) {
        this.fundSize = fundSize;
    }

    @Column(name="predecessor_invested_pct")
    public Double getPredecessorInvestedPct() {
        return predecessorInvestedPct;
    }

    public void setPredecessorInvestedPct(Double predecessorInvestedPct) {
        this.predecessorInvestedPct = predecessorInvestedPct;
    }

    @Column(name = "target_size")
    public double getTargetSize() {
        return targetSize;
    }

    public void setTargetSize(double targetSize) {
        this.targetSize = targetSize;
    }

    @Column(name = "hard_cap")
    public double getHardCap() {
        return hardCap;
    }

    public void setHardCap(double hardCap) {
        this.hardCap = hardCap;
    }

    @Column(name = "target_hard_cap_comment")
    public String getTargetHardCapComment() {
        return targetHardCapComment;
    }

    public void setTargetHardCapComment(String targetHardCapComment) {
        this.targetHardCapComment = targetHardCapComment;
    }

    @Column(name = "gp_commitment")
    public double getGpCommitment() {
        return gpCommitment;
    }

    public void setGpCommitment(double gpCommitment) {
        this.gpCommitment = gpCommitment;
    }

    @Column(name = "management_fee")
    public double getManagementFee() {
        return managementFee;
    }

    public void setManagementFee(double managementFee) {
        this.managementFee = managementFee;
    }

    @Column(name = "management_fee_commentary")
    public String getManagementFeeComment() {
        return managementFeeComment;
    }

    public void setManagementFeeComment(String managementFeeComment) {
        this.managementFeeComment = managementFeeComment;
    }

    @Column(name = "carried_interest")
    public double getCarriedInterest() {
        return carriedInterest;
    }

    public void setCarriedInterest(double carriedInterest) {
        this.carriedInterest = carriedInterest;
    }

    @Column(name = "hurdle_rate")
    public double getHurdleRate() {
        return hurdleRate;
    }

    public void setHurdleRate(double hurdleRate) {
        this.hurdleRate = hurdleRate;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "pe_fund_industries",
            joinColumns = @JoinColumn(name = "fund_id", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "pe_industry_id", referencedColumnName = "ID")
    )
    public Set<PEIndustry> getIndustry() {
        return industry;
    }

    public void setIndustry(Set<PEIndustry> industry) {
        this.industry = industry;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "pe_fund_strategies",
            joinColumns = @JoinColumn(name = "fund_id", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "strategy_id", referencedColumnName = "ID")
    )
    public Set<Strategy> getStrategy() {
        return strategy;
    }

    public void setStrategy(Set<Strategy> strategy) {
        this.strategy = strategy;
    }

    @Column(name = "strategy_comments", columnDefinition = "TEXT")
    public String getStrategyComment() {
        return strategyComment;
    }

    public void setStrategyComment(String strategyComments) {
        this.strategyComment = strategyComments;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "pe_fund_geography",
            joinColumns = @JoinColumn(name = "fund_id", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "geography_id", referencedColumnName = "ID")
    )
    public Set<Geography> getGeography() {
        return geography;
    }

    public void setGeography(Set<Geography> geography) {
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

    @Column(name = "number_of_investments")
    public Integer getNumberOfInvestments() {
        return numberOfInvestments;
    }

    public void setNumberOfInvestments(Integer numberOfInvestments) {
        this.numberOfInvestments = numberOfInvestments;
    }

    @Column(name = "invested_amount")
    public Double getInvestedAmount() {
        return investedAmount;
    }

    public void setInvestedAmount(Double investedAmount) {
        this.investedAmount = investedAmount;
    }

    @Column(name = "realized_amount")
    public Double getRealized() {
        return realized;
    }

    public void setRealized(Double realized) {
        this.realized = realized;
    }

    @Column(name = "unrealized_amount")
    public Double getUnrealized() {
        return unrealized;
    }

    public void setUnrealized(Double unrealized) {
        this.unrealized = unrealized;
    }

    @Column(name = "dpi")
    public Double getDpi() {
        return dpi;
    }

    public void setDpi(Double dpi) {
        this.dpi = dpi;
    }

    @Column(name = "net_irr")
    public Double getNetIrr() {
        return netIrr;
    }

    public void setNetIrr(Double netIrr) {
        this.netIrr = netIrr;
    }

    @Column(name = "net_tvpi")
    public Double getNetTvpi() {
        return netTvpi;
    }

    public void setNetTvpi(Double netTvpi) {
        this.netTvpi = netTvpi;
    }

    @Column(name = "gross_irr")
    public Double getGrossIrr() {
        return grossIrr;
    }

    public void setGrossIrr(Double gross_irr) {
        this.grossIrr = gross_irr;
    }

    @Column(name = "gross_tvpi")
    public Double getGrossTvpi() {
        return grossTvpi;
    }

    public void setGrossTvpi(Double grossTvpi) {
        this.grossTvpi = grossTvpi;
    }

    @Column(name = "as_of_date")
    public Date getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(Date asOfDate) {
        this.asOfDate = asOfDate;
    }

    @Column(name = "benchmark_net_irr")
    public Double getBenchmarkNetIrr() {
        return benchmarkNetIrr;
    }

    public void setBenchmarkNetIrr(Double benchmarkNetIrr) {
        this.benchmarkNetIrr = benchmarkNetIrr;
    }

    @Column(name = "benchmark_net_tvpi")
    public Double getBenchmarkNetTvpi() {
        return benchmarkNetTvpi;
    }

    public void setBenchmarkNetTvpi(Double benchmarkNetTvpi) {
        this.benchmarkNetTvpi = benchmarkNetTvpi;
    }

    @Column(name = "benchmark_name")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firm_id")
    public PEFirm getFirm() {
        return firm;
    }

    public void setFirm(PEFirm firm) {
        this.firm = firm;
    }

    @Column(name = "investment_period")
    public String getInvestmentPeriod() {
        return investmentPeriod;
    }

    public void setInvestmentPeriod(String investmentPeriod) {
        this.investmentPeriod = investmentPeriod;
    }


    @Column(name = "fund_term")
    public double getFundTerm() {
        return fundTerm;
    }

    public void setFundTerm(double fundTerm) {
        this.fundTerm = fundTerm;
    }


    @Column(name = "fund_term_commentary")
    public String getFundTermComment() {
        return fundTermComment;
    }

    public void setFundTermComment(String fundTermComment) {
        this.fundTermComment = fundTermComment;
    }

    @Column(name = "traget_inv_size_range")
    public String getTargetInvSizeRange() {
        return targetInvSizeRange;
    }

    public void setTargetInvSizeRange(String targetInvSizeRange) {
        this.targetInvSizeRange = targetInvSizeRange;
    }

    @Column(name = "target_ev_range")
    public String getTargetEvRange() {
        return targetEvRange;
    }

    public void setTargetEvRange(String targetEvRange) {
        this.targetEvRange = targetEvRange;
    }

    @Column(name = "target_number_of_inv_lower_bound")
    public double getTargetNumberOfInv1() {
        return targetNumberOfInv1;
    }

    public void setTargetNumberOfInv1(double targetNumberOfInv1) {
        this.targetNumberOfInv1 = targetNumberOfInv1;
    }

    @Column(name = "target_number_of_inv_upper_bound")
    public double getTargetNumberOfInv2() {
        return targetNumberOfInv2;
    }

    public void setTargetNumberOfInv2(double targetNumberOfInv2) {
        this.targetNumberOfInv2 = targetNumberOfInv2;
    }


    @Column(name = "expected_annual_number_of_inv_lower_bound")
    public double getExpAnnualNumberOfInv1() {
        return expAnnualNumberOfInv1;
    }

    public void setExpAnnualNumberOfInv1(double expAnnualNumberOfInv1) {
        this.expAnnualNumberOfInv1 = expAnnualNumberOfInv1;
    }

    @Column(name = "expected_annual_number_of_inv_upper_bound")
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

    @Column(name = "first_close")
    public Date getFirstClose() {
        return firstClose;
    }

    public void setFirstClose(Date firstClose) {
        this.firstClose = firstClose;
    }


    @Column(name = "final_close")
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

    @Column(name = "general_partner_merits", columnDefinition = "TEXT")
    public String getGeneralPartnerMerits() {
        return generalPartnerMerits;
    }

    public void setGeneralPartnerMerits(String generalPartnerMerits) {
        this.generalPartnerMerits = generalPartnerMerits;
    }


    @Column(name = "general_partner_risks", columnDefinition = "TEXT")
    public String getGeneralPartnerRisks() {
        return generalPartnerRisks;
    }

    public void setGeneralPartnerRisks(String generalPartnerRisks) {
        this.generalPartnerRisks = generalPartnerRisks;
    }


    @Column(name = "strategy_structure_merits", columnDefinition = "TEXT")
    public String getStrategyStructureMerits() {
        return strategyStructureMerits;
    }

    public void setStrategyStructureMerits(String strategyStructureMerits) {
        this.strategyStructureMerits = strategyStructureMerits;
    }


    @Column(name = "strategy_structure_risks", columnDefinition = "TEXT")
    public String getStrategyStructureRisks() {
        return strategyStructureRisks;
    }

    public void setStrategyStructureRisks(String strategyStructureRisks) {
        this.strategyStructureRisks = strategyStructureRisks;
    }

    @Column(name = "performance_merits", columnDefinition = "TEXT")
    public String getPerformanceMerits() {
        return performanceMerits;
    }

    public void setPerformanceMerits(String performanceMerits) {
        this.performanceMerits = performanceMerits;
    }

    @Column(name = "performance_risks", columnDefinition = "TEXT")
    public String getPerformanceRisks() {
        return performanceRisks;
    }

    public void setPerformanceRisks(String performanceRisks) {
        this.performanceRisks = performanceRisks;
    }

    @Column(name="opening_schedule", length = DataConstraints.C_TYPE_ENTITY_NAME)
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

    @Column(name="nonsuitable_desc", length = DataConstraints.C_TYPE_ENTITY_DESCRIPTION_LONG)
    public String getNonsuitableReason() {
        return nonsuitableReason;
    }

    public void setNonsuitableReason(String nonsuitableReason) {
        this.nonsuitableReason = nonsuitableReason;
    }
}