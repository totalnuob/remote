package kz.nicnbk.repo.api.reporting.privateequity;

import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEScheduleInvestment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by magzumov on 28.06.2017.
 */
public interface ReportingPEScheduleInvestmentRepository extends PagingAndSortingRepository<ReportingPEScheduleInvestment, Long> {

    @Query("SELECT e from ReportingPEScheduleInvestment e where e.report.id=?1 and e.tranche=?2")
    List<ReportingPEScheduleInvestment> getEntitiesByReportIdAndTranche(Long reportId, int tranche, Pageable pageable);

    @Query("SELECT e from ReportingPEScheduleInvestment e where e.report.id=?1 and e.tranche=?2 and e.name=?3")
    ReportingPEScheduleInvestment getEntities(Long reportId, int tranche, String name);

    @Modifying
    @Transactional
    @Query("DELETE from ReportingPEScheduleInvestment e where e.report.id=?1")
    void deleteByReportId(long reportId);

}
