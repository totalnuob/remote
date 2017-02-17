import {FundMemo} from "./fund-memo";
import {HFManager} from "../../hf/model/hf.manager";
import {HedgeFund} from "../../hf/model/hf.fund";

export class HFMemo extends FundMemo{

    conviction: number;

    managementAndTeamNotes: string;
    managementAndTeamScore: number;
    portfolioNotes: string;
    portfolioScore: number;
    strategyNotes: string;
    strategyScore: number;
    otherInfo: string;
    NICFollowups: string;
    otherFollowups: string;

    manager: HFManager;
    fund: HedgeFund;
}