package kz.nicnbk.service.dto.common;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.service.dto.employee.EmployeeDto;

/**
 * Created by magzumov on 07.11.2017.
 */
public class EmployeeApproveDto implements BaseDto {

    private EmployeeDto employee;
    private boolean approved;

    public EmployeeApproveDto(){}

    public EmployeeApproveDto(EmployeeDto employee, boolean approved){
        this.employee = employee;
        this.approved = approved;
    }

    public EmployeeDto getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeDto employee) {
        this.employee = employee;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
