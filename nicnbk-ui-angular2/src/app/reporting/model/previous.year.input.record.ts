import {PeriodicReport} from "./periodic.report";
import {NICReportingChartOfAccounts} from "./nic.reporting.chart.of.accounts.";
export class PreviousYearInputRecord {

    chartOfAccounts: NICReportingChartOfAccounts;
    accountBalance: number;
    accountBalanceKZT: number;
    report: PeriodicReport;

    constructor(reportId){
        this.chartOfAccounts = new NICReportingChartOfAccounts();
        this.report = new PeriodicReport(reportId);
    }

}