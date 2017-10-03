package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.reporting.hedgefunds.HFFinancialStatementType;
import kz.nicnbk.repo.model.reporting.privateequity.PEBalanceType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhambyl on 27-Mar-17.
 */
public interface HFFinancialStatementTypeRepository extends PagingAndSortingRepository<HFFinancialStatementType, Long> {

    HFFinancialStatementType findByCode(String code);

    HFFinancialStatementType findByNameEnIgnoreCase(String name);



}