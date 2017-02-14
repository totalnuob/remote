import {Injectable} from '@angular/core'
import {Http, Response, Headers, RequestOptions } from '@angular/http';
import {Observable}     from 'rxjs/Observable';

import {CommonService} from "../common/common.service";
import {DATA_APP_URL} from "../common/common.service.constants";
import {PEFirm} from "./model/pe.firm";


@Injectable()
export class PEFirmService extends CommonService {

    private PE_BASE_URL = DATA_APP_URL + 'pe/firm/';
    private PE_FIRM_SAVE_URL = this.PE_BASE_URL + 'save/';
    private PE_FIRM_GET_URL = this.PE_BASE_URL + 'get/';
    private PE_FIRM_SEARCH_URL = this.PE_BASE_URL + "search/";
    private PE_FIRM_FIRMS_URL = this.PE_BASE_URL + "all/";

    constructor(
        private http: Http)
    {
        super();
    }

    save(entity) {
        let body = JSON.stringify(entity);
        let headers = new Headers({ 'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});

        console.log(body);

        return this.http.post(this.PE_FIRM_SAVE_URL, body, options)
            .map(this.extractData)
            .catch(this.handleError);
    }

    get(id): Observable<PEFirm> {
        return this.http.get(this.PE_FIRM_GET_URL + id)
            .map(this.extractData)
            .catch(this.handleError);
    }

    search(searchParams){
        let body = JSON.stringify(searchParams);
        let headers = new Headers({ 'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});

        return this.http.post(this.PE_FIRM_SEARCH_URL, body, options)
            .map(this.extractData)
            .catch(this.handleError);
    }

    getFirms(){
        return this.http.get(this.PE_FIRM_FIRMS_URL)
            .map(this.extractDataList)
            .catch(this.handleError);
    }
}