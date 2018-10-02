package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.common.service.model.BaseParams;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by magzumov on 18.07.2017.
 */
public class PeriodicDataSearchParamsDto implements BaseParams {
    private Date dateFrom;
    private Date dateTo;
    private String type;

    /* Pagination */
    private int page;
    private int pageSize;

    public Date getDateFrom() {
        return dateFrom != null ? dateFrom : DateUtils.getDate("01.01.1970");
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo != null ? dateTo : DateUtils.getDate("12.31.2030");
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public String getType() {
        return type;
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
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isEmpty(){
        return this.dateFrom == null && this.dateTo == null && StringUtils.isEmpty(this.type);
    }

    public String getSearchParamsAsString(){
        StringBuilder params = new StringBuilder("&");
        params.append(this.page > 0 ? "page=" + this.page + "&"  : "");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        params.append(type != null ? "type=" + type + "&"  : "");
        params.append(dateFrom != null ? "fromDate=" + simpleDateFormat.format(dateFrom) + "&"  : "");
        params.append(dateTo != null ? "toDate=" + simpleDateFormat.format(dateTo) + "&"  : "");
        return params.toString();
    }
}
