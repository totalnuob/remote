package kz.nicnbk.repo.api.pe;

import kz.nicnbk.repo.model.pe.PENetCashflow;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zhambyl on 12-Jan-17.
 */
public interface PeNetCashflowRepository extends PagingAndSortingRepository<PENetCashflow, Long>{

    @Query("SELECT e from pe_net_cashflow e where e.fund.id=?1")
    List<PENetCashflow> getEntitiesByFundId(Long fundId);

    @Modifying
    @Transactional
    @Query("DELETE from pe_net_cashflow e where e.fund.id=?1")
    void deleteByFundId(long fundId);

}
