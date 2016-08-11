package kz.nicnbk.service.dto.tripMemo;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.service.api.tripMemo.TripMemoService;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhambyl on 04-Aug-16.
 */
public class TripMemoSearchParamsDto {

    private String tripType;
    private String organization;
    private String location;
    private String status;

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date fromDate;

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date toDate;

    /* Pagination */
    private int page;
    private int pageSize;


    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
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

    /**
     * Returns true if no search parameters are specified, false otherwise.
     * @return - true/false
     */
    public boolean isEmpty(){
        return StringUtils.isEmpty(this.organization) && StringUtils.isEmpty(this.location) && this.fromDate == null && this.toDate == null &&
                (StringUtils.isEmpty(this.tripType) || this.tripType.equals("NONE")) && (StringUtils.isEmpty(this.status) || this.status.equals("NONE"));
    }

    /**
     * Returns search params as http params string.
     *
     * @return - params string
     */
    public String getSearchParamsAsString(){
        StringBuilder params = new StringBuilder("&");
        params.append(this.page > 0 ? "page=" + this.page + "&"  : "");
        params.append(StringUtils.isNotEmpty(tripType) ? "tripType=" + tripType + "&"  : "");
        params.append(StringUtils.isNotEmpty(status) ? "status=" + status + "&"  : "");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        params.append(fromDate != null ? "fromDate=" + simpleDateFormat.format(fromDate) + "&"  : "");
        params.append(toDate != null ? "toDate=" + simpleDateFormat.format(toDate) + "&"  : "");
        params.append(StringUtils.isNotEmpty(organization) ? "organization=" + organization + "&"  : "");
        params.append(StringUtils.isNotEmpty(location) ? "location=" + location + "&"  : "");
        return params.toString();
    }

}

