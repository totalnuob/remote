import {PEFirm} from "./pe.firm";

export class PEFund {

    fundName: string;
    status: string;
    nicStatus: string;
    currency: string;
    vintage: number;
    fundSize: number;
    targetSize: number;
    hardCap: number;
    targetHardCapComment: string;
    gpCommitment: number;
    managementFee: number;
    managementFeeComment;
    carriedInterest: number;
    hurdleRate: number;
    industry: any[];
    strategy: any[];
    strategyComment: string;
    geography: any[];

    calculationType: number;
    //autoCalculation: boolean;
    numberOfInvestments: number;
    investedAmount: number;
    realized: number;
    unrealized: number;
    dpi: number;
    netIrr: number;
    netTvpi: number // MOIC
    grossIrr: number;
    grossTvpi: number;
    asOfDate: Date;
    benchmarkNetIrr: number;
    benchmarkNetTvpi: number;
    benchmarkName: string;

    investmentPeriod: string;
    fundTerm: number;
    fundTermComment: string;
    targetInvSizeRange: string;
    targetEvRange: string;
    targetNumberOfInv1: number;
    targetNumberOfInv2: number;
    expAnnualNumberOfInv1: number;
    expAnnualNumberOfInv2: number;
    expHoldPeriodPerInvestment: string;

    firstClose: Date;
    finalClose: Date;

    generalPartnerMerits: string;
    generalPartnerRisks: string;
    strategyStructureMerits: string;
    strategyStructureRisks: string;
    performanceMerits: string;
    performanceRisks: string;

    firm: PEFirm;
    id: number;
    creationDate: string;
    updateDate: string;

    grossCashflow = [];
    netCashflow = [];
    companyPerformance = [];
    companyPerformanceIdd = [];

    onePagerDescriptions = [];
    managementTeam = [];
    asOfDateOnePager: Date;

    predecessorInvestedPct: number;
    openingSchedule: string;
    suitable: boolean;
    nonsuitableReason: string;
}