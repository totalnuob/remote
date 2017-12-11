package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.ReserveCalculation;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface ReserveCalculationRepository extends PagingAndSortingRepository<ReserveCalculation, Long> {

    @Query("SELECT e from ReserveCalculation e where e.expenseType.code=?1")
    List<ReserveCalculation> getEntitiesByExpenseType(String code);

    @Override
    Iterable<ReserveCalculation> findAll(Sort sort);
}
