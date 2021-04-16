package kz.nicnbk.repo.api.risk;

import kz.nicnbk.repo.model.risk.MonitoringRiskHFAllocationYear;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface MonitoringRiskHFPortfolioAllocationYearRepository extends PagingAndSortingRepository<MonitoringRiskHFAllocationYear, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM MonitoringRiskHFAllocationYear e WHERE e.report.id=?1")
    void deleteByReportId(Long reportId);

    List<MonitoringRiskHFAllocationYear> findByReportId(Long reportId);
}
