package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreeningFundCounts;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningParsedDataReturn;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


public interface HedgeFundScreeningParsedDataReturnRepository extends PagingAndSortingRepository<HedgeFundScreeningParsedDataReturn, Long> {

    List<HedgeFundScreeningParsedDataReturn> findByScreeningId(Long screeningId, Sort sorting);

    @Modifying
    @Transactional
    @Query("DELETE from HedgeFundScreeningParsedDataReturn e WHERE e.screening.id=?1")
    void deleteByScreeningId(Long screeningId);

    @Query("SELECT e FROM HedgeFundScreeningParsedDataReturn e WHERE e.screening.id=?1 AND " +
            " e.date >= ?2 AND e.date <= ?3 ORDER BY e.fundId")
    List<HedgeFundScreeningParsedDataReturn> findByScreeningIdAndDateRange(Long screeningId, Date dateFrom, Date dateTo);

    @Query("SELECT max(e.date) FROM HedgeFundScreeningParsedDataReturn e WHERE e.screening.id=?1")
    Date getMaxDate(Long screeningId);

//    @Query("SELECT new kz.nicnbk.repo.model.hf.HedgeFundScreeningFundCounts(e.fundId, COUNT(e)) " +
//            " FROM HedgeFundScreeningParsedDataReturn e WHERE e.screening.id=?1 AND " +
//            " e.date >= ?2 AND e.date <= ?3 GROUP BY e.fundId")
//    List<HedgeFundScreeningFundCounts> getFundIdCounts(Long screeningId, Date dateFrom, Date dateTo);

    List<HedgeFundScreeningParsedDataReturn> findByScreeningIdAndFundId(Long screeningId, Long fundId, Sort sorting);

    @Query("SELECT e FROM HedgeFundScreeningParsedDataReturn e WHERE e.screening.id=?1 AND e.fundId=?2 AND e.date >= ?3 AND e.date <= ?4")
    List<HedgeFundScreeningParsedDataReturn> findByScreeningIdAndFundIdDateRange(Long screeningId, Long fundId, Date dateFrom, Date dateTo, Sort sorting);

    @Query("SELECT DISTINCT e.returnsCurrency FROM HedgeFundScreeningParsedDataReturn e WHERE e.screening.id=?1")
    List<String> getUniqueCurrencies(Long screeningId);


}