package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.NICKMFReportingData;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface NICKMFReportingDataRepository extends PagingAndSortingRepository<NICKMFReportingData, Long> {

    @Query("SELECT e from NICKMFReportingData e where e.report.id=?1")
    List<NICKMFReportingData> getEntitiesByReportId(Long reportId);

    @Modifying
    @Transactional
    @Query("DELETE from NICKMFReportingData e where e.report.id=?1")
    void deleteAllByReportId(long reportId);
}
