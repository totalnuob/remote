//package kz.nicnbk.repo.api.corpmeetings;
//
//import kz.nicnbk.repo.model.corpmeetings.ICMeetingAttendees;
//import kz.nicnbk.repo.model.corpmeetings.ICMeetingInvitees;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.PagingAndSortingRepository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//
///**
// * Created by magzumov.
// */
//public interface ICMeetingInviteesRepository extends PagingAndSortingRepository<ICMeetingInvitees, Long> {
//
//    @Modifying
//    @Transactional
//    @Query("DELETE from ic_meeting_invitees e WHERE e.icMeeting.id=?1")
//    void deleteByICMeetingId(Long id);
//
//    List<ICMeetingInvitees> findByIcMeetingId(Long icMeetingId);
//
//}
