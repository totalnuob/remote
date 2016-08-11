import {TripMemo} from "./trip-memo"

export class TripMemoSearchResults {
    showPageFrom : number;
    showPageTo: number;
    totalPages: number;
    totalElements: number;
    currentPage: number;
    tripMemos: TripMemo[];
    searchParams: string;
}
