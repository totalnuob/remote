package kz.nicnbk.repo.api.risk;

import kz.nicnbk.repo.model.risk.MonitoringRiskHFPortfolioReturn;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MonitoringRiskHFPortfolioReturnRepository extends PagingAndSortingRepository<MonitoringRiskHFPortfolioReturn, Long> {

    List<MonitoringRiskHFPortfolioReturn> findByPortfolioTypeCode(String code);

    @Modifying
    @Transactional
    //@Query("DELETE FROM MonitoringRiskHFPortfolioReturn e JOIN FETCH e.portfolioType WHERE e.report.id=?1 AND e.portfolioType.code=?2")
    void deleteByReportIdAndPortfolioTypeCode(Long reportId, String typeCode);
}
