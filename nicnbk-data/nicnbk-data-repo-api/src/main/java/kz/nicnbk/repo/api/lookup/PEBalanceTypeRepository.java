package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.reporting.privateequity.PEBalanceType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by zhambyl on 27-Mar-17.
 */
public interface PEBalanceTypeRepository extends PagingAndSortingRepository<PEBalanceType, Integer> {

    PEBalanceType findByCode(String code);

    PEBalanceType findByNameEnIgnoreCase(String name);

    @Query("SELECT e FROM PEBalanceType e WHERE e.parent.code = ?1")
    List<PEBalanceType> findByParentCode(String code);



}