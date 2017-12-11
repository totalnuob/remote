export class PeriodicReport{

    id: number;
    reportDate: string;
    type: string;
    status: string;

    constructor(){
    }

    constructor(id){
        this.id = id
    }
}