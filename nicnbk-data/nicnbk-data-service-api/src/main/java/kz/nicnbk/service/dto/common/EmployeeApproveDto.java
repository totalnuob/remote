package kz.nicnbk.service.dto.common;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.service.dto.employee.EmployeeDto;

import java.util.List;

/**
 * Created by magzumov on 07.11.2017.
 */
public class EmployeeApproveDto implements BaseDto {

    private EmployeeDto employeeDto;
    private boolean approved;

    public EmployeeDto getEmployeeDto() {
        return employeeDto;
    }

    public void setEmployeeDto(EmployeeDto employeeDto) {
        this.employeeDto = employeeDto;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
