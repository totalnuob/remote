import {PeriodicReport} from "./periodic.report";
import {BaseDictionary} from "../../common/model/base-dictionary";
import {NICReportingChartOfAccounts} from "./nic.reporting.chart.of.accounts.";

export class NICKMFReportingInfo{

    nbChartOfAccountsCode: string;
    nicChartOfAccountsCode: string;
    accountBalance: number;

    nbChartOfAccountsName: string;
    nicChartOfAccountsName: string;

    matchingNICChartOfAccounts: NICReportingChartOfAccounts[]; // handled in UI only

}