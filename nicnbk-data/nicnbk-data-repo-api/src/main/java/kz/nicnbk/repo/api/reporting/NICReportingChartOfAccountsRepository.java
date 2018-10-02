package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.NICReportingChartOfAccounts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface NICReportingChartOfAccountsRepository extends PagingAndSortingRepository<NICReportingChartOfAccounts, Integer> {

    @Query("select entity from NICReportingChartOfAccounts entity where entity.nbChartOfAccounts.code=:code ORDER BY  entity.code ASC")
    List<NICReportingChartOfAccounts> findByNBChartOfAccountsCode(@Param("code") String code);

    @Query("select entity from NICReportingChartOfAccounts entity where entity.code=?1 ORDER BY entity.code ASC")
    NICReportingChartOfAccounts findByCode(String code);

    @Query("select e from NICReportingChartOfAccounts e where (e.nbChartOfAccounts.code=?1 or ?1 is null)" +
            " ORDER BY e.code ASC")
    Page<NICReportingChartOfAccounts> searchByNBCode(String code, Pageable pageable);

}
