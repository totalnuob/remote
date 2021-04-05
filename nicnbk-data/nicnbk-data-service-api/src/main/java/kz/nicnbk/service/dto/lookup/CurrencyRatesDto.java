package kz.nicnbk.service.dto.lookup;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.common.CurrencyRates;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by magzumov on 08.07.2016.
 */
public class CurrencyRatesDto extends CreateUpdateBaseEntityDto<CurrencyRates> {
    private Long id;
    private BaseDictionaryDto currency;

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date date;

    private Double value;
    private Double averageValue;
    private Double averageValueYear;
    private Double valueUSD;
    private Double quoteCurrencyValue;
    private String quoteCurrencyCode;

    private boolean editable;

    public CurrencyRatesDto(){}

    public CurrencyRatesDto(Long id, String currencyCode, Date date, Double value, Double averageValue, Double averageValueYear, boolean editable){
        this.id = id;
        if(this.currency == null){
            this.currency = new BaseDictionaryDto();
        }
        if(currency != null) {
            this.currency.setCode(currencyCode);
        }
        this.date = date;
        this.value = value;
        this.averageValue = averageValue;
        this.averageValueYear = averageValueYear;
        this.editable = editable;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public Double getValueUSD() {
        return valueUSD;
    }

    public void setValueUSD(Double valueUSD) {
        this.valueUSD = valueUSD;
    }

    public Double getQuoteCurrencyValue() {
        return quoteCurrencyValue;
    }

    public void setQuoteCurrencyValue(Double quoteCurrencyValue) {
        this.quoteCurrencyValue = quoteCurrencyValue;
    }

    public String getQuoteCurrencyCode() {
        return quoteCurrencyCode;
    }

    public void setQuoteCurrencyCode(String quoteCurrencyCode) {
        this.quoteCurrencyCode = quoteCurrencyCode;
    }
}
