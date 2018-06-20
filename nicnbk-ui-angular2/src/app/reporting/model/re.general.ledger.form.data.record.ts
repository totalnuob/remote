import {GeneralLedgerRecord} from "./general.ledger.record";
import {GeneratedGLFormRecord} from "./generated.form.record";
export class REGeneralLedgerFormDataRecord {

    //tranche: number;
    financialStatementCategory: string;
    terraNICChartOfAccountsName: string;
    glaccountBalance: number;
    entityName: string;

    nbAccountNumber: string;
    nicAccountName: string;


    constructor();

    constructor(record?: GeneratedGLFormRecord) {
        if(record) {
            //if (record.acronym === 'TARRAGON') {
            //    this.tranche = 1;
            //} else if (record.acronym === 'TARRAGON B') {
            //    this.tranche = 2;
            //}
            if (record != null) {
                this.financialStatementCategory = record.financialStatementCategory;
                this.terraNICChartOfAccountsName = record.terraNICChartOfAccountsName;
                this.entityName = record.subscriptionRedemptionEntity;
                this.nbAccountNumber = record.nbAccountNumber;
                this.nicAccountName = record.nicAccountName;
                this.glaccountBalance = record.glaccountBalance;
            }
        }
    }
}