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
    startDate: string;

    filteredResultStatistics: HedgeFundScreeningFilteredResultStatistics;

    constructor(){}

}
