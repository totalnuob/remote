package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.Date;

/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundScreeningStrategyAUMDto implements BaseDto {

    private String investorName;
    private Double value;
    private String currency;
    private boolean hasMissingCurrencyRates;

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


