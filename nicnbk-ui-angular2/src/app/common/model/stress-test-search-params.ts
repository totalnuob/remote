import {PageableSearchParams} from "../../common/model/pageable-search-params";
export class StressTestSearchParams extends PageableSearchParams{

    name: string;
    fromDate: string;
    toDate: string;

    path: string;
}