package kz.nicnbk.repo.api.pe;

import kz.nicnbk.repo.model.pe.PEIndustry;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhambyl on 14-Nov-16.
 */
public interface IndustryRepository extends PagingAndSortingRepository<PEIndustry, Long> {
    PEIndustry findByCode(String code);
}