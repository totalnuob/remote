package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.reporting.privateequity.PETrancheType;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PETrancheTypeRepository extends PagingAndSortingRepository<PETrancheType, Long> {

    PETrancheType findByCode(String code);

    PETrancheType findByNameEnIgnoreCase(String name);

}