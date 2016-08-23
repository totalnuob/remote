export class Memo{
    id: number;
    memoType: number;
    meetingType: string;
    firmName: string;
    fundName: string;
    meetingDate: Date;
    meetingTime: string;
    meetingLocation: string;
    arrangedBy: string;
    arrangedByDescription: string;
    purpose: string;

    // TODO: match REST format
    attendeesNIC: any[];

    attendeesNICOther: string;
    attendeesOther: string;
    author: string;

    // TODO: move to fund-memo.ts ?
    strategies: any[];
    geographies: any[];

    creationDate: string;

    files: any[];

    tags: string[];

}