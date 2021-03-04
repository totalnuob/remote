package kz.nicnbk.repo.model.corpmeetings;

import kz.nicnbk.repo.model.base.BaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov
 */

@Entity(name = "ic_meeting_topic_assignment")
public class ICMeetingTopicAssignment extends BaseEntity {

    private ICMeetingTopic icMeetingTopic;
    private String name;
    private String dueDate;
    private String status;
    private boolean closed;

    public ICMeetingTopicAssignment(){}

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ic_meeting_topic_id", nullable = false)
    public ICMeetingTopic getIcMeetingTopic() {
        return icMeetingTopic;
    }

    public void setIcMeetingTopic(ICMeetingTopic icMeetingTopic) {
        this.icMeetingTopic = icMeetingTopic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
