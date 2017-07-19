package kz.nicnbk.repo.api.reporting.privateequity;

import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementCashflows;
import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementOperations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov on 28.06.2017.
 */
public interface ReportingPEStatementCashflowsRepository extends PagingAndSortingRepository<ReportingPEStatementCashflows, Long> {

    @Query("SELECT e from ReportingPEStatementCashflows e where e.report.id=?1")
    List<ReportingPEStatementCashflows> getEntitiesByReportId(Long reportId, Pageable pageable);

}
