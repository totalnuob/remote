package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.PageableResult;

import java.util.List;

/**
 * Created by zhambyl on 04-Aug-16.
 */
public class ICMeetingTopicsPagedSearchResult extends PageableResult {

    private List<ICMeetingTopicDto> icMeetingTopics;
    private String searchParams;

    public List<ICMeetingTopicDto> getIcMeetingTopics() {
        return icMeetingTopics;
    }

    public void setIcMeetingTopics(List<ICMeetingTopicDto> icMeetingTopics) {
        this.icMeetingTopics = icMeetingTopics;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }
}
