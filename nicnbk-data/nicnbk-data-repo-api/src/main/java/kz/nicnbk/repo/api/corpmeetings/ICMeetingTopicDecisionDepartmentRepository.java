//package kz.nicnbk.repo.api.corpmeetings;
//
//import kz.nicnbk.repo.model.corpmeetings.ICMeetingTopicDecisionDepartment;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.PagingAndSortingRepository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
///**
// * Created by magzumov
// */
//@Deprecated
//public interface ICMeetingTopicDecisionDepartmentRepository extends PagingAndSortingRepository<ICMeetingTopicDecisionDepartment, Long> {
//
//    List<ICMeetingTopicDecisionDepartment> findByDecisionId(Long id);
//
//    @Modifying
//    @Transactional
//    @Query("DELETE from ic_meeting_topic_decision_dept e WHERE e.decision.id=?1")
//    void deleteByDecisionId(Long id);
//
//}
