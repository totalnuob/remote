package kz.nicnbk.service.dto.tripmemo;

import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.tripmemo.TripMemo;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.files.FilesDto;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Set;

/**
 * Created by zhambyl on 04-Aug-16.
 */
public class TripMemoDto extends CreateUpdateBaseEntityDto<TripMemo> {

    private Long id;
    private String tripType;
    private String name;

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date meetingDateStart;

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date meetingDateEnd;

    private String organization;
    private String location;
    private Set<EmployeeDto> attendees;
    private String status;
    private String description;

    private String author;
    private String owner;

    private Set<FilesDto> files;

    // TODO: apply inheritance similar to entity classes
    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date creationDate;

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getMeetingDateStart() {
        return meetingDateStart;
    }

    public void setMeetingDateStart(Date meetingDateStart) {
        this.meetingDateStart = meetingDateStart;
    }

    public Date getMeetingDateEnd() {
        return meetingDateEnd;
    }

    public void setMeetingDateEnd(Date meetingDateEnd) {
        this.meetingDateEnd = meetingDateEnd;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Set<EmployeeDto> getAttendees() {
        return attendees;
    }

    public void setAttendees(Set<EmployeeDto> attendees) {
        this.attendees = attendees;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Set<FilesDto> getFiles() {
        return files;
    }

    public void setFiles(Set<FilesDto> files) {
        this.files = files;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}

