//package kz.nicnbk.repo.api.corpmeetings;
//
//import kz.nicnbk.repo.model.corpmeetings.ICMeetingTopicFiles;
//import kz.nicnbk.repo.model.corpmeetings.ICMeetingTopicTags;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.PagingAndSortingRepository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//public interface ICMeetingTopicTagsRepository extends PagingAndSortingRepository<ICMeetingTopicTags, Long> {
//
//    List<ICMeetingTopicTags> findByIcMeetingTopicId(Long topicId);
//
//    @Modifying
//    @Transactional
//    @Query("DELETE from ic_meeting_topic_tags e WHERE e.icMeetingTopic.id=?1")
//    void deleteByICMeetingTopicId(Long topicId);
//}
