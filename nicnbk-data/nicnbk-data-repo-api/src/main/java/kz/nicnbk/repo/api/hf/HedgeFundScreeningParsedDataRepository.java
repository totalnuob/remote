package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreeningParsedData;
import org.junit.internal.requests.SortingRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface HedgeFundScreeningParsedDataRepository extends PagingAndSortingRepository<HedgeFundScreeningParsedData, Long> {

    List<HedgeFundScreeningParsedData> findByScreeningId(Long screeningId, Sort sorting);

    @Modifying
    @Transactional
    @Query("DELETE from HedgeFundScreeningParsedData e WHERE e.screening.id=?1")
    void deleteByScreeningId(Long screeningId);

    HedgeFundScreeningParsedData findByFundId(Long fundId);
}