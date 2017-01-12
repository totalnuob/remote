package kz.nicnbk.repo.api.lookup;
import kz.nicnbk.repo.model.hf.ManagementFeeType;
import kz.nicnbk.repo.model.hf.PerformanceFeeType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface PerformanceFeeTypeRepository extends PagingAndSortingRepository<PerformanceFeeType, Long> {
    PerformanceFeeType findByCode(String code);
}
