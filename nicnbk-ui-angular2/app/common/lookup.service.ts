import { Injectable } from '@angular/core';
import {NEWS_TYPES} from "./mock.news.lookups";
import {MEETING_TYPES} from "./mock.news.lookups";
import {MEMO_TYPES} from "./mock.news.lookups";
import {Lookup} from "./lookup";


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
        var list = [];
        var lookup = new Lookup;
        lookup.code = "USD";
        lookup.nameEn = "USD";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = "EUR";
        lookup.nameEn = "EUR";
        list.push(lookup);

        return Promise.resolve(list);
    }

    getPEStrategies(){

        var list = [];
        var lookup = new Lookup;
        lookup.code = "MEGA_CUP";
        lookup.nameEn = "Mega/Large Cup Buyout";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = "MID_CUP";
        lookup.nameEn = "Mid Cup Buyout";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = "SM_CUP";
        lookup.nameEn = "Small Cup Buyout";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = "CREDIT";
        lookup.nameEn = "Credit";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = "MULTI";
        lookup.nameEn = "Multi";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = "OTHER";
        lookup.nameEn = "Other";
        list.push(lookup);

        return Promise.resolve(list);
    }

    getREStrategies(){

        var list = [];
        var lookup = new Lookup;
        lookup.code = "CORE";
        lookup.nameEn = "Core";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = "CORE+";
        lookup.nameEn = "Core+";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = "VAL_ADD";
        lookup.nameEn = "Value Added";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = "OPPORTUN";
        lookup.nameEn = "Opportunistic";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = "MULTI";
        lookup.nameEn = "Multistrategy";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = "OTHER";
        lookup.nameEn = "Other";
        list.push(lookup);

        return Promise.resolve(list);
    }

    getHFStrategies(){

        var list = [];
        var lookup = new Lookup;
        lookup.code = "MULTI";
        lookup.nameEn = "Multistrategy";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = "EQUITY";
        lookup.nameEn = "Equity";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = "EVENT";
        lookup.nameEn = "Event Driven";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = "REL_VAL";
        lookup.nameEn = "Relative Value";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = "MACRO";
        lookup.nameEn = "Macro";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = "CREDIT";
        lookup.nameEn = "Credit";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = "COMMOD";
        lookup.nameEn = "Commodities";
        list.push(lookup);

        return Promise.resolve(list);
    }

    getGeographies(){
        var list = [];

        var lookup = new Lookup;
        lookup.code = "NA";
        lookup.nameEn = "North America";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = "EUR";
        lookup.nameEn = "Eurpoe";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = "ASIA";
        lookup.nameEn = "Asia";
        list.push(lookup);

        lookup = new Lookup;
        lookup.code = "ROW";
        lookup.nameEn = "Row";
        list.push(lookup);

        return Promise.resolve(list);
    }
}