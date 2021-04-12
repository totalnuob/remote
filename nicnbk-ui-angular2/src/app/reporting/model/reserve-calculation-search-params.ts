import {PageableSearchParams} from "../../common/model/pageable-search-params";

export class ReserveCalculationSearchParams extends PageableSearchParams{
    path: string;
    expenseType: string;
    sourceType: string;
    destinationType: string;
    dateFrom: string;
    dateTo: string;

}