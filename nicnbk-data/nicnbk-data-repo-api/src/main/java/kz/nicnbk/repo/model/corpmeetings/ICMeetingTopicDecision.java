package kz.nicnbk.repo.model.corpmeetings;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.employee.Department;
import kz.nicnbk.repo.model.employee.Employee;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by magzumov
 */

@Entity(name = "ic_meeting_topic_decision")
public class ICMeetingTopicDecision extends BaseEntity {

    private ICMeetingTopic icMeetingTopic;
    private String name;
    private ICMeetingTopicDecisionType type;
    private int order;

    private List<Department> departments;

    public ICMeetingTopicDecision(){}

    public ICMeetingTopicDecision(int order, String name, ICMeetingTopicDecisionType type){
        this.order = order;
        this.name = name;
        this.type = type;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ic_meeting_topic_id", nullable = false)
    public ICMeetingTopic getIcMeetingTopic() {
        return icMeetingTopic;
    }

    public void setIcMeetingTopic(ICMeetingTopic icMeetingTopic) {
        this.icMeetingTopic = icMeetingTopic;
    }

    @Column(name="name", columnDefinition="TEXT")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="decision_type_id", nullable = false)
    public ICMeetingTopicDecisionType getType() {
        return type;
    }

    public void setType(ICMeetingTopicDecisionType type) {
        this.type = type;
    }

    @Column(name="order_num", nullable = false)
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="ic_meeting_topic_decision_depts",
            joinColumns=
            @JoinColumn(name="decision_id", referencedColumnName="ID"),
            inverseJoinColumns=
            @JoinColumn(name="department_id", referencedColumnName="ID")
    )
    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
}
