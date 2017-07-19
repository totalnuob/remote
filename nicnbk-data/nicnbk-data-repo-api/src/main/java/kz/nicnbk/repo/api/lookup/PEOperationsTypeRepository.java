package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.reporting.privateequity.PEBalanceType;
import kz.nicnbk.repo.model.reporting.privateequity.PEOperationsType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhambyl on 27-Mar-17.
 */
public interface PEOperationsTypeRepository extends PagingAndSortingRepository<PEOperationsType, Long> {

    PEOperationsType findByCode(String code);

    PEOperationsType findByNameEn(String name);



}