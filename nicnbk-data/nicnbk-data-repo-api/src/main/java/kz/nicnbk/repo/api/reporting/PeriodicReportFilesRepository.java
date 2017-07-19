package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.PeriodicReportFiles;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by magzumov on 21.04.2017.
 */
public interface PeriodicReportFilesRepository extends PagingAndSortingRepository<PeriodicReportFiles, Long> {

    @Query("SELECT e from periodic_report_files e where e.periodicReport.id=?1 and (e.file.deleted is null or e.file.deleted=false)")
    List<PeriodicReportFiles> getEntitiesByReportId(Long reportId);

    @Query("SELECT e from periodic_report_files e where e.file.id=?1 and (e.file.deleted is null or e.file.deleted=false)")
    PeriodicReportFiles getEntitiesByFileId(Long fileId);

    @Modifying
    @Transactional
    @Query("DELETE from periodic_report_files e where e.file.id=?1")
    void deleteByFileId(long fileId);
}
