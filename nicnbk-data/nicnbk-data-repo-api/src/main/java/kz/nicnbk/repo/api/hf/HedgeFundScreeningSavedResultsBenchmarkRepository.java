package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreeningSavedResultsBenchmark;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HedgeFundScreeningSavedResultsBenchmarkRepository extends PagingAndSortingRepository<HedgeFundScreeningSavedResultsBenchmark, Long> {

    @Modifying
    @Transactional
    @Query("DELETE from HedgeFundScreeningSavedResultsBenchmark e WHERE e.savedResults.id=?1")
    void deleteBySavedResultsId(Long savedResultsId);

    List<HedgeFundScreeningSavedResultsBenchmark> findBySavedResultsIdOrderByBenchmarkIdDescDateAsc(Long savedResultsId);
}