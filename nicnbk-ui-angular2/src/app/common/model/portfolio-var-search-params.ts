import {PageableSearchParams} from "../../common/model/pageable-search-params";
export class PortfolioVarSearchParams extends PageableSearchParams{

    portfolioVarCode: string;
    fromDate: string;
    toDate: string;

    path: string;
}