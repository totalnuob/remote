import {HedgeFundScreeningParsedData} from "./hf.screening.parsed.data";
import {HedgeFundScreeningFilteredResultStatistics} from "./hf.screening.filtered.result.statistics";
import {HedgeFundScreening} from "./hf.screening";

export class HedgeFundScreeningFilteredResult {
    screeningId: number;
    screening: HedgeFundScreening;

    id: number;
    fundAUM;
    managerAUM;
    trackRecord: number;
    lookbackReturns: number;
    lookbackAUM: number;
    startDateMonth: string;

    filteredResultStatistics: HedgeFundScreeningFilteredResultStatistics;

    addedFunds: any[];
    excludedFunds: any[];
    autoExcludedFunds: any[];

    editable: boolean;

    constructor(){}

}
