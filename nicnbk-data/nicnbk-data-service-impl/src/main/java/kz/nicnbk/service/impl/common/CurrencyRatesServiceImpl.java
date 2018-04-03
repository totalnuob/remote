package kz.nicnbk.service.impl.common;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.MathUtils;
import kz.nicnbk.repo.api.common.CurrencyRatesRepository;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.CurrencyRates;
import kz.nicnbk.service.api.common.CurrencyRatesService;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.lookup.CurrencyRatesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 20.11.2017.
 */

@Service
public class CurrencyRatesServiceImpl implements CurrencyRatesService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyRatesServiceImpl.class);

    @Autowired
    private CurrencyRatesRepository currencyRatesRepository;

    @Autowired
    private LookupService lookupService;


    @Override
    public CurrencyRatesDto getRateForDateAndCurrency(Date date, String currencyCode){
        Date dateFormatted = DateUtils.getDateOnly(date);
        CurrencyRates entity = this.currencyRatesRepository.getRateForDateAndCurrency(dateFormatted, currencyCode);
        if(entity != null){
            CurrencyRatesDto dto = new CurrencyRatesDto();
            dto.setDate(entity.getDate());
            dto.setValue(entity.getValue());
            dto.setAverageValue(entity.getAverageValue());
            return dto;
        }
        return null;
    }

    @Override
    public Double getAverageRateForFixedDateAndCurrency(Date date, String currencyCode){
        Date dateFormatted = DateUtils.getDateOnly(date);

        CurrencyRates rates = this.currencyRatesRepository.getRateForDateAndCurrency(dateFormatted, currencyCode);

        if(rates != null && rates.getAverageValueYear() != null) {
            return rates.getAverageValueYear();
        }else{
            String errorMessage = "Average USD rate for date " + DateUtils.getDateFormatted(date) +" not found.";
            logger.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
    }

    @Override
    public Double getAverageRateForAllMonthsBeforeDateAndCurrency(Date date, String currencyCode, int scale) {
        Date dateFormatted = DateUtils.getDateOnly(date);
        Date firstDay = DateUtils.getFirstDayOfDateYear(dateFormatted);

        Date dateTo = DateUtils.getFirstDayOfNextMonth(date);

        List<CurrencyRates> rates = this.currencyRatesRepository.getAverageRatesAfterDateAndCurrency(firstDay, dateTo, currencyCode);
        int months = DateUtils.getMonthsDifference(firstDay, dateTo);

        if(rates != null){
            //BigDecimal sum = new BigDecimal("0");
            Double sum = 0.0;
            int count = 0;
            for(CurrencyRates rate: rates){
                if(rate.getDate().compareTo(dateFormatted) <= 0 && rate.getAverageValue() != null){
                    sum  = MathUtils.add(sum, rate.getAverageValue().doubleValue());
                    count++;
                }
            }
            if(months != count){
                String errorMessage = "Average Monthly USD rate calculation error on date " + DateUtils.getDateFormatted(date) +
                        " (since " + DateUtils.getDateFormatted(firstDay) + ") : expected rate records for " + months + " months, found " + count + " rate records";
                logger.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
            if(count > 0) {
                return MathUtils.divide(sum, count + 0.0).doubleValue();
            }
        }
        return null;
    }

    @Override
    public boolean save(CurrencyRatesDto dto) {
        try {
            if (dto != null) {
                CurrencyRates entity = new CurrencyRates();
                entity.setDate(dto.getDate());
                if (dto.getCurrency() != null && dto.getCurrency().getCode() != null) {
                    Currency currency = this.lookupService.findByTypeAndCode(Currency.class, dto.getCurrency().getCode());
                    entity.setCurrency(currency);
                }
                entity.setValue(dto.getValue());
                entity.setAverageValue(dto.getAverageValue());

                this.currencyRatesRepository.save(entity);
            }
            return true;
        }catch (Exception ex){
            logger.error("Error saving currency rate", ex);
            return false;
        }
    }

}
