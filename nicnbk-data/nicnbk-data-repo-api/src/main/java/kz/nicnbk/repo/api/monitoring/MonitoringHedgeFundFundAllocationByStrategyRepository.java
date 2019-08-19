package kz.nicnbk.repo.api.monitoring;

import kz.nicnbk.repo.model.monitoring.MonitoringHedgeFundAllocationByStrategy;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 04.07.2016.
 */
public interface MonitoringHedgeFundFundAllocationByStrategyRepository extends PagingAndSortingRepository<MonitoringHedgeFundAllocationByStrategy, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM MonitoringHedgeFundAllocationByStrategy e WHERE e.type.id=?1 AND e.reportDate.monitoringDate=?2")
    void deleteByTypeIdAndDate(Integer typeId, Date monitoringDate);

    @Query("SELECT e FROM MonitoringHedgeFundAllocationByStrategy e WHERE e.type.id=?1 AND e.reportDate.monitoringDate=?2")
    List<MonitoringHedgeFundAllocationByStrategy> findByTypeIdAndMonitoringDate(Integer typeId, Date monitoringDate);

    @Query("SELECT e FROM MonitoringHedgeFundAllocationByStrategy e")
    List<MonitoringHedgeFundAllocationByStrategy> findAll();

    @Query("SELECT e FROM MonitoringHedgeFundAllocationByStrategy e WHERE e.type.id=?1 AND e.reportDate.monitoringDate=" +
            "(SELECT MAX(e2.reportDate.monitoringDate) FROM MonitoringHedgeFundAllocationByStrategy e2 WHERE e2.type.id=?1)")
    List<MonitoringHedgeFundAllocationByStrategy> getMostRecentRecordsBeforeDate(Integer typeId);
}
