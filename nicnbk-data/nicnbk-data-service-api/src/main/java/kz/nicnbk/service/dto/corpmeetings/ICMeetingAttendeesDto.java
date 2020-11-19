package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by magzumov on 04-Aug-16.
 */
public class ICMeetingAttendeesDto implements BaseDto, Comparable {

    private ICMeetingDto icMeeting;
    private EmployeeDto employee;
    private boolean present;
    private String absenceType;

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

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public String getAbsenceType() {
        return absenceType;
    }

    public void setAbsenceType(String absenceType) {
        this.absenceType = absenceType;
    }

    @Override
    public int compareTo(Object o) {
        int thisPosition = 0;
        if(this.getEmployee() != null && this.getEmployee().getPosition() != null){
            if(this.getEmployee().getPosition().getCode() != null && this.getEmployee().getPosition().getCode().equalsIgnoreCase("CEO")){
                thisPosition = 100;
            }else if(this.getEmployee().getPosition().getCode() != null && this.getEmployee().getPosition().getCode().equalsIgnoreCase("DEP_CEO")){
                thisPosition = 10;
            }else{
                thisPosition = 1;
            }
        }
        ICMeetingAttendeesDto other = (ICMeetingAttendeesDto)o;
        int otherPosition = 0;
        if(other.getEmployee() != null && other.getEmployee().getPosition() != null){
            if(other.getEmployee().getPosition().getCode() != null && other.getEmployee().getPosition().getCode().equalsIgnoreCase("CEO")){
                otherPosition = 100;
            }else if(other.getEmployee().getPosition().getCode() != null && other.getEmployee().getPosition().getCode().equalsIgnoreCase("DEP_CEO")){
                otherPosition = 10;
            }else{
                otherPosition = 1;
            }
        }
        return otherPosition - thisPosition;
    }
}

