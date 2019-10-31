package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.reporting.realestate.RETrancheType;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RETrancheTypeRepository extends PagingAndSortingRepository<RETrancheType, Long> {

    RETrancheType findByCode(String code);

    RETrancheType findByNameEnIgnoreCase(String name);



}