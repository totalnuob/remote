package kz.nicnbk.repo.api.corpmeetings;

import kz.nicnbk.repo.model.corpmeetings.ICMeeting;
import kz.nicnbk.repo.model.corpmeetings.ICMeetingAttendees;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;


/**
 * Created by magzumov.
 */
public interface ICMeetingAttendeesRepository extends PagingAndSortingRepository<ICMeetingAttendees, Long> {

    @Modifying
    @Transactional
    @Query("DELETE from ic_meeting_attendees e WHERE e.icMeeting.id=?1")
    void deleteByICMeetingId(Long id);

    List<ICMeetingAttendees> findByIcMeetingId(Long icMeetingId);
}
