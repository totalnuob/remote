package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.PeriodicData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface PeriodicDataRepository extends PagingAndSortingRepository<PeriodicData, Long> {

    @Query("SELECT e from PeriodicData e where e.date=?1 and e.type.code=?2")
    PeriodicData getByDateAndType(Date date, String code);

    // Date parameters are required
    @Query("select e from PeriodicData e where (e.type.code=?3 or ?3 is null) " +
            " and (e.date >= ?1 AND e.date <= ?2)" +
            " ORDER BY e.date DESC, e.type.code DESC")
    Page<PeriodicData> search(@Temporal(TemporalType.DATE) Date dateFrom, @Temporal(TemporalType.DATE) Date dateTo, String typeCode, Pageable pageable);

}
