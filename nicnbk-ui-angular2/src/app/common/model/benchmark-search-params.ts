import {PageableSearchParams} from "../../common/model/pageable-search-params";
export class BenchmarkSearchParams extends PageableSearchParams{

    stationCode: string;
    benchmarkCode: string;
    fromDate: string;
    toDate: string;

    path: string;
}