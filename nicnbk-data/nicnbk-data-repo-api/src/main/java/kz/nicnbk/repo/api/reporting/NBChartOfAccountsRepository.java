package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.NBChartOfAccounts;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface NBChartOfAccountsRepository extends PagingAndSortingRepository<NBChartOfAccounts, Long> {

    @Override
    Iterable<NBChartOfAccounts> findAll(Sort sort);
}
