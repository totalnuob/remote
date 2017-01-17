package kz.nicnbk.repo.api.pe;

import kz.nicnbk.repo.model.pe.PeGrossCashflow;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zhambyl on 05-Jan-17.
 */
public interface PeGrossCashflowRepository extends PagingAndSortingRepository<PeGrossCashflow, Long> {

    @Query("SELECT e from pe_gross_cashflow e where e.fund.id=?1")
    List<PeGrossCashflow> getEntitiesByFundId(Long fundId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE from pe_gross_cashflow e where e.fund.id=?1")
    void deleteByFundId(long fundId);

}
