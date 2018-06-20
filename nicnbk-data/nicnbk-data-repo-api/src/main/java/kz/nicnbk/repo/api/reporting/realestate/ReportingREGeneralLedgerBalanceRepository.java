package kz.nicnbk.repo.api.reporting.realestate;

import kz.nicnbk.repo.model.reporting.realestate.ReportingREGeneralLedgerBalance;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by magzumov on 28.06.2017.
 */
public interface ReportingREGeneralLedgerBalanceRepository extends PagingAndSortingRepository<ReportingREGeneralLedgerBalance, Long> {

    @Query("SELECT e from ReportingREGeneralLedgerBalance e where e.report.id=?1")
    List<ReportingREGeneralLedgerBalance> getEntitiesByReportId(Long reportId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE from ReportingREGeneralLedgerBalance e where e.report.id=?1")
    void deleteByReportId(long reportId);

    @Query("SELECT e from ReportingREGeneralLedgerBalance e where (e.adjustedRedemption IS NOT NULL OR e.interestRate IS NOT NULL) AND e.report.id=?1")
    List<ReportingREGeneralLedgerBalance> getAdjustedEntitiesByReportId(Long reportId, Pageable pageable);

}
