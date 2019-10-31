package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.PeriodicDataChartAccountsType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface PeriodicDataChartAccountsTypeRepository extends PagingAndSortingRepository<PeriodicDataChartAccountsType, Integer> {

    @Override
    Iterable<PeriodicDataChartAccountsType> findAll(Sort sort);

    @Query("select entity from PeriodicDataChartAccountsType entity where entity.code=?1 ORDER BY entity.code ASC")
    PeriodicDataChartAccountsType findByCode(String code);
}
