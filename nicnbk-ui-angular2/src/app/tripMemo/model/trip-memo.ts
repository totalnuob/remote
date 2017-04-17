import {UpdatedEntity} from "../../common/model/updated-entity";
export class TripMemo extends UpdatedEntity{
    id: number;
    tripType: string;
    name: string;
    meetingDateStart: Date;
    meetingDateEnd: Date;
    organization: string;
    location: string;

    // TODO: match REST format
    attendees: any[];

    status: string;
    description: string;
    author: string;

    creationDate: string;

    files: any[];

    owner: string;
}