package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.ConsolidatedReportUSDFormBalance;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface ConsolidatedReportUSDFormBalanceRepository extends PagingAndSortingRepository<ConsolidatedReportUSDFormBalance, Long> {

    @Query("SELECT e from ConsolidatedReportUSDFormBalance e where e.report.id=?1 " +
            "ORDER BY e.id ASC")
    List<ConsolidatedReportUSDFormBalance> getEntitiesByReportId(Long reportId);

    @Modifying
    @Transactional
    @Query("DELETE from ConsolidatedReportUSDFormBalance e where e.report.id=?1")
    void deleteAllByReportId(long reportId);
}
