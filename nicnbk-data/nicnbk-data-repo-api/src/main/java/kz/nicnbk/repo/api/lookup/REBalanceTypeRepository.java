package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.reporting.realestate.REBalanceType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhambyl on 27-Mar-17.
 */
public interface REBalanceTypeRepository extends PagingAndSortingRepository<REBalanceType, Long> {

    REBalanceType findByCode(String code);

    REBalanceType findByNameEnIgnoreCase(String name);



}