import {FundMemo} from "./fund-memo";
import {PEFirm} from "../../pe/model/pe.firm";
import {PEFund} from "../../pe/model/pe.fund";

export class PEMemo extends FundMemo{

    conviction: number;

    memoSummary: string;

    teamNotes: string;
    teamScore: number;
    trackRecordNotes: string;
    trackRecordScore: number;
    strategyNotes: string;
    strategyScore: number;
    otherNotes: string;
    nicFollowups: string;
    otherPartyFollowups: string;

    firm: PEFirm;
    fund: PEFund;
}