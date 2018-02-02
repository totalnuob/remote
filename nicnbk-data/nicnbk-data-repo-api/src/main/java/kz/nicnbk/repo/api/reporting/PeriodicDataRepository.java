package kz.nicnbk.repo.api.reporting;

import kz.nicnbk.repo.model.reporting.PeriodicData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

/**
 * Created by magzumov on 20.04.2017.
 */
public interface PeriodicDataRepository extends PagingAndSortingRepository<PeriodicData, Long> {

    @Query("SELECT e from PeriodicData e where e.date=?1 and e.type.code=?2")
    PeriodicData getByDateAndType(Date date, String code);
}
