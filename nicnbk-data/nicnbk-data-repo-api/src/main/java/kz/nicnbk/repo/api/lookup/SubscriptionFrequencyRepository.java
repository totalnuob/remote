package kz.nicnbk.repo.api.lookup;
import kz.nicnbk.repo.model.hf.LegalStructure;
import kz.nicnbk.repo.model.hf.SubscriptionFrequency;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface SubscriptionFrequencyRepository extends PagingAndSortingRepository<SubscriptionFrequency, Long> {
    SubscriptionFrequency findByCode(String code);
}
