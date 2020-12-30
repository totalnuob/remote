package kz.nicnbk.service.dto.lookup;

import kz.nicnbk.common.service.model.BaseParams;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RiskStressTestPagedSearchParams implements BaseParams {
    String name;
    private Date fromDate;
    private Date toDate;
    private int page;
    private int pageSize;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return pageSize;
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
