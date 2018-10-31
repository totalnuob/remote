import {ICMeeting} from "./ic-meeting";
import {BaseDictionary} from "../../common/model/base-dictionary";

export class ICMeetingTopic{
    id: number;
    icMeeting: ICMeeting;
    shortName: string;
    longDescription: string;
    type: string;

    materials: any[];
    protocols: any[];

    decision: string;

    tags: any[];

    constructor(){
        this.icMeeting = new ICMeeting();
    }
}