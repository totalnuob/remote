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

@Injectable()
export class HedgeFundScreeningService extends CommonService{

    private HF_BASE_URL = DATA_APP_URL + "hf/screening/";

    private HF_SCREENING_SAVE_URL = this.HF_BASE_URL + "save/";
    private HF_SCREENING_GET_URL = this.HF_BASE_URL + "get/";
    private HF_SCREENING_SEARCH_URL = this.HF_BASE_URL + "search/";
    private HF_SCREENING_DATA_FILE_UPLOAD_URL = this.HF_BASE_URL + "file/upload/";
    private HF_SCREENING_DATA_FILE_DELETE_URL = this.HF_BASE_URL + "file/delete/";

    private HF_SCREENING_RETURN_SEARCH_URL = this.HF_BASE_URL + "return/search/";
    private HF_SCREENING_AUM_SEARCH_URL = this.HF_BASE_URL + "aum/search/";

    private HF_SCREENING_FILTERED_RESULTS_GET_URL = this.HF_BASE_URL + "filteredResults/get/";
    private HF_SCREENING_FILTERED_RESULTS_SAVE_URL = this.HF_BASE_URL + "filteredResults/save/";
    private HF_SCREENING_FILTERED_RESULTS_FINDALL_URL = this.HF_BASE_URL + "filteredResults/findAll/";

    private HF_SCREENING_FILTERED_RESULTS_STATISTICS_GET_URL = this.HF_BASE_URL + "filteredResults/statistics/get/";

    private HF_SCREENING_FILTERED_RESULTS_QUALIFIED_FUNDS_LIST_GET_URL = this.HF_BASE_URL + "filteredResults/fundList/get/";





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

    postFiles(screeningId, params, files){
        return this.uploadService.postFiles(this.HF_SCREENING_DATA_FILE_UPLOAD_URL + screeningId, [], files, null);
    }

    removeFile(screeningId, fileId) {
        return this.http.delete(this.HF_SCREENING_DATA_FILE_DELETE_URL + screeningId + "/" + fileId, this.getOptionsWithCredentials())
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

    getFilteredResultQualifiedFundList(params): Observable<any[]> {

        let body = JSON.stringify(params);

        return this.http.post(this.HF_SCREENING_FILTERED_RESULTS_QUALIFIED_FUNDS_LIST_GET_URL, body, this.getOptionsWithCredentials())
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

}