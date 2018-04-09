package kz.nicnbk.repo.api.corpmeetings;

import kz.nicnbk.repo.model.corpmeetings.CorpMeeting;
import kz.nicnbk.repo.model.tripmemo.TripMemo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;


/**
 * Created by magzumov.
 */
public interface CorpMeetingsRepository extends PagingAndSortingRepository<CorpMeeting, Long> {

    /**
     * Return all entities ordered by id descending
     *
     * @return - all entities
     */
    List<CorpMeeting> findAllByOrderByIdDesc();

    /**
     * Return entities page.
     *
     * @param pageable - page params
     * @return - page
     */
    Page<CorpMeeting> findAllByOrderByDateDesc(Pageable pageable);


    @Query("select e from CorpMeeting e where " +
            " (e.type.code=?1 or ?1 is null) " +
            " and (e.number=?2 or ?2 is null)" +
            " and (LOWER(e.shortName) LIKE %" + "?3" + "% or ?3 is null or ?3 = '')" +
            " and (e.date >= ?4 AND e.date <= ?5)" +
            " ORDER BY e.date DESC")
    Page<CorpMeeting> findWithoutDates(String type,
                                       String number,
                                       String searchText,
                                       @Temporal(TemporalType.DATE) Date dateFrom,
                                       @Temporal(TemporalType.DATE) Date dateTo,
                                       Pageable pageable);


}
