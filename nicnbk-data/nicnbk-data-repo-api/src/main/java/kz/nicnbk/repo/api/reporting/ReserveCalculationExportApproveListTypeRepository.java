package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.ReserveCalculationExportApproveListType;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface ReserveCalculationExportApproveListTypeRepository extends PagingAndSortingRepository<ReserveCalculationExportApproveListType, Long> {

    @Override
    Iterable<ReserveCalculationExportApproveListType> findAll(Sort sort);

    ReserveCalculationExportApproveListType findByCode(String code);
}
