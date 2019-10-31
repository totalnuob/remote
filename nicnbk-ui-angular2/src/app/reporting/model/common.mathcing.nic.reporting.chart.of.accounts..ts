import {BaseDictionary} from "../../common/model/base-dictionary";
import {NICReportingChartOfAccounts} from "./nic.reporting.chart.of.accounts.";
export class CommonMatchingNICReportingChartOfAccounts{

    id: number;
    accountNumber: string;
    nameEn: string;
    nicchartOfAccounts: NICReportingChartOfAccounts;
    addable: boolean;
    negativeOnly: boolean;
    positiveOnly: boolean;
    editable: boolean;
    deletable: boolean

    chartAccountsType: BaseDictionary;

    constructor(){
        this.nicchartOfAccounts = new NICReportingChartOfAccounts();
        this.chartAccountsType = new BaseDictionary();
    }
}