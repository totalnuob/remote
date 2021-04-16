package kz.nicnbk.repo.model.notification;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.base.HistoryBaseEntity;
import kz.nicnbk.repo.model.employee.Employee;

import javax.persistence.*;


@Entity
@Table(name="notification")
public class Notification extends HistoryBaseEntity {

    private Employee employee;
    private String name;
    private boolean closed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Column(name="name", nullable = false, columnDefinition="TEXT")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="closed", nullable = false)
    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
