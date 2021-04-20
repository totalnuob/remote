package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.common.service.util.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundScreeningParsedDataDto extends BaseEntityDto implements Comparable, Serializable {

    private HedgeFundScreeningDto screening;
    private Long filteredResultId;

    private Long fundId;
    private String fundName;
    private String investmentManager;
    private String mainStrategy;
    private String fundStatus;

    private Double fundAUM;
    private Date fundAUMDate;
    private String fundAUMComment;

    private Double managerAUM;
    private Double strategyAUM;

    private String currency;
    private Double fundAUMByCurrency;

    /**/
    private Double recentFundAUM;
    private Date recentFundAUMDate;

    private Double recentStrategyAUM;
    private Date recentStrategyAUMDate;

    private Date recentTrackRecordDate;
    private Boolean recentTrackRecordDateWithinLookback;
    private Boolean strategyAUMWithMissingCurrency;

    private Double editedFundAUM;
    private Date editedFundAUMDate;
    private String editedFundAUMDateMonthYear;
    private String editedFundAUMComment;

    private boolean added;

    private boolean excluded;
    private String excludeComment;
    private boolean excludeFromStrategyAUM;

    private List<HedgeFundScreeningFundReturnDto> returns;

    //private Boolean strategyAUMCheck;
    private Boolean fundAUMCheck;
    private Boolean managerAUMCheck;
    private Boolean trackRecordCheck;

    /* SCORING ***********************************************************/
    private Double annualizedReturn;
    private Double sortino;
    private Double beta;
    private Double alpha;
    private Double omega;
    private Double cfVar;
    private Double totalScore;

    public HedgeFundScreeningDto getScreening() {
        return screening;
    }

    public void setScreening(HedgeFundScreeningDto screening) {
        this.screening = screening;
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

    public Double getManagerAUM() {
        return managerAUM;
    }

    public void setManagerAUM(Double managerAUM) {
        this.managerAUM = managerAUM;
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

    public Boolean getRecentTrackRecordDateWithinLookback() {
        return recentTrackRecordDateWithinLookback;
    }

    public void setRecentTrackRecordDateWithinLookback(Boolean recentTrackRecordDateWithinLookback) {
        this.recentTrackRecordDateWithinLookback = recentTrackRecordDateWithinLookback;
    }

    public Double getStrategyAUM() {
        return strategyAUM;
    }

    public void setStrategyAUM(Double strategyAUM) {
        this.strategyAUM = strategyAUM;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getFundAUMByCurrency() {
        return fundAUMByCurrency;
    }

    public void setFundAUMByCurrency(Double fundAUMByCurrency) {
        this.fundAUMByCurrency = fundAUMByCurrency;
    }

    public Double getRecentStrategyAUM() {
        return recentStrategyAUM;
    }

    public void setRecentStrategyAUM(Double recentStrategyAUM) {
        this.recentStrategyAUM = recentStrategyAUM;
    }

    public Date getRecentStrategyAUMDate() {
        return recentStrategyAUMDate;
    }

    public void setRecentStrategyAUMDate(Date recentStrategyAUMDate) {
        this.recentStrategyAUMDate = recentStrategyAUMDate;
    }

    public Boolean getStrategyAUMWithMissingCurrency() {
        return strategyAUMWithMissingCurrency;
    }

    public void setStrategyAUMWithMissingCurrency(Boolean strategyAUMWithMissingCurrency) {
        this.strategyAUMWithMissingCurrency = strategyAUMWithMissingCurrency;
    }

    public boolean isEmpty(){
        return this.fundId == null && StringUtils.isEmpty(this.fundName) &&
                StringUtils.isEmpty(this.investmentManager) && StringUtils.isEmpty(this.mainStrategy) &&
                this.fundAUM == null && this.managerAUM == null;
    }

    public Double getEditedFundAUM() {
        return editedFundAUM;
    }

    public void setEditedFundAUM(Double editedFundAUM) {
        this.editedFundAUM = editedFundAUM;
    }

    public String getEditedFundAUMComment() {
        return editedFundAUMComment;
    }

    public void setEditedFundAUMComment(String editedFundAUMComment) {
        this.editedFundAUMComment = editedFundAUMComment;
    }

    public Date getEditedFundAUMDate() {
        return editedFundAUMDate;
    }

    public void setEditedFundAUMDate(Date editedFundAUMDate) {
        this.editedFundAUMDate = editedFundAUMDate;
    }

    public String getEditedFundAUMDateMonthYear() {
        return editedFundAUMDateMonthYear;
    }

    public void setEditedFundAUMDateMonthYear(String editedFundAUMDateMonthYear) {
        this.editedFundAUMDateMonthYear = editedFundAUMDateMonthYear;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public List<HedgeFundScreeningFundReturnDto> getReturns() {
        return returns;
    }

    public void setReturns(List<HedgeFundScreeningFundReturnDto> returns) {
        this.returns = returns;
    }

    public Long getFilteredResultId() {
        return filteredResultId;
    }

    public void setFilteredResultId(Long filteredResultId) {
        this.filteredResultId = filteredResultId;
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

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
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

    public boolean isExcluded() {
        return excluded;
    }

    public void setExcluded(boolean excluded) {
        this.excluded = excluded;
    }

    public String getExcludeComment() {
        return excludeComment;
    }

    public void setExcludeComment(String excludeComment) {
        this.excludeComment = excludeComment;
    }

    public String getFundStatus() {
        return fundStatus;
    }

    public void setFundStatus(String fundStatus) {
        this.fundStatus = fundStatus;
    }

    public String getFundAUMComment() {
        return fundAUMComment;
    }

    public void setFundAUMComment(String fundAUMComment) {
        this.fundAUMComment = fundAUMComment;
    }

    public boolean isExcludeFromStrategyAUM() {
        return excludeFromStrategyAUM;
    }

    public void setExcludeFromStrategyAUM(boolean excludeFromStrategyAUM) {
        this.excludeFromStrategyAUM = excludeFromStrategyAUM;
    }

//    public Boolean getStrategyAUMCheck() {
//        return strategyAUMCheck;
//    }
//
//    public void setStrategyAUMCheck(Boolean strategyAUMCheck) {
//        this.strategyAUMCheck = strategyAUMCheck;
//    }


    public Boolean getFundAUMCheck() {
        return fundAUMCheck;
    }

    public void setFundAUMCheck(Boolean fundAUMCheck) {
        this.fundAUMCheck = fundAUMCheck;
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

    @Override
    public int compareTo(Object o) {
        if(this.totalScore == null){
            //return -1;
            return this.fundName == null ? -1 :
                    this.fundName.toLowerCase().compareTo(((HedgeFundScreeningParsedDataDto) o).fundName.toLowerCase());
        }
        if(((HedgeFundScreeningParsedDataDto) o).totalScore == null){
            //return 1;
            return this.fundName == null ? 1 :
                    this.fundName.toLowerCase().compareTo(((HedgeFundScreeningParsedDataDto) o).fundName.toLowerCase());
        }
        if(this.totalScore.doubleValue() == ((HedgeFundScreeningParsedDataDto) o).totalScore.doubleValue()){
            return this.fundName.toLowerCase().compareTo(((HedgeFundScreeningParsedDataDto) o).fundName.toLowerCase());
        }
        return this.totalScore.compareTo(((HedgeFundScreeningParsedDataDto) o).totalScore) * (-1);
    }

//    @Override
//    public int compareTo(Object o) {
//        return this.fundName.toLowerCase().compareTo(((HedgeFundScreeningParsedDataDto) o).fundName.toLowerCase());
//    }
}


