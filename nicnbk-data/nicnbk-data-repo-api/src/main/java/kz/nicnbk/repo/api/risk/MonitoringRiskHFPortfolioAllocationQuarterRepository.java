package kz.nicnbk.repo.api.risk;

import kz.nicnbk.repo.model.risk.MonitoringRiskHFAllocationQuarter;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface MonitoringRiskHFPortfolioAllocationQuarterRepository extends PagingAndSortingRepository<MonitoringRiskHFAllocationQuarter, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM MonitoringRiskHFAllocationQuarter e WHERE e.report.id=?1")
    void deleteByReportId(Long reportId);

    List<MonitoringRiskHFAllocationQuarter> findByReportId(Long reportId);
}
