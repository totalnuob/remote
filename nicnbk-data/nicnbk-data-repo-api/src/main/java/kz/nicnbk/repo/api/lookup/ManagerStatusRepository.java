package kz.nicnbk.repo.api.lookup;
import kz.nicnbk.repo.model.hf.FundStatus;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface ManagerStatusRepository extends PagingAndSortingRepository<FundStatus, Long> {
}
