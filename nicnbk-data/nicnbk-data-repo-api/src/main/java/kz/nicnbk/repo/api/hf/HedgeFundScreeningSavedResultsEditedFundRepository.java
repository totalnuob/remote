package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreeningSavedResultsEditedFund;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface HedgeFundScreeningSavedResultsEditedFundRepository extends PagingAndSortingRepository<HedgeFundScreeningSavedResultsEditedFund, Long> {

    List<HedgeFundScreeningSavedResultsEditedFund> findBySavedResultsId(Long savedResultsId);
}