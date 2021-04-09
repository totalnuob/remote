package kz.nicnbk.service.dto.lookup;

import kz.nicnbk.common.service.model.BaseParams;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.service.api.m2s2.MeetingMemoService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by magzumov on 18.07.2016.
 */
public class CurrencyRatesSearchParams implements BaseParams {

    String currencyCode;

    //@DateTimeFormat(pattern="dd-MM-yyyy")
    private Date fromDate;

    //@DateTimeFormat(pattern="dd-MM-yyyy")
    private Date toDate;

    /* Pagination */
    private int page;
    private int pageSize;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Date getFromDate() {
        return fromDate != null ? fromDate : DateUtils.DEFAULT_START;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate != null ? toDate : DateUtils.getDate("12.31." + (DateUtils.getYear(new Date()) + 10));
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
        return pageSize > 0 ? pageSize : MeetingMemoService.DEFAULT_PAGE_SIZE;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSearchParamsAsString(){
        StringBuilder params = new StringBuilder("&");
        params.append(this.page > 0 ? "page=" + this.page + "&"  : "");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        params.append(fromDate != null ? "fromDate=" + simpleDateFormat.format(fromDate) + "&"  : "");
        params.append(toDate != null ? "toDate=" + simpleDateFormat.format(toDate) + "&"  : "");
        return params.toString();
    }
}
