package kz.nicnbk.repo.model.corpmeetings;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.employee.Employee;

import javax.persistence.*;

/**
 * Created by magzumov
 */

//@Entity(name = "ic_meeting_invitees")
//public class ICMeetingInvitees extends BaseEntity {
//
//    private ICMeeting icMeeting;
//    private Employee employee;
//
//    public ICMeetingInvitees(){}
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="ic_meeting_id", nullable = false)
//    public ICMeeting getIcMeeting() {
//        return icMeeting;
//    }
//
//    public void setIcMeeting(ICMeeting icMeeting) {
//        this.icMeeting = icMeeting;
//    }
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="employee_id", nullable = false)
//    public Employee getEmployee() {
//        return employee;
//    }
//
//    public void setEmployee(Employee employee) {
//        this.employee = employee;
//    }
//}
