package kz.nicnbk.repo.api.risk;

import kz.nicnbk.repo.model.risk.MonitoringRiskHFMonthlyReport;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

public interface MonitoringRiskHFMonthlyReportRepository extends PagingAndSortingRepository<MonitoringRiskHFMonthlyReport, Long> {

    MonitoringRiskHFMonthlyReport findByReportDate(Date reportDate);
}
