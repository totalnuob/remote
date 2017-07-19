package kz.nicnbk.repo.api.reporting.privateequity;

import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementOperations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov on 28.06.2017.
 */
public interface ReportingPEStatementOperationsRepository extends PagingAndSortingRepository<ReportingPEStatementOperations, Long> {

    @Query("SELECT e from ReportingPEStatementOperations e where e.report.id=?1 and e.tranche=?2")
    List<ReportingPEStatementOperations> getEntitiesByReportIdAndTranche(Long reportId, int tranche, Pageable pageable);

}
