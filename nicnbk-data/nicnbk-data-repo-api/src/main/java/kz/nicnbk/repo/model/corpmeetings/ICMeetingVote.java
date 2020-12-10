package kz.nicnbk.repo.model.corpmeetings;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.base.DataConstraints;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.files.Files;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov.
 */

@Entity
@Table(name = "ic_meeting_vote")
public class ICMeetingVote extends CreateUpdateBaseEntity{

    private ICMeetingTopic icMeetingTopic;
    private Employee employee;
    private ICMeetingVoteType vote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    public ICMeetingTopic getIcMeetingTopic() {
        return icMeetingTopic;
    }

    public void setIcMeetingTopic(ICMeetingTopic icMeetingTopic) {
        this.icMeetingTopic = icMeetingTopic;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", nullable = false)
    public ICMeetingVoteType getVote() {
        return vote;
    }

    public void setVote(ICMeetingVoteType vote) {
        this.vote = vote;
    }
}