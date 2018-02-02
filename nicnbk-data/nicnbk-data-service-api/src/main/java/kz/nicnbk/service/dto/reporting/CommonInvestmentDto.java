package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by magzumov on 12.05.2017.
 */
@Deprecated
public class CommonInvestmentDto implements BaseDto {

    private String investmentName;
    private Double capitalCommitments;
    private Double netCost;
    private Double fairValue;
    private String currency;

    public CommonInvestmentDto(){}

    public CommonInvestmentDto(String investmentName, Double capitalCommitments, Double netCost, Double fairValue, String currency){
        this.investmentName = investmentName;
        this.capitalCommitments = capitalCommitments;
        this.netCost = netCost;
        this.fairValue = fairValue;
        this.currency = currency;
    }

    public String getInvestmentName() {
        return investmentName;
    }

    public void setInvestmentName(String investmentName) {
        this.investmentName = investmentName;
    }

    public Double getCapitalCommitments() {
        return capitalCommitments;
    }

    public void setCapitalCommitments(Double capitalCommitments) {
        this.capitalCommitments = capitalCommitments;
    }

    public Double getNetCost() {
        return netCost;
    }

    public void setNetCost(Double netCost) {
        this.netCost = netCost;
    }

    public Double getFairValue() {
        return fairValue;
    }

    public void setFairValue(Double fairValue) {
        this.fairValue = fairValue;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


}
