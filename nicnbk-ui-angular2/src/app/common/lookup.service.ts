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
import {HF_STRATEGIES_URL, HF_SUBSTRATEGIES_URL} from "./lookup.service.url";
import {GEOGRAPHIES_URL} from "./lookup.service.url";
import {CURRENCIES_URL, HEDGE_FUND_STATUS_URL} from "./lookup.service.url";
import {SUBSCRIPTION_FREQUENCY_URL} from "./lookup.service.url";
import {REDEMPTION_FREQUENCY_URL} from "./lookup.service.url";
import {REDEMPTION_NOTICE_PERIOD_URL} from "./lookup.service.url";
import {SIDE_POCKET_URL} from "./lookup.service.url";
import {PE_INDUSTRY_FOCUS_URL} from "./lookup.service.url";


@Injectable()
export class LookupService extends CommonService{

    // TODO: Promise vs Observable ???

    // TODO: common lookup service passing lookup name ???

    constructor(private http: Http){
        super();
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

    getHFSubStrategies(strategy){
        return this.http.get(HF_SUBSTRATEGIES_URL + strategy)
            .map(this.extractDataList)
            .catch(this.handleError);
    }

    getGeographies(){
        return this.http.get(GEOGRAPHIES_URL)
            .map(this.extractDataList)
            .catch(this.handleError);
    }

    getPEIndustryFocus(){
        return this.http.get(PE_INDUSTRY_FOCUS_URL)
            .map(this.extractDataList)
            .catch(this.handleError);
    }

    getManagerTypes(){
        var list = [];
        var lookup = new Lookup;
        lookup.code = 'FOF';
        lookup.nameEn = "Fund of Funds";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = 'MANAGER';
        lookup.nameEn = "Manager Fund";
        list.push(lookup);

        return Promise.resolve(list);
    }

    getHedgeFundStatuses(){
        return this.http.get(HEDGE_FUND_STATUS_URL)
            .map(this.extractDataList)
            .catch(this.handleError);
    }

    getSubscriptionFrequencies(){
        return this.http.get(SUBSCRIPTION_FREQUENCY_URL)
            .map(this.extractDataList)
            .catch(this.handleError);
    }

    getRedemptionFrequencies(){
        return this.http.get(REDEMPTION_FREQUENCY_URL)
            .map(this.extractDataList)
            .catch(this.handleError);
    }

    getRedemptionNoticePeriods(){
        return this.http.get(REDEMPTION_NOTICE_PERIOD_URL)
            .map(this.extractDataList)
            .catch(this.handleError);
    }

    getSidePocketLookup(){
        return this.http.get(SIDE_POCKET_URL)
            .map(this.extractDataList)
            .catch(this.handleError);
    }

    getManagerStatuses(){
        var list = [];
        var lookup = new Lookup;
        lookup.code = 'OPEN_ALL';
        lookup.nameEn = "Open to all";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = 'CLOSED';
        lookup.nameEn = "Closed";
        list.push(lookup);

        return Promise.resolve(list);
    }

    getLegalStructures(){
        var list = [];
        var lookup = new Lookup;
        lookup.code = 'LLP';
        lookup.nameEn = "Limited Liability Partnership";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = 'LLC';
        lookup.nameEn = "Limited Liability Company";
        list.push(lookup);

        return Promise.resolve(list);
    }

    getDomicileCountries(){
        var list = [];
        var lookup = new Lookup;
        lookup.code = 'US';
        lookup.nameEn = "United States";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = 'UK';
        lookup.nameEn = "United Kingdom";
        list.push(lookup);

        return Promise.resolve(list);
    }


    getRedemptionFrequencyTypes(){
        var list = [];
        var lookup = new Lookup;
        lookup.code = 'TYPE1';
        lookup.nameEn = "Redemption Freq Type 1";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = 'TYPE2';
        lookup.nameEn = "Redemption Freq Type 2";
        list.push(lookup);

        return Promise.resolve(list);
    }

    getRedemptionNotificationPeriodTypes(){
        var list = [];
        var lookup = new Lookup;
        lookup.code = 'TYPE1';
        lookup.nameEn = "Notif Period Type 1";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = 'TYPE2';
        lookup.nameEn = "Notif Period Type 2";
        list.push(lookup);

        return Promise.resolve(list);
    }

    getSubscriptionFrequencyTypes(){
        var list = [];
        var lookup = new Lookup;
        lookup.code = 'TYPE1';
        lookup.nameEn = "Subscription Type 1";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = 'TYPE2';
        lookup.nameEn = "Subscription Type 2";
        list.push(lookup);

        return Promise.resolve(list);
    }
}