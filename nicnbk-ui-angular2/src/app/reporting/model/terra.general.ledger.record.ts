import {PeriodicReport} from "./periodic.report";
import {BaseDictionary} from "../../common/model/base-dictionary";

export class TerraGeneralLedgerRecord{
    id: number;
    acronym: string;
    balanceDate: string;
    financialStatementCategory: string;
    glSubclass: string;
    portfolioFund: string;

    accountBalanceGP: number;
    accountBalanceNICKMF: number;
    accountBalanceGrandTotal: number;
}