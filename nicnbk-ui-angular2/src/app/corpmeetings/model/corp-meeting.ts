
export class CorpMeeting{
    id: number;
    type: string;
    date: string;
    number: string;
    files: any[];

    attendeesNIC: any[];
    attendeesOther: any[];
    shortName: string;

    status: string;
    longDescription: string;
}