
import {HedgeFund} from "./hf.fund";
import {HFManager} from "./hf.manager";

export class HFManagerSearchResults{
    showPageFrom : number;
    showPageTo: number;
    totalPages: number;
    totalElements: number;
    currentPage: number;
    funds: HFManager[];
    searchParams: string;
}