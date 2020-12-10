package kz.nicnbk.repo.model.corpmeetings;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.employee.Employee;

import javax.persistence.*;

/**
 * Created by magzumov
 */

@Entity(name = "ic_meeting_attendees")
public class ICMeetingAttendees extends BaseEntity {

    private ICMeeting icMeeting;
    private Employee employee;
    private boolean present;
    private ICMeetingAttendeeAbsenceType absenceType;

    public ICMeetingAttendees(){}

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ic_meeting_id", nullable = false)
    public ICMeeting getIcMeeting() {
        return icMeeting;
    }

    public void setIcMeeting(ICMeeting icMeeting) {
        this.icMeeting = icMeeting;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="employee_id", nullable = false)
    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="absence_type_id")
    public ICMeetingAttendeeAbsenceType getAbsenceType() {
        return absenceType;
    }

    public void setAbsenceType(ICMeetingAttendeeAbsenceType absenceType) {
        this.absenceType = absenceType;
    }

    @Column(name="present", nullable = false)
    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }
}
