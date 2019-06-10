package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreeningAddedFundReturn;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


public interface HedgeFundScreeningAddedFundReturnRepository extends PagingAndSortingRepository<HedgeFundScreeningAddedFundReturn, Long> {

    @Modifying
    @Transactional
    @Query("DELETE from HedgeFundScreeningAddedFundReturn e WHERE e.addedFund.id=?1")
    void deleteByFundId(Long fundId);

    @Query("SELECT e FROM HedgeFundScreeningAddedFundReturn e WHERE e.addedFund.id=?1")
    List<HedgeFundScreeningAddedFundReturn> findByFundId(Long fundId, Sort sort);

    @Query("SELECT e FROM HedgeFundScreeningAddedFundReturn e WHERE e.addedFund.filteredResult.id=?1 AND e.addedFund.fundName=?2" +
            " AND e.date >= ?3 AND e.date <=?4")
    List<HedgeFundScreeningAddedFundReturn> findByFilteredResultIdAndFundName(Long filteredResultId, String fundName, Date dateFrom , Date dateTo, Sort sort);
}