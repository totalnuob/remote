package kz.nicnbk.repo.api.monitoring;

import kz.nicnbk.repo.model.monitoring.MonitoringHedgeFundReportDate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 04.07.2016.
 */
public interface MonitoringHedgeFundReportDateRepository extends PagingAndSortingRepository<MonitoringHedgeFundReportDate, Long> {

    @Query("SELECT e FROM MonitoringHedgeFundReportDate e WHERE e.monitoringDate=?1")
    List<MonitoringHedgeFundReportDate> findByMonitoringDate(Date monitoringDate);

}
