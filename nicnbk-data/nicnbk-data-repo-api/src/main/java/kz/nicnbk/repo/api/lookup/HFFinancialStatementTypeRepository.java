package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.reporting.hedgefunds.FinancialStatementCategory;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhambyl on 27-Mar-17.
 */
public interface HFFinancialStatementTypeRepository extends PagingAndSortingRepository<FinancialStatementCategory, Long> {

    FinancialStatementCategory findByCode(String code);

    FinancialStatementCategory findByNameEnIgnoreCase(String name);



}