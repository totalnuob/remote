import { Injectable } from '@angular/core';
//import {NEWS_TYPES} from "./mock.news.lookups";
//import {MEETING_TYPES} from "./mock.news.lookups";
//import {MEMO_TYPES} from "./mock.news.lookups";
import {Lookup} from "./lookup";

import {Strategy} from "./model/strategy";

import { Http, Response, Headers, RequestOptions } from '@angular/http';
import {Observable} from "rxjs/Observable";
import {CommonService} from "./common.service";
import {PE_STRATEGIES_URL} from "./lookup.service.url";
import {ALL_STRATEGIES_URL} from "./lookup.service.url";
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
import {NB_CHART_OF_ACCOUNTS_URL} from "./lookup.service.url";
import {NIC_REPORTING_PE_TRANCHE_TYPES_URL} from "./lookup.service.url";
import {NIC_REPORTING_RE_TRANCHE_TYPES_URL} from "./lookup.service.url";

import {NIC_REPORTING_CHART_OF_ACCOUNTS_URL} from "./lookup.service.url";
import {NIC_REPORTING_CHART_OF_ACCOUNTS_TYPE_URL} from "./lookup.service.url";
import {NICReportingChartOfAccounts} from "../reporting/model/nic.reporting.chart.of.accounts.";
import {OptionsBehavior} from "ng2-select/index";
import {BaseDictionary} from "./model/base-dictionary";
import {TarragonNICReportingChartOfAccounts} from "../reporting/model/tarragon,.nic.reporting.chart.of.accounts.";
import {TARRAGON_NIC_REPORTING_CHART_OF_ACCOUNTS_URL} from "./lookup.service.url";
import {RESERVE_CALCULATION_EXPENSE_TYPE_URL} from "./lookup.service.url";
import {RESERVE_CALCULATION_ENTITY_TYPE_URL} from "./lookup.service.url";
import {MM_FIELDS_URL} from "./lookup.service.url";
import {NEWS_TYPE_URL} from "./lookup.service.url";
import {MEETING_TYPE_URL} from "./lookup.service.url";
import {MEMO_TYPE_URL} from "./lookup.service.url";
import {RESERVE_CALCULATION_EXPORT_SIGNER_TYPE_URL} from "./lookup.service.url";
import {RESERVE_CALCULATION_EXPORT_DOER_TYPE_URL} from "./lookup.service.url";
import {RESERVE_CALCULATION_EXPORT_APPROVE_LIST_TYPE_URL} from "./lookup.service.url";
import {CORP_MEETING_TYPE_URL} from "./lookup.service.url";
import {TERRA_NIC_REPORTING_CHART_OF_ACCOUNTS_URL} from "./lookup.service.url";
import {TerraNICReportingChartOfAccounts} from "../reporting/model/terra,.nic.reporting.chart.of.accounts.";
import {GET_CURRENCY_RATES_URL} from "./lookup.service.url";
import {SEARCH_CURRENCY_RATES_URL} from "./lookup.service.url";
import {SAVE_CURRENCY_RATES_URL} from "./lookup.service.url";
import {DELETE_CURRENCY_RATES_URL} from "./lookup.service.url";
import {NB_REP_TARRAGON_BALANCE_TYPE_URL} from "./lookup.service.url";
import {NB_REP_TARRAGON_OPERATIONS_TYPE_URL} from "./lookup.service.url";
import {NB_REP_TARRAGON_CASHFLOWS_TYPE_URL} from "./lookup.service.url";
import {NB_REP_TARRAGON_INVESTMENT_TYPE_URL} from "./lookup.service.url";
import {SAVE_LOOKUP_VALUE_URL} from "./lookup.service.url";
import {SAVE_STRATEGY_LOOKUP_VALUE_URL} from "./lookup.service.url";
import {NB_REP_SINGULARITY_CHART_ACCOUNTS_TYPE_URL} from "./lookup.service.url";
import {NB_CHART_ACCOUNTS__URL} from "./lookup.service.url";
import {NB_REP_TERRA_CHART_ACCOUNTS_TYPE_URL} from "./lookup.service.url";
import {NB_REP_TERRA_BALANCE_TYPE_URL} from "./lookup.service.url";
import {NB_REP_TERRA_PROFIT_LOSS_TYPE_URL} from "./lookup.service.url";
import {PERIODIC_DATA_TYPES_URL} from "./lookup.service.url";
import {NIC_SINGULARITY_CHART_ACCOUNTS_URL} from "./lookup.service.url";
import {NIC_TARRAGON_CHART_ACCOUNTS_URL} from "./lookup.service.url";
import {NIC_TERRA_CHART_ACCOUNTS_URL} from "./lookup.service.url";
import {SAVE_MATCHING_NIC_CHART_ACCOUNTS_URL} from "./lookup.service.url";
import {SAVE_NIC_CHART_ACCOUNTS_URL} from "./lookup.service.url";
import {SEARCH_NIC_REPORTING_CHART_OF_ACCOUNTS_URL} from "./lookup.service.url";
import {DELETE_LOOKUP_VALUE_BY_TYPE_URL} from "./lookup.service.url";
import {DELETE_MATCHING_LOOKUP_VALUE_BY_TYPE_URL} from "./lookup.service.url";
import {IC_MEETING_TOPIC_TYPES_URL} from "./lookup.service.url";
import {BENCHMARK_TYPE_URL} from "./lookup.service.url";
import {SEARCH_BENCHMARKS_URL} from "./lookup.service.url";
import {SAVE_BENCHMARK_URL} from "./lookup.service.url";
import {SAVE_CURRENCY_RATES_LIST_URL} from "./lookup.service.url";
import {SAVE_BENCHMARKS_LIST_URL} from "./lookup.service.url";


