import {PeriodicReport} from "./periodic.report";
import {BaseDictionary} from "../../common/model/base-dictionary";
export class TerraSecuritiesCostRecord{
    id: number;
    name: string;

    totalCostFCY: number;
    costLCYHistorical: number;
    costLCYCurrentFXRate: number;
    unrealizedGainFCY: number;
    unrealizedGainLCY: number;
    FXGainLCY: number;
    marketValueFCY: number;

    report: PeriodicReport;
    type: BaseDictionary;

    excludeFromTerraCalculation: boolean;
    isTotalSum: boolean;

}