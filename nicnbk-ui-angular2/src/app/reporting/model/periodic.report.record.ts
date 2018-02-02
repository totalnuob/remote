export class PeriodicReportRecord{

    name: string;
    values: number[];

    public getValue(value){
        return value > 0 ? value : (value == 0 ? '-' : '(' + (-1)* value + ')');
    }
}