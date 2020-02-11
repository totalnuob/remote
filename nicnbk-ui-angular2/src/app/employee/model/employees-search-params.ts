import {PageableSearchParams} from "../../common/model/pageable-search-params";

export class EmployeesSearchParams extends PageableSearchParams{
    firstName: string;
    lastName: string;
    status: string;
}