package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.ReserveCalculationEntityType;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface ReserveCalculationEntityTypeRepository extends PagingAndSortingRepository<ReserveCalculationEntityType, Long> {

    Iterable<ReserveCalculationEntityType> findByDeletedFalse();

    ReserveCalculationEntityType findByCode(String code);
}
