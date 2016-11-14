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
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({headers: headers});

        return this.http.post(this.HF_FUND_SAVE_URL, body, options)
            .map(this.extractData)
            .catch(this.handleError);
    }

    get(id): Observable<HedgeFund> {
        // TODO: check id
        return this.http.get(this.HF_FUND_GET_URL + "/" + id)
            .map(this.extractData)
            .catch(this.handleError);
    }

    search(searchParams){
        let body = JSON.stringify(searchParams);
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        //console.log(body);
        return this.http.post(this.HF_FUND_SEARCH_URL, body, options)
            .map(this.extractData)
            .catch(this.handleError);
    }

}