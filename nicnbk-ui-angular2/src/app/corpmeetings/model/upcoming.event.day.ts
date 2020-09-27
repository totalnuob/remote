import {UpcomingEvent} from "./model/upcoming.event";

export class UpcomingEvent{
    date: string;
    day: string;
    isCurrentMonth: boolean;

    events: UpcomingEvent[];
}