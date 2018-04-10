package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.corpmeetings.CorpMeeting;
import kz.nicnbk.repo.model.corpmeetings.CorpMeetingType;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.tripmemo.TripMemo;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.files.FilesDto;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.Set;

/**
 * Created by zhambyl on 04-Aug-16.
 */
public class CorpMeetingDto extends CreateUpdateBaseEntityDto<CorpMeeting> {

    private String type;
    private String number;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date date;

    private String shortName;
    private String agenda;

    private Set<EmployeeDto> attendeesNIC;
    private String attendeesOther;

    private Set<FilesDto> files;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    public Set<EmployeeDto> getAttendeesNIC() {
        return attendeesNIC;
    }

    public void setAttendeesNIC(Set<EmployeeDto> attendeesNIC) {
        this.attendeesNIC = attendeesNIC;
    }

    public String getAttendeesOther() {
        return attendeesOther;
    }

    public void setAttendeesOther(String attendeesOther) {
        this.attendeesOther = attendeesOther;
    }

    public Set<FilesDto> getFiles() {
        return files;
    }

    public void setFiles(Set<FilesDto> files) {
        this.files = files;
    }
}

