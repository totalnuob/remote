package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseParams;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.service.api.m2s2.MeetingMemoService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by magzumov on 18.07.2016.
 */
public class ReserveCalculationSearchParams implements BaseParams {

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
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSearchParamsAsString(){
        StringBuilder params = new StringBuilder("&");
        params.append(this.page > 0 ? "page=" + this.page + "&"  : "");
        return params.toString();
    }
}
