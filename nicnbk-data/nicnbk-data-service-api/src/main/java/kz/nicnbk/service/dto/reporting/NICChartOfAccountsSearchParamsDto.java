package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseParams;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by magzumov on 18.07.2017.
 */
public class NICChartOfAccountsSearchParamsDto implements BaseParams {

    private String nbCode;

    /* Pagination */
    private int page;
    private int pageSize;

    public String getNbCode() {
        return nbCode;
    }

    public void setNbCode(String nbCode) {
        this.nbCode = nbCode;
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
        params.append(this.nbCode != null ? "nbCode=" + this.nbCode + "&"  : "");
        return params.toString();
    }
}
