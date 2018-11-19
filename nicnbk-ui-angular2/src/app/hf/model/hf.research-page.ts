import {LegalEntity} from "../../common/model/legal-entity";
import {HFManager} from "./hf.manager";
export class HFResearchPage extends LegalEntity {
    id: number;
    manager: HFManager;
    date: Date;
    topic: string;
    files: any[];
}