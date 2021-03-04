package kz.nicnbk.repo.api.corpmeetings;

import kz.nicnbk.repo.model.corpmeetings.ICMeetingTopicAssignmentDepartment;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by magzumov
 */
public interface ICMeetingTopicAssignmentDepartmentRepository extends PagingAndSortingRepository<ICMeetingTopicAssignmentDepartment, Long> {

    List<ICMeetingTopicAssignmentDepartment> findByDepartmentId(int id);
}
