import {ICMeetingTopic} from "ic-meeting-topic";

export class ICMeetingAssignment{
    id: number;
    name: string;
    dateDue: string;
    status: string;

    icMeetingTopic: ICMeetingTopic;

    closed: boolean;
}