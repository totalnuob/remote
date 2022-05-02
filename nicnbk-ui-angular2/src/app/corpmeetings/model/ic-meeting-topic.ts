import {ICMeeting} from "./ic-meeting";
import {BaseDictionary} from "../../common/model/base-dictionary";
import {Employee} from "../../employee/model/employee";
import {FileEntity} from "../../common/model/file-entity";

export class ICMeetingTopic{
    id: number;
    icMeeting: ICMeeting;
    type: string;
    name: string;
    //nameUpd: string;
    description: string;
    explanatoryNote: any;
    explanatoryNoteUpd: any;
    speaker: Employee;
    executor: Employee;

    decision: string;
    decisions: any[];
    //decisionUpd: string;
    approveList: [];

    materials: FileEntity[];
    materialsUpd: FileEntity[];

    uploadMaterials: FileEntity[];
    uploadMaterialsUpd: FileEntity[];

    votes: [];
    authenticatedUserVote: string;

    publishedUpd: boolean;

    tags: any[];

    published: boolean;
    closed: boolean;
    toPublish: boolean;

    constructor(){
        this.icMeeting = new ICMeeting();
        this.speaker = new Employee();
        this.executor = new Employee();
    }
}