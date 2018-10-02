package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.reporting.privateequity.PEBalanceType;
import kz.nicnbk.repo.model.reporting.privateequity.PEOperationsType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by zhambyl on 27-Mar-17.
 */
public interface PEOperationsTypeRepository extends PagingAndSortingRepository<PEOperationsType, Integer> {

    PEOperationsType findByCode(String code);

    PEOperationsType findByNameEnIgnoreCase(String name);

    @Query("SELECT e FROM PEOperationsType e WHERE e.parent.code = ?1")
    List<PEOperationsType> findByParentCode(String code);

}