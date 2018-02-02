import {PeriodicReport} from "./periodic.report";
export class OtherInfoNBReporting{

    constructor(){
        this.report = new PeriodicReport();
    }

    closingBalance: number;
    exchangeRateDate: string;
    exchangeRate: number;

    monthlyCashStatementFileName: string;
    monthlyCashStatementFileId: number;

    report: PeriodicReport;
}