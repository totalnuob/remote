package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.ConsolidatedReportKZTForm6;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface ConsolidatedReportKZTForm6Repository extends PagingAndSortingRepository<ConsolidatedReportKZTForm6, Long> {

    @Query("SELECT e from ConsolidatedReportKZTForm6 e where e.report.id=?1 " +
            "ORDER BY e.id ASC")
    List<ConsolidatedReportKZTForm6> getEntitiesByReportId(Long reportId);

    @Modifying
    @Transactional
    @Query("DELETE from ConsolidatedReportKZTForm6 e where e.report.id=?1")
    void deleteAllByReportId(long reportId);
}
