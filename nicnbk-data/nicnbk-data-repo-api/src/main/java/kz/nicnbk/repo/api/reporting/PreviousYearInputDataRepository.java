package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.PreviousYearInputData;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface PreviousYearInputDataRepository extends PagingAndSortingRepository<PreviousYearInputData, Long> {

    @Query("SELECT e from PreviousYearInputData e where e.report.id=?1")
    List<PreviousYearInputData> getEntitiesByReportId(Long reportId);

    @Modifying
    @Transactional
    @Query("DELETE from PreviousYearInputData e where e.report.id=?1")
    void deleteAllByReportId(long reportId);
}
