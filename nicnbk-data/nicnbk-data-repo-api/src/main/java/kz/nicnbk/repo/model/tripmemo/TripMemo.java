package kz.nicnbk.repo.model.tripmemo;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.employee.Employee;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by zhambyl on 04-Aug-16.
 */

@Entity
@Table(name = "trip_memo")
public class TripMemo extends CreateUpdateBaseEntity{

    @Column(name="trip_type")
    private String tripType;

    @Column(name="name", length=255)
    private String name;

    @Column(name="trip_date_start")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date meetingDateStart;

    @Column(name="trip_date_end")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date meetingDateEnd;

    @Column(name="organization", length=255)
    private String organization;

    @Column(name="location", length=255)
    private String location;

    private Set<Employee> attendees;

    @Column(name="status", length=255)
    private String status;

    @Column(name="description", columnDefinition = "TEXT")
    private String description;

    // TODO: TEMP in place of authentication
    private String author;

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="trip_memo_attendees",
            joinColumns=
            @JoinColumn(name="trip_memo_id", referencedColumnName="ID"),
            inverseJoinColumns=
            @JoinColumn(name="employee_id", referencedColumnName="ID")
    )
    public Set<Employee> getAttendees() {
        return attendees;
    }

    public void setAttendees(Set<Employee> attendees) {
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

}