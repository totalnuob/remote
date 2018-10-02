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
    deletable: boolean;

    constructor(){
        this.nicchartOfAccounts = new NICReportingChartOfAccounts();
    }
}