package kz.nicnbk.repo.model.corpmeetings;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.employee.Department;
import kz.nicnbk.repo.model.employee.Employee;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by magzumov
 */

@Entity(name = "ic_meeting_topic_assignment")
public class ICMeetingTopicAssignment extends BaseEntity {

    private ICMeetingTopic icMeetingTopic;
    private String name;
    //private String dueDate;
    private Date dateDue;
    private String status;
    private boolean closed;

    private List<Department> departments;
    private List<Employee> employees;

    public ICMeetingTopicAssignment(){}

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ic_meeting_topic_id", nullable = false)
    public ICMeetingTopic getIcMeetingTopic() {
        return icMeetingTopic;
    }

    public void setIcMeetingTopic(ICMeetingTopic icMeetingTopic) {
        this.icMeetingTopic = icMeetingTopic;
    }

    @Column(name="name", columnDefinition="TEXT")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getDueDate() {
//        return dueDate;
//    }
//
//    public void setDueDate(String dueDate) {
//        this.dueDate = dueDate;
//    }


    public Date getDateDue() {
        return dateDue;
    }

    public void setDateDue(Date dateDue) {
        this.dateDue = dateDue;
    }

    @Column(name="status", columnDefinition="TEXT")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    //@OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="ic_meeting_topic_assignment_depts",
            joinColumns=
            @JoinColumn(name="assignment_id", referencedColumnName="ID"),
            inverseJoinColumns=
            @JoinColumn(name="department_id", referencedColumnName="ID")
    )
    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="ic_meeting_topic_assignment_employees",
            joinColumns=
            @JoinColumn(name="assignment_id", referencedColumnName="ID"),
            inverseJoinColumns=
            @JoinColumn(name="employee_id", referencedColumnName="ID")
    )
    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
}
