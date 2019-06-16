import {PageableSearchParams} from "../../common/model/pageable-search-params";

export class HedgeFundScoringSearchParams extends PageableSearchParams{
    searchText: string;
    dateFrom: string;
    dateTo: string;
}