package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.PageableResult;

import java.util.List;

/**
 * Created by magzumov on 18.07.2016.
 */
public class HedgeFundScreeningPagedSearchResult extends PageableResult {

    private List<HedgeFundScreeningDto> screenings;
    private String searchParams;

    public List<HedgeFundScreeningDto> getScreenings() {
        return screenings;
    }

    public void setScreenings(List<HedgeFundScreeningDto> screenings) {
        this.screenings = screenings;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }
}
