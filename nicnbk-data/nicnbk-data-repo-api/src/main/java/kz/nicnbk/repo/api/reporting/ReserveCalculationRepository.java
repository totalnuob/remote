package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.ReserveCalculation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.persistence.TemporalType;
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

    // Date parameters are required
    @Query("SELECT e FROM ReserveCalculation e WHERE " +
            " (e.expenseType.code=?1 OR ?1 is null) " +
            " AND (e.source.code=?2 OR ?2 is null) " +
            " AND (e.recipient.code=?3 OR ?3 is null) " +
            " AND ((e.valueDate IS NOT NULL AND e.valueDate >= ?4 AND e.valueDate <= ?5) OR " +
            " (e.valueDate IS NULL AND e.date >= ?4 AND e.date <= ?5))" +
            " ORDER BY e.date DESC")
    Page<ReserveCalculation> search(String expenseType,
                                     String sourceType,
                                     String destinationType,
                                     @Temporal(TemporalType.DATE) Date dateFrom,
                                     @Temporal(TemporalType.DATE) Date dateTo,
                                     Pageable pageable);

    @Query("SELECT DISTINCT e from ReserveCalculation e where (?1 is null OR e.expenseType.code=?1) AND e.date >= ?2 AND e.date < ?3")
    List<ReserveCalculation> getEntitiesByExpenseTypeBetweenDates(String code, Date from, Date to);

    @Query("SELECT DISTINCT e from ReserveCalculation e where (?1 is null OR e.expenseType.code=?1) AND ((e.valueDate is null AND e.date >= ?2 AND e.date < ?3) " +
            " OR (e.valueDate is not null AND e.valueDate >= ?2 AND e.valueDate < ?3))")
    List<ReserveCalculation> getEntitiesByExpenseTypeBetweenDatesUsingValuationDate(String code, Date from, Date to);

    @Query("SELECT count(e) from ReserveCalculation e where e.recipient.code LIKE CONCAT(?1,'%')")
    int getEntitiesCountByRecipientTypeStartsWith(String codeStart);
}
