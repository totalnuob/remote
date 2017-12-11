package kz.nicnbk.service.api.common;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.CurrencyRatesDto;

import java.util.Date;

/**
 * Created by magzumov on 20.11.2017.
 */
public interface CurrencyRatesService extends BaseService {

    CurrencyRatesDto getRateForDateAndCurrency(Date date, String currencyCode);

    Double getAverageRateForDateAndCurrency(Date date, String currencyCode);

    boolean save(CurrencyRatesDto dto);
}
