import {Memo} from "./memo";

export class FundMemo extends Memo{

    fundName: string;
    currentlyFundRaising: boolean;
    closingSchedule: string;
    fundSize: number;
    fundSizeCurrency: string;
    preInvested: number;
    openingSoon: boolean;
    openingSchedule: string;
    suitable: boolean;
    nonsuitableReason: string;
}