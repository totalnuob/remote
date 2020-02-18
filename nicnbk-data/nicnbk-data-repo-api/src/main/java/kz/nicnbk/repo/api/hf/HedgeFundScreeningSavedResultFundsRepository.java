package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreeningSavedResultFunds;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface HedgeFundScreeningSavedResultFundsRepository extends PagingAndSortingRepository<HedgeFundScreeningSavedResultFunds, Long> {

    List<HedgeFundScreeningSavedResultFunds> findBySavedResultsId(Long savedResults);
}