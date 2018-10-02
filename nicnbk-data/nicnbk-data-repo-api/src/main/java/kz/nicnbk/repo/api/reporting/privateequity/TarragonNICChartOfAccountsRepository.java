package kz.nicnbk.repo.api.reporting.privateequity;

import kz.nicnbk.repo.model.reporting.privateequity.TarragonNICChartOfAccounts;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface TarragonNICChartOfAccountsRepository extends PagingAndSortingRepository<TarragonNICChartOfAccounts, Long> {

    List<TarragonNICChartOfAccounts> findByTarragonChartOfAccountsNameAndAddable(String name, Boolean addable);

    List<TarragonNICChartOfAccounts> findByTarragonChartOfAccountsName(String name);

    List<TarragonNICChartOfAccounts> findByAddable(Boolean addable);


}
