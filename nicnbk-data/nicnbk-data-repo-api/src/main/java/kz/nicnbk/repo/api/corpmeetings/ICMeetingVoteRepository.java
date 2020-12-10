package kz.nicnbk.repo.api.corpmeetings;

import kz.nicnbk.repo.model.corpmeetings.ICMeetingVote;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Created by magzumov.
 */
public interface ICMeetingVoteRepository extends PagingAndSortingRepository<ICMeetingVote, Long> {
    List<ICMeetingVote> findByIcMeetingTopicId(Long id);

    @Modifying
    @Transactional
    @Query("DELETE from ICMeetingVote e WHERE e.icMeetingTopic.id=?1 AND e.employee.id=?2")
    void deleteByTopicIdAndUserId(Long topicId, Long employeeId);

    @Modifying
    @Transactional
    @Query("DELETE from ICMeetingVote e WHERE e.icMeetingTopic.id=?1")
    void deleteByTopicId(Long topicId);

}
