import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable }     from 'rxjs/Observable';

import {DATA_APP_URL} from "../common/common.service.constants";
import {CommonService} from "../common/common.service";
import {HedgeFundScreening} from "./model/hf.screening";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {FileUploadService} from "../upload/file.upload.service";
import {HedgeFundScreeningFilteredResult} from "./model/hf.screening.filtered.result";
import {HedgeFundScreeningFilteredResultStatistics} from "./model/hf.screening.filtered.result.statistics";
import {ListResponse} from "../common/list.response";

var fileSaver = require("file-saver");

@Injectable()
export class HedgeFundScreeningService extends CommonService{

    private HF_BASE_URL = DATA_APP_URL + "hf/screening/";

    private HF_SCREENING_SAVE_URL = this.HF_BASE_URL + "save/";
    private HF_SCREENING_GET_URL = this.HF_BASE_URL + "get/";
    private HF_SCREENING_SEARCH_URL = this.HF_BASE_URL + "search/";
    private HF_SCREENING_GET_ALL_URL = this.HF_BASE_URL + "getAll/";

    private HF_SCREENING_DATA_FILE_UPLOAD_URL = this.HF_BASE_URL + "file/upload/";
    private HF_SCREENING_UCITS_FILE_UPLOAD_URL = this.HF_BASE_URL + "file/ucits/upload/";

    private HF_SCREENING_DATA_FILE_DELETE_URL = this.HF_BASE_URL + "file/delete/";
    private HF_SCREENING_UCITS_FILE_DELETE_URL = this.HF_BASE_URL + "file/ucits/delete/";

    private HF_SCREENING_RETURN_SEARCH_URL = this.HF_BASE_URL + "return/search/";
    private HF_SCREENING_AUM_SEARCH_URL = this.HF_BASE_URL + "aum/search/";
    private HF_SCREENING_UCITS_AUM_SEARCH_URL = this.HF_BASE_URL + "aum/ucits/search/";

    private HF_SCREENING_FILTERED_RESULTS_GET_URL = this.HF_BASE_URL + "filteredResults/get/";
    private HF_SCREENING_FILTERED_RESULTS_SAVE_URL = this.HF_BASE_URL + "filteredResults/save/";
    private HF_SCREENING_FILTERED_RESULTS_FINDALL_URL = this.HF_BASE_URL + "filteredResults/findAll/";

    private HF_SCREENING_FILTERED_RESULTS_STATISTICS_GET_URL = this.HF_BASE_URL + "filteredResults/statistics/get/";

    private HF_SCREENING_FILTERED_RESULTS_QUALIFIED_FUNDS_LIST_GET_URL = this.HF_BASE_URL + "filteredResults/qualifiedFundList/get/";
    private HF_SCREENING_FILTERED_RESULTS_UNQUALIFIED_FUNDS_LIST_GET_URL = this.HF_BASE_URL + "filteredResults/unqualifiedFundList/get/";
    private HF_SCREENING_FILTERED_RESULTS_UNDECIDED_FUNDS_LIST_GET_URL = this.HF_BASE_URL + "filteredResults/undecidedFundList/get/";

    private HF_SCREENING_FILTERED_UPDATE_MANAGER_AUM_URL = this.HF_BASE_URL + "filteredResults/updateManagerAUM/";
    private HF_SCREENING_FILTERED_UPDATE_FUND_URL = this.HF_BASE_URL + "filteredResults/updateFund/";
    private HF_SCREENING_FILTERED_DELETE_ADDED_FUND_URL = this.HF_BASE_URL + "filteredResults/deleteAddedFund/";
    private HF_SCREENING_FILTERED_EXCLUDE_FUND_URL = this.HF_BASE_URL + "filteredResults/excludeFund/";
    private HF_SCREENING_FILTERED_INCLUDE_FUND_URL = this.HF_BASE_URL + "filteredResults/includeFund/";
    private HF_SCREENING_FILTERED_SAVE_RESULTS_URL = this.HF_BASE_URL + "filteredResults/saveResults/";










    constructor( private http: Http,
                 private moduleAccessChecker: ModuleAccessCheckerService,
                 private uploadService: FileUploadService) {
        super();
    }

