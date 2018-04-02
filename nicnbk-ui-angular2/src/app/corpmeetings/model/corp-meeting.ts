import {UpdatedEntity} from "../../common/model/updated-entity";

export class CorpMeeting {
    id: number;
    type: string;
    date: string;
    number: string;
    files: any[];

    attendeesNIC: any[];
}