package kz.nicnbk.repo.api.corpmeetings;

import kz.nicnbk.repo.model.corpmeetings.ICMeetingTopicDecision;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by magzumov
 */
public interface ICMeetingTopicDecisionRepository extends PagingAndSortingRepository<ICMeetingTopicDecision, Long> {

    List<ICMeetingTopicDecision> findByIcMeetingTopicId(Long id);

    @Modifying
    @Transactional
    @Query("DELETE from ic_meeting_topic_decision e WHERE e.icMeetingTopic.id=?1")
    void deleteByICMeetingTopicId(Long id);
}
