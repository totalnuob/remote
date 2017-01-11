import {PeFirm} from "./pe.firm";

export class PeFund {

    fundName: string;
    status: string;
    fundCurrency: string;
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

    firm: PeFirm;
    id: number;
    creationDate: string;

    cashflow = [];
    fundCompanyPerformance = [];
}