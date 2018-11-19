import {HedgeFund} from "./hf.fund";

export class HedgeFundScreeningSearchResults{
    showPageFrom : number;
    showPageTo: number;
    totalPages: number;
    totalElements: number;
    currentPage: number;

    records: any[];
    searchParams: string;
}