package kz.nicnbk.repo.api.reporting.privateequity;

import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementChanges;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by magzumov on 28.06.2017.
 */
public interface ReportingPEStatementChangesRepository extends PagingAndSortingRepository<ReportingPEStatementChanges, Long> {

    @Query("SELECT e from ReportingPEStatementChanges e where e.report.id=?1")
    List<ReportingPEStatementChanges> getEntitiesByReportId(Long reportId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE from ReportingPEStatementChanges e where e.report.id=?1")
    void deleteByReportId(long reportId);

}
