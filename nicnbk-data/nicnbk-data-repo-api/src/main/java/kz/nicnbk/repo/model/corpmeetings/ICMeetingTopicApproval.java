package kz.nicnbk.repo.model.corpmeetings;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.files.Files;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov
 */

@Entity(name = "ic_meeting_topic_approval")
public class ICMeetingTopicApproval extends BaseEntity {

    private ICMeetingTopic icMeetingTopic;
    private Employee employee;
    private Boolean approved;
    private Date approveDate;
    private String hash;

    public ICMeetingTopicApproval(){}

    public ICMeetingTopicApproval(Long topicId, Long employeeId, boolean approved){
        this.icMeetingTopic = new ICMeetingTopic();
        this.icMeetingTopic.setId(topicId);
        this.employee = new Employee();
        this.employee.setId(employeeId);
        this.approved = approved;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ic_meeting_topic_id", nullable = false)
    public ICMeetingTopic getIcMeetingTopic() {
        return icMeetingTopic;
    }

    public void setIcMeetingTopic(ICMeetingTopic icMeetingTopic) {
        this.icMeetingTopic = icMeetingTopic;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="employee_id", nullable = false)
    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Column(name="approved")
    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    @Column(name = "approve_date"/*, nullable = false*/)
    @Temporal(value = TemporalType.TIMESTAMP)
    public Date getApproveDate() {
        return approveDate;
    }

    public void setApproveDate(Date approveDate) {
        this.approveDate = approveDate;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
