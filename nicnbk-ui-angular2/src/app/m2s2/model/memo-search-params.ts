import {PageableSearchParams} from "../../common/model/pageable-search-params";
export class MemoSearchParams extends PageableSearchParams{
    meetingType: string;
    memoType: string;
    fromDate: string;
    toDate: string;
    firmName: string;
    fundName: string;
    tags: string[];

}