
import {HedgeFundScreeningFilteredResult} from "./hf.screening.filtered.result";
import {HedgeFundScoringResultFund} from "./hf.scoring.result.fund";

export class HedgeFundScoring {
    id: number;

    filteredResult: HedgeFundScreeningFilteredResult;
    date;

    funds: HedgeFundScoringResultFund[];

}