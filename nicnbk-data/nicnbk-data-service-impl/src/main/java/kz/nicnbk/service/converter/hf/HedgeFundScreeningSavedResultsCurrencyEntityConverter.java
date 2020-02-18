package kz.nicnbk.service.converter.hf;

import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.CurrencyRates;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningSavedResultFunds;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningSavedResults;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningSavedResultsCurrency;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.hf.HedgeFundScreeningParsedDataDto;
import kz.nicnbk.service.dto.hf.HedgeFundScreeningSavedResultCurrencyDto;
import kz.nicnbk.service.dto.hf.HedgeFundScreeningSavedResultFundsDto;
import kz.nicnbk.service.dto.lookup.CurrencyRatesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HedgeFundScreeningSavedResultsCurrencyEntityConverter extends BaseDozerEntityConverter<HedgeFundScreeningSavedResultsCurrency, HedgeFundScreeningSavedResultCurrencyDto> {

    @Autowired
    private LookupService lookupService;

    public HedgeFundScreeningSavedResultsCurrency assemble(CurrencyRatesDto currencyRatesDto, Long savedResultId){
        HedgeFundScreeningSavedResultsCurrency entity = new HedgeFundScreeningSavedResultsCurrency();
        entity.setSavedResults(new HedgeFundScreeningSavedResults(savedResultId));
        Currency foundCurrency = this.lookupService.findByTypeAndCode(Currency.class, currencyRatesDto.getCurrency().getCode());
        if(foundCurrency != null) {
            Currency currency = new Currency();
            currency.setId(foundCurrency.getId());
            entity.setCurrency(currency);
        }
        entity.setDate(currencyRatesDto.getDate());
        entity.setValue(currencyRatesDto.getValue());
        entity.setValueUSD(currencyRatesDto.getValueUSD());
        entity.setAverageValue(currencyRatesDto.getAverageValue());
        entity.setAverageValueYear(currencyRatesDto.getAverageValueYear());
        return  entity;
    }

    public List<HedgeFundScreeningSavedResultsCurrency> assembleListFromCurrencyRates(List<CurrencyRatesDto> currencyRatesList, Long savedResultId){
        List<HedgeFundScreeningSavedResultsCurrency> entities = new ArrayList<>();
        if(currencyRatesList != null){
            for(CurrencyRatesDto currencyRate: currencyRatesList){
                entities.add(assemble(currencyRate, savedResultId));
            }
        }
        return entities;
    }
}
