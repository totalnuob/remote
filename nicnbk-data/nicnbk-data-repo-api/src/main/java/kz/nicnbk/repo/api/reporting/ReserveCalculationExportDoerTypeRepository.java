package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.ReserveCalculationExportDoerType;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface ReserveCalculationExportDoerTypeRepository extends PagingAndSortingRepository<ReserveCalculationExportDoerType, Long> {

    @Override
    Iterable<ReserveCalculationExportDoerType> findAll(Sort sort);

    ReserveCalculationExportDoerType findByCode(String code);
}
