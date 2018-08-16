package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.PeriodicReportFundRenameInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface PeriodicReportFundRenameInfoRepository extends PagingAndSortingRepository<PeriodicReportFundRenameInfo, Long> {

    @Query("SELECT e from PeriodicReportFundRenameInfo e where e.report.id=?1")
    List<PeriodicReportFundRenameInfo> getEntitiesReportId(Long reportId);

    @Modifying
    @Transactional
    @Query("DELETE from PeriodicReportFundRenameInfo e where e.report.id=?1")
    void deleteByReportId(Long reportId);
}
