package kz.nicnbk.repo.api.monitoring;

import kz.nicnbk.repo.model.monitoring.MonitoringHedgeFundApprovedFund;
import kz.nicnbk.repo.model.monitoring.MonitoringHedgeFundContributionToReturn;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 04.07.2016.
 */
public interface MonitoringHedgeFundContributionToReturnRepository extends PagingAndSortingRepository<MonitoringHedgeFundContributionToReturn, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM MonitoringHedgeFundContributionToReturn e WHERE e.type.id=?1 AND e.reportDate.monitoringDate=?2")
    void deleteByTypeIdAndDate(Integer typeId, Date monitoringDate);

    @Query("SELECT e FROM MonitoringHedgeFundContributionToReturn e WHERE e.type.id=?1 AND e.reportDate.monitoringDate=?2")
    List<MonitoringHedgeFundContributionToReturn> findByTypeIdAndMonitoringDate(Integer typeId, Date monitoringDate);

    @Query("SELECT e FROM MonitoringHedgeFundContributionToReturn e")
    List<MonitoringHedgeFundContributionToReturn> findAll();

    @Query("SELECT e FROM MonitoringHedgeFundContributionToReturn e WHERE e.type.id=?1 AND e.reportDate.monitoringDate=" +
            "(SELECT MAX(e2.reportDate.monitoringDate) FROM MonitoringHedgeFundContributionToReturn e2 WHERE e2.type.id=?1)")
    List<MonitoringHedgeFundContributionToReturn> getMostRecentRecordsBeforeDate(Integer typeId);
}
