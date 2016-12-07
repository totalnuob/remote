package kz.nicnbk.repo.model.pe;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.Geography;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.pe.common.Industry;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by zhambyl on 15-Sep-16.
 */

@Entity
@Table(name = "fund")
public class Fund extends CreateUpdateBaseEntity {

    //FUND SUMMARY
    private String fundName;
    private String status;
    private Currency fundCurrency;
    private int vintage;
    private int fundSize;
    private float targetSize;
    private float hardCap;
    private String targetHardCapComment;
    private float gpCommitment;
    private float managementFee;
    private String managementFeeComment;
    private float carriedInterest;
    private float hurdleRate;
    private Set<Industry> industry;
    private Set<Strategy> strategy;
    private String strategyComment;
    private Set<Geography> geography;

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
    public int getFundSize() {
        return fundSize;
    }

    public void setFundSize(int fundSize) {
        this.fundSize = fundSize;
    }

    @Column(name = "target_size")
    public float getTargetSize() {
        return targetSize;
    }

    public void setTargetSize(float targetSize) {
        this.targetSize = targetSize;
    }

    @Column(name = "hard_cap")
    public float getHardCap() {
        return hardCap;
    }

    public void setHardCap(float hardCap) {
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
    public float getGpCommitment() {
        return gpCommitment;
    }

    public void setGpCommitment(float gpCommitment) {
        this.gpCommitment = gpCommitment;
    }

    @Column(name = "management_fee")
    public float getManagementFee() {
        return managementFee;
    }

    public void setManagementFee(float managementFee) {
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
    public float getCarriedInterest() {
        return carriedInterest;
    }

    public void setCarriedInterest(float carriedInterest) {
        this.carriedInterest = carriedInterest;
    }

    @Column(name = "hurdle_rate")
    public float getHurdleRate() {
        return hurdleRate;
    }

    public void setHurdleRate(float hurdleRate) {
        this.hurdleRate = hurdleRate;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "pe_fund_industries",
            joinColumns = @JoinColumn(name = "fund_id", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "pe_industry_id", referencedColumnName = "ID")
    )
    public Set<Industry> getIndustry() {
        return industry;
    }

    public void setIndustry(Set<Industry> industry) {
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

    @Column(name = "number_of_investments")
    public int getNumberOfInvestments() {
        return numberOfInvestments;
    }

    public void setNumberOfInvestments(int numberOfInvestments) {
        this.numberOfInvestments = numberOfInvestments;
    }

    @Column(name = "invested_amount")
    public float getInvestedAmount() {
        return investedAmount;
    }

    public void setInvestedAmount(float investedAmount) {
        this.investedAmount = investedAmount;
    }

    @Column(name = "realized_amount")
    public float getRealized() {
        return realized;
    }

    public void setRealized(float realized) {
        this.realized = realized;
    }

    @Column(name = "unrealized_amount")
    public float getUnrealized() {
        return unrealized;
    }

    public void setUnrealized(float unrealized) {
        this.unrealized = unrealized;
    }

    @Column(name = "dpi")
    public float getDpi() {
        return dpi;
    }

    public void setDpi(float dpi) {
        this.dpi = dpi;
    }

    @Column(name = "net_irr")
    public float getNetIrr() {
        return netIrr;
    }

    public void setNetIrr(float netIrr) {
        this.netIrr = netIrr;
    }

    @Column(name = "net_tvpi")
    public float getNetTvpi() {
        return netTvpi;
    }

    public void setNetTvpi(float netTvpi) {
        this.netTvpi = netTvpi;
    }

    @Column(name = "gross_irr")
    public float getGrossIrr() {
        return grossIrr;
    }

    public void setGrossIrr(float gross_irr) {
        this.grossIrr = gross_irr;
    }

    @Column(name = "gross_tvpi")
    public float getGrossTvpi() {
        return grossTvpi;
    }

    public void setGrossTvpi(float grossTvpi) {
        this.grossTvpi = grossTvpi;
    }

    @Column(name = "as_of_date")
    public Date getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(Date asOfDate) {
        this.asOfDate = asOfDate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fund_benchmark_id")
    public Benchmark getBenchmark() {
        return benchmark;
    }

    public void setBenchmark(Benchmark benchmark) {
        this.benchmark = benchmark;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firm_id")
    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }

    @Column(name = "investment_period")
    public float getInvestmentPeriod() {
        return investmentPeriod;
    }

    public void setInvestmentPeriod(float investmentPeriod) {
        this.investmentPeriod = investmentPeriod;
    }


    @Column(name = "fund_term")
    public float getFundTerm() {
        return fundTerm;
    }

    public void setFundTerm(float fundTerm) {
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
    public float getTargetInvSizeRange() {
        return targetInvSizeRange;
    }

    public void setTargetInvSizeRange(float targetInvSizeRange) {
        this.targetInvSizeRange = targetInvSizeRange;
    }

    @Column(name = "target_ev_range")
    public float getTargetEvRange() {
        return targetEvRange;
    }

    public void setTargetEvRange(float targetEvRange) {
        this.targetEvRange = targetEvRange;
    }

    @Column(name = "target_number_of_inv_lower_bound")
    public float getTargetNumberOfInv1() {
        return targetNumberOfInv1;
    }

    public void setTargetNumberOfInv1(float targetNumberOfInv1) {
        this.targetNumberOfInv1 = targetNumberOfInv1;
    }

    @Column(name = "target_number_of_inv_upper_bound")
    public float getTargetNumberOfInv2() {
        return targetNumberOfInv2;
    }

    public void setTargetNumberOfInv2(float targetNumberOfInv2) {
        this.targetNumberOfInv2 = targetNumberOfInv2;
    }


    @Column(name = "expected_annual_number_of_inv_lower_bound")
    public float getExpAnnualNumberOfInv1() {
        return expAnnualNumberOfInv1;
    }

    public void setExpAnnualNumberOfInv1(float expAnnualNumberOfInv1) {
        this.expAnnualNumberOfInv1 = expAnnualNumberOfInv1;
    }

    @Column(name = "expected_annual_number_of_inv_upper_bound")
    public float getExpAnnualNumberOfInv2() {
        return expAnnualNumberOfInv2;
    }

    public void setExpAnnualNumberOfInv2(float expAnnualNumberOfInv2) {
        this.expAnnualNumberOfInv2 = expAnnualNumberOfInv2;
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
}
