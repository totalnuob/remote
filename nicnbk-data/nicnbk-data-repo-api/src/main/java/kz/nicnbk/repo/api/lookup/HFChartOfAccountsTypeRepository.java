package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.reporting.hedgefunds.HFChartOfAccountsType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhambyl on 27-Mar-17.
 */
public interface HFChartOfAccountsTypeRepository extends PagingAndSortingRepository<HFChartOfAccountsType, Long> {

    HFChartOfAccountsType findByCode(String code);

    HFChartOfAccountsType findByNameEnIgnoreCase(String name);



}