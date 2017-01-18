package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundSubstrategy;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HedgeFundSubstrategyRepository extends PagingAndSortingRepository<HedgeFundSubstrategy, Long> {

    @Query("SELECT e from hf_strategy_breakdown e where e.fund.id=?1")
    List<HedgeFundSubstrategy> getEntitiesByFundId(Long fundId);


    @Modifying
    @Transactional
    @Query("DELETE from hf_strategy_breakdown e where e.fund.id=?1")
    void deleteByFundId(Long fundId);
}