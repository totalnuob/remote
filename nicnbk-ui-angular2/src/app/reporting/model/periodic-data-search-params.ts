import {PageableSearchParams} from "../../common/model/pageable-search-params";

export class PeriodicDataSearchParams extends PageableSearchParams{
    dateFrom: string;
    dateTo: string;
    type: string;

    path: string;
}