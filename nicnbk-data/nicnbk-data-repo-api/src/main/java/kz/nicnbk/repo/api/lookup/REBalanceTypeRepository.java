package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.reporting.privateequity.PEBalanceType;
import kz.nicnbk.repo.model.reporting.realestate.REBalanceType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by zhambyl on 27-Mar-17.
 */
public interface REBalanceTypeRepository extends PagingAndSortingRepository<REBalanceType, Integer> {

    REBalanceType findByCode(String code);

    REBalanceType findByNameEnIgnoreCase(String name);

    @Query("SELECT e FROM REBalanceType e WHERE e.parent.code = ?1")
    List<REBalanceType> findByParentCode(String code);

}