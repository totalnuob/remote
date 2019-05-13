import {UpdatedEntity} from "./updated-entity";
import {BaseDictionary} from "./base-dictionary";
export class CurrencyRate{

    constructor(currencyCode){
        this.currency = new BaseDictionary();
        this.currency.code = currencyCode;
    }

    constructor(){
        this.currency = new BaseDictionary();
    }


    id: number;
    currency: BaseDictionary;
    date: Date;
    value: number;
    valueUSD: number;
    averageValue: number;
    averageValueYear: number;
    editable: boolean;

}