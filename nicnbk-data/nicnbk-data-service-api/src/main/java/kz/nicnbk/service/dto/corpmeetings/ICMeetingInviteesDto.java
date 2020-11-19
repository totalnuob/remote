package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.service.dto.employee.EmployeeDto;

/**
 * Created by magzumov on 04-Aug-16.
 */
public class ICMeetingInviteesDto implements BaseDto {

    private ICMeetingDto icMeeting;
    private EmployeeDto employee;

    public ICMeetingDto getIcMeeting() {
        return icMeeting;
    }

    public void setIcMeeting(ICMeetingDto icMeeting) {
        this.icMeeting = icMeeting;
    }

    public EmployeeDto getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeDto employee) {
        this.employee = employee;
    }

}

