package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScoringResultFund;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;


public interface HedgeFundScoringResultRepository extends PagingAndSortingRepository<HedgeFundScoringResultFund, Long> {

    List<HedgeFundScoringResultFund> findByScoringId(Long scoringId);
}