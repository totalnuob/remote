package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.common.Strategy;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface StrategyRepository extends PagingAndSortingRepository<Strategy, Long> {
    List<Strategy> findByGroupType(int groupType);

    Strategy findByCode(String code);

    Strategy findByNameEnAndGroupType(String nameEn, int groupType);
}
