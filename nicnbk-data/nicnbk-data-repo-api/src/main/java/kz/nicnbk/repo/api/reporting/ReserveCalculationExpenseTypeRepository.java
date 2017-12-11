package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.ReserveCalculationExpenseType;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface ReserveCalculationExpenseTypeRepository extends PagingAndSortingRepository<ReserveCalculationExpenseType, Long> {

    @Override
    Iterable<ReserveCalculationExpenseType> findAll(Sort sort);

    ReserveCalculationExpenseType findByCode(String code);
}
