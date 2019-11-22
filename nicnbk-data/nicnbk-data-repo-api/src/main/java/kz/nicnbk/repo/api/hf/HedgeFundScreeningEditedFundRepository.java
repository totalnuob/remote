package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreeningEditedFund;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface HedgeFundScreeningEditedFundRepository extends PagingAndSortingRepository<HedgeFundScreeningEditedFund, Long> {

    @Query("SELECT e FROM HedgeFundScreeningEditedFund e WHERE e.filteredResult.id=?1 AND (e.excluded=?2 OR (?2=false AND e.excluded IS NULL))")
    List<HedgeFundScreeningEditedFund> findByFilteredResultIdAndExcluded(Long filteredResultId, boolean excluded);

    @Query("SELECT e FROM HedgeFundScreeningEditedFund e WHERE e.filteredResult.id=?1 AND e.excluded=false ")
    List<HedgeFundScreeningEditedFund> findIncludedByFilteredResultId(Long filteredResultId);

    @Query("SELECT e FROM HedgeFundScreeningEditedFund e WHERE e.filteredResult.id=?1")
    List<HedgeFundScreeningEditedFund> findAllByFilteredResultId(Long filteredResultId);

    @Modifying
    @Transactional
    @Query("DELETE from HedgeFundScreeningEditedFund e WHERE e.filteredResult.id=?1")
    void deleteByFilteredResultId(Long filteredResultId);

    @Modifying
    @Transactional
    @Query("DELETE from HedgeFundScreeningEditedFund e WHERE e.parsedData.fundName=?1 AND e.filteredResult.id=?2")
    void deleteByFundNameAndFilteredResultId(String fundName, Long filteredResultId);

    @Query("SELECT e FROM HedgeFundScreeningEditedFund e WHERE e.filteredResult.id=?1 AND e.parsedData.fundId=?2")
    HedgeFundScreeningEditedFund findByFilteredResultIdAndFundId(Long filteredResultId, Long fundId);
}