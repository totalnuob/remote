package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.ReportOtherInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
@Deprecated
public interface ReportingOtherInfoRepository extends PagingAndSortingRepository<ReportOtherInfo, Long> {

    @Query("SELECT e from ReportOtherInfo e where e.report.id=?1")
    ReportOtherInfo getEntityByReportId(Long reportId);
}
