package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseParams;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.service.api.hf.HedgeFundService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by timur on 25.10.2016.
 */
public class HedgeFundScreeningSearchParams implements BaseParams {

    private String searchText;
    private Date dateFrom;
    private Date dateTo;

    /* Pagination */
    private int page;
    private int pageSize;

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
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

    public Date getDateFromNonEmpty(){
        return this.dateFrom != null ? this.dateFrom : DateUtils.getDate("01.01.1970");
    }

    public Date getDateToNonEmpty(){
        return this.dateTo != null ? this.dateTo : DateUtils.getDate("12.12.2030");
    }

    public String getSearchTextLowerCase() {
        return searchText != null ? searchText.toLowerCase().trim() : "";
    }

    public String getSearchParamsAsString(){
        StringBuilder params = new StringBuilder("&");
        params.append(this.page > 0 ? "page=" + this.page + "&"  : "");
        params.append(searchText != null ? "searchText=" + searchText + "&" : "");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        params.append(dateFrom != null ? "dateFrom=" + simpleDateFormat.format(dateFrom) + "&"  : "");
        params.append(dateTo != null ? "dateTo=" + simpleDateFormat.format(dateTo) + "&"  : "");
        return params.toString();
    }
}
