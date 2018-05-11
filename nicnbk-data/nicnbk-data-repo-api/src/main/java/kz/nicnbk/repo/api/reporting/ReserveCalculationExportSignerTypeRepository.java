package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.ReserveCalculationExportSignerType;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface ReserveCalculationExportSignerTypeRepository extends PagingAndSortingRepository<ReserveCalculationExportSignerType, Long> {

    @Override
    Iterable<ReserveCalculationExportSignerType> findAll(Sort sort);

    ReserveCalculationExportSignerType findByCode(String code);
}
