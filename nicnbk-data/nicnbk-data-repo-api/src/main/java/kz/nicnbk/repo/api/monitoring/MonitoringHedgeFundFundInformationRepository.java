package kz.nicnbk.repo.api.monitoring;

import kz.nicnbk.repo.model.monitoring.MonitoringHedgeFundFundInformation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 04.07.2016.
 */
public interface MonitoringHedgeFundFundInformationRepository extends PagingAndSortingRepository<MonitoringHedgeFundFundInformation, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM MonitoringHedgeFundFundInformation e WHERE e.type.id=?1 AND e.fundInfoType.id = ?2 AND e.monitoringDate=?3")
    void deleteByTypeIdAndFundInfoTypeIdAndDate(Integer typeId, Integer fundInfoType, Date monitoringDate);

    @Query("SELECT e FROM MonitoringHedgeFundFundInformation e WHERE e.type.id=?1 AND e.fundInfoType.id = ?2 AND e.monitoringDate=?3")
    List<MonitoringHedgeFundFundInformation> findByTypeIdAndFundInfoTypeIdAndMonitoringDate(Integer typeId, Integer fundInfoTypeId, Date monitoringDate);

    @Query("SELECT e FROM MonitoringHedgeFundFundInformation e ")
    List<MonitoringHedgeFundFundInformation> findAll();
}
