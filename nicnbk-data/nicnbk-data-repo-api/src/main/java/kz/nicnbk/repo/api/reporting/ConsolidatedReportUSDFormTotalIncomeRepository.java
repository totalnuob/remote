package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.ConsolidatedReportUSDFormTotalIncome;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface ConsolidatedReportUSDFormTotalIncomeRepository extends PagingAndSortingRepository<ConsolidatedReportUSDFormTotalIncome, Long> {

    @Query("SELECT e from ConsolidatedReportUSDFormTotalIncome e where e.report.id=?1 " +
            "ORDER BY e.id ASC")
    List<ConsolidatedReportUSDFormTotalIncome> getEntitiesByReportId(Long reportId);

    @Modifying
    @Transactional
    @Query("DELETE from ConsolidatedReportUSDFormTotalIncome e where e.report.id=?1")
    void deleteAllByReportId(long reportId);
}
