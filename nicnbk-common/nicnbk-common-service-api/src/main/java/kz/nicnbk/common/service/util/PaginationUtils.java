package kz.nicnbk.common.service.util;

/**
 * Created by magzumov on 18.07.2016.
 */
public class PaginationUtils {

    /**
     * Returns starting page number in a paginated view.
     * Starting page can be greater than 1.
     *
     * @param pagesPerView - number of pages per view
     * @param currentPage - current page number
     * @return - start page number
     */
    public static final int getShowPageFrom(int pagesPerView, int currentPage){
        for(int i = pagesPerView - 1; i > 0 && currentPage > 0; i--){
            if((currentPage - i) > 0){
                return currentPage - i;
            }
        }
        return Math.max(1, currentPage);
    }

    /**
     * Returns end page number in a paginated view.
     * End page number can be less than total page number.
     *
     * @param pagesPerView - number of pages per view
     * @param currentPage - current page number
     * @param fromPage - starting page number
     * @param totalPages - total page number
     * @return - end page number
     */
    public static final int getShowPageTo(int pagesPerView, int currentPage, int fromPage, int totalPages){
        for(int i = 4 - currentPage + fromPage; i > 0; i--){
            if((currentPage + i) <= totalPages){
                return currentPage + i;
            }
        }
        return Math.max(1, currentPage);
    }
}
