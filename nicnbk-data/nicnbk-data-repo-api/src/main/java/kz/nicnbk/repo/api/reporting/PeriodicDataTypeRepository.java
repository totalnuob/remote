package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.PeriodicDataType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface PeriodicDataTypeRepository extends PagingAndSortingRepository<PeriodicDataType, Long> {

    PeriodicDataType findByCode(String code);
}
