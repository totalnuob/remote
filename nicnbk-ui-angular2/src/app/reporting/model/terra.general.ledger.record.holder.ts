import {PeriodicReport} from "./periodic.report";
import {TerraGeneralLedgerRecord} from "./terra.general.ledger.record";

export class TerraGeneralLedgerRecordHolder{

    records: TerraGeneralLedgerRecord[];

    report: PeriodicReport;

    public constructor(){
        this.records = [];
    }

}