import {PEFirm} from "./pe.firm";

export class PEFund {

    fundName: string;
    status: string;
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
    benchmark: string;

    investmentPeriod: number;
    fundTerm: number;
    fundTermComment: string;
    targetInvSizeRange: number;
    targetEvRange: number;
    targetNumberOfInv1: number;
    targetNumberOfInv2: number;
    expAnnualNumberOfInv1: number;
    expAnnualNumberOfInv2: number;

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

    grossCashflow = [];
    netCashflow = [];
    fundCompanyPerformance = [];

    predecessorInvestedPct: number;
    openingSchedule: string;
    suitable: boolean;
    nonsuitableReason: string;
}