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
public class ICMeetingsSearchParamsDto implements BaseDto {

    private String number;

    private String type;

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date dateFrom;

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date dateTo;

    //private String searchText;

    /* Pagination */
    private int page;
    private int pageSize;


    public String getNumber() {
        return this.number;
    }

    public String getNumberNonEmpty() {
        return StringUtils.isNotEmpty(this.number) ? this.number : null;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public String getTypeNonEmpty() {
        return StringUtils.isNotEmpty(this.type) ? this.type : null;
    }

    public void setType(String type) {
        this.type = type;
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

    public Date getDateFrom() {
        return dateFrom;
    }

    public Date getDateFromNonEmpty() {
        return dateFrom != null ? dateFrom : DateUtils.getDate("01.01.1970");
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public Date getDateToNonEmpty() {
        return dateTo != null ? dateTo : DateUtils.getDate("12.31.2030");
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }


    /**
     * Returns true if no search parameters are specified, false otherwise.
     * @return - true/false
     */
    public boolean isEmpty(){
        return StringUtils.isEmpty(this.number) && this.dateFrom == null && this.dateTo == null;
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
        params.append(dateFrom != null ? "dateFrom=" + simpleDateFormat.format(dateFrom) + "&"  : "");
        params.append(dateTo != null ? "dateTo=" + simpleDateFormat.format(dateTo) + "&"  : "");
        params.append(StringUtils.isNotEmpty(number) ? "number=" + number + "&"  : "");
        params.append(StringUtils.isNotEmpty(type) ? "type=" + type + "&" : "");
        return params.toString();
    }

}

