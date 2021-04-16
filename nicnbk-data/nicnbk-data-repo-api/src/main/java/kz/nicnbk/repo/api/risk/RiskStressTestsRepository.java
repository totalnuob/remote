package kz.nicnbk.repo.api.risk;


import kz.nicnbk.repo.model.risk.RiskStressTests;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;


public interface RiskStressTestsRepository extends PagingAndSortingRepository<RiskStressTests, Long> {

    List<RiskStressTests> findByDate(Date date);

    RiskStressTests findByDateAndName(Date date, String name);

    List<RiskStressTests> findAll();

    RiskStressTests getRiskStressTestsByDate(Date date);

    @Query("SELECT value FROM RiskStressTests value WHERE value.date >= :fromDate")
    Page<RiskStressTests> getValuesFromDate(@Param("fromDate") @Temporal(TemporalType.DATE) Date fromDate, Pageable pageable);

    @Query("SELECT value FROM RiskStressTests value WHERE (cast(:fromDate as date) is null OR value.date >= :fromDate)" +
            " AND (cast(:toDate as date) is null OR value.date <= :toDate)")
    Page<RiskStressTests> getValuesBetweenDates(@Param("fromDate") @Temporal(TemporalType.DATE) Date fromDate,
                                                  @Param("toDate") @Temporal(TemporalType.DATE) Date toDate, Pageable pageable);

    @Query("SELECT entity FROM RiskStressTests entity WHERE entity.date = ?1")
    RiskStressTests getValuesForDate(@Param("date") @Temporal(TemporalType.DATE) Date date);

    @Query("SELECT max(e.date) FROM RiskStressTests e")
    Date getMaxDate();

    @Query("SELECT min(e.date) FROM RiskStressTests e")
    Date getMinDate();
}
