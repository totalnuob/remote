package kz.nicnbk.repo.api.common;

import kz.nicnbk.repo.model.common.CurrencyRates;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface CurrencyRatesRepository extends PagingAndSortingRepository<CurrencyRates, Long> {

    @Query("SELECT entity FROM CurrencyRates entity WHERE entity.date = ?1 AND entity.currency.code = ?2")
    CurrencyRates getRateForDateAndCurrency(@Param("date") @Temporal(TemporalType.DATE) Date date,
                                           @Param("currencyCode") String currencyCode);

    @Query("SELECT entity FROM CurrencyRates entity WHERE entity.date > ?1 AND entity.currency.code = ?2")
    List<CurrencyRates> getRatesAfterDateAndCurrency(@Param("date") @Temporal(TemporalType.DATE) Date date,
                                                  @Param("currencyCode") String currencyCode);
}
