package kz.nicnbk.service.dto.m2s2;

import kz.nicnbk.common.service.util.StringUtils;

/**
 * Created by Pak on 15/12/2017.
 */
public class MemoSearchParamsExtended extends MemoSearchParams {
    private boolean onlyMyOwn;

    public boolean isOnlyMyOwn() {
        return onlyMyOwn;
    }

    public void setOnlyMyOwn(boolean onlyMyOwn) {
        this.onlyMyOwn = onlyMyOwn;
    }

    public boolean isEmpty(){
        return StringUtils.isEmpty(this.getFirmName()) && StringUtils.isEmpty(this.getFundName()) &&
                this.getFromDate() == null && this.getToDate() == null &&
                (StringUtils.isEmpty(this.getMeetingType()) || this.getMeetingType().equals(StringUtils.VALUE_NONE)) &&
                (getMemoType() == null || getMemoType().intValue() == 0) && this.isOnlyMyOwn() == false;
    }
}
