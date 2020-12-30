
import {StressTestValue} from "./stress-test.value";
export class StressTestSearchResults{
    showPageFrom : number;
    showPageTo: number;
    totalPages: number;
    totalElements: number;
    currentPage: number;
    stressTestValue: StressTestValue[];
    searchParams: string;
}