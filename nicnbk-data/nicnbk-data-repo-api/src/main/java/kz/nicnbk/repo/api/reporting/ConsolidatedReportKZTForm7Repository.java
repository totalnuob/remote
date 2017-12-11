package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.ConsolidatedReportKZTForm7;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface ConsolidatedReportKZTForm7Repository extends PagingAndSortingRepository<ConsolidatedReportKZTForm7, Long> {

    @Query("SELECT e from ConsolidatedReportKZTForm7 e where e.report.id=?1")
    List<ConsolidatedReportKZTForm7> getEntitiesByReportId(Long reportId);

    @Modifying
    @Transactional
    @Query("DELETE from ConsolidatedReportKZTForm7 e where e.report.id=?1")
    void deleteAllByReportId(long reportId);
}
