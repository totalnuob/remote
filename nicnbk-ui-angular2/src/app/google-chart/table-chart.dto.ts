export class TableChartDto{
    columns: TableColumnDto[];
    rows: any[];
}

export class TableColumnDto{
    type: string;
    name: string;
}