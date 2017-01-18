import {PEFirm} from "./pe.firm";

export class PESearchResults {
    showPageFrom: number;
    showPageTo: number;
    totalPages: number;
    totalElements: number;
    currentPage: number;
    firms: PEFirm[];
    searchParams: string;
}