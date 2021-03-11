package kz.nicnbk.repo.api.corpmeetings;

import kz.nicnbk.repo.model.corpmeetings.ICMeetingTopicShare;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by magzumov
 */
public interface ICMeetingTopicShareRepository extends PagingAndSortingRepository<ICMeetingTopicShare, Long> {

    List<ICMeetingTopicShare> findByIcMeetingTopicId(Long id);

    @Modifying
    @Transactional
    @Query("DELETE from ic_meeting_topic_share e WHERE e.icMeetingTopic.id=?1 AND e.department.id=?2")
    void deleteByICMeetingTopicId(Long id, int departmentId);

    ICMeetingTopicShare findByIcMeetingTopicIdAndDepartmentId(Long topicId, int departmentId);
}
