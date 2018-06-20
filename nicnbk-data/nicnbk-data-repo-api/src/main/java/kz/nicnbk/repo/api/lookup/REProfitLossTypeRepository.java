package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.reporting.realestate.REBalanceType;
import kz.nicnbk.repo.model.reporting.realestate.REProfitLossType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhambyl on 27-Mar-17.
 */
public interface REProfitLossTypeRepository extends PagingAndSortingRepository<REProfitLossType, Long> {

    REProfitLossType findByCode(String code);

    REProfitLossType findByNameEnIgnoreCase(String name);



}