package kz.nicnbk.service.dto.hf;
import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.lookup.CurrencyLookup;

import java.util.Date;


public class HedgeFundScreeningSavedResultFundsDto extends CreateUpdateBaseEntityDto {

    private HedgeFundScreeningSavedResultsDto savedResults;

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
    private BaseDictionaryDto currency;

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


    public HedgeFundScreeningSavedResultsDto getSavedResults() {
        return savedResults;
    }

    public void setSavedResults(HedgeFundScreeningSavedResultsDto savedResults) {
        this.savedResults = savedResults;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getFundId() {
        return fundId;
    }

    public void setFundId(Long fundId) {
        this.fundId = fundId;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getInvestmentManager() {
        return investmentManager;
    }

    public void setInvestmentManager(String investmentManager) {
        this.investmentManager = investmentManager;
    }

    public String getMainStrategy() {
        return mainStrategy;
    }

    public void setMainStrategy(String mainStrategy) {
        this.mainStrategy = mainStrategy;
    }

    public BaseDictionaryDto getCurrency() {
        return currency;
    }

    public void setCurrency(BaseDictionaryDto currency) {
        this.currency = currency;
    }

    public Double getFundAUM() {
        return fundAUM;
    }

    public void setFundAUM(Double fundAUM) {
        this.fundAUM = fundAUM;
    }

    public Date getFundAUMDate() {
        return fundAUMDate;
    }

    public void setFundAUMDate(Date fundAUMDate) {
        this.fundAUMDate = fundAUMDate;
    }

    public Double getFundAUMUSD() {
        return fundAUMUSD;
    }

    public void setFundAUMUSD(Double fundAUMUSD) {
        this.fundAUMUSD = fundAUMUSD;
    }

    public Double getStrategyAUM() {
        return strategyAUM;
    }

    public void setStrategyAUM(Double strategyAUM) {
        this.strategyAUM = strategyAUM;
    }

    public Double getManagerAUM() {
        return managerAUM;
    }

    public void setManagerAUM(Double managerAUM) {
        this.managerAUM = managerAUM;
    }

    public Double getEditedFundAUM() {
        return editedFundAUM;
    }

    public void setEditedFundAUM(Double editedFundAUM) {
        this.editedFundAUM = editedFundAUM;
    }

    public Date getEditedFundAUMDate() {
        return editedFundAUMDate;
    }

    public void setEditedFundAUMDate(Date editedFundAUMDate) {
        this.editedFundAUMDate = editedFundAUMDate;
    }

    public String getEditedFundAUMComment() {
        return editedFundAUMComment;
    }

    public void setEditedFundAUMComment(String editedFundAUMComment) {
        this.editedFundAUMComment = editedFundAUMComment;
    }

    public Double getRecentFundAUM() {
        return recentFundAUM;
    }

    public void setRecentFundAUM(Double recentFundAUM) {
        this.recentFundAUM = recentFundAUM;
    }

    public Date getRecentFundAUMDate() {
        return recentFundAUMDate;
    }

    public void setRecentFundAUMDate(Date recentFundAUMDate) {
        this.recentFundAUMDate = recentFundAUMDate;
    }

    public Date getRecentTrackRecordDate() {
        return recentTrackRecordDate;
    }

    public void setRecentTrackRecordDate(Date recentTrackRecordDate) {
        this.recentTrackRecordDate = recentTrackRecordDate;
    }

    public Double getAnnualizedReturn() {
        return annualizedReturn;
    }

    public void setAnnualizedReturn(Double annualizedReturn) {
        this.annualizedReturn = annualizedReturn;
    }

    public Double getSortino() {
        return sortino;
    }

    public void setSortino(Double sortino) {
        this.sortino = sortino;
    }

    public Double getBeta() {
        return beta;
    }

    public void setBeta(Double beta) {
        this.beta = beta;
    }

    public Double getAlpha() {
        return alpha;
    }

    public void setAlpha(Double alpha) {
        this.alpha = alpha;
    }

    public Double getOmega() {
        return omega;
    }

    public void setOmega(Double omega) {
        this.omega = omega;
    }

    public Double getCfVar() {
        return cfVar;
    }

    public void setCfVar(Double cfVar) {
        this.cfVar = cfVar;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public Boolean getStrategyAUMCheck() {
        return strategyAUMCheck;
    }

    public void setStrategyAUMCheck(Boolean strategyAUMCheck) {
        this.strategyAUMCheck = strategyAUMCheck;
    }

    public Boolean getManagerAUMCheck() {
        return managerAUMCheck;
    }

    public void setManagerAUMCheck(Boolean managerAUMCheck) {
        this.managerAUMCheck = managerAUMCheck;
    }

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

    public Double getActualStrategyAUM(){
        if(this.strategyAUM != null && this.strategyAUM.doubleValue() != 0){
            return strategyAUM;
        }
        if(this.editedFundAUM != null && this.editedFundAUM.doubleValue() != 0){
            return this.editedFundAUM;
        }
        if(this.fundAUMUSD != null && this.fundAUMUSD.doubleValue() != 0){
            return this.fundAUMUSD;
        }
        if((this.currency == null || this.currency.getCode().equalsIgnoreCase(CurrencyLookup.USD.getCode()))
                && this.fundAUM != null && this.fundAUM.doubleValue() != 0){
            return this.fundAUM;
        }
        return null;
    }
}
