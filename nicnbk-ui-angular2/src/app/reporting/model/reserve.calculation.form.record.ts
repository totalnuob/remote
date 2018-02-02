import {BaseDictionary} from "../../common/model/base-dictionary";
export class ReserveCalculationFormRecord {

    expenseType: BaseDictionary;
    source: BaseDictionary;
    recipient: BaseDictionary;
    date: string;
    amount: number;

    currencyRate: number;
    amountKZT: number;

    constructor(){
        this.expenseType = new BaseDictionary();
        this.source = new BaseDictionary();
        this.recipient = new BaseDictionary();
    }

}