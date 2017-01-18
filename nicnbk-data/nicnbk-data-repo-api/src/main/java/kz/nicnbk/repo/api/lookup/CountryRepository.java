package kz.nicnbk.repo.api.lookup;
import kz.nicnbk.repo.model.common.Country;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface CountryRepository extends PagingAndSortingRepository<Country, Long> {

    Country findByCode(String code);
}
