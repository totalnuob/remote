package kz.nicnbk.repo.api.corpmeetings;

import kz.nicnbk.repo.model.corpmeetings.CorpMeetingType;
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

    @Query("select e from ICMeeting e where e.deleted is null or e.deleted=false" +
            " ORDER BY e.number DESC")
    Page<ICMeeting> searchAll(Pageable pageable);

    @Query("select e from ICMeeting e where (e.corpMeetingType=?1 or ?1 is null) and (e.deleted is null or e.deleted=false)")
    Page<ICMeeting> searchAllByType(CorpMeetingType type, Pageable pageable);

    @Query("select e from ICMeeting e where " +
            " (e.date IS NOT NULL AND e.date >= ?1 AND e.date <= ?2) " +
            " AND (e.closed is null or e.closed=false)" +
            " AND (e.deleted is null or e.deleted=false)" +
            " ORDER BY e.number DESC")
    List<ICMeeting> getOpenICMeetingsWithinDates(Date dateFrom, Date dateTo);

    @Query("select e from ICMeeting e where " +
            " (e.number=?1 or ?1 is null)" +
            " and (e.date >= ?2 AND e.date <= ?3) " +
            " and (e.deleted is null or e.deleted=false)" +
            " ORDER BY e.number DESC")
    Page<ICMeeting> search(String number, @Temporal(TemporalType.DATE) Date dateFrom,@Temporal(TemporalType.DATE) Date dateTo,
                                     Pageable pageable);

    @Query("select e from ICMeeting e where " +
            " (e.number=?1 or ?1 is null)" +
            " and (e.corpMeetingType=?2 or ?1 is null)" +
            " and (e.date >= ?3 AND e.date <=?4)" +
            " and (e.deleted is null or e.deleted=false)" +
            " ORDER BY e.number DESC")
    Page<ICMeeting> searchByType(String number, String type, @Temporal(TemporalType.DATE) Date dateFrom,
                                 @Temporal(TemporalType.DATE) Date dateTo, Pageable pageable);

    @Query("select e from ICMeeting e where e.number=?1 AND (e.deleted is null OR e.deleted=false)" +
            " ORDER BY e.number DESC")
    List<ICMeeting> findByNumberAndNotDeleted(String number);

    @Query("select e from ICMeeting e where (e.closed is null OR e.closed=false) AND (e.deleted is null OR e.deleted=false)")
    List<ICMeeting> findNotClosed();

}