    save(entity){

        let body = JSON.stringify(entity);

        return this.http.post(this.HF_SCREENING_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    get(id): Observable<HedgeFundScreening> {
        // TODO: check id
        return this.http.get(this.HF_SCREENING_GET_URL + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    search(searchParams){
        let body = JSON.stringify(searchParams);

        //console.log(body);
        return this.http.post(this.HF_SCREENING_SEARCH_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getALlScreenings(){
        //console.log(body);
        return this.http.get(this.HF_SCREENING_GET_ALL_URL, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    postFiles(screeningId, params, files){
        return this.uploadService.postFiles(this.HF_SCREENING_DATA_FILE_UPLOAD_URL + screeningId, [], files, null);
    }

    postUcitsFile(screeningId, params, files){
        return this.uploadService.postFiles(this.HF_SCREENING_UCITS_FILE_UPLOAD_URL + screeningId, [], files, null);
    }


    removeFile(screeningId, fileId) {
        return this.http.delete(this.HF_SCREENING_DATA_FILE_DELETE_URL + screeningId + "/" + fileId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    removeUcitsFile(screeningId, fileId) {
        return this.http.delete(this.HF_SCREENING_UCITS_FILE_DELETE_URL + screeningId + "/" + fileId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }



    searchReturns(screeningId, numberOfMonths, date){
        let body = {"screeningId": screeningId, "numberOfMonths": numberOfMonths, "date": date};

        //console.log(body);
        return this.http.post(this.HF_SCREENING_RETURN_SEARCH_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    searchAUMs(screeningId, numberOfMonths, date){
        let body = {"screeningId": screeningId, "numberOfMonths": numberOfMonths, "date": date};

        //console.log(body);
        return this.http.post(this.HF_SCREENING_AUM_SEARCH_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    searchUcitsAUMs(screeningId, numberOfMonths, date){
        let body = {"screeningId": screeningId, "numberOfMonths": numberOfMonths, "date": date};

        //console.log(body);
        return this.http.post(this.HF_SCREENING_UCITS_AUM_SEARCH_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getFilteredResult(id): Observable<HedgeFundScreeningFilteredResult> {
        return this.http.get(this.HF_SCREENING_FILTERED_RESULTS_GET_URL + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getFilteredResultStatistics(params): Observable<HedgeFundScreeningFilteredResultStatistics> {

        let body = JSON.stringify(params);

        return this.http.post(this.HF_SCREENING_FILTERED_RESULTS_STATISTICS_GET_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getFilteredResultQualifiedFundList(params): Observable<ListResponse> {

        let body = JSON.stringify(params);

        return this.http.post(this.HF_SCREENING_FILTERED_RESULTS_QUALIFIED_FUNDS_LIST_GET_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getFilteredResultUnqualifiedFundList(params): Observable<any[]> {

        let body = JSON.stringify(params);

        return this.http.post(this.HF_SCREENING_FILTERED_RESULTS_UNQUALIFIED_FUNDS_LIST_GET_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }
    getFilteredResultUndecidedFundList(params): Observable<any[]> {

        let body = JSON.stringify(params);

        return this.http.post(this.HF_SCREENING_FILTERED_RESULTS_UNDECIDED_FUNDS_LIST_GET_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    saveFilteredResult(entity){

        let body = JSON.stringify(entity);

        return this.http.post(this.HF_SCREENING_FILTERED_RESULTS_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    findAllFilteredResults(id): Observable<HedgeFundScreeningFilteredResult[]> {
        // TODO: check id
        return this.http.get(this.HF_SCREENING_FILTERED_RESULTS_FINDALL_URL + id, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    updateManagerAUM(list){

        let body = JSON.stringify(list);

        return this.http.post(this.HF_SCREENING_FILTERED_UPDATE_MANAGER_AUM_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    updateFund(fund){

        let body = JSON.stringify(fund);

        return this.http.post(this.HF_SCREENING_FILTERED_UPDATE_FUND_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    saveResults(saveParams){
        let body = JSON.stringify(saveParams);

        return this.http.post(this.HF_SCREENING_FILTERED_SAVE_RESULTS_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    deleteFund(fund){

        let body = JSON.stringify(fund);

        return this.http.post(this.HF_SCREENING_FILTERED_DELETE_ADDED_FUND_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    excludeFund(fund){

        let body = JSON.stringify(fund);

        return this.http.post(this.HF_SCREENING_FILTERED_EXCLUDE_FUND_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    includeFund(fund){

        let body = JSON.stringify(fund);

        return this.http.post(this.HF_SCREENING_FILTERED_INCLUDE_FUND_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    public makeFileRequest(url, method?: string, body?): Observable<Response> {
        return Observable.fromPromise(new Promise((resolve, reject) => {
            let xhr = new XMLHttpRequest();
            xhr.withCredentials = true; // send auth token with the request
            // TODO: url const
            //let url =  DATA_APP_URL + `periodicReport/export/${this.reportId}/${'KZT_FORM_1'}`;

            var methodName = 'GET';
            if(method){
                if(method === 'GET'){
                    methodName = 'GET';
                }else if(method === 'POST'){
                    methodName = 'POST';
                }
            }
            xhr.open(methodName, url, true);

            xhr.setRequestHeader("Content-type", "application/json");
            xhr.responseType = 'blob';

            // Xhr callback when we get a result back
            // We are not using arrow function because we need the 'this' context
            xhr.onreadystatechange = function () {
                // We use setTimeout to trigger change detection in Zones
                setTimeout(() => {
                    //self.pending = false;
                }, 0);

                // If we get an HTTP status OK (200), save the file using fileSaver
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        resolve(JSON.parse("{\"message\" : \"OK\"}"));
                        var blob = new Blob([this.response], {type: this.response.type});
                        var file_name = xhr.getResponseHeader("Content-Disposition").match(/filename=(.*?)$/)[1];
                        fileSaver.saveAs(blob, file_name);
                    }else {
                        console.log("Error - " + xhr.status);
                        console.log(xhr);
                        reject(xhr.response);
                    }
                }
            };
            // Start the Ajax request
            //xhr.open("GET", url);
            if(body){
                xhr.send(JSON.stringify(body));
            }else {
                xhr.send();
            }
        }));
        //.map(this.extractData);
        //.catch(this.handleErrorResponse);
    }

}