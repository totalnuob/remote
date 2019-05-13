package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.Date;

/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundScreeningFundAUMDto implements BaseDto {

    private Long fundId;
    private Date fundAUMDate;
    private Double fundAUM;
    private Double strategyAUM;
    private Double managerAUM;

    private String fundAUMCurrency;
    private Double fundAUMValueUSD;

    private boolean strategyAUMWithMissingCurrency;

    public HedgeFundScreeningFundAUMDto(){}

    public HedgeFundScreeningFundAUMDto(Long fundId, Double fundAUM, Double strategyAUM, Double managerAUM,
                                        String fundAUMCurrency, Double fundAUMValueUSD, Date fundAUMDate, boolean strategyAUMWithMissingCurrency){
        this.fundId = fundId;
        this.fundAUM = fundAUM;
        this.strategyAUM = strategyAUM;
        this.managerAUM = managerAUM;
        this.fundAUMCurrency = fundAUMCurrency;
        this.fundAUMValueUSD = fundAUMValueUSD;
        this.fundAUMDate = fundAUMDate;
        this.strategyAUMWithMissingCurrency = strategyAUMWithMissingCurrency;
    }

    public Long getFundId() {
        return fundId;
    }

    public void setFundId(Long fundId) {
        this.fundId = fundId;
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
}


