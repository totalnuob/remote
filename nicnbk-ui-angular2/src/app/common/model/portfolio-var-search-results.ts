
import {PortfolioVarValue} from "./portfolio-var.value";
export class PortfolioVarSearchResults{
    showPageFrom : number;
    showPageTo: number;
    totalPages: number;
    totalElements: number;
    currentPage: number;
    portfolioVars: PortfolioVarValue[];
    searchParams: string;
}