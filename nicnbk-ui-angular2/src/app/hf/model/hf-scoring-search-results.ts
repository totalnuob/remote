import {HedgeFund} from "./hf.fund";
import {HedgeFundScoring} from "./hf.scoring";

export class HedgeFundScoringSearchResults{
    showPageFrom : number;
    showPageTo: number;
    totalPages: number;
    totalElements: number;
    currentPage: number;

    scorings: HedgeFundScoring[];
    searchParams: string;
}