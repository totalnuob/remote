package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.NBChartOfAccounts;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface NBChartOfAccountsRepository extends PagingAndSortingRepository<NBChartOfAccounts, Integer> {

    @Override
    Iterable<NBChartOfAccounts> findAll(Sort sort);

    @Query("select entity from NBChartOfAccounts entity where entity.code=?1 ORDER BY entity.code ASC")
    NBChartOfAccounts findByCode(String code);
}
