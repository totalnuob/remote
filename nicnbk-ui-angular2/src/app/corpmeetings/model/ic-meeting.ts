import {ICMeetingTopic} from "ic-meeting-topic";

export class ICMeeting{
    id: number;
    date: string;
    time: string;
    place: string;
    number: string;
    corpMeetingType: string;

    //numberOfTopics: number;

    attendees: [];
    invitees: [];

    questions: ICMeetingTopic[];

    //protocolFileId: number;
    //protocolFileName: string;

    closed: boolean;
    lockedByDeadline: boolean;
    unlockedForFinalize: boolean;

    closeable: boolean;

}