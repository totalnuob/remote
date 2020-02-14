package kz.nicnbk.service.dto.employee;

/**
 * Created by pak on 14.02.2020.
 */
public class EmployeeFullDto extends EmployeeDto {

    private Integer failedLoginAttempts;
    private Boolean locked;

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }
}
