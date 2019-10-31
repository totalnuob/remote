import {PeriodicReport} from "./periodic.report";
import {PeriodicReportTarragonStatementBalanceOperationsTrancheHolder} from "./periodic.report.tarragon.statement.balance.operations.tranche.holder";

export class PeriodicReportTarragonStatementBalanceOperationsRecordHolder{

    balanceRecords: PeriodicReportTarragonStatementBalanceOperationsTrancheHolder[];
    operationsRecords: PeriodicReportRecord[];

    report: PeriodicReport;
}