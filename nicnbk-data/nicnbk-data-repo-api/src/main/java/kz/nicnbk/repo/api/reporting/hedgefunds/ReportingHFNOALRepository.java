package kz.nicnbk.repo.api.reporting.hedgefunds;

import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFNOAL;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov on 28.06.2017.
 */
public interface ReportingHFNOALRepository extends PagingAndSortingRepository<ReportingHFNOAL, Long> {

    @Query("SELECT e from ReportingHFNOAL e where e.report.id=?1 AND e.tranche=?2")
    List<ReportingHFNOAL> getEntitiesByReportIdAndTranche(Long reportId, int tranche, Pageable pageable);

}
