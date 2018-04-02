package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.ConsolidatedReportKZTForm3;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface ConsolidatedReportKZTForm3Repository extends PagingAndSortingRepository<ConsolidatedReportKZTForm3, Long> {

    @Query("SELECT e from ConsolidatedReportKZTForm3 e where e.report.id=?1 " +
            "ORDER BY e.lineNumber, e.id ASC")
    List<ConsolidatedReportKZTForm3> getEntitiesByReportId(Long reportId);

    @Modifying
    @Transactional
    @Query("DELETE from ConsolidatedReportKZTForm3 e where e.report.id=?1")
    void deleteAllByReportId(long reportId);
}
