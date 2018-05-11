
import {CorpMeeting} from "./corp-meeting";

export class CorpMeetingSearchResults {
    showPageFrom : number;
    showPageTo: number;
    totalPages: number;
    totalElements: number;
    currentPage: number;
    corpMeetings: CorpMeeting[];
    searchParams: string;
}
