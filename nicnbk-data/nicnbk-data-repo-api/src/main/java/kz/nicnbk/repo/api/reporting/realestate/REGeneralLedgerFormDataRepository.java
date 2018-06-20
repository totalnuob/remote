package kz.nicnbk.repo.api.reporting.realestate;

import kz.nicnbk.repo.model.reporting.realestate.RealEstateGeneralLedgerFormData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov on 28.06.2017.
 */
public interface REGeneralLedgerFormDataRepository extends PagingAndSortingRepository<RealEstateGeneralLedgerFormData, Long> {

    @Query("SELECT e from RealEstateGeneralLedgerFormData e where e.report.id=?1")
    List<RealEstateGeneralLedgerFormData> getEntitiesByReportId(Long reportId, Pageable pageable);

}
