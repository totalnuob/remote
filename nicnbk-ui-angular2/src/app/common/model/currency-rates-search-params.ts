import {PageableSearchParams} from "../../common/model/pageable-search-params";
export class CurrencyRatesSearchParams extends PageableSearchParams{

    currencyCode: string;
    fromDate: string;
    toDate: string;

    path: string;
}