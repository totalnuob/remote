package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreeningAddedFund;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface HedgeFundScreeningAddedFundRepository extends PagingAndSortingRepository<HedgeFundScreeningAddedFund, Long> {

    List<HedgeFundScreeningAddedFund> findByFilteredResultId(Long filteredResultId, Sort sorting);

    @Modifying
    @Transactional
    @Query("DELETE from HedgeFundScreeningAddedFund e WHERE e.filteredResult.id=?1")
    void deleteByFilteredResultId(Long filteredResultId);

    @Modifying
    @Transactional
    @Query("DELETE from HedgeFundScreeningAddedFund e WHERE e.fundName=?1 AND e.filteredResult.id=?2")
    void deleteByFundNameAndFilteredResultId(String fundName, Long filteredResultId);

    HedgeFundScreeningAddedFund findByFundNameAndFilteredResultId(String fundName, Long filteredResultId);
}