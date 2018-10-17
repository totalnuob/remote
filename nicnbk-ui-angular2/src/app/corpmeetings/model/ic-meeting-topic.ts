import {ICMeeting} from "./ic-meeting";

export class ICMeetingTopic{
    id: number;
    icMeeting: ICMeeting;
    shortName: string;
    longDescription: string;

    materials: any[];
    protocols: any[];

    constructor(){
        this.icMeeting = new ICMeeting();
    }
}