package kz.nicnbk.repo.api.lookup;
import kz.nicnbk.repo.model.hf.RedemptionFrequencyType;
import kz.nicnbk.repo.model.hf.RedemptionNotificationPeriodType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface RedemptionNotificationPeriodRepository extends PagingAndSortingRepository<RedemptionNotificationPeriodType, Long> {
}
