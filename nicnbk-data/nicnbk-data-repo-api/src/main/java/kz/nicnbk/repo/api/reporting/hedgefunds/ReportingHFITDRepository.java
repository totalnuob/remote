package kz.nicnbk.repo.api.reporting.hedgefunds;

import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFITD;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by magzumov on 28.06.2017.
 */
public interface ReportingHFITDRepository extends PagingAndSortingRepository<ReportingHFITD, Long> {

    @Query("SELECT e from ReportingHFITD e where e.report.id=?1 AND e.tranche=?2")
    List<ReportingHFITD> getEntitiesByReportIdAndTranche(Long reportId, int tranche);

    @Modifying
    @Transactional
    @Query("DELETE from ReportingHFITD e where e.report.id=?1")
    void deleteByReportId(long reportId);

}
