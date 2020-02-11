package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.common.Currency;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name = "hf_screening_saved_result_funds")
public class HedgeFundScreeningSavedResultFunds extends BaseEntity {

    private HedgeFundScreeningSavedResults savedResults;

    /**
     * 1 - Qualified
     * 2 - Unqualified
     * 3 - Undecided
     */
    private int type;

    private Long fundId;
    private String fundName;
    private String investmentManager;
    private String mainStrategy;
    private Currency currency;

    private Double fundAUM;
    private Date fundAUMDate;
    private Double fundAUMUSD;

    private Double strategyAUM;
    private Double managerAUM;

    private Double editedFundAUM;
    private Date editedFundAUMDate;
    private String editedFundAUMComment;

    /* Unqualified or Undecided fund list ********************************/
    private Double recentFundAUM;
    private Date recentFundAUMDate;
    private Date recentTrackRecordDate;

    private Boolean strategyAUMCheck;
    private Boolean managerAUMCheck;
    private Boolean trackRecordCheck;
    private Boolean excluded;
    private Boolean added;

    /* Scoring ***********************************************************/
    private Double annualizedReturn;
    private Double sortino;
    private Double beta;
    private Double alpha;
    private Double omega;
    private Double cfVar;
    private Double totalScore;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="saved_result_id", nullable = false)
    public HedgeFundScreeningSavedResults getSavedResults() {
        return savedResults;
    }

    public void setSavedResults(HedgeFundScreeningSavedResults savedResults) {
        this.savedResults = savedResults;
    }

    @Column(name="fund_id")
    public Long getFundId() {
        return fundId;
    }

    public void setFundId(Long fundId) {
        this.fundId = fundId;
    }

    @Column(name="fund_name")
    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    @Column(name="investment_manager")
    public String getInvestmentManager() {
        return investmentManager;
    }

    public void setInvestmentManager(String investmentManager) {
        this.investmentManager = investmentManager;
    }

    @Column(name="main_strategy")
    public String getMainStrategy() {
        return mainStrategy;
    }

    public void setMainStrategy(String mainStrategy) {
        this.mainStrategy = mainStrategy;
    }

    @Column(name="manager_aum")
    public Double getManagerAUM() {
        return managerAUM;
    }

    public void setManagerAUM(Double managerAUM) {
        this.managerAUM = managerAUM;
    }

    @Column(name="fund_aum")
    public Double getFundAUM() {
        return fundAUM;
    }

    public void setFundAUM(Double fundAUM) {
        this.fundAUM = fundAUM;
    }

    @Column(name="fund_aum_date")
    public Date getFundAUMDate() {
        return fundAUMDate;
    }

    public void setFundAUMDate(Date fundAUMDate) {
        this.fundAUMDate = fundAUMDate;
    }

    @Column(name="fund_aum_comment")
    public String getEditedFundAUMComment() {
        return editedFundAUMComment;
    }

    public void setEditedFundAUMComment(String editedFundAUMComment) {
        this.editedFundAUMComment = editedFundAUMComment;
    }

    @Column(name="type")
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="currency_id")
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Column(name="fund_aum_usd")
    public Double getFundAUMUSD() {
        return fundAUMUSD;
    }

    public void setFundAUMUSD(Double fundAUMUSD) {
        this.fundAUMUSD = fundAUMUSD;
    }

    @Column(name="strategy_aum")
    public Double getStrategyAUM() {
        return strategyAUM;
    }

    public void setStrategyAUM(Double strategyAUM) {
        this.strategyAUM = strategyAUM;
    }

    @Column(name="edited_fund_aum")
    public Double getEditedFundAUM() {
        return editedFundAUM;
    }

    public void setEditedFundAUM(Double editedFundAUM) {
        this.editedFundAUM = editedFundAUM;
    }

    @Column(name="edited_fund_aum_date")
    public Date getEditedFundAUMDate() {
        return editedFundAUMDate;
    }

    public void setEditedFundAUMDate(Date editedFundAUMDate) {
        this.editedFundAUMDate = editedFundAUMDate;
    }

    @Column(name="recent_fund_aum")
    public Double getRecentFundAUM() {
        return recentFundAUM;
    }

    public void setRecentFundAUM(Double recentFundAUM) {
        this.recentFundAUM = recentFundAUM;
    }

    @Column(name="recent_fund_aum_date")
    public Date getRecentFundAUMDate() {
        return recentFundAUMDate;
    }

    public void setRecentFundAUMDate(Date recentFundAUMDate) {
        this.recentFundAUMDate = recentFundAUMDate;
    }

    @Column(name="recent_valid_track_record_date")
    public Date getRecentTrackRecordDate() {
        return recentTrackRecordDate;
    }

    public void setRecentTrackRecordDate(Date recentTrackRecordDate) {
        this.recentTrackRecordDate = recentTrackRecordDate;
    }

    @Column(name="ann_ror")
    public Double getAnnualizedReturn() {
        return annualizedReturn;
    }

    public void setAnnualizedReturn(Double annualizedReturn) {
        this.annualizedReturn = annualizedReturn;
    }

    @Column(name="sortino")
    public Double getSortino() {
        return sortino;
    }

    public void setSortino(Double sortino) {
        this.sortino = sortino;
    }

    @Column(name="beta")
    public Double getBeta() {
        return beta;
    }

    public void setBeta(Double beta) {
        this.beta = beta;
    }

    @Column(name="alpha")
    public Double getAlpha() {
        return alpha;
    }

    public void setAlpha(Double alpha) {
        this.alpha = alpha;
    }

    @Column(name="omega")
    public Double getOmega() {
        return omega;
    }

    public void setOmega(Double omega) {
        this.omega = omega;
    }

    @Column(name="cf_var")
    public Double getCfVar() {
        return cfVar;
    }

    public void setCfVar(Double cfVar) {
        this.cfVar = cfVar;
    }

    @Column(name="total_score")
    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    @Column(name="strategy_aum_check")
    public Boolean getStrategyAUMCheck() {
        return strategyAUMCheck;
    }

    public void setStrategyAUMCheck(Boolean strategyAUMCheck) {
        this.strategyAUMCheck = strategyAUMCheck;
    }

    @Column(name="manager_aum_check")
    public Boolean getManagerAUMCheck() {
        return managerAUMCheck;
    }

    public void setManagerAUMCheck(Boolean managerAUMCheck) {
        this.managerAUMCheck = managerAUMCheck;
    }

    @Column(name="track_record_check")
    public Boolean getTrackRecordCheck() {
        return trackRecordCheck;
    }

    public void setTrackRecordCheck(Boolean trackRecordCheck) {
        this.trackRecordCheck = trackRecordCheck;
    }

    public Boolean getExcluded() {
        return excluded;
    }

    public void setExcluded(Boolean excluded) {
        this.excluded = excluded;
    }

    public Boolean getAdded() {
        return added;
    }

    public void setAdded(Boolean added) {
        this.added = added;
    }
}
