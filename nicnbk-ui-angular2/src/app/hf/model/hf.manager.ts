
import {LegalEntity} from "../../common/model/legal-entity";
import {HedgeFund} from "./hf.fund";

export class HFManager extends LegalEntity{
    inception: number;
    inceptionDate: string;
    aum: string;
    aumDigit; string;
    aumCurrency: string;

    investedInB: boolean;
    investmentAmount: number;
    investmentDate: string;
    researchUpdated: Date;


    fundManagers: string;
    headquarters: string;
    contactPerson: string;
    telephone: string;
    fax: string;
    website: string;
    email: string;
    funds: any[];


    //fields from m2s2
    conviction: number;
    managementAndTeamNotes: string;
    managementAndTeamScore: number;
    portfolioNotes: string;
    portfolioScore: number;
    strategyNotes: string;
    strategyScore: number;

}