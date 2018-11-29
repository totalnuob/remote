package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreeningParsedUcitsData;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface HedgeFundScreeningParsedUcitsDataRepository extends PagingAndSortingRepository<HedgeFundScreeningParsedUcitsData, Long> {

    List<HedgeFundScreeningParsedUcitsData> findByScreeningId(Long screeningId, Sort sorting);
//
//    Long countByScreeningId(Long screeningId);
//
    @Modifying
    @Transactional
    @Query("DELETE from HedgeFundScreeningParsedUcitsData e WHERE e.screening.id=?1")
    void deleteByScreeningId(Long screeningId);
//
//    HedgeFundScreeningParsedUcitsData findByFundIdAndScreeningId(Long fundId, Long screeningId);
}