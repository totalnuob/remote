
import {LegalEntity} from "../../common/model/legal-entity";
import {HFManager} from "./hf.manager";

export class HedgeFund extends LegalEntity{

    manager: HFManager;

    summary: string;
    inception: string;
    AUMAmount: number;
    AUMDigit: string;
    AUMCurrency: string;
    strategy: string;

    numMonths: string;
    numPositiveMonths: string;
    numNegativeMonths: string;
    returnSinceInception: string;
    annualizedReturn: string;
    YTD: string;
    beta: string;
    annualVolatility: string;
    worstDrawdown: string;
    sharpeRatio: string;
    sortinoRatio: string;
    leverage: string;

    status: string;
    managementFee: string;
    performanceFee: string;
    redemptionFee: string;
    minInitialInvestment: string;
    minSubsInvestment: string;
    subscriptionFrequency: string;
    redemptionFrequency: string;
    redemptionNoticePeriod: string;
    sidePocket: string;
    gates: string;

    liquidityPercent: string;
    liquidityPeriod: string;

    concentrationTop5: string;
    concentrationTop10: string;
    concentrationTop20: string;

    strategyBreakdownList = [];
    investBaseList = [];
    managerList = [];
    returns = [];


    // ALbourne ratings
    ALBIDDAnalystAssessment: string;
    ALBConviction: string;
    ALBExpectedAlpha: string;
    ALBExpectedBeta: string;
    ALBExpectedRisk: string;
    ALBStrategyInvestmentProcess: string;
    ALBManagementTeam: string;
    ALBRiskProcess: string;


}