package kz.nicnbk.repo.api.lookup;
import kz.nicnbk.repo.model.hf.PerformanceFeeType;
import kz.nicnbk.repo.model.hf.RedemptionFrequencyType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface RedemptionFrequencyTypeRepository extends PagingAndSortingRepository<RedemptionFrequencyType, Long> {
}
