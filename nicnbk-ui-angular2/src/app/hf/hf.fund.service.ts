import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable }     from 'rxjs/Observable';

import {DATA_APP_URL} from "../common/common.service.constants";
import {CommonService} from "../common/common.service";
import {HFManager} from "./model/hf.manager";
import {HedgeFund} from "./model/hf.fund";

@Injectable()
export class HedgeFundService extends CommonService{

    private HF_BASE_URL = DATA_APP_URL + "hf/fund/";

    private HF_FUND_SAVE_URL = this.HF_BASE_URL + "save/";
    private HF_FUND_GET_URL = this.HF_BASE_URL + "get/";
    private HF_FUND_SEARCH_URL = this.HF_BASE_URL + "search/";

    constructor (
        private http: Http)
    {
        super();
    }

    save(entity){

        let body = JSON.stringify(entity);

        return this.http.post(this.HF_FUND_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    get(id): Observable<HedgeFund> {
        // TODO: check id
        return this.http.get(this.HF_FUND_GET_URL + "/" + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    search(searchParams){
        let body = JSON.stringify(searchParams);

        //console.log(body);
        return this.http.post(this.HF_FUND_SEARCH_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

}