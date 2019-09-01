package kz.nicnbk.service.dto.employee;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.service.api.tripmemo.TripMemoService;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhambyl on 04-Aug-16.
 */
public class EmployeeSearchParamsDto {

    private String firstName;
    private String lastName;
    private String status;

    /* Pagination */
    private int page;
    private int pageSize;


    public String getFirstName() {
        return firstName;
    }
    public String getFirstNameOrEmpty(){
        return this.firstName != null ? this.firstName : "";
    }

    public String getLastNameOrEmpty(){
        return this.lastName != null ? this.lastName : "";
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize > 0 ? pageSize : TripMemoService.DEFAULT_PAGE_SIZE;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getStatusBoolean(){
        if(this.status != null){
            if(this.status.equalsIgnoreCase("ACTIVE")){
                return true;
            }else if(this.status.equalsIgnoreCase("INACTIVE")){
                return false;
            }
        }
        return null;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns true if no search parameters are specified, false otherwise.
     * @return - true/false
     */
    public boolean isEmpty(){
        return StringUtils.isEmpty(this.firstName) && StringUtils.isEmpty(this.lastName) && this.page == 0
                && StringUtils.isEmpty(this.getStatus());
    }

    /**
     * Returns search params as http params string.
     *
     * @return - params string
     */
    public String getSearchParamsAsString(){
        StringBuilder params = new StringBuilder("&");
        params.append(this.page > 0 ? "page=" + this.page + "&"  : "");
        params.append(StringUtils.isNotEmpty(firstName) ? "firstName=" + firstName + "&"  : "");
        params.append(StringUtils.isNotEmpty(lastName) ? "lastName=" + lastName + "&"  : "");
        params.append(this.status != null ? "status=" + status + "&"  : "");
        return params.toString();
    }

}

