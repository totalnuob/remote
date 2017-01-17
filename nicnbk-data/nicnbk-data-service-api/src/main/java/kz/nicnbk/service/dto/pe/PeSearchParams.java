package kz.nicnbk.service.dto.pe;

import kz.nicnbk.common.service.model.BaseParams;
import java.text.SimpleDateFormat;

/**
 * Created by zhambyl on 22-Nov-16.
 */
public class PESearchParams implements BaseParams {

    private String name;

    /* Pagination */
    private int page;
    private int pageSize;

    private long id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        //params.append(StringUtils.isNotEmpty(meetingType) ? "meetingType=" + meetingType + "&" : "");
        //params.append(memoType != null ? "memoType=" + memoType + "&" : "");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        //params.append(fromDate != null ? "fromDate=" + simpleDateFormat.format(fromDate) + "&"  : "");
        //params.append(toDate != null ? "toDate=" + simpleDateFormat.format(toDate) + "&"  : "");
        //params.append(StringUtils.isNotEmpty(firmName) ? "firmName=" + firmName + "&"  : "");
        //params.append(StringUtils.isNotEmpty(fundName) ? "fundName=" + fundName + "&"  : "");
        return params.toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
