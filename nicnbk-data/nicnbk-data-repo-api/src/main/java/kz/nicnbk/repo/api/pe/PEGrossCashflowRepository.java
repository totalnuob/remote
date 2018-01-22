package kz.nicnbk.repo.api.pe;

import kz.nicnbk.repo.model.pe.PEGrossCashflow;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zhambyl on 05-Jan-17.
 */
public interface PEGrossCashflowRepository extends PagingAndSortingRepository<PEGrossCashflow, Long> {

    @Query("SELECT e from pe_gross_cashflow e where e.fund.id=?1")
    List<PEGrossCashflow> getEntitiesByFundId(Long fundId, Pageable pageable);

    @Query("SELECT e from pe_gross_cashflow e where e.fund.id=?1 and e.companyName=?2")
    List<PEGrossCashflow> getEntitiesByFundIdAndCompanyName(Long fundId, String name, Pageable pageable);

//    @Modifying
//    @Transactional
//    @Query("DELETE from pe_gross_cashflow e where e.fund.id=?1")
//    void deleteByFundId(long fundId);
}