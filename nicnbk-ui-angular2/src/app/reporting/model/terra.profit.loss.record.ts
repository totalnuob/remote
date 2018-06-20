import {PeriodicReport} from "./periodic.report";
import {BaseDictionary} from "../../common/model/base-dictionary";
export class TerraProfitLossRecord{
    id: number;
    name: string;
    valueGP: number;
    valueNICKMF: number;
    grandTotal: number;
    report: PeriodicReport;

    excludeFromTerraCalculation: boolean;

    type: BaseDictionary;
    isTotalSum: boolean;
}