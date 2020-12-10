package kz.nicnbk.repo.api.corpmeetings;

import kz.nicnbk.repo.model.corpmeetings.ICMeetingTopicApproval;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by magzumov
 */
public interface ICMeetingTopicApprovalRepository extends PagingAndSortingRepository<ICMeetingTopicApproval, Long> {

    List<ICMeetingTopicApproval> findByIcMeetingTopicId(Long id);

    @Modifying
    @Transactional
    @Query("DELETE from ic_meeting_topic_approval e WHERE e.icMeetingTopic.id=?1")
    void deleteByICMeetingTopicId(Long id);

    ICMeetingTopicApproval findByIcMeetingTopicIdAndEmployeeId(Long topicId, Long employeeId);
}
