package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreeningParsedDataAUM;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


public interface HedgeFundScreeningParsedDataAUMRepository extends PagingAndSortingRepository<HedgeFundScreeningParsedDataAUM, Long> {

    List<HedgeFundScreeningParsedDataAUM> findByScreeningId(Long screeningId, Sort sorting);

    @Modifying
    @Transactional
    @Query("DELETE from HedgeFundScreeningParsedDataAUM e WHERE e.screening.id=?1")
    void deleteByScreeningId(Long screeningId);

    @Query("SELECT e FROM HedgeFundScreeningParsedDataAUM e WHERE e.screening.id=?1 AND " +
            " e.date >= ?2 AND e.date <= ?3 ")
    List<HedgeFundScreeningParsedDataAUM> findByScreeningIdAndDateRange(Long screeningId, Date dateFrom, Date dateTo, Sort sorting);

    @Query("SELECT max(e.date) FROM HedgeFundScreeningParsedDataAUM e WHERE e.screening.id=?1")
    Date getMaxDate(Long screeningId);

    @Query("SELECT e FROM HedgeFundScreeningParsedDataAUM e WHERE e.screening.id=?1 AND e.date >= ?2 AND e.date <= ?3 " +
            " AND e.value is not null AND e.date=(SELECT MAX(e2.date) from HedgeFundScreeningParsedDataAUM e2 WHERE e2.value is not null AND " +
            "e2.screening.id=?1 AND e2.date >= ?2 AND e2.date <= ?3 AND e.fundId=e2.fundId)")
    List<HedgeFundScreeningParsedDataAUM> getLastAUMByFundId(Long screeningId, Date dateFrom, Date dateTo);
}