import {PageableSearchParams} from "../../common/model/pageable-search-params";

export class ICAssignmentSearchParams extends PageableSearchParams{
    dateFrom: string;
    dateTo: string;
    searchText: string;
    icNumber: string;
    hideClosed: boolean;

    activeTab: string;
    path: string;

}