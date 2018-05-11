package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.ReserveCalculation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface ReserveCalculationRepository extends PagingAndSortingRepository<ReserveCalculation, Long> {

    @Query("SELECT e from ReserveCalculation e where e.expenseType.code=?1")
    List<ReserveCalculation> getEntitiesByExpenseType(String code);

    @Override
    Iterable<ReserveCalculation> findAll(Sort sort);

    @Query("select e from ReserveCalculation e")
    Page<ReserveCalculation> search(Pageable pageable);

    @Query("SELECT e from ReserveCalculation e where e.expenseType.code=?1 AND e.date >= ?2 AND e.date < ?3")
    List<ReserveCalculation> getEntitiesByExpenseTypeBetweenDates(String code, Date from, Date to);

    @Query("SELECT e from ReserveCalculation e where e.expenseType.code=?1 AND ((e.valueDate is null AND e.date >= ?2 AND e.date < ?3) " +
            " OR (e.valueDate is not null AND e.valueDate >= ?2 AND e.valueDate < ?3))")
    List<ReserveCalculation> getEntitiesByExpenseTypeBetweenDatesUsingValuationDate(String code, Date from, Date to);

    @Query("SELECT count(e) from ReserveCalculation e where e.recipient.code LIKE CONCAT(?1,'%')")
    int getEntitiesCountByRecipientTypeStartsWith(String codeStart);
}
