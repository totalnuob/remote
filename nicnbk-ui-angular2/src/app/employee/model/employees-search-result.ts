import {Employee} from "./employee";

export class EmployeesSearchResult {
    showPageFrom: number;
    showPageTo: number;
    totalPages: number;
    totalElements: number;
    currentPage: number;
    employees: Employee[];
    searchParams: string;
}