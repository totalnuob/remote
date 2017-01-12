package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseParams;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.service.api.hf.HedgeFundService;

import java.text.SimpleDateFormat;

/**
 * Created by timur on 25.10.2016.
 */
public class HedgeFundSearchParams implements BaseParams {

    private String name;

    /* Pagination */
    private int page;
    private int pageSize;

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
        return pageSize > 0 ? pageSize : HedgeFundService.DEFAULT_PAGE_SIZE;
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
}
