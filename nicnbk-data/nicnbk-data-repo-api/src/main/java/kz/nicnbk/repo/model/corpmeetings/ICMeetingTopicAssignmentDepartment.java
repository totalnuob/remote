package kz.nicnbk.repo.model.corpmeetings;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.employee.Department;
import kz.nicnbk.repo.model.employee.Employee;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov
 */

@Entity(name = "ic_meeting_topic_assignment_dept")
public class ICMeetingTopicAssignmentDepartment extends BaseEntity {

    private ICMeetingTopicAssignment assignment;
    private Department department;

    public ICMeetingTopicAssignmentDepartment(){}


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="assignment_id", nullable = false)
    public ICMeetingTopicAssignment getAssignment() {
        return assignment;
    }

    public void setAssignment(ICMeetingTopicAssignment assignment) {
        this.assignment = assignment;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="department_id", nullable = false)
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
