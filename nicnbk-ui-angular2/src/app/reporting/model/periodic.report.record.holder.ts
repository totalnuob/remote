import {PeriodicReportRecord} from "./periodic.report.record";
import {PeriodicReport} from "./periodic.report";
import {GeneralLedgerRecord} from "./general.ledger.record";
import {SingularityNOALRecord} from "./singularity.noal.record";
export class PeriodicReportRecordHolder{

    trancheA: PeriodicReportRecord[];
    trancheB: PeriodicReportRecord[];

    balanceTrancheA: PeriodicReportRecord[];
    balanceTrancheB: PeriodicReportRecord[];

    operationsTranceA: PeriodicReportRecord[];
    operationsTranceB: PeriodicReportRecord[];

    generalLedgerBalanceList: GeneralLedgerRecord[];
    noalTrancheAList: SingularityNOALRecord[];
    noalTrancheBList: SingularityNOALRecord[];

    realEstateGeneralLedgerBalanceList: GeneralLedgerRecord[];

    report: PeriodicReport;
}