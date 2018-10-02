package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.reporting.hedgefunds.HFChartOfAccountsType;
import kz.nicnbk.repo.model.reporting.privateequity.PEBalanceType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by zhambyl on 27-Mar-17.
 */
public interface HFChartOfAccountsTypeRepository extends PagingAndSortingRepository<HFChartOfAccountsType, Integer> {

    HFChartOfAccountsType findByCode(String code);

    HFChartOfAccountsType findByNameEnIgnoreCase(String name);

    @Query("SELECT e FROM HFChartOfAccountsType e WHERE e.parent.code = ?1")
    List<HFChartOfAccountsType> findByParentCode(String code);



}