package kz.nicnbk.repo.api.reporting.privateequity;

import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementBalance;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by magzumov on 28.06.2017.
 */
public interface ReportingPEStatementBalanceRepository extends PagingAndSortingRepository<ReportingPEStatementBalance, Long> {

    @Query("SELECT e from ReportingPEStatementBalance e where e.report.id=?1 and e.tranche=?2")
    List<ReportingPEStatementBalance> getEntitiesByReportIdAndTranche(Long reportId, int tranche, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE from ReportingPEStatementBalance e where e.report.id=?1")
    void deleteByReportId(long reportId);

}
