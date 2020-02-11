package kz.nicnbk.service.dto.hf;
import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;

import java.util.Date;


public class HedgeFundScreeningSavedResultCurrencyDto extends CreateUpdateBaseEntityDto {

    private HedgeFundScreeningSavedResultsDto savedResults;

    private BaseDictionaryDto currency;
    private Date date;
    private Double value; // KZT value
    private Double averageValue;
    private Double averageValueYear;
    private Double valueUSD;

    public HedgeFundScreeningSavedResultsDto getSavedResults() {
        return savedResults;
    }

    public void setSavedResults(HedgeFundScreeningSavedResultsDto savedResults) {
        this.savedResults = savedResults;
    }

    public BaseDictionaryDto getCurrency() {
        return currency;
    }

    public void setCurrency(BaseDictionaryDto currency) {
        this.currency = currency;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getAverageValue() {
        return averageValue;
    }

    public void setAverageValue(Double averageValue) {
        this.averageValue = averageValue;
    }

    public Double getAverageValueYear() {
        return averageValueYear;
    }

    public void setAverageValueYear(Double averageValueYear) {
        this.averageValueYear = averageValueYear;
    }

    public Double getValueUSD() {
        return valueUSD;
    }

    public void setValueUSD(Double valueUSD) {
        this.valueUSD = valueUSD;
    }
}
