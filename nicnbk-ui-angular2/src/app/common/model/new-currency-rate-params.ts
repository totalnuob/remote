import {PageableSearchParams} from "../../common/model/pageable-search-params";
export class NewCurrencyRateParams extends PageableSearchParams{

    date: Date;
    currency: string;
    value: number;

    path: string;
}