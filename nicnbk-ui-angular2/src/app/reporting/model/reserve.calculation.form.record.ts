import {BaseDictionary} from "../../common/model/base-dictionary";
export class ReserveCalculationFormRecord {

    id: number;
    expenseType: BaseDictionary;
    source: BaseDictionary;
    recipient: BaseDictionary;
    date: string;
    valueDate: string;
    amount: number;

    currencyRate: number;
    amountKZT: number;

    canDelete: boolean;

    constructor(){
        this.expenseType = new BaseDictionary();
        this.source = new BaseDictionary();
        this.recipient = new BaseDictionary();
    }

}