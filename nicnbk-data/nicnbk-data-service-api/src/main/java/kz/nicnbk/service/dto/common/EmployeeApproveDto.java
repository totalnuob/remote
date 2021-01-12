package kz.nicnbk.service.dto.common;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.service.dto.employee.EmployeeDto;

import java.util.Date;

/**
 * Created by magzumov on 07.11.2017.
 */
public class EmployeeApproveDto implements BaseDto {

    private EmployeeDto employee;
    private boolean approved;
    private Date approveDate;
    private String hash;

    public EmployeeApproveDto(){}

    public EmployeeApproveDto(EmployeeDto employee, boolean approved, Date approveDate, String hash){
        this.employee = employee;
        this.approved = approved;
        this.approveDate = approveDate;
        this.hash = hash;
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

    public Date getApproveDate() {
        return approveDate;
    }

    public void setApproveDate(Date approveDate) {
        this.approveDate = approveDate;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
