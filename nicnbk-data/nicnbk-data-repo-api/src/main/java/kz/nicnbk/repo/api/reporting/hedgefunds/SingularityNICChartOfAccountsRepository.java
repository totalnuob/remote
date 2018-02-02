package kz.nicnbk.repo.api.reporting.hedgefunds;

import kz.nicnbk.repo.model.reporting.hedgefunds.SingularityNICChartOfAccounts;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface SingularityNICChartOfAccountsRepository extends PagingAndSortingRepository<SingularityNICChartOfAccounts, Long> {

    List<SingularityNICChartOfAccounts> findBySingularityAccountNumber(String singularityAccountNumber);

}
