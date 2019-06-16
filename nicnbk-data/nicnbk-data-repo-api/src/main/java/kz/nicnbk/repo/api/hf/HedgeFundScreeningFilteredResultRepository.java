package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreening;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningFilteredResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

public interface HedgeFundScreeningFilteredResultRepository extends PagingAndSortingRepository<HedgeFundScreeningFilteredResult, Long> {


    List<HedgeFundScreeningFilteredResult> findByScreeningId(Long screeningId);

    @Modifying
    @Transactional
    @Query("DELETE from HedgeFundScreeningFilteredResult e WHERE e.screening.id=?1")
    void deleteByScreeningId(Long screeningId);
}