package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.reporting.privateequity.PEBalanceType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhambyl on 27-Mar-17.
 */
public interface PEBalanceTypeRepository extends PagingAndSortingRepository<PEBalanceType, Long> {

    PEBalanceType findByCode(String code);

    PEBalanceType findByNameEn(String name);



}