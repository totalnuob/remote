export class TripMemo {
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
}