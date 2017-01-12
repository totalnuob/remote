package kz.nicnbk.repo.api.hf;

import kz.nicnbk.repo.model.hf.HedgeFundReturn;
import kz.nicnbk.repo.model.hf.InvestorBase;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HedgeFundReturnRepository extends PagingAndSortingRepository<HedgeFundReturn, Long> {

    @Query("SELECT e from hf_returns e where e.fund.id=?1")
    List<HedgeFundReturn> getEntitiesByFundId(Long fundId);


    @Modifying
    @Transactional
    @Query("DELETE from hf_returns e where e.fund.id=?1")
    void deleteByFundId(Long fundId);
}