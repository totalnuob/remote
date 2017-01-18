package kz.nicnbk.service.dto.m2s2;

import kz.nicnbk.common.service.model.BaseResult;
import kz.nicnbk.common.service.model.PageableResult;

import java.util.List;

/**
 * Created by magzumov on 18.07.2016.
 */
public class MemoPagedSearchResult extends PageableResult {

    private List<MeetingMemoDto> memos;
    private String searchParams;

    public List<MeetingMemoDto> getMemos() {
        return memos;
    }

    public void setMemos(List<MeetingMemoDto> memos) {
        this.memos = memos;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }
}
