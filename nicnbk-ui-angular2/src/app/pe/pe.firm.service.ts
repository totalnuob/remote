import {Injectable} from '@angular/core'
import {Http, Response, Headers, RequestOptions } from '@angular/http';
import {Observable}     from 'rxjs/Observable';

import {CommonService} from "../common/common.service";
import {DATA_APP_URL} from "../common/common.service.constants";
import {PEFirm} from "./model/pe.firm";
import {FileUploadService} from "../upload/file.upload.service";

@Injectable()
export class PEFirmService extends CommonService {

    private PE_BASE_URL = DATA_APP_URL + 'pe/firm/';
    private PE_FIRM_SAVE_URL = this.PE_BASE_URL + 'save/';
    private PE_FIRM_GET_URL = this.PE_BASE_URL + 'get/';
    //private PE_FIRM_FUNDS_GET_URL = this.PE_BASE_URL + 'getFunds/';
    private PE_FIRM_SEARCH_URL = this.PE_BASE_URL + "search/";
    private PE_FIRM_LIST_URL = this.PE_BASE_URL + "all/";
    private PE_FIRM_LOGO_URL = this.PE_BASE_URL + "logo/"
    private PE_FIRM_LOGO_UPLOAD_URL = this.PE_FIRM_LOGO_URL + "upload/";
    private PE_FIRM_FUNDS_AND_TOTAL_IRR_AND_BAR_CHARTS_FOR_ONE_PAGER_URL = this.PE_BASE_URL + "getFundsAndTotalIrrAndBarChartsForOnePager/";

    constructor(
        private uploadService: FileUploadService,
        private http: Http)
    {
        super();
    }

    save(entity) {
        let body = JSON.stringify(entity);

        return this.http.post(this.PE_FIRM_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    get(id): Observable<PEFirm> {
        return this.http.get(this.PE_FIRM_GET_URL + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    //loadFirmFunds(id) {
    //    return this.http.get(this.PE_FIRM_FUNDS_GET_URL + id, this.getOptionsWithCredentials())
    //        .map(this.extractData)
    //        .catch(this.handleErrorResponse);
    //}

    getFundsAndTotalIrrAndBarChartsForOnePager(id, fundId) {
        return this.http.get(this.PE_FIRM_FUNDS_AND_TOTAL_IRR_AND_BAR_CHARTS_FOR_ONE_PAGER_URL + id + '/' + fundId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    search(searchParams){
        let body = JSON.stringify(searchParams);

        return this.http.post(this.PE_FIRM_SEARCH_URL, body, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getFirms(){
        return this.http.get(this.PE_FIRM_LIST_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    postFiles(firmId, params, files){
        return this.uploadService.postFiles(this.PE_FIRM_LOGO_UPLOAD_URL + firmId, [], files);
    }
}