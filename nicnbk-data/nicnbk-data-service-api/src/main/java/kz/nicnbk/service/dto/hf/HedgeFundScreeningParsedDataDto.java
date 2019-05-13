package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.base.BaseEntity;

import java.io.Serializable;
import java.util.Date;


/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundScreeningParsedDataDto extends BaseEntityDto implements Comparable, Serializable {

    private HedgeFundScreeningDto screening;

    private Long fundId;
    private String fundName;
    private String investmentManager;
    private String mainStrategy;
    private Double fundAUM;
    private Date fundAUMDate;

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
    private String editedFundAUMComment;

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

    @Override
    public int compareTo(Object o) {
        return this.fundId.compareTo(((HedgeFundScreeningParsedDataDto) o).fundId);
    }
}


