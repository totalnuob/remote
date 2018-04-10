package kz.nicnbk.repo.model.corpmeetings;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.base.DataConstraints;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.tripmemo.TripType;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by zhambyl on 04-Aug-16.
 */

@Entity
@Table(name = "corp_meeting")
public class CorpMeeting extends CreateUpdateBaseEntity{

    private CorpMeetingType type;
    private String number;

    @Basic
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date date;

    private String shortName;
    private String agenda;

    private Set<Employee> attendeesNIC;
    private String attendeesOther;

    private Boolean deleted;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id")
    public CorpMeetingType getType() {
        return type;
    }

    public void setType(CorpMeetingType type) {
        this.type = type;
    }

    @Column(name="number", length=DataConstraints.C_TYPE_ENTITY_NAME)
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

    @Column(name="short_name", length=DataConstraints.C_TYPE_ENTITY_NAME)
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    @Column(name="agenda", columnDefinition="TEXT")
    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="corp_meeting_attendees",
            joinColumns=
            @JoinColumn(name="corp_meeting_id", referencedColumnName="ID"),
            inverseJoinColumns=
            @JoinColumn(name="employee_id", referencedColumnName="ID")
    )
    public Set<Employee> getAttendeesNIC() {
        return attendeesNIC;
    }

    public void setAttendeesNIC(Set<Employee> attendeesNIC) {
        this.attendeesNIC = attendeesNIC;
    }

    @Column(name="attendees_other", length = DataConstraints.C_TYPE_ENTITY_DESCRIPTION_LONG)
    public String getAttendeesOther() {
        return attendeesOther;
    }

    public void setAttendeesOther(String attendeesOther) {
        this.attendeesOther = attendeesOther;
    }


    @Column(name="deleted")
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}