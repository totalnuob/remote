package kz.nicnbk.repo.api.lookup;
import kz.nicnbk.repo.model.hf.ManagerType;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface ManagerTypeRepository extends PagingAndSortingRepository<ManagerType, Long> {
    ManagerType findByCode(String code);
}
