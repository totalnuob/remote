
import {CorpMeeting} from "./corp-meeting";
import {ICMeeting} from "./ic-meeting";

export class ICAssignmentSearchResults {
    showPageFrom : number;
    showPageTo: number;
    totalPages: number;
    totalElements: number;
    currentPage: number;
    icMeetingTopics: ICMeeting[];
    searchParams: string;
}
