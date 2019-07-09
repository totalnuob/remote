import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable }     from 'rxjs/Observable';

import {DATA_APP_URL} from "../common/common.service.constants";
import {CommonService} from "../common/common.service";
import {MonitoringHFDataHolder} from "./model/monitoring-hf-data-holder";
import {MonitoringHFListDataHolder} from "./model/monitoring-hf-list-data-holder";
import {MonitoringHFData} from "./model/monitoring-hf-data";

@Injectable()
export class MonitoringHedgeFundService extends CommonService {

    private MONITORING_HF_BASE_URL = DATA_APP_URL + "monitoring/hf";

    private MONITORING_HF_GET_ALL_URL = this.MONITORING_HF_BASE_URL + "/getAll";
    private MONITORING_HF_GET_URL = this.MONITORING_HF_BASE_URL + "/get";
    private MONITORING_HF_GET_PREVIOUS_URL = this.MONITORING_HF_BASE_URL + "/getPrevious/";
    private MONITORING_HF_SAVE_URL = this.MONITORING_HF_BASE_URL + "/save";


    constructor(private http:Http) {
        super();
    }


    getAll():Observable<MonitoringHFListDataHolder> {

        return this.http.get(this.MONITORING_HF_GET_ALL_URL, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    get(searchParams):Observable<MonitoringHFDataHolder> {

        let body = JSON.stringify(searchParams);

        return this.http.post(this.MONITORING_HF_GET_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getPreviousData(params): Observable<MonitoringHFData>{
        let body = JSON.stringify(params);

        return this.http.post(this.MONITORING_HF_GET_PREVIOUS_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    save(entity){

        let body = JSON.stringify(entity);

        return this.http.post(this.MONITORING_HF_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }
}