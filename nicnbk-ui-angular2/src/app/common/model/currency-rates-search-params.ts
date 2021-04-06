import {PageableSearchParams} from "../../common/model/pageable-search-params";
export class CurrencyRatesSearchParams extends PageableSearchParams{

    stationCode: string;
    currencyCode: string;
    quoteCurrencyCode: string;
    fromDate: string;
    toDate: string;
    isBaseQuote: boolean;

    path: string;
}