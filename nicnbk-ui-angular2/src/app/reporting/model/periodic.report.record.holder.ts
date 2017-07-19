import {PeriodicReportRecord} from "./periodic.report.record";
import {PeriodicReport} from "./periodic.report";
export class PeriodicReportRecordHolder{

    trancheA: PeriodicReportRecord[];
    trancheB: PeriodicReportRecord[];

    balanceTrancheA: PeriodicReportRecord[];
    balanceTrancheB: PeriodicReportRecord[];

    operationsTranceA: PeriodicReportRecord[];
    operationsTranceB: PeriodicReportRecord[];


    report: PeriodicReport;
}