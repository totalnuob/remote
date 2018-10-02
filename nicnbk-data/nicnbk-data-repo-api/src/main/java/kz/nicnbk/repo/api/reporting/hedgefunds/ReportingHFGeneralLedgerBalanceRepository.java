package kz.nicnbk.repo.api.reporting.hedgefunds;

import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFGeneralLedgerBalance;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by magzumov on 28.06.2017.
 */
public interface ReportingHFGeneralLedgerBalanceRepository extends PagingAndSortingRepository<ReportingHFGeneralLedgerBalance, Long> {

    @Query("SELECT e from ReportingHFGeneralLedgerBalance e where e.report.id=?1")
    List<ReportingHFGeneralLedgerBalance> getEntitiesByReportId(Long reportId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE from ReportingHFGeneralLedgerBalance e where e.report.id=?1")
    void deleteByReportId(long reportId);

    @Query("SELECT e from ReportingHFGeneralLedgerBalance e where (e.adjustedRedemption IS NOT NULL OR e.interestRate IS NOT NULL) AND e.report.id=?1")
    List<ReportingHFGeneralLedgerBalance> getAdjustedEntitiesByReportId(Long reportId, Pageable pageable);

    @Query("SELECT count(e) from ReportingHFGeneralLedgerBalance e where e.chartAccountsType.code=?1")
    int getEntitiesCountByChartAccountsType(String code);

}
