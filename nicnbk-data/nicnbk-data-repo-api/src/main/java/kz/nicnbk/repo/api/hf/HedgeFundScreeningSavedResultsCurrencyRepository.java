package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreeningSavedResultsCurrency;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HedgeFundScreeningSavedResultsCurrencyRepository extends PagingAndSortingRepository<HedgeFundScreeningSavedResultsCurrency, Long> {

    @Modifying
    @Transactional
    @Query("DELETE from HedgeFundScreeningSavedResultsCurrency e WHERE e.savedResults.id=?1")
    void deleteBySavedResultsId(Long savedResultsId);

    List<HedgeFundScreeningSavedResultsCurrency> findBySavedResultsIdOrderByCurrencyAscDateAsc(Long savedResultsId);

}