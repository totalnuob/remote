package kz.nicnbk.service.dto.employee;

/**
 * Created by pak on 07.02.2020.
 */
public class EmployeePasswordDto {

    private EmployeeFullDto employeeFullDto;
    private String password;
    private Boolean emailCheckbox;

    public EmployeePasswordDto() {
    }

    public EmployeePasswordDto(EmployeeFullDto employeeFullDto, String password, Boolean emailCheckbox) {
        this.employeeFullDto = employeeFullDto;
        this.password = password;
        this.emailCheckbox = emailCheckbox;
    }

    public EmployeeFullDto getEmployeeFullDto() {
        return employeeFullDto;
    }

    public void setEmployeeFullDto(EmployeeFullDto employeeFullDto) {
        this.employeeFullDto = employeeFullDto;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEmailCheckbox() {
        return emailCheckbox;
    }

    public void setEmailCheckbox(Boolean emailCheckbox) {
        this.emailCheckbox = emailCheckbox;
    }
}
