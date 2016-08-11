package kz.nicnbk.service.dto.tripMemo;

import kz.nicnbk.common.service.model.BaseResult;
import java.util.List;

/**
 * Created by zhambyl on 04-Aug-16.
 */
public class TripMemoPagedSearchResult implements BaseResult {
    private int showPageFrom;
    private int showPageTo;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private List<TripMemoDto> tripMemos;
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

    public List<TripMemoDto> getTripMemos() {
        return tripMemos;
    }

    public void setTripMemos(List<TripMemoDto> tripMemos) {
        this.tripMemos = tripMemos;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }
}
