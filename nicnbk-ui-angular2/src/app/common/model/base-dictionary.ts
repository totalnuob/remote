import {UpdatedEntity} from "./updated-entity";
export class BaseDictionary extends UpdatedEntity {
    id: number;
    code: string;
    nameEn: string;
    nameRu: string;
    nameKz: string;

    parent: BaseDictionary;

    constructor(code?: string, nameEn?: string, nameRu?: string, nameKz?: string){

        super();

        this.code = code;
        this.nameEn = nameEn;
        this.nameRu = nameRu;
        this.nameKz = nameKz;
    }

}