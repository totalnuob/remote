package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.reporting.privateequity.PEInvestmentType;
import kz.nicnbk.repo.model.tripmemo.TripType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhambyl on 27-Mar-17.
 */
public interface PEInvestmentTypeRepository extends PagingAndSortingRepository<PEInvestmentType, Long> {

    PEInvestmentType findByCode(String code);

    PEInvestmentType findByNameEnIgnoreCase(String name);



}