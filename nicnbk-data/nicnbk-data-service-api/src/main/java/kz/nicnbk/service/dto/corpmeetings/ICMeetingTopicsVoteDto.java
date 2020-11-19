package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.service.dto.employee.EmployeeDto;


public class ICMeetingTopicsVoteDto implements BaseDto {
    private Long icMeetingTopicId;
    private EmployeeDto employee;
    private String vote;

    public Long getIcMeetingTopicId() {
        return icMeetingTopicId;
    }

    public void setIcMeetingTopicId(Long icMeetingTopicId) {
        this.icMeetingTopicId = icMeetingTopicId;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public EmployeeDto getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeDto employee) {
        this.employee = employee;
    }
}

