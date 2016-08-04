import { Injectable } from '@angular/core';
import {NEWS_TYPES} from "./mock.news.lookups";
import {MEETING_TYPES} from "./mock.news.lookups";
import {MEMO_TYPES} from "./mock.news.lookups";
import {Lookup} from "./lookup";

import { Http, Response, Headers, RequestOptions } from '@angular/http';
import {Observable} from "rxjs/Observable";
import {CommonService} from "./common.service";
import {PE_STRATEGIES_URL} from "./lookup.service.url";
import {RE_STRATEGIES_URL} from "./lookup.service.url";
import {HF_STRATEGIES_URL} from "./lookup.service.url";
import {GEOGRAPHIES_URL} from "./lookup.service.url";
import {CURRENCIES_URL} from "./lookup.service.url";


@Injectable()
export class LookupService extends CommonService{

    // TODO: Promise vs Observable ???

    // TODO: common lookup service passing lookup name ???

    constructor(private http: Http){

    }

    getNewsTypes(){
        return Promise.resolve(NEWS_TYPES);
    }

    getMemoTypes(){
        return Promise.resolve(MEMO_TYPES);
    }

    getMeetingTypes(){
        return Promise.resolve(MEETING_TYPES);
    }

    getClosingSchedules(){
        var list = [];
        var year = new Date().getFullYear();
        for(var i = year; i <= year + 2; i++){
            for(var j = 1; j <= 4; j++){
                var lookup = new Lookup;
                lookup.code = ("Q" + j + " " + i);
                lookup.nameEn = "Open until " + ("Q" + j + " " + i);
                list.push(lookup);
            }
        }
        return Promise.resolve(list);
    }

    getOpeningScheduleList(){
        var list = [];
        var year = new Date().getFullYear();
        for(var i = year; i <= year + 2; i++){
            var lookup = new Lookup;
            lookup.code = i + "";
            lookup.nameEn = i + "";
            list.push(lookup);
        }
        for(var i = year; i <= year + 2; i++){
            for(var j = 1; j <= 4; j++){
                var lookup = new Lookup;
                lookup.code = ("Q" + j + " " + i);
                lookup.nameEn = ("Q" + j + " " + i);
                list.push(lookup);
            }
        }
        return Promise.resolve(list);
    }

    getCurrencyList(){
        return this.http.get(CURRENCIES_URL)
            .map(this.extractDataList)
            .catch(this.handleError);
    }

    getPEStrategies(): Observable<Lookup[]>{
        return this.http.get(PE_STRATEGIES_URL)
            .map(this.extractDataList)
            .catch(this.handleError);
    }

    getREStrategies(){
        return this.http.get(RE_STRATEGIES_URL)
            .map(this.extractDataList)
            .catch(this.handleError);
    }

    getHFStrategies(){
        return this.http.get(HF_STRATEGIES_URL)
            .map(this.extractDataList)
            .catch(this.handleError);
    }

    getGeographies(){
        return this.http.get(GEOGRAPHIES_URL)
            .map(this.extractDataList)
            .catch(this.handleError);
    }
}