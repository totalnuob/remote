package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.NBChartOfAccounts;
import kz.nicnbk.repo.model.reporting.PeriodicData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface NBChartOfAccountsRepository extends PagingAndSortingRepository<NBChartOfAccounts, Long> {

}
