package kz.nicnbk.repo.api.monitoring;

import kz.nicnbk.repo.model.monitoring.MonitoringHedgeFundApprovedFund;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 04.07.2016.
 */
public interface MonitoringHedgeFundApprovedFundRepository extends PagingAndSortingRepository<MonitoringHedgeFundApprovedFund, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM MonitoringHedgeFundApprovedFund e WHERE e.reportDate.id=?1")
    void deleteByMonitoringDateId(Long monitoringDate);

    @Query("SELECT e FROM MonitoringHedgeFundApprovedFund e WHERE e.reportDate.id=?1")
    List<MonitoringHedgeFundApprovedFund> findByMonitoringId(Long monitoringId);


    @Query("SELECT e FROM MonitoringHedgeFundApprovedFund e")
    List<MonitoringHedgeFundApprovedFund> findAll();

    @Query("SELECT e FROM MonitoringHedgeFundApprovedFund e WHERE e.reportDate.monitoringDate=" +
            "(SELECT MAX(e2.reportDate.monitoringDate) FROM MonitoringHedgeFundApprovedFund e2)")
    List<MonitoringHedgeFundApprovedFund> getMostRecentRecordsBeforeDate();
}
