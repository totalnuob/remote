import {PageableSearchParams} from "../../common/model/pageable-search-params";

export class ICMeetingTopicSearchParams extends PageableSearchParams{
    dateFrom: string;
    dateTo: string;
    searchText: string;
    icNumber: string;
    type: string;

    activeTab: string;
    path: string;


}