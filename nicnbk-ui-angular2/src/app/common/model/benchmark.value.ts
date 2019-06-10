import {UpdatedEntity} from "./updated-entity";
import {BaseDictionary} from "./base-dictionary";
export class BenchmarkValue{

    constructor(type){
        this.benchmark = new BaseDictionary();
        this.benchmark.code = type;
    }

    constructor(){
        this.benchmark = new BaseDictionary();
    }


    id: number;
    benchmark: BaseDictionary;
    date: Date;
    returnValue: number;
    indexValue: number;

    editable: boolean;

}