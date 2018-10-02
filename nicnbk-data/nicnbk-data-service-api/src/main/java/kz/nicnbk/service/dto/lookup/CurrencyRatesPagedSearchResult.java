package kz.nicnbk.service.dto.lookup;

import kz.nicnbk.common.service.model.PageableResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 18.07.2016.
 */
public class CurrencyRatesPagedSearchResult extends PageableResult {

    private List<CurrencyRatesDto> currencyRates;
    private String searchParams;

    public List<CurrencyRatesDto> getCurrencyRates() {
        return currencyRates;
    }

    public void setCurrencyRates(List<CurrencyRatesDto> currencyRates) {
        this.currencyRates = currencyRates;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }

    public void add(CurrencyRatesDto ratesDto){
        if(this.currencyRates == null){
            this.currencyRates = new ArrayList<>();
        }
        this.currencyRates.add(ratesDto);
    }
}
