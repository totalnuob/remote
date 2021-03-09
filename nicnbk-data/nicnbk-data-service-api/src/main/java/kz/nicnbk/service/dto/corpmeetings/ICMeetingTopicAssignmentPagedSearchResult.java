package kz.nicnbk.service.dto.corpmeetings;

import kz.nicnbk.common.service.model.PageableResult;
import kz.nicnbk.repo.model.corpmeetings.ICMeetingTopicAssignment;

import java.util.List;

/**
 * Created by zhambyl on 04-Aug-16.
 */
public class ICMeetingTopicAssignmentPagedSearchResult extends PageableResult {

    private List<ICMeetingTopicAssignmentDto> assignments;
    private String searchParams;

    public List<ICMeetingTopicAssignmentDto> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<ICMeetingTopicAssignmentDto> assignments) {
        this.assignments = assignments;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }
}
