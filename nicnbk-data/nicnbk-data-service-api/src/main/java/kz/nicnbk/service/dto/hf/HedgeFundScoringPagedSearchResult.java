package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.PageableResult;

import java.util.List;

/**
 * Created by magzumov on 18.07.2016.
 */
public class HedgeFundScoringPagedSearchResult extends PageableResult {

    private List<HedgeFundScoringDto> scorings;
    private String searchParams;

    public List<HedgeFundScoringDto> getScorings() {
        return scorings;
    }

    public void setScorings(List<HedgeFundScoringDto> scorings) {
        this.scorings = scorings;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }
}
