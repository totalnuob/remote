
import {HedgeFund} from "./hf.fund";

export class HedgeFundSearchResults{
    showPageFrom : number;
    showPageTo: number;
    totalPages: number;
    totalElements: number;
    currentPage: number;
    funds: HedgeFund[];
    searchParams: string;
}