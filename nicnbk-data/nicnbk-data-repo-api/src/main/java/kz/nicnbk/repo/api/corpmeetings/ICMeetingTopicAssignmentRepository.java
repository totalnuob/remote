package kz.nicnbk.repo.api.corpmeetings;

import kz.nicnbk.repo.model.corpmeetings.ICMeetingTopicAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 * Created by magzumov
 */
public interface ICMeetingTopicAssignmentRepository extends PagingAndSortingRepository<ICMeetingTopicAssignment, Long> {


    @Modifying
    @Transactional
    @Query("DELETE from ic_meeting_topic_assignment e WHERE e.icMeetingTopic.id=?1")
    void deleteByTopicId(Long id);

    @Query("SELECT DISTINCT e FROM ic_meeting_topic_assignment e LEFT JOIN e.icMeetingTopic topic LEFT JOIN topic.icMeeting ic " +
            " LEFT JOIN e.departments dept WHERE " +
            " (:hideClosed=false OR e.closed=false) AND " +
            " (:departmentId=0 OR :departmentId=dept.id) AND " +
            " (ic.id IS NULL OR (topic.icMeeting.date >= :dateFrom AND topic.icMeeting.date <= :dateTo)) AND " +
            " (:icNumber IS NULL OR :icNumber='' OR  LOWER(topic.icMeeting.number)=LOWER(:icNumber)) AND " +
            " (:searchText IS NULL OR :searchText='' OR LOWER(e.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            " LOWER(e.status) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            " LOWER(topic.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            " LOWER(topic.description) LIKE LOWER(CONCAT('%', :searchText, '%')))) "
    )
    Page<ICMeetingTopicAssignment> searchAssignments(@Param("departmentId") Integer departmentId,
                                                     @Param("dateFrom") @Temporal(TemporalType.DATE) Date dateFrom,
                                                     @Param("dateTo") @Temporal(TemporalType.DATE) Date dateTo,
                                                     @Param("searchText") String searchText,
                                                     @Param("icNumber")String icNumber,
                                                     @Param("hideClosed")boolean hideClosed,
                                                     Pageable pageable);

    @Query("SELECT e FROM ic_meeting_topic_assignment e JOIN FETCH e.departments " +
            " WHERE (e.closed IS NULL OR e.closed=false) AND e.dateDue = :date")
    List<ICMeetingTopicAssignment> searchOpenAssignmentsDueThisDay( @Param("date") @Temporal(TemporalType.DATE) Date date);
}
