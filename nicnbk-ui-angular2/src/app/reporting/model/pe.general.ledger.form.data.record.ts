import {GeneralLedgerRecord} from "./general.ledger.record";
import {GeneratedGLFormRecord} from "./generated.form.record";
export class PEGeneralLedgerFormDataRecord {

    tranche: number;
    financialStatementCategory: string;
    tarragonNICChartOfAccountsName: string;
    glaccountBalance: number;
    entityName: string;

    nbAccountNumber: string;
    nicAccountName: string;

    constructor(){}

    constructor(record: GeneratedGLFormRecord){
        if(record.acronym === 'TARRAGON'){
            this.tranche = 1;
        }else if(record.acronym === 'TARRAGON B'){
            this.tranche = 2;
        }
        this.financialStatementCategory = record.financialStatementCategory;
        this.tarragonNICChartOfAccountsName = record.tarragonNICChartOfAccountsName;
        this.entityName = record.subscriptionRedemptionEntity;
        this.nbAccountNumber = record.nbAccountNumber;
        this.nicAccountName = record.nicAccountName;
        this.glaccountBalance = record.glaccountBalance;
    }
}