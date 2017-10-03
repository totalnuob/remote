package kz.nicnbk.repo.api.reporting.hedgefunds;

import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFGeneralLedgerBalance;
import kz.nicnbk.repo.model.reporting.privateequity.ReportingPEStatementChanges;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov on 28.06.2017.
 */
public interface ReportingHFGeneralLedgerBalanceRepository extends PagingAndSortingRepository<ReportingHFGeneralLedgerBalance, Long> {

    @Query("SELECT e from ReportingHFGeneralLedgerBalance e where e.report.id=?1")
    List<ReportingHFGeneralLedgerBalance> getEntitiesByReportId(Long reportId, Pageable pageable);

}
