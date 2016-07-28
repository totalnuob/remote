package kz.nicnbk.service.dto.m2s2;

import kz.nicnbk.common.service.model.BaseResult;

import java.util.List;

/**
 * Created by magzumov on 18.07.2016.
 */
public class MemoPagedSearchResult implements BaseResult {

    private int showPageFrom = 1;
    private int showPageTo = 1;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private List<MeetingMemoDto> memos;
    private String searchParams;

    public int getShowPageFrom() {
        return showPageFrom;
    }

    public void setShowPageFrom(int showPageFrom) {
        this.showPageFrom = showPageFrom;
    }

    public int getShowPageTo() {
        return showPageTo;
    }

    public void setShowPageTo(int showPageTo) {
        this.showPageTo = showPageTo;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

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
