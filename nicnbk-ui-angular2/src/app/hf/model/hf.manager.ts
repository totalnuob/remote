
import {LegalEntity} from "../../common/model/legal-entity";
import {HedgeFund} from "./hf.fund";

export class HFManager extends LegalEntity{

    managerType: string;
    strategy: string;
    status: string;
    inception: number;
    legalStructure: string;
    domicileCountry: string;
    aum: string;

    fundManagers: string;
    headquarters: string;
    contactPerson: string;
    telephone: string;
    fax: string;
    website: string;
    email: string;

    funds: any[];
}