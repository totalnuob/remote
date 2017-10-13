package kz.nicnbk.repo.api.pe;

import kz.nicnbk.repo.model.pe.PECompanyPerformance;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Pak on 03.10.2017.
 */
public interface PECompanyPerformanceRepository extends PagingAndSortingRepository<PECompanyPerformance, Long> {

    @Query("SELECT e from pe_company_performance e where e.fund.id=?1")
    List<PECompanyPerformance> getEntitiesByFundId(Long fundId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE from pe_company_performance e where e.fund.id=?1")
    void deleteByFundId(Long fundId);
}
