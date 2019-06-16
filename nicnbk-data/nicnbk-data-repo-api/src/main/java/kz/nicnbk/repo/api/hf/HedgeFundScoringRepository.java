package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScoring;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.TemporalType;
import java.util.Date;

public interface HedgeFundScoringRepository extends PagingAndSortingRepository<HedgeFundScoring, Long> {

    @Query("select DISTINCT e from HedgeFundScoring e where " +
            " (:searchText='' OR (LOWER(e.filteredResult.screening.name) LIKE CONCAT('%', :searchText, '%')) " +
            " or (LOWER(e.filteredResult.screening.description) LIKE CONCAT('%', :searchText,'%'))) " +
            " and (e.date is null OR (e.date >= :dateFrom AND e.date <= :dateTo)) " )
    Page<HedgeFundScoring> search(@Param("dateFrom") @Temporal(TemporalType.DATE) Date dateFrom,
                                    @Param("dateTo") @Temporal(TemporalType.DATE) Date dateTo,
                                    @Param("searchText") String searchText, Pageable pageable);
}