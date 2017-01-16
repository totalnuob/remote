package kz.nicnbk.service.dto.tripMemo;

import kz.nicnbk.common.service.model.BaseResult;
import kz.nicnbk.common.service.model.PageableResult;

import java.util.List;

/**
 * Created by zhambyl on 04-Aug-16.
 */
public class TripMemoPagedSearchResult extends PageableResult {

    private List<TripMemoDto> tripMemos;
    private String searchParams;


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
