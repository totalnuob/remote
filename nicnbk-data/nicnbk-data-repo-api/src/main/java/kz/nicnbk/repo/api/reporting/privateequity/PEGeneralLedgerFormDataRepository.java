package kz.nicnbk.repo.api.reporting.privateequity;

import kz.nicnbk.repo.model.reporting.privateequity.PEGeneralLedgerFormData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov on 28.06.2017.
 */
public interface PEGeneralLedgerFormDataRepository extends PagingAndSortingRepository<PEGeneralLedgerFormData, Long> {

    @Query("SELECT e from PEGeneralLedgerFormData e where e.report.id=?1")
    List<PEGeneralLedgerFormData> getEntitiesByReportId(Long reportId, Pageable pageable);

}
