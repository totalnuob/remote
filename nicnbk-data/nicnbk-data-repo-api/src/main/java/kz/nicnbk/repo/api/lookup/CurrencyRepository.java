package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.common.Currency;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 04.08.2016.
 */
public interface CurrencyRepository extends PagingAndSortingRepository<Currency, Long> {
}
