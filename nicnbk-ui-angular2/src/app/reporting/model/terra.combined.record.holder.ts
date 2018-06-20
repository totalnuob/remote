import {PeriodicReportRecord} from "./periodic.report.record";
import {PeriodicReport} from "./periodic.report";
import {GeneralLedgerRecord} from "./general.ledger.record";
import {SingularityNOALRecord} from "./singularity.noal.record";
import {TerraBalanceSheetRecord} from "./terra.balance.sheet.record";
import {TerraProfitLossRecord} from "./terra.profit.loss.record";
import {TerraSecuritiesCostRecord} from "./terra.securities.cost.record";
export class TerraCombinedRecordHolder{

    balanceSheetRecords: TerraBalanceSheetRecord[];
    profitLossRecords: TerraProfitLossRecord[];
    securitiesCostRecords: TerraSecuritiesCostRecord[];

    report: PeriodicReport;

    public constructor(){
        this.balanceSheetRecords = [];
        this.profitLossRecords = [];
        this.securitiesCostRecords = [];
    }

}