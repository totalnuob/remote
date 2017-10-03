package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.NBChartOfAccounts;
import kz.nicnbk.repo.model.reporting.NICReportingChartOfAccounts;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface NICReportingChartOfAccountsRepository extends PagingAndSortingRepository<NICReportingChartOfAccounts, Long> {

    @Query("select entity from NICReportingChartOfAccounts entity where entity.nbChartOfAccounts.code=:code ")
    List<NICReportingChartOfAccounts> findByNBChartOfAccountsCode(@Param("code") String code);

    NICReportingChartOfAccounts findByCode(String code);

}
