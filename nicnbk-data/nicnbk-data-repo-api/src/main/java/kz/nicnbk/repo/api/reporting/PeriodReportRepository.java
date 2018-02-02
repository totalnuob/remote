package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.PeriodicReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface PeriodReportRepository extends PagingAndSortingRepository<PeriodicReport, Long> {


    PeriodicReport findByReportDate(@Param("reportDate") @Temporal(TemporalType.DATE) Date reportDate);
}
