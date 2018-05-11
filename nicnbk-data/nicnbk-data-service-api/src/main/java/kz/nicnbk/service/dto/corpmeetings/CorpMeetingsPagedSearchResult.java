package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.PageableResult;
import kz.nicnbk.service.dto.tripmemo.TripMemoDto;

import java.util.List;

/**
 * Created by zhambyl on 04-Aug-16.
 */
public class CorpMeetingsPagedSearchResult extends PageableResult {

    private List<CorpMeetingDto> corpMeetings;
    private String searchParams;


    public List<CorpMeetingDto> getCorpMeetings() {
        return corpMeetings;
    }

    public void setCorpMeetings(List<CorpMeetingDto> corpMeetings) {
        this.corpMeetings = corpMeetings;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }
}
