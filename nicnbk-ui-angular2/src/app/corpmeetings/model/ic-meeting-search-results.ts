
import {CorpMeeting} from "./corp-meeting";
import {ICMeeting} from "./ic-meeting";

export class ICMeetingSearchResults {
    showPageFrom : number;
    showPageTo: number;
    totalPages: number;
    totalElements: number;
    currentPage: number;
    icMeetings: ICMeeting[];
    searchParams: string;
}
