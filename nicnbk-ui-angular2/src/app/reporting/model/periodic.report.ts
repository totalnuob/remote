export class PeriodicReport{

    id: number;
    reportDate: string;
    type: string;
    status: string;

    interestRate: string;

    constructor(id?: number){
        if(id != null) {
            this.id = id
        }
    }
}