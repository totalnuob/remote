package kz.nicnbk.repo.api.corpmeetings;

import kz.nicnbk.repo.model.corpmeetings.ICMeeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;


/**
 * Created by magzumov.
 */
public interface ICMeetingsRepository extends PagingAndSortingRepository<ICMeeting, Long> {

    @Query("select e from ICMeeting e where " +
            " (e.number=?1 or ?1 is null)" +
            " and (e.date >= ?2 AND e.date <= ?3)" +
            " ORDER BY e.number DESC")
    Page<ICMeeting> search(String number,@Temporal(TemporalType.DATE) Date dateFrom,@Temporal(TemporalType.DATE) Date dateTo,
                                     Pageable pageable);

    List<ICMeeting> findByNumber(String number);
}
