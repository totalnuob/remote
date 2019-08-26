package kz.nicnbk.service.dto.employee;

import kz.nicnbk.common.service.model.PageableResult;

import java.util.List;

/**
 * Created by zhambyl on 04-Aug-16.
 */
public class EmployeePagedSearchResult extends PageableResult {

    private List<EmployeeDto> employees;
    private String searchParams;


    public List<EmployeeDto> getEmployees() {
        return employees;
    }

    public void setEmployees(List<EmployeeDto> employees) {
        this.employees = employees;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }
}
