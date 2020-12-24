import {UpdatedEntity} from "./updated-entity";
import {BaseDictionary} from "./base-dictionary";
export class PortfolioVarValue{

    constructor(type){
        this.portfolioVar = new BaseDictionary();
        this.portfolioVar.code = type;
    }

    constructor(){
        this.portfolioVar = new BaseDictionary();
    }


    id: number;
    portfolioVar: BaseDictionary;
    date: Date;
    value: number;

    editable: boolean;

}