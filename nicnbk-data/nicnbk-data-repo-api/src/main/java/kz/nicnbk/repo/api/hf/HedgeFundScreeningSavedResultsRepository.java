package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreeningSavedResults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface HedgeFundScreeningSavedResultsRepository extends PagingAndSortingRepository<HedgeFundScreeningSavedResults, Long> {

    List<HedgeFundScreeningSavedResults> findByFilteredResultId(Long filteredResultId);
}