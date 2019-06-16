import {PageableSearchParams} from "../../common/model/pageable-search-params";
export class BenchmarkSearchParams extends PageableSearchParams{

    benchmarkCode: string;
    fromDate: string;
    toDate: string;

    path: string;
}