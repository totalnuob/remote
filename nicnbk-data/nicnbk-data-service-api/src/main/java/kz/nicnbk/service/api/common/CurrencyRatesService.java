package kz.nicnbk.service.api.common;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.EntityListSaveResponseDto;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.lookup.CurrencyRatesDto;
import kz.nicnbk.service.dto.lookup.CurrencyRatesPagedSearchResult;
import kz.nicnbk.service.dto.lookup.CurrencyRatesSearchParams;

import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 20.11.2017.
 */
public interface CurrencyRatesService extends BaseService {

    CurrencyRatesDto getRateForDateAndCurrency(Date date, String currencyCode);

    CurrencyRatesDto getLstRateForMonthDateAndCurrencyBackwards(Date date, String currencyCode);

    Double getAverageRateForAllMonthsBeforeDateAndCurrency(Date date, String currencyCode, int scale);

    Double getAverageYearRateForFixedDateAndCurrency(Date date, String currencyCode);

    CurrencyRatesPagedSearchResult search(CurrencyRatesSearchParams searchParams);

    EntitySaveResponseDto save(CurrencyRatesDto dto, String username);

    EntityListSaveResponseDto save(List<CurrencyRatesDto> dtoList, String username);

    boolean delete(Long id, String username);

    Double getUSDValueRateForDateAndCurrency(Date date, String currencyCode);
}
