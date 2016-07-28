import { Injectable } from '@angular/core';
import {NEWS_TYPES} from "./mock.news.lookups";
import {MEETING_TYPES} from "./mock.news.lookups";
import {MEMO_TYPES} from "./mock.news.lookups";


@Injectable()
export class LookupService {

    // TODO: Promise vs Observable ???

    // TODO: common lookup service passing lookup name ???

    getNewsTypes(){
        return Promise.resolve(NEWS_TYPES);
    }

    getMemoTypes(){
        return Promise.resolve(MEMO_TYPES);
    }

    getMeetingTypes(){
        return Promise.resolve(MEETING_TYPES);
    }
}