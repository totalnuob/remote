import {Memo} from "./memo";

export class MemoSearchResults{
    showPageFrom : number;
    showPageTo: number;
    totalPages: number;
    totalElements: number;
    currentPage: number;
    memos: Memo[];
    searchParams: string;
}