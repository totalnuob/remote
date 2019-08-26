import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import {DATA_APP_URL} from "../common/common.service.constants";
import { Observable }     from 'rxjs/Observable';
import {CommonService} from "../common/common.service";

@Injectable()
export class HRNewsService extends CommonService{

    constructor (private http: Http) {
        super();
    }

    private HR_NEWS_BASE_URL = DATA_APP_URL + "hrnews/";
    private NEWS_LIST_URL  = this.HR_NEWS_BASE_URL + "load/";
    private NEWS_GET_BASE_URL = this.HR_NEWS_BASE_URL + "get/";
    private NEWS_SAVE_URL = this.HR_NEWS_BASE_URL + "save/";

    loadNews(): Observable<any[]> {
        return null;
    }



}
