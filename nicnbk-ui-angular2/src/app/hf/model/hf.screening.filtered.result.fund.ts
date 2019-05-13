import {HedgeFundScreeningParsedData} from "./hf.screening.parsed.data";
import {HedgeFundScreeningFilteredResultStatistics} from "./hf.screening.filtered.result.statistics";

export class HedgeFundScreeningFilteredResultFund {

    screening: any;

    id: number;

    fundId: number;
    fundName: string;
    investmentManager: string;
    mainStrategy: string;

    fundAUM: number;
    fundAUMDate: string;

    recentFundAUM: number;
    recentFundAUMDate: string;

    fundAUMByCurrency: number;
    currency: string;

    strategyAUM: number;
    recentStrategyAUM: number;
    recentStrategyAUMDate: string;

    managerAUM;

    editedFundAUM;
    editedFundAUMDate;
    editedFundAUMComment;

    constructor(){}

    clone(copy: HedgeFundScreeningFilteredResultFund){
        this.screening = copy.screening;
        this.fundId = copy.fundId;
        this.fundName = copy.fundName;
        this.investmentManager = copy.investmentManager;
        this.mainStrategy = copy.mainStrategy;

        this.fundAUM = copy.fundAUM;
        this.fundAUMDate = copy.fundAUMDate;

        this.editedFundAUM = copy.editedFundAUM;
        this.editedFundAUMComment = copy.editedFundAUMComment;

        this.managerAUM = copy.managerAUM;
    }

}