@Injectable()
export class LookupService extends CommonService{

    // TODO: Promise vs Observable ???

    // TODO: common lookup service passing lookup name ???

    constructor(private http: Http){
        super();
    }

    getNewsTypes(){
        //return Promise.resolve(NEWS_TYPES);
        return this.http.get(NEWS_TYPE_URL, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getMemoTypes(){
        //return Promise.resolve(MEMO_TYPES);
        return this.http.get(MEMO_TYPE_URL, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);

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
        //return Promise.resolve(MEETING_TYPES);
        return this.http.get(MEETING_TYPE_URL, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getCorpMeetingTypes(){
        //return Promise.resolve(MEETING_TYPES);
        return this.http.get(CORP_MEETING_TYPE_URL, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
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

    getBenchmarkTypeList(){
        return this.http.get(BENCHMARK_TYPE_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getAllStrategies(): Observable<Strategy[]>{
            return this.http.get(ALL_STRATEGIES_URL, this.getOptionsWithCredentials())
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

    getNBChartOfAccounts(): Observable<BaseDictionary[]>{
        return this.http.get(NB_CHART_OF_ACCOUNTS_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getPETrancheTypes(): Observable<BaseDictionary[]>{
        return this.http.get(NIC_REPORTING_PE_TRANCHE_TYPES_URL, this.getOptionsWithCredentials())
                .map(this.extractDataList)
                .catch(this.handleErrorResponse);
    }

    getRETrancheTypes(): Observable<BaseDictionary[]>{
        return this.http.get(NIC_REPORTING_RE_TRANCHE_TYPES_URL, this.getOptionsWithCredentials())
                .map(this.extractDataList)
                .catch(this.handleErrorResponse);
    }


    //TODO: switch to searching

    getNICReportingChartOfAccounts(code): Observable<NICReportingChartOfAccounts[]>{
        if(code){
            return this.http.get(NIC_REPORTING_CHART_OF_ACCOUNTS_URL + code, this.getOptionsWithCredentials())
                .map(this.extractDataList)
                .catch(this.handleErrorResponse);
        }else{
            return this.http.get(NIC_REPORTING_CHART_OF_ACCOUNTS_URL, this.getOptionsWithCredentials())
                .map(this.extractDataList)
                .catch(this.handleErrorResponse);
        }

    }

    getChartAccountsType(){
        return this.http.get(NIC_REPORTING_CHART_OF_ACCOUNTS_TYPE_URL, this.getOptionsWithCredentials())
                .map(this.extractDataList)
                .catch(this.handleErrorResponse);
    }

    searchNICReportingChartOfAccounts(searchParams){
        let body = JSON.stringify(searchParams);
        //console.log(body);
        return this.http.post(SEARCH_NIC_REPORTING_CHART_OF_ACCOUNTS_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getAddableTarragonNICReportingChartOfAccounts(): Observable<TarragonNICReportingChartOfAccounts[]>{
        return this.http.get(TARRAGON_NIC_REPORTING_CHART_OF_ACCOUNTS_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getAddableTerraNICReportingChartOfAccounts(): Observable<TerraNICReportingChartOfAccounts[]>{
        return this.http.get(TERRA_NIC_REPORTING_CHART_OF_ACCOUNTS_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getReserveCalculationExpenseTypeLookup(): Observable<BaseDictionary[]>{
        return this.http.get(RESERVE_CALCULATION_EXPENSE_TYPE_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getReserveCalculationEntityTypeLookup(): Observable<BaseDictionary[]>{
        return this.http.get(RESERVE_CALCULATION_ENTITY_TYPE_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getReserveCalculationExportSignerTypeLookup(): Observable<BaseDictionary[]>{
        return this.http.get(RESERVE_CALCULATION_EXPORT_SIGNER_TYPE_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getReserveCalculationExportDoerTypeLookup(): Observable<BaseDictionary[]>{
        return this.http.get(RESERVE_CALCULATION_EXPORT_DOER_TYPE_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getReserveCalculationExportApproveListTypeLookup(): Observable<BaseDictionary[]>{
        return this.http.get(RESERVE_CALCULATION_EXPORT_APPROVE_LIST_TYPE_URL, this.getOptionsWithCredentials())
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

    getMacroMonitorFields(){
        return this.http.get(MM_FIELDS_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getCurrencyRates(searchParams){
        let body = JSON.stringify(searchParams);

        return this.http.post(SEARCH_CURRENCY_RATES_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getBenchmarks(searchParams){
        let body = JSON.stringify(searchParams);

        return this.http.post(SEARCH_BENCHMARKS_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    saveCurrencyRates(item){
        let body = JSON.stringify(item);
        return this.http.post(SAVE_CURRENCY_RATES_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    saveCurrencyRatesList(list){
        let body = JSON.stringify(list);
        return this.http.post(SAVE_CURRENCY_RATES_LIST_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    saveBenchmarksList(list){
        let body = JSON.stringify(list);
        return this.http.post(SAVE_BENCHMARKS_LIST_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    saveBenchmark(item){
        let body = JSON.stringify(item);
        return this.http.post(SAVE_BENCHMARK_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    deleteCurrencyRates(id){
        return this.http.delete(DELETE_CURRENCY_RATES_URL + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }


    getNBReportingTarragonBalanceType(): Observable<BaseDictionary[]>{
        return this.http.get(NB_REP_TARRAGON_BALANCE_TYPE_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getNBReportingTarragonOperationsType(): Observable<BaseDictionary[]>{
        return this.http.get(NB_REP_TARRAGON_OPERATIONS_TYPE_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }
    getNBReportingTarragonCashflowsType(): Observable<BaseDictionary[]>{
        return this.http.get(NB_REP_TARRAGON_CASHFLOWS_TYPE_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }
    getNBReportingTarragonInvestmentType(): Observable<BaseDictionary[]>{
            return this.http.get(NB_REP_TARRAGON_INVESTMENT_TYPE_URL, this.getOptionsWithCredentials())
                .map(this.extractDataList)
                .catch(this.handleErrorResponse);
        }
    getNBReportingSingularityChartAccountsType(): Observable<BaseDictionary[]>{
        return this.http.get(NB_REP_SINGULARITY_CHART_ACCOUNTS_TYPE_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }
    getNBReportingTerraChartAccountsType(): Observable<BaseDictionary[]>{
        return this.http.get(NB_REP_TERRA_CHART_ACCOUNTS_TYPE_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }
    getNBReportingTerraBalanceType(): Observable<BaseDictionary[]>{
        return this.http.get(NB_REP_TERRA_BALANCE_TYPE_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }
    getNBReportingTerraProfitLossType(): Observable<BaseDictionary[]>{
        return this.http.get(NB_REP_TERRA_PROFIT_LOSS_TYPE_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }
    getNBChartAccounts(): Observable<BaseDictionary[]>{
        return this.http.get(NB_CHART_ACCOUNTS__URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    saveLookupValue(lookupType, item){
        let body = JSON.stringify(item);
        return this.http.post(SAVE_LOOKUP_VALUE_URL + lookupType, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    saveStrategyLookupValue(item){
        let body = JSON.stringify(item);
        return this.http.post(SAVE_STRATEGY_LOOKUP_VALUE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getPeriodicDataType(): Observable<BaseDictionary[]>{
        return this.http.get(PERIODIC_DATA_TYPES_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getNBReportingNICSingularityChartAccounts(){
        return this.http.get(NIC_SINGULARITY_CHART_ACCOUNTS_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getNBReportingNICTarragonChartAccounts(){
        return this.http.get(NIC_TARRAGON_CHART_ACCOUNTS_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getNBReportingNICTerraChartAccounts(){
        return this.http.get(NIC_TERRA_CHART_ACCOUNTS_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    saveMatchingNICChartAccounts(lookupType, item){
        let body = JSON.stringify(item);
        return this.http.post(SAVE_MATCHING_NIC_CHART_ACCOUNTS_URL + lookupType, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }
    saveNICChartAccounts(item){
        let body = JSON.stringify(item);
        return this.http.post(SAVE_NIC_CHART_ACCOUNTS_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    deleteTypedLookupValue(type, id){
        return this.http.delete(DELETE_LOOKUP_VALUE_BY_TYPE_URL + type + "/" + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    deleteMathcingLookupValue(type, id){
        return this.http.delete(DELETE_MATCHING_LOOKUP_VALUE_BY_TYPE_URL + type + "/" + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getICMeetingTopicTypes(){
        return this.http.get(IC_MEETING_TOPIC_TYPES_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }




}