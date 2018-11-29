import {HedgeFundScreeningParsedData} from "./hf.screening.parsed.data";

export class HedgeFundScreening {
    id: number;
    name: string;
    description: string;
    date: string;

    fileId: number;
    fileName: string;

    ucitsFileId: number;
    ucitsFileName: string;

    parsedData: HedgeFundScreeningParsedData[];

    parsedUcitsData: HedgeFundScreeningParsedData[];
}