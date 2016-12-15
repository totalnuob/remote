
import {LegalEntity} from "../../common/model/legal-entity";
import {HedgeFund} from "./hf.fund";

export class HFManager extends LegalEntity{

    managerType: string;
    inception: number;
    aum: string;
    aumDigit; string;
    aumCurrency: string;

    fundManagers: string;
    headquarters: string;
    contactPerson: string;
    telephone: string;
    fax: string;
    website: string;
    email: string;

    funds: any[];
}