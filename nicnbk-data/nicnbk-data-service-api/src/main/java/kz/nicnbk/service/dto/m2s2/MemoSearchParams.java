package kz.nicnbk.service.dto.m2s2;

import kz.nicnbk.common.service.model.BaseParams;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.service.api.m2s2.MeetingMemoService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by magzumov on 18.07.2016.
 */
public class MemoSearchParams implements BaseParams {

    private String meetingType;
    //private String memoType;
    private Integer memoType;

    //@DateTimeFormat(pattern="dd-MM-yyyy")
    private Date fromDate;

    //@DateTimeFormat(pattern="dd-MM-yyyy")
    private Date toDate;

    private String firmName;
    private String fundName;

    /* Pagination */
    private int page;
    private int pageSize;

    public String getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(String meetingType) {
        this.meetingType = meetingType;
    }

    public Integer getMemoType() {
        return memoType;
    }

    public void setMemoType(Integer memoType) {
        this.memoType = memoType;
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

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
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

    public boolean isEmpty(){
        return StringUtils.isEmpty(this.firmName) && StringUtils.isEmpty(this.fundName) &&
                this.fromDate == null && this.toDate == null &&
                (StringUtils.isEmpty(this.meetingType) || this.meetingType.equals("NONE")) &&
                memoType == null;
    }

    public String getSearchParamsAsString(){
        StringBuilder params = new StringBuilder("&");
        params.append(this.page > 0 ? "page=" + this.page + "&"  : "");
        params.append(StringUtils.isNotEmpty(meetingType) ? "meetingType=" + meetingType + "&" : "");
        params.append(memoType != null ? "memoType=" + memoType + "&" : "");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        params.append(fromDate != null ? "fromDate=" + simpleDateFormat.format(fromDate) + "&"  : "");
        params.append(toDate != null ? "toDate=" + simpleDateFormat.format(toDate) + "&"  : "");
        params.append(StringUtils.isNotEmpty(firmName) ? "firmName=" + firmName + "&"  : "");
        params.append(StringUtils.isNotEmpty(fundName) ? "fundName=" + fundName + "&"  : "");
        return params.toString();
    }
}
