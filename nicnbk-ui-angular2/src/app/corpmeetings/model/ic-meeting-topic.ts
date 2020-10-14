import {ICMeeting} from "./ic-meeting";
import {BaseDictionary} from "../../common/model/base-dictionary";
import {FileEntity} from "../../common/model/file-entity";

export class ICMeetingTopic{
    id: number;
    icMeeting: ICMeeting;
    name: string;
    description: string;
    explanatoryNote: any[];

    decision: string;
    decisionApproveList: [];

    materials: FileEntity[];
    tags: any[];

    constructor(){
        this.icMeeting = new ICMeeting();
    }
}