package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.reporting.privateequity.PECashflowsType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhambyl on 27-Mar-17.
 */
public interface PECashflowsTypeRepository extends PagingAndSortingRepository<PECashflowsType, Long> {

    PECashflowsType findByCode(String code);

    PECashflowsType findByNameEn(String name);



}