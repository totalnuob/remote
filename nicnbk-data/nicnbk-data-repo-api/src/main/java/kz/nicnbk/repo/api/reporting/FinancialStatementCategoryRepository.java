package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.hedgefunds.FinancialStatementCategory;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface FinancialStatementCategoryRepository extends PagingAndSortingRepository<FinancialStatementCategory, Long> {

}
