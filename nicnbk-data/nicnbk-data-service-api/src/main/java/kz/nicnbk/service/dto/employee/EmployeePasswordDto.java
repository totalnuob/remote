package kz.nicnbk.service.dto.employee;

/**
 * Created by pak on 07.02.2020.
 */
public class EmployeePasswordDto {

    private EmployeeFullDto employeeFullDto;
    private String password;

    public EmployeePasswordDto() {
    }

    public EmployeePasswordDto(EmployeeFullDto employeeFullDto, String password) {
        this.employeeFullDto = employeeFullDto;
        this.password = password;
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
}
