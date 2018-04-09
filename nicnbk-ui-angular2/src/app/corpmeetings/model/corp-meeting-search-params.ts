import {PageableSearchParams} from "../../common/model/pageable-search-params";

export class CorpMeetingSearchParams extends PageableSearchParams{
    type: string;
    dateFrom: string;
    dateTo: string;
    searchText: string;

    path: string;
}