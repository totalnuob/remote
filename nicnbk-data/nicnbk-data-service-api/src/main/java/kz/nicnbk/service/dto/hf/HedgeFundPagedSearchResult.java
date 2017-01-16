package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.PageableResult;

import java.util.List;

/**
 * Created by magzumov on 18.07.2016.
 */
public class HedgeFundPagedSearchResult extends PageableResult {

    private List<HedgeFundDto> funds;
    private String searchParams;

    public List<HedgeFundDto> getFunds() {
        return funds;
    }

    public void setFunds(List<HedgeFundDto> funds) {
        this.funds = funds;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }
}
