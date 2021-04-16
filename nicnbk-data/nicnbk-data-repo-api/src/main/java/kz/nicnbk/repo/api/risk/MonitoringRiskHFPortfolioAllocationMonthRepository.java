package kz.nicnbk.repo.api.risk;

import kz.nicnbk.repo.model.risk.MonitoringRiskHFAllocationMonth;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface MonitoringRiskHFPortfolioAllocationMonthRepository extends PagingAndSortingRepository<MonitoringRiskHFAllocationMonth, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM MonitoringRiskHFAllocationMonth e WHERE e.report.id=?1")
    void deleteByReportId(Long reportId);

    List<MonitoringRiskHFAllocationMonth> findByReportId(Long reportId);

}
