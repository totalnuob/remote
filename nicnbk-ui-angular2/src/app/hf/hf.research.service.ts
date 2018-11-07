import { Injectable } from '@angular/core';
import {CommonService} from "../common/common.service";
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable }     from 'rxjs/Observable';

import {DATA_APP_URL} from "../common/common.service.constants";
import {CommonService} from "../common/common.service";

@Injectable()
export class HFResearchService extends CommonService{

    constructor (
        private http: Http)
    {
        super();
    }
}