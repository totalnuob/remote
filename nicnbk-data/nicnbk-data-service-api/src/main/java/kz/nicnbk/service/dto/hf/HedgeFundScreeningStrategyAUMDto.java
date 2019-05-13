package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.util.MathUtils;

import java.util.Date;

/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundScreeningStrategyAUMDto implements BaseDto {

    private String investorName;
    private Double value;
    private String currency;
    private boolean hasMissingCurrencyRates;

    public HedgeFundScreeningStrategyAUMDto(){}

    public HedgeFundScreeningStrategyAUMDto(String investorName, Double value, String currency, boolean hasMissingCurrencyRates){
        this.investorName = investorName;
        this.value = value;
        this.currency = currency;
        this.hasMissingCurrencyRates = hasMissingCurrencyRates;
    }

    public String getInvestorName() {
        return investorName;
    }

    public void setInvestorName(String investorName) {
        this.investorName = investorName;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void addValue(Double value){
        this.value = MathUtils.add(this.value, value);
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isHasMissingCurrencyRates() {
        return hasMissingCurrencyRates;
    }

    public void setHasMissingCurrencyRates(boolean hasMissingCurrencyRates) {
        this.hasMissingCurrencyRates = hasMissingCurrencyRates;
    }
}


