import {PageableSearchParams} from "../../common/model/pageable-search-params";

export class ICMeetingSearchParams extends PageableSearchParams{
    dateFrom: string;
    dateTo: string;
    meetingType: string;

    number: string;

    path: string;


}