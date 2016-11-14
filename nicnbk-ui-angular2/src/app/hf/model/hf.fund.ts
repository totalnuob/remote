
import {LegalEntity} from "../../common/model/legal-entity";
import {HFManager} from "./hf.manager";

export class HedgeFund extends LegalEntity{

    manager: HFManager;

    strategyDescription: string;
    size: string;
    status: string;
    strategy: string;
    geography: string;
    shareClassCurrency: string;
    inception: number;
    legalStructure: string;
    domicileCountry: string;

    // subscriptions
    minInvestment: string;
    investmentCurrency: string;
    subscriptionFrequency: string;

    // fees
    managementFeeType: string;
    managementFee: string;
    performanceFeeType: string;
    performanceFee: string;
    performanceFeePayFrequency: string;

    // redemptions
    redemptionFrequency: string;
    redemptionNotificationPeriod: string;

    calculatedValues: any[];

}