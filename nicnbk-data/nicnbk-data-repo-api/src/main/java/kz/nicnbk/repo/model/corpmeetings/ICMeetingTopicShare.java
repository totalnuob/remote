package kz.nicnbk.repo.model.corpmeetings;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.employee.Department;
import kz.nicnbk.repo.model.employee.Employee;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov
 */

@Entity(name = "ic_meeting_topic_share")
public class ICMeetingTopicShare extends BaseEntity {

    private ICMeetingTopic icMeetingTopic;
    private Department department;

    public ICMeetingTopicShare(){}


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ic_meeting_topic_id", nullable = false)
    public ICMeetingTopic getIcMeetingTopic() {
        return icMeetingTopic;
    }

    public void setIcMeetingTopic(ICMeetingTopic icMeetingTopic) {
        this.icMeetingTopic = icMeetingTopic;
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
