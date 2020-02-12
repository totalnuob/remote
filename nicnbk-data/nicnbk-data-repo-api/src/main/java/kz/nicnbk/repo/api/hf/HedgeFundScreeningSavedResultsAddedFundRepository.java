package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreeningSavedResultsAddedFund;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface HedgeFundScreeningSavedResultsAddedFundRepository extends PagingAndSortingRepository<HedgeFundScreeningSavedResultsAddedFund, Long> {

    List<HedgeFundScreeningSavedResultsAddedFund> findBySavedResultsId(Long savedResultsId);
}