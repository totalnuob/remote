package kz.nicnbk.repo.api.reporting.hedgefunds;

import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFITDHistorical;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


public interface ReportingHFITDHistoricalRepository extends PagingAndSortingRepository<ReportingHFITDHistorical, Long> {
    @Query("SELECT e from ReportingHFITDHistorical e")
    List<ReportingHFITDHistorical> getAll();

    @Query("SELECT e from ReportingHFITDHistorical e where e.report.id=?1 AND e.tranche=?2")
    List<ReportingHFITDHistorical> getEntitiesByReportIdAndTranche(Long reportId, int tranche);

    //@Query("SELECT e from ReportingHFITDHistorical e where e.report.id=?1 AND e.tranche=?2")
    ReportingHFITDHistorical findByDateAndFundNameAndPortfolio(Date date, String fundName, String portfolio);

    @Modifying
    @Transactional
    @Query("DELETE from ReportingHFITDHistorical e where e.report.id=?1")
    void deleteByReportId(long reportId);

}
