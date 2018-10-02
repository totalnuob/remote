package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.reporting.privateequity.PEBalanceType;
import kz.nicnbk.repo.model.reporting.privateequity.PECashflowsType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by zhambyl on 27-Mar-17.
 */
public interface PECashflowsTypeRepository extends PagingAndSortingRepository<PECashflowsType, Integer> {

    PECashflowsType findByCode(String code);

    PECashflowsType findByNameEnIgnoreCase(String name);

    @Query("SELECT e FROM PECashflowsType e WHERE e.parent.code = ?1")
    List<PECashflowsType> findByParentCode(String code);

}