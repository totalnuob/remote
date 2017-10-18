import {Injectable} from '@angular/core'
import {Http, Response, Headers, RequestOptions } from '@angular/http';
import {Observable}     from 'rxjs/Observable';

import {CommonService} from "../common/common.service";
import {DATA_APP_URL} from "../common/common.service.constants";
import {PEFund} from "./model/pe.fund";
import {FileUploadService} from "../upload/file.upload.service";

@Injectable()
export class PEFundService extends CommonService {
    private PE_BASE_URL = DATA_APP_URL + "pe/fund/";
    private PE_FUND_SAVE_URL = this.PE_BASE_URL + "save/";
    private PE_FUND_GET_URL = this.PE_BASE_URL + "get/";
    private PE_FUND_SEARCH_URL = this.PE_BASE_URL + "search/";
    private PE_FUND_UPLOAD_GROSS_CF_URL = this.PE_BASE_URL + "uploadGrossCF/";
    private PE_FUND_SAVE_COMPANY_PERFORMANCE_URL = this.PE_BASE_URL + "savePerformance/";
    private PE_FUND_SAVE_GROSS_CF_URL = this.PE_BASE_URL + "saveGrossCF/";

    constructor(
        private http: Http,
        private uploadService: FileUploadService
    ){
        super();
    }

    save(entity){
        let body = JSON.stringify(entity);

        return this.http.post(this.PE_FUND_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    //savePerformance(entity, id) {
    //    let body = JSON.stringify(entity);
    //
    //    return this.http.post(this.PE_FUND_SAVE_COMPANY_PERFORMANCE_URL + id, body, this.getOptionsWithCredentials())
    //        .map(this.extractData)
    //        .catch(this.handleErrorResponse);
    //}

    calculateTrackRecord(id) {
        return this.http.get(this.PE_BASE_URL + "recalculate/" + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    savePerformanceAndUpdateStatistics(entity, id) {
        let body = JSON.stringify(entity);

        return this.http.post(this.PE_FUND_SAVE_COMPANY_PERFORMANCE_URL + id + "/recalculate", body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    //saveGrossCF(entity, id) {
    //    let body = JSON.stringify(entity);
    //
    //    return this.http.post(this.PE_FUND_SAVE_GROSS_CF_URL + id, body, this.getOptionsWithCredentials())
    //        .map(this.extractData)
    //        .catch(this.handleErrorResponse);
    //}

    saveGrossCFAndRecalculatePerformanceAndUpdateStatistics(entity, id) {
        let body = JSON.stringify(entity);

        return this.http.post(this.PE_FUND_SAVE_GROSS_CF_URL + id + "/recalculate", body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    get(id): Observable<PEFund> {
        return this.http.get(this.PE_FUND_GET_URL + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    search(searchParams) {
        let body = JSON.stringify(searchParams);

        return this.http.post(this.PE_FUND_SEARCH_URL, body, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    postFiles(files, id) {
        return this.uploadService.postFiles(this.PE_FUND_UPLOAD_GROSS_CF_URL + id, [], files);
    }
}