package kz.nicnbk.repo.api.reporting.realestate;

import kz.nicnbk.repo.model.reporting.realestate.TerraNICChartOfAccounts;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface TerraNICChartOfAccountsRepository extends PagingAndSortingRepository<TerraNICChartOfAccounts, Long> {

    List<TerraNICChartOfAccounts> findByTerraChartOfAccountsNameAndAddable(String name, Boolean addable);

    List<TerraNICChartOfAccounts> findByTerraChartOfAccountsName(String name);

    List<TerraNICChartOfAccounts> findByAddable(Boolean addable);

}
