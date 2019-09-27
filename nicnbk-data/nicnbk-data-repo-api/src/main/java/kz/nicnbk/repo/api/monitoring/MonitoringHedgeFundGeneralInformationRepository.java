package kz.nicnbk.repo.api.monitoring;

import kz.nicnbk.repo.model.monitoring.MonitoringHedgeFundGeneralInformation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 04.07.2016.
 */
public interface MonitoringHedgeFundGeneralInformationRepository extends PagingAndSortingRepository<MonitoringHedgeFundGeneralInformation, Long> {

    @Modifying
    @Transactional
//    @Query("DELETE FROM MonitoringHedgeFundGeneralInformation e WHERE e.type.id=?1 AND e2.reportDate.monitoringDate=?2")
//    void deleteByTypeIdAndDate(Integer typeId,  @Temporal(TemporalType.DATE) Date monitoringDate);
    @Query("DELETE FROM MonitoringHedgeFundGeneralInformation e WHERE e.type.id=?1 AND e.reportDate.id=?2")
    void deleteByTypeIdAndReportDateId(Integer typeId, Long reportDateId);

    @Query("SELECT e FROM MonitoringHedgeFundGeneralInformation e WHERE e.type.id=?1 AND e.reportDate.id=?2")
    List<MonitoringHedgeFundGeneralInformation> findByTypeIdAndMonitoringId(Integer typeId, Long monitoringId);

    @Query("SELECT e FROM MonitoringHedgeFundGeneralInformation e WHERE e.reportDate.id=?1")
    List<MonitoringHedgeFundGeneralInformation> findAllByMonitoringId(Long monitoringId);

    @Query("SELECT e FROM MonitoringHedgeFundGeneralInformation e")
    List<MonitoringHedgeFundGeneralInformation> findAll();

    @Query("SELECT e FROM MonitoringHedgeFundGeneralInformation e WHERE e.type.id=?1 AND e.reportDate.monitoringDate=" +
            "(SELECT MAX(e2.reportDate.monitoringDate) FROM MonitoringHedgeFundGeneralInformation e2 WHERE e2.type.id=?1)")
    List<MonitoringHedgeFundGeneralInformation> getMostRecentRecordsBeforeDate(Integer type);
}
