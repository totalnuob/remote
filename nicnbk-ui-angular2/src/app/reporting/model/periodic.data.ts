import {BaseDictionary} from "../../common/model/base-dictionary";
export class PeriodicData{
    id: number;
    date: Date;
    value: number;
    type: BaseDictionary;
    revaluated: boolean;
    editable: boolean;

    constructor(){
        this.type = new BaseDictionary();
    }
}