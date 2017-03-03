import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable }     from 'rxjs/Observable';

import {DATA_APP_URL} from "../common/common.service.constants";
import {CommonService} from "../common/common.service";
import {HFManager} from "./model/hf.manager";

@Injectable()
export class HFManagerService extends CommonService{

    private HF_BASE_URL = DATA_APP_URL + "hf/manager/";

    private HF_MANAGER_SAVE_URL = this.HF_BASE_URL + "save/";
    private HF_MANAGER_GET_URL = this.HF_BASE_URL + "get/";
    private HF_MANAGER_SEARCH_URL = this.HF_BASE_URL + "search/";
    private HF_MANAGER_LIST_URL = this.HF_BASE_URL + "all/";

    constructor (
        private http: Http)
    {
        super();
    }

    save(entity){

        let body = JSON.stringify(entity);

        return this.http.post(this.HF_MANAGER_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    get(id): Observable<HFManager> {
        // TODO: check id
        return this.http.get(this.HF_MANAGER_GET_URL + "/" + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    search(searchParams){
        let body = JSON.stringify(searchParams);

        //console.log(body);
        return this.http.post(this.HF_MANAGER_SEARCH_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getManagers(){
        return this.http.get(this.HF_MANAGER_LIST_URL)
            .map(this.extractDataList)
            .catch(this.handleError);
    }
}