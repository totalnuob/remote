package kz.nicnbk.repo.api.reporting.realestate;

import kz.nicnbk.repo.model.reporting.realestate.ReportingREProfitLoss;
import kz.nicnbk.repo.model.reporting.realestate.ReportingRESecuritiesCost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by magzumov on 14.06.2018.
 */
public interface ReportingRESecuritiesCostRepository extends PagingAndSortingRepository<ReportingRESecuritiesCost, Long> {

    @Query("SELECT e from ReportingRESecuritiesCost e where e.report.id=?1")
    List<ReportingRESecuritiesCost> getEntitiesByReportId(Long reportId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE from ReportingRESecuritiesCost e where e.report.id=?1")
    void deleteByReportId(long reportId);
}
