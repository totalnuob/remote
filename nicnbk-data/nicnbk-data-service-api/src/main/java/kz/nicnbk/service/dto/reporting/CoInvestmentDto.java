package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by magzumov on 12.05.2017.
 */
@Deprecated
public class CoInvestmentDto extends CommonInvestmentDto {

    private String description;

    public  CoInvestmentDto(){}

    public  CoInvestmentDto(String investmentName, String description, Double capitalCommitments, Double netCost, Double fairValue,
                            String currency){
        super(investmentName, capitalCommitments, netCost, fairValue, currency);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
