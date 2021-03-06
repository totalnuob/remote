package kz.nicnbk.service.dto.lookup;

import kz.nicnbk.common.service.model.BaseParams;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.service.api.m2s2.MeetingMemoService;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PortfolioVarSearchParams implements BaseParams {
    String portfolioVarCode;
    private Date fromDate;
    private Date toDate;
    private int page;
    private int pageSize;

    public String getPortfolioVarCode() {
        return portfolioVarCode;
    }

    public void setPortfolioVarCode(String portfolioVarCode) {
        this.portfolioVarCode = portfolioVarCode;
    }

    public Date getFromDate() {
        return fromDate != null ? fromDate : DateUtils.getDate("01.01.1970");
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
