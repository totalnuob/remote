package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.BaseDto;


public class ICMeetingTopicAssignmentDto implements BaseDto {

    private ICMeetingTopicDto icMeetingTopic;
    private String name;
    private String dueDate;
    private String status;
    private boolean closed;

    public ICMeetingTopicDto getIcMeetingTopic() {
        return icMeetingTopic;
    }

    public void setIcMeetingTopic(ICMeetingTopicDto icMeetingTopic) {
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

