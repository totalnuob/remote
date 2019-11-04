package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.common.Strategy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface StrategyRepository extends PagingAndSortingRepository<Strategy, Long> {

    List<Strategy> findByGroupType(int groupType);

    @Query("SELECT e FROM Strategy e ORDER BY groupType, nameEn)")
    List<Strategy> findByAllGroups();

    Strategy findByCode(String code);

    @Query("SELECT e FROM Strategy e WHERE LOWER(e.nameEn)=LOWER(?1) AND (e.groupType=?2)")
    Strategy findByNameEnAndGroupType(String nameEn, int groupType);
}
