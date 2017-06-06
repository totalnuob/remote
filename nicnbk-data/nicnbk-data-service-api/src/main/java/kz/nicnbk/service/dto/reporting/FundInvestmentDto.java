package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by magzumov on 12.05.2017.
 */
public class FundInvestmentDto extends CommonInvestmentDto{

    private String strategy;

    public FundInvestmentDto(){}

    public FundInvestmentDto(String investmentName, Double capitalCommitments, Double netCost, Double fairValue,
                             String currency, String strategy){
        super(investmentName, capitalCommitments, netCost, fairValue, currency);
        this.strategy = strategy;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }
}
