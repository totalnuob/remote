package kz.nicnbk.repo.api.lookup;
import kz.nicnbk.repo.model.hf.PerformanceFeePayFrequencyType;
import kz.nicnbk.repo.model.hf.PerformanceFeeType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface PerformanceFeePayFrequencyRepository extends PagingAndSortingRepository<PerformanceFeePayFrequencyType, Long> {
    PerformanceFeePayFrequencyType findByCode(String code);
}
