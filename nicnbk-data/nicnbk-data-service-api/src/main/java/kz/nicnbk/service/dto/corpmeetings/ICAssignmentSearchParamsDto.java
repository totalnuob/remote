package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.service.api.tripmemo.TripMemoService;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ICAssignmentSearchParamsDto implements BaseDto {

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date dateFrom;

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date dateTo;

    private String searchText;
    private String icNumber;
    //private String type;
    private boolean hideClosed;

    /* Pagination */
    private int page;
    private int pageSize;

    private String username;


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

    public Date getDateFrom() {
        return dateFrom;
    }

    public Date getDateFromNonEmpty() {
        return dateFrom != null ? dateFrom : DateUtils.getDate("01.01.1970");
    }

    public String getDateFromAsText(){
        return DateUtils.getDateFormatted_YYYY_MM_DD(dateFrom);
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public Date getDateToNonEmpty() {
        return dateTo != null ? dateTo : DateUtils.getDate("31.12.2030");
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public String getSearchText() {
        return searchText;
    }

    public String getSearchTextLowerCase() {
        return searchText != null ? searchText.toLowerCase().trim() : "";
    }

    public String getICNumberLowerCase() {
        return icNumber != null ? icNumber.toLowerCase().trim() : "";
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getIcNumber() {
        return icNumber;
    }

    public void setIcNumber(String icNumber) {
        this.icNumber = icNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    //    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }

    /**
     * Returns true if no search parameters are specified, false otherwise.
     * @return - true/false
     */
    public boolean isEmpty(){
        return  StringUtils.isEmpty(this.searchText) && StringUtils.isEmpty(this.icNumber) /*&& StringUtils.isEmpty(this.type)*/
                && this.dateFrom == null && this.dateTo == null;
    }

    /**
     * Returns search params as http params string.
     *
     * @return - params string
     */
    public String getSearchParamsAsString(){
        StringBuilder params = new StringBuilder("&");
        params.append(this.page > 0 ? "page=" + this.page + "&"  : "");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        params.append(searchText != null ? "searchText=" + searchText + "&"  : "");
        params.append(icNumber != null ? "icNumber=" + icNumber + "&"  : "");
        //params.append(type != null ? "type=" + type + "&"  : "");
        params.append(dateFrom != null ? "dateFrom=" + simpleDateFormat.format(dateFrom) + "&"  : "");
        params.append(dateTo != null ? "dateTo=" + simpleDateFormat.format(dateTo) + "&"  : "");
        return params.toString();
    }

    public boolean isHideClosed() {
        return hideClosed;
    }

    public void setHideClosed(boolean hideClosed) {
        this.hideClosed = hideClosed;
    }
}

