
import {LegalEntity} from "../../common/model/legal-entity";
import {HFManager} from "./hf.manager";

export class HedgeFund extends LegalEntity{

    manager: HFManager;

    summary: string;
    inception: string;
    inceptionDate: string;
    aum: number;
    aumDigit: string;
    aumCurrency: string;
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
    investorBaseList = [];
    managers = [];
    returns = [];

    // ALbourne ratings
    albourneIddAnalysisAssessment: string;
    albourneConviction: string;
    albourneExpectedAlpha: string;
    albourneExpectedBeta: string;
    alalbournebExpectedRisk: string;
    albourneStrategyInvestmentProcess: string;
    albourneManagementTeam: string;
    albourneRiskProcess: string;


}