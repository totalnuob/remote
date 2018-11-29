package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.common.service.util.StringUtils;

import java.util.Date;


/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundScreeningParsedUcitsDataDto extends BaseEntityDto implements Comparable {

    private HedgeFundScreeningDto screening;

    private Long fundId;
    private String fundName;
    private String investmentManager;
    private String mainStrategy;

    private Double fundAUM;
    private Double managerAUM;
    private Double strategyAUM;

    private String currency;
    private Double fundAUMByCurrency;

    private Double recentFundAUM;
    private Date recentFundAUMDate;

    private Date recentTrackRecordDate;
    private Boolean recentTrackRecordDateWithinLookback;

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

    public boolean isEmpty(){
        return this.fundId == null && StringUtils.isEmpty(this.fundName) &&
                StringUtils.isEmpty(this.investmentManager) && StringUtils.isEmpty(this.mainStrategy) &&
                this.fundAUM == null && this.managerAUM == null;
    }

    @Override
    public int compareTo(Object o) {
        return this.fundId.compareTo(((HedgeFundScreeningParsedUcitsDataDto) o).fundId);
    }
}


