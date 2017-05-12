import { Injectable } from '@angular/core';
import { Observable } from "rxjs/Observable";
import { Http } from '@angular/http';
import {CommonService} from "../common/common.service";
import {DATA_APP_URL} from "../common/common.service.constants";
import {MacroMonitorScore} from "./model/macromonitor.score";

@Injectable()
export class MacroMonitorService extends CommonService {

    private MM_BASE_URL = DATA_APP_URL + "macromonitor/";
    private MM_SAVE_URL = this.MM_BASE_URL + "save/";
    private MM_GET_URL = this.MM_BASE_URL + "get/";
    private MM_DELETEALL_URL = this.MM_BASE_URL + "deleteAll/";

    constructor (
        private http: Http
    ) {
        super();
    }

    save(entity) {
        let body = JSON.stringify(entity);

        return this.http.post(this.MM_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    get(id): Observable<[MacroMonitorScore]> {
        return this.http.get(this.MM_GET_URL + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    delete(id) {
        return this.http.get(this.MM_DELETEALL_URL + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }
}