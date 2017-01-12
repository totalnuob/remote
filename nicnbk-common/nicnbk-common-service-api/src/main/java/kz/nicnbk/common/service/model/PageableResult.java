package kz.nicnbk.common.service.model;

/**
 * Created by magzumov on 05.07.2016.
 */
public abstract class PageableResult implements BaseResult {

    private int showPageFrom = 1;
    private int showPageTo = 1;
    private int totalPages;
    private long totalElements;
    private int currentPage;

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
}
