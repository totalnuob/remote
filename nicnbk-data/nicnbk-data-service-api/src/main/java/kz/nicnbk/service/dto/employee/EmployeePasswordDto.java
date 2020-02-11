package kz.nicnbk.service.dto.employee;

/**
 * Created by pak on 07.02.2020.
 */
public class EmployeePasswordDto {

    private EmployeeDto employeeDto;
    private String password;

    public EmployeePasswordDto() {
    }

    public EmployeePasswordDto(EmployeeDto employeeDto, String password) {
        this.employeeDto = employeeDto;
        this.password = password;
    }

    public EmployeeDto getEmployeeDto() {
        return employeeDto;
    }

    public void setEmployeeDto(EmployeeDto employeeDto) {
        this.employeeDto = employeeDto;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
