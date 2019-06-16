import {PageableSearchParams} from "../../common/model/pageable-search-params";

export class HedgeFundScreeningSearchParams extends PageableSearchParams{
    searchText: string;
    dateFrom: string;
    dateTo: string;
}