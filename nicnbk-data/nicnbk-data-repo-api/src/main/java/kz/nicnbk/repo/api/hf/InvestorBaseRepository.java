package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundSubstrategy;
import kz.nicnbk.repo.model.hf.InvestorBase;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InvestorBaseRepository extends PagingAndSortingRepository<InvestorBase, Long> {

    @Query("SELECT e from hf_investor_base e where e.hedgeFund.id=?1")
    List<InvestorBase> getEntitiesByFundId(Long fundId);


    @Modifying
    @Transactional
    @Query("DELETE from hf_investor_base e where e.hedgeFund.id=?1")
    void deleteByFundId(Long fundId);
}