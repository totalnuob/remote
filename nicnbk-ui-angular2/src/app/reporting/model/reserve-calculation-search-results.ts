import {ReserveCalculationFormRecord} from "./reserve.calculation.form.record";

export class ReserveCalculationSearchResults{
    showPageFrom : number;
    showPageTo: number;
    totalPages: number;
    totalElements: number;
    currentPage: number;

    records: ReserveCalculationFormRecord[];
    searchParams: string;
}