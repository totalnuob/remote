import {UpdatedEntity} from "./updated-entity";
export class BaseDictionary extends UpdatedEntity {

    code: string;
    nameEn: string;
    nameRu: string;
    nameKz: string;

    constructor(code?: string, nameEn?: string, nameRu?: string, nameKz?: string){

        super();

        this.code = code;
        this.nameEn = nameEn;
        this.nameRu = nameRu;
        this.nameKz = nameKz;
    }

}