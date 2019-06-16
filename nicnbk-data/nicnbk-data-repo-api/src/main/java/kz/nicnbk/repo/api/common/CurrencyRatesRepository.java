package kz.nicnbk.repo.api.common;

import kz.nicnbk.repo.model.common.CurrencyRates;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT entity FROM CurrencyRates entity WHERE entity.date >= ?1 AND entity.date <= ?2 AND entity.currency.code = ?3")
    List<CurrencyRates> getRatesAfterDateAndCurrency(@Param("dateFrom") @Temporal(TemporalType.DATE) Date dateFrom,
                                                     @Param("dateTo") @Temporal(TemporalType.DATE) Date dateTo,
                                                  @Param("currencyCode") String currencyCode);

    @Query("SELECT entity FROM CurrencyRates entity WHERE entity.date >= ?1 AND entity.date <= ?2 AND entity.currency.code = ?3 AND entity.averageValue IS NOT NULL")
    List<CurrencyRates> getAverageRatesAfterDateAndCurrency(@Param("dateFrom") @Temporal(TemporalType.DATE) Date dateFrom,
                                                     @Param("dateTo") @Temporal(TemporalType.DATE) Date dateTo,
                                                     @Param("currencyCode") String currencyCode);


    // Date parameters are required
    @Query("select e from CurrencyRates e where " +
            " (e.date >= ?1 AND e.date <= ?2)" +
            " ORDER BY e.date DESC")
    Page<CurrencyRates> search(@Temporal(TemporalType.DATE) Date dateFrom,@Temporal(TemporalType.DATE) Date dateTo,Pageable pageable);

    @Query("select e from CurrencyRates e where " +
            " (?1 is null OR e.currency.code = ?1) AND (e.date >= ?2 AND e.date <= ?3)" +
            " ORDER BY e.date DESC")
    Page<CurrencyRates> search(@Param("currencyCode") String currencyCode, @Param("dateFrom") @Temporal(TemporalType.DATE) Date dateFrom,
                               @Param("dateTo") @Temporal(TemporalType.DATE) Date dateTo,Pageable pageable);
}
