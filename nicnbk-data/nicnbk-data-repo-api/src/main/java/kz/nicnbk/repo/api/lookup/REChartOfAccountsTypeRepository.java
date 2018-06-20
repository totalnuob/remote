package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.reporting.realestate.REChartOfAccountsType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhambyl on 27-Mar-17.
 */
public interface REChartOfAccountsTypeRepository extends PagingAndSortingRepository<REChartOfAccountsType, Long> {

    REChartOfAccountsType findByCode(String code);

    REChartOfAccountsType findByNameEnIgnoreCase(String name);



}