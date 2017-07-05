package kz.nicnbk.repo.api.reporting.privateequity;

import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEScheduleInvestment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov on 28.06.2017.
 */
public interface ReportingPEScheduleInvestmentRepository extends PagingAndSortingRepository<ReportingPEScheduleInvestment, Long> {

    @Query("SELECT e from ReportingPEScheduleInvestment e where e.report.id=?1 and e.tranche=?2")
    List<ReportingPEScheduleInvestment> getEntitiesByReportIdAndTranche(Long reportId, int tranche, Pageable pageable);

}
