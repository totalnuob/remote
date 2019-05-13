import {HedgeFundScreeningParsedData} from "./hf.screening.parsed.data";
import {HedgeFundScreeningFilteredResultStatistics} from "./hf.screening.filtered.result.statistics";

export class HedgeFundScreeningFilteredResult {
    screeningId: number;

    id: number;
    fundAUM;
    managerAUM;
    trackRecord: number;
    lookbackReturns: number;
    lookbackAUM: number;
    startDateMonth: string;

    filteredResultStatistics: HedgeFundScreeningFilteredResultStatistics;

    constructor(){}

}
