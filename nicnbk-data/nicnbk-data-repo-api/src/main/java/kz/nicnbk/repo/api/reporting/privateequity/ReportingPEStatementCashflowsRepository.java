package kz.nicnbk.repo.api.reporting.privateequity;

import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementCashflows;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by magzumov on 28.06.2017.
 */
public interface ReportingPEStatementCashflowsRepository extends PagingAndSortingRepository<ReportingPEStatementCashflows, Long> {

    @Query("SELECT e from ReportingPEStatementCashflows e where e.report.id=?1")
    List<ReportingPEStatementCashflows> getEntitiesByReportId(Long reportId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE from ReportingPEStatementCashflows e where e.report.id=?1")
    void deleteByReportId(long reportId);

    @Query("SELECT count(e) from ReportingPEStatementCashflows e where e.type.code=?1")
    int getEntitiesCountByType(String code);

}
