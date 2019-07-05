package kz.nicnbk.repo.api.monitoring;

import kz.nicnbk.repo.model.monitoring.MonitoringHedgeFundGeneralInformation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 04.07.2016.
 */
public interface MonitoringHedgeFundGeneralInformationRepository extends PagingAndSortingRepository<MonitoringHedgeFundGeneralInformation, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM MonitoringHedgeFundGeneralInformation e WHERE e.type.id=?1 AND e.monitoringDate=?2")
    void deleteByTypeIdAndDate(Integer typeId, Date monitoringDate);

    @Query("SELECT e FROM MonitoringHedgeFundGeneralInformation e WHERE e.type.id=?1 AND e.monitoringDate=?2")
    List<MonitoringHedgeFundGeneralInformation> findByTypeIdAndMonitoringDate(Integer typeId, Date monitoringDate);

    @Query("SELECT e FROM MonitoringHedgeFundGeneralInformation e")
    List<MonitoringHedgeFundGeneralInformation> findAll();
}
