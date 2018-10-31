package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.PageableResult;

import java.util.List;

/**
 * Created by zhambyl on 04-Aug-16.
 */
public class ICMeetingsPagedSearchResult extends PageableResult {

    private List<ICMeetingDto> icMeetings;
    private String searchParams;

    public List<ICMeetingDto> getIcMeetings() {
        return icMeetings;
    }

    public void setIcMeetings(List<ICMeetingDto> icMeetings) {
        this.icMeetings = icMeetings;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }
}
