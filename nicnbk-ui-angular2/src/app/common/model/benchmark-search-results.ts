
import {BenchmarkValue} from "./benchmark.value";
export class BenchmarkSearchResults{
    showPageFrom : number;
    showPageTo: number;
    totalPages: number;
    totalElements: number;
    currentPage: number;
    benchmarks: BenchmarkValue[];
    searchParams: string;
}