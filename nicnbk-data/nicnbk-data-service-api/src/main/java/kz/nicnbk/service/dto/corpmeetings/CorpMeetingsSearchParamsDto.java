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
public class CorpMeetingsSearchParamsDto implements BaseDto {

    private String type;
    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date date;
    private String number;
    private String searchText;

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date dateFrom;

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date dateTo;



    /* Pagination */
    private int page;
    private int pageSize;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSearchText() {
        return searchText != null? searchText.toLowerCase() : searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
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

    /**
     * Returns true if no search parameters are specified, false otherwise.
     * @return - true/false
     */
    public boolean isEmpty(){
        return StringUtils.isEmpty(this.number) && StringUtils.isEmpty(this.searchText) && this.dateFrom == null && this.dateTo == null &&
                (StringUtils.isEmpty(this.type) || this.type.equals("NONE"));
    }

    /**
     * Returns search params as http params string.
     *
     * @return - params string
     */
    public String getSearchParamsAsString(){
        StringBuilder params = new StringBuilder("&");
        params.append(this.page > 0 ? "page=" + this.page + "&"  : "");
        params.append(StringUtils.isNotEmpty(type) ? "type=" + type+ "&"  : "");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        params.append(dateFrom != null ? "dateFrom=" + simpleDateFormat.format(dateFrom) + "&"  : "");
        params.append(dateTo != null ? "dateTo=" + simpleDateFormat.format(dateTo) + "&"  : "");
        params.append(StringUtils.isNotEmpty(number) ? "number=" + number + "&"  : "");
        params.append(StringUtils.isNotEmpty(searchText) ? "searchText=" + searchText + "&"  : "");
        return params.toString();
    }

}

