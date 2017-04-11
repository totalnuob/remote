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
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {TRIP_TYPES} from "./mock.news.lookups";


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

        //if(localStorage.getItem("authenticatedUserRoles").indexOf("ADMIN") != -1){
        //    return Promise.resolve(MEMO_TYPES);
        //}
        //
        //var availableTypes = [
        //    {id: 1, code: '1', nameEn: 'General', nameKz: '', nameRu: ''}
        //];
        //var accessChecker = new ModuleAccessCheckerService;
        //if(accessChecker.checkAccessHedgeFunds()){
        //    availableTypes.push({id: 1, code: '3', nameEn: 'Hedge Funds', nameKz: '', nameRu: ''});
        //}
        //if(accessChecker.checkAccessPrivateEquity()){
        //    availableTypes.push({id: 1, code: '2', nameEn: 'Private Equity', nameKz: '', nameRu: ''});
        //}
        //if(accessChecker.checkAccessRealEstate()){
        //    availableTypes.push({id: 1, code: '4', nameEn: 'Real Estate', nameKz: '', nameRu: ''});
        //}
        //
        //return Promise.resolve(availableTypes);
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
        return this.http.get(CURRENCIES_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getPEStrategies(): Observable<Lookup[]>{
        return this.http.get(PE_STRATEGIES_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getREStrategies(){
        return this.http.get(RE_STRATEGIES_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getHFStrategies(){
        return this.http.get(HF_STRATEGIES_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getHFSubStrategies(strategy){
        return this.http.get(HF_SUBSTRATEGIES_URL + strategy, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getGeographies(){
        return this.http.get(GEOGRAPHIES_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getPEIndustryFocus(){
        return this.http.get(PE_INDUSTRY_FOCUS_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
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
        return this.http.get(HEDGE_FUND_STATUS_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getSubscriptionFrequencies(){
        return this.http.get(SUBSCRIPTION_FREQUENCY_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getRedemptionFrequencies(){
        return this.http.get(REDEMPTION_FREQUENCY_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getRedemptionNoticePeriods(){
        return this.http.get(REDEMPTION_NOTICE_PERIOD_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getSidePocketLookup(){
        return this.http.get(SIDE_POCKET_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
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

    getTripTypes(){
        return Promise.resolve(TRIP_TYPES);
    }
}