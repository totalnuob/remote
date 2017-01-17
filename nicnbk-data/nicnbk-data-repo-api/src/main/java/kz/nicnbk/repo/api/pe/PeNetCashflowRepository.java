package kz.nicnbk.repo.api.pe;

import kz.nicnbk.repo.model.pe.PeNetCashflow;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zhambyl on 12-Jan-17.
 */
public interface PeNetCashflowRepository extends PagingAndSortingRepository<PeNetCashflow, Long>{

    @Query("SELECT e from pe_net_cashflow e where e.fund.id=?1")
    List<PeNetCashflow> getEntitiesByFundId(Long fundId);

    @Modifying
    @Transactional
    @Query("DELETE from pe_net_cashflow e where e.fund.id=?1")
    void deleteByFundId(long fundId);

}
