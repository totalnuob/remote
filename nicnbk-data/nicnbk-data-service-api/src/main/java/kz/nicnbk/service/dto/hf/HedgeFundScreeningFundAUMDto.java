package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.Date;

/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundScreeningFundAUMDto implements BaseDto {

    private Long fundId;
    private String fundName;
    private Date fundAUMDate;
    private Double fundAUM;
    private Double strategyAUM;
    private Double managerAUM;

    private String investmentManager;
    private String mainStrategy;

    private String fundAUMCurrency;
    private Double fundAUMValueUSD;

    private boolean strategyAUMWithMissingCurrency;

    private Double editedFundAUM;
    private Date editedFundAUMDate;
    private String editedFundAUMComment;

    private boolean addedFund;

    public HedgeFundScreeningFundAUMDto(){}

    public HedgeFundScreeningFundAUMDto(Long fundId, Double fundAUM, Double strategyAUM, Double managerAUM,
                                        String fundAUMCurrency, Double fundAUMValueUSD, Date fundAUMDate,
                                        Double editedFundAUM, Date editedFundAUMDate, String editedFundAUMComment, boolean strategyAUMWithMissingCurrency){
        this.fundId = fundId;
        this.fundAUM = fundAUM;
        this.strategyAUM = strategyAUM;
        this.managerAUM = managerAUM;
        this.fundAUMCurrency = fundAUMCurrency;
        this.fundAUMValueUSD = fundAUMValueUSD;
        this.fundAUMDate = fundAUMDate;
        this.editedFundAUM = editedFundAUM;
        this.editedFundAUMDate = editedFundAUMDate;
        this.editedFundAUMComment = editedFundAUMComment;
        this.strategyAUMWithMissingCurrency = strategyAUMWithMissingCurrency;
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

    public Date getFundAUMDate() {
        return fundAUMDate;
    }

    public void setFundAUMDate(Date fundAUMDate) {
        this.fundAUMDate = fundAUMDate;
    }

    public Double getFundAUM() {
        return fundAUM;
    }

    public void setFundAUM(Double fundAUM) {
        this.fundAUM = fundAUM;
    }

    public String getFundAUMCurrency() {
        return fundAUMCurrency;
    }

    public void setFundAUMCurrency(String fundAUMCurrency) {
        this.fundAUMCurrency = fundAUMCurrency;
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

    public Double getFundAUMValueUSD() {
        return fundAUMValueUSD;
    }

    public void setFundAUMValueUSD(Double fundAUMValueUSD) {
        this.fundAUMValueUSD = fundAUMValueUSD;
    }

    public boolean isStrategyAUMWithMissingCurrency() {
        return strategyAUMWithMissingCurrency;
    }

    public void setStrategyAUMWithMissingCurrency(boolean strategyAUMWithMissingCurrency) {
        this.strategyAUMWithMissingCurrency = strategyAUMWithMissingCurrency;
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

    public boolean isAddedFund() {
        return addedFund;
    }

    public void setAddedFund(boolean addedFund) {
        this.addedFund = addedFund;
    }

    public String getMainStrategy() {
        return mainStrategy;
    }

    public void setMainStrategy(String mainStrategy) {
        this.mainStrategy = mainStrategy;
    }

    public String getInvestmentManager() {
        return investmentManager;
    }

    public void setInvestmentManager(String investmentManager) {
        this.investmentManager = investmentManager;
    }
}


