package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.reporting.privateequity.PEBalanceType;
import kz.nicnbk.repo.model.reporting.realestate.REBalanceType;
import kz.nicnbk.repo.model.reporting.realestate.REProfitLossType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by zhambyl on 27-Mar-17.
 */
public interface REProfitLossTypeRepository extends PagingAndSortingRepository<REProfitLossType, Integer> {

    REProfitLossType findByCode(String code);

    REProfitLossType findByNameEnIgnoreCase(String name);

    @Query("SELECT e FROM REProfitLossType e WHERE e.parent.code = ?1")
    List<REProfitLossType> findByParentCode(String code);

}