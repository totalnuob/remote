import {LegalEntity} from "../../common/model/legal-entity";

export class MacroMonitorScore extends LegalEntity {
    date: Date;
    score: number;
    field: string;
    type: string;
}