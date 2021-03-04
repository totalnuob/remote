package kz.nicnbk.repo.model.corpmeetings;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.employee.Department;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by magzumov
 */

@Entity(name = "ic_meeting_topic_decision_dept")
public class ICMeetingTopicDecisionDepartment extends BaseEntity {

    private ICMeetingTopicDecision decision;
    private Department department;

    public ICMeetingTopicDecisionDepartment(){}


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="decision_id", nullable = false)
    public ICMeetingTopicDecision getDecision() {
        return decision;
    }

    public void setDecision(ICMeetingTopicDecision decision) {
        this.decision = decision;
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
