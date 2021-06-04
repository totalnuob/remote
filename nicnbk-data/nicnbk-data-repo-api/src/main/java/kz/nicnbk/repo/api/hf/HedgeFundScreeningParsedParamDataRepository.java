package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreeningParsedParamData;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface HedgeFundScreeningParsedParamDataRepository extends PagingAndSortingRepository<HedgeFundScreeningParsedParamData, Long> {

    List<HedgeFundScreeningParsedParamData> findByScreeningId(Long screeningId, Sort sorting);

    @Modifying
    @Transactional
    @Query("DELETE from HedgeFundScreeningParsedParamData e WHERE e.screening.id=?1")
    void deleteByScreeningId(Long screeningId);

    List<HedgeFundScreeningParsedParamData> findByFundNameAndScreeningId(String fundName, Long screeningId);
}