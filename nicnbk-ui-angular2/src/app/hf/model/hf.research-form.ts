import {LegalEntity} from "../../common/model/legal-entity";
import {HFManager} from "./hf.manager";
import {HFResearchPage} from "./hf.research-page";

export class HFResearch extends LegalEntity {
    manager: HFManager;
    id: number;
    investmentsDates: string;
    allocationSize: number;
    contacts: string;
    nicCoverage: string;
    website: string;
    typesOfCommunication: string;
    importantNotes: string;
    keyPeople: string;

    researchPages: HFResearchPage[];
}