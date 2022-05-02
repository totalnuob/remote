package kz.nicnbk.repo.model.corpmeetings;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.base.DataConstraints;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.files.Files;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by magzumov.
 */

@Entity
@Table(name = "ic_meeting")
public class ICMeeting extends CreateUpdateBaseEntity{

    private String number;
    private CorpMeetingType corpMeetingType;
    @Basic
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date date;
    private String time;
    private ICMeetingPlaceType place;

    private Boolean closed;
    private Boolean deleted;
    private Boolean unlockedForFinalize;

    private Files agenda;
    private Files protocol;
    private Files bulletin;

    private Employee ceoSubEmployee;

    private List<Employee> invitees;

    public ICMeeting(){}

    public ICMeeting(Long id){
        this.setId(id);
    }

    @Column(name="meeting_number", length=DataConstraints.C_TYPE_ENTITY_NAME, nullable = false)
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "corp_meeting_type_id")
    public CorpMeetingType getCorpMeetingType() {
        return corpMeetingType;
    }

    public void setCorpMeetingType(CorpMeetingType type) {
        this.corpMeetingType = type;
    }

    @Column(name="date", nullable = false)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name="time")
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    public ICMeetingPlaceType getPlace() {
        return place;
    }

    public void setPlace(ICMeetingPlaceType place) {
        this.place = place;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_file_id")
    public Files getAgenda() {
        return agenda;
    }

    public void setAgenda(Files agenda) {
        this.agenda = agenda;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protocol_file_id")
    public Files getProtocol() {
        return protocol;
    }

    public void setProtocol(Files protocol) {
        this.protocol = protocol;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bulletin_file_id")
    public Files getBulletin() {
        return bulletin;
    }

    public void setBulletin(Files bulletin) {
        this.bulletin = bulletin;
    }

    @Column(name="unlocked_for_finalize")
    public Boolean getUnlockedForFinalize() {
        return unlockedForFinalize;
    }

    public void setUnlockedForFinalize(Boolean unlockedForFinalize) {
        this.unlockedForFinalize = unlockedForFinalize;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ceo_sub_employee_id")
    public Employee getCeoSubEmployee() {
        return ceoSubEmployee;
    }

    public void setCeoSubEmployee(Employee ceoSubEmployee) {
        this.ceoSubEmployee = ceoSubEmployee;
    }


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="ic_meeting_invitees",
            joinColumns=
            @JoinColumn(name="ic_meeting_id", referencedColumnName="ID"),
            inverseJoinColumns=
            @JoinColumn(name="employee_id", referencedColumnName="ID")
    )
    public List<Employee> getInvitees() {
        return invitees;
    }

    public void setInvitees(List<Employee> invitees) {
        this.invitees = invitees;
    }
}