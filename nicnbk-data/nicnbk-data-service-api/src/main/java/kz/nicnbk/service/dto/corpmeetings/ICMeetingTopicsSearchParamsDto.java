package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.service.api.tripmemo.TripMemoService;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhambyl on 04-Aug-16.
 */
public class ICMeetingTopicsSearchParamsDto implements BaseDto {

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date dateFrom;

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date dateTo;

    private String searchText;

    /* Pagination */
    private int page;
    private int pageSize;


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
        return dateFrom != null ? dateFrom : DateUtils.getDate("01.01.1970");
    }

    public String getDateFromAsText(){
        return DateUtils.getDateFormatted_YYYY_MM_DD(dateFrom);
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo != null ? dateTo : new Date();
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    /**
     * Returns true if no search parameters are specified, false otherwise.
     * @return - true/false
     */
    public boolean isEmpty(){
        return  StringUtils.isEmpty(this.searchText) && this.dateFrom == null && this.dateTo == null;
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
        params.append(dateFrom != null ? "dateFrom=" + simpleDateFormat.format(dateFrom) + "&"  : "");
        params.append(dateTo != null ? "dateTo=" + simpleDateFormat.format(dateTo) + "&"  : "");
        return params.toString();
    }

}

