package kz.nicnbk.service.dto.notification;

import kz.nicnbk.common.service.model.HistoryBaseEntityDto;
import kz.nicnbk.repo.model.notification.Notification;
import kz.nicnbk.service.dto.employee.EmployeeDto;

public class NotificationDto extends HistoryBaseEntityDto<Notification> {

    private EmployeeDto employee;
    private String name;
    private boolean closed;

    public EmployeeDto getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeDto employee) {
        this.employee = employee;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
