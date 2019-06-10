package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreeningParsedData;
import org.junit.internal.requests.SortingRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


public interface HedgeFundScreeningParsedDataRepository extends PagingAndSortingRepository<HedgeFundScreeningParsedData, Long> {

    List<HedgeFundScreeningParsedData> findByScreeningId(Long screeningId, Sort sorting);

    Long countByScreeningId(Long screeningId);

    @Modifying
    @Transactional
    @Query("DELETE from HedgeFundScreeningParsedData e WHERE e.screening.id=?1")
    void deleteByScreeningId(Long screeningId);

    HedgeFundScreeningParsedData findByFundIdAndScreeningId(Long fundId, Long screeningId);

    List<HedgeFundScreeningParsedData> findByFundNameAndScreeningId(String fundName, Long screeningId);

    @Query("SELECT e FROM HedgeFundScreeningParsedData e WHERE e.screening.id=?1 AND e.excluded is not null AND e.excluded=true")
    List<HedgeFundScreeningParsedData> findExcludedFundsByScreeningId(Long screeningId);
}