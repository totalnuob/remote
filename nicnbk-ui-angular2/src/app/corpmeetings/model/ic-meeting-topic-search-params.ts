import {PageableSearchParams} from "../../common/model/pageable-search-params";

export class ICMeetingTopicSearchParams extends PageableSearchParams{
    dateFrom: string;
    dateTo: string;
    searchText: string;

    path: string;


}