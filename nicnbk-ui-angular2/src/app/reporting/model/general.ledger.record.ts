export class GeneralLedgerRecord{

    id: number;
    acronym: string;
    financialStatementCategory: string;
    GLAccount: string;
    financialStatementCategoryDescription: string;
    chartAccountsDescription: string;
    chartAccountsLongDescription: string;
    shortName: string;
    glaccountBalance: number;
    segValCCY: string;
    fundCCY: string;

    tranche: number;
    tarragonNICChartOfAccountsName: string;

    adjustedRedemption: number;
    interestRate: string;
    comment: string;
}