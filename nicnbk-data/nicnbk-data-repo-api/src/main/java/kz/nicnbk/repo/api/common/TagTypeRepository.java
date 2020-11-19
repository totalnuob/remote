package kz.nicnbk.repo.api.common;

import kz.nicnbk.repo.model.common.CurrencyRates;
import kz.nicnbk.repo.model.common.TagType;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface TagTypeRepository extends PagingAndSortingRepository<TagType, Long> {

    TagType findByCode(String code);

}
