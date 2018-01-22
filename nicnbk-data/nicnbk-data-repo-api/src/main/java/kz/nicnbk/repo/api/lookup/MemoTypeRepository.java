package kz.nicnbk.repo.api.lookup;
import kz.nicnbk.repo.model.m2s2.MemoType;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Pak on 29.09.2017.
 */
public interface MemoTypeRepository extends PagingAndSortingRepository<MemoType, Long> {
}
