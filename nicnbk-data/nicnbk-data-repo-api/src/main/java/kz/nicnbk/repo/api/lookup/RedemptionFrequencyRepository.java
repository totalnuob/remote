package kz.nicnbk.repo.api.lookup;
import kz.nicnbk.repo.model.hf.RedemptionFrequency;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface RedemptionFrequencyRepository extends PagingAndSortingRepository<RedemptionFrequency, Long> {
    RedemptionFrequency findByCode(String code);
}
