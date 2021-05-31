import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable }     from 'rxjs/Observable';

import {DATA_APP_URL} from "../common/common.service.constants";
import {CommonService} from "../common/common.service";
import {Memo} from "./model/memo";
import {FileUploadService} from "../upload/file.upload.service";
import {get} from "http";

var fileSaver = require("file-saver");

@Injectable()
export class MemoService extends CommonService{

    private MEMO_BASE_URL = DATA_APP_URL + "m2s2/";

    private PE_MEMO_SAVE_URL = this.MEMO_BASE_URL + "PE/save/";
    private PE_MEMO_SEARCH_URL = this.MEMO_BASE_URL + "PE/search/";

    private HF_MEMO_SAVE_URL = this.MEMO_BASE_URL + "HF/save/";
    private HF_MEMO_SEARCH_URL = this.MEMO_BASE_URL + "HF/search/";

    private RE_MEMO_SAVE_URL = this.MEMO_BASE_URL + "RE/save/";
    private INFR_MEMO_SAVE_URL = this.MEMO_BASE_URL + "INFR/save/";
    private GN_MEMO_SAVE_URL = this.MEMO_BASE_URL + "GN/save/";


    private MEMO_SEARCH_URL = this.MEMO_BASE_URL + "search/";
    private MEMO_EXPORT_URL = this.MEMO_BASE_URL + "export/";
    private MEMO_GET_URL = this.MEMO_BASE_URL + "get/";
    private MEMO_DELETE_URL = this.MEMO_BASE_URL + "delete/";

    private MEMO_ATTACHMENT_URL = this.MEMO_BASE_URL + "attachment/";
    private MEMO_ATTACHMENT_DELETE_URL = this.MEMO_ATTACHMENT_URL + "delete/";
    private MEMO_ATTACHMENT_UPLOAD_URL = this.MEMO_ATTACHMENT_URL + "upload/";

        constructor (
        private http: Http,
        private uploadService: FileUploadService)
    {
        super();
    }

    search(searchParam){
        let body = JSON.stringify(searchParam);

        //console.log(body);
        return this.http.post(this.MEMO_SEARCH_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    export(searchParam) {
        return this.makeFileRequest(DATA_APP_URL + `m2s2/export`, searchParam, "POST", searchParam);
    }

    public makeFileRequest(url, searchParams, method?: string, body?): Observable<Response> {
            return Observable.fromPromise(new Promise((resolve, reject) => {
                console.log(body)

                let xhr = new XMLHttpRequest();
                xhr.withCredentials = true;

                var methodName = 'GET';
                if(method){
                    if(method === 'GET'){
                        methodName = 'GET';
                    }else if(method === 'POST'){
                        methodName = 'POST';
                    }
                }
                xhr.open(methodName, url, true);

                xhr.setRequestHeader("Content-type", "application/json");
                xhr.responseType = 'blob';

                // Xhr callback when we get a result back
                // We are not using arrow function because we need the 'this' context
                xhr.onreadystatechange = function () {
                    // We use setTimeout to trigger change detection in Zones
                    setTimeout(() => {
                        //self.pending = false;
                    }, 0);

                    // If we get an HTTP status OK (200), save the file using fileSaver
                    if (xhr.readyState === 4) {
                        if (xhr.status === 200) {
                            console.log("OK " + xhr.status);
                            resolve(JSON.parse("{\"message\" : \"OK\"}"));
                            var blob = new Blob([this.response], {type: this.response.type});
                            fileSaver.saveAs(blob, "exported_memos.xlsx");
                        }else {
                            console.log("Error - " + xhr.status);
                            console.log(xhr);
                            reject(xhr.response);
                        }
                    }
                };
                // Start the Ajax request
                //xhr.open("GET", url);
                if(body){
                    xhr.send(JSON.stringify(body));
                }else {
                    xhr.send();
                }
            }));
            //.map(this.extractData);
            //.catch(this.handleErrorResponse);
    }

    searchPE(searchParam){
        let body = JSON.stringify(searchParam);

        //console.log(body);
        return this.http.post(this.PE_MEMO_SEARCH_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    searchHF(searchParam){
        let body = JSON.stringify(searchParam);

        //console.log(body);
        return this.http.post(this.HF_MEMO_SEARCH_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    savePE(entity){
        return this.save(this.PE_MEMO_SAVE_URL, entity );
    }

    saveHF(entity){
        return this.save(this.HF_MEMO_SAVE_URL, entity);
    }
    saveRE(entity){
        return this.save(this.RE_MEMO_SAVE_URL, entity);
    }
    saveINFR(entity){
            return this.save(this.INFR_MEMO_SAVE_URL, entity);
        }
    saveGeneral(entity){
        return this.save(this.GN_MEMO_SAVE_URL, entity);
    }

    private save(URL, entity){

        let body = JSON.stringify(entity);

        return this.http.post(URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    postFiles(memoId, params, files){
        return this.uploadService.postFiles(this.MEMO_ATTACHMENT_UPLOAD_URL + memoId, [], files, null);
    }

    get(type, id): Observable<any> {
        // TODO: check type and id

        return this.http.get(this.MEMO_GET_URL + type + "/" + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    public deleteAttachment(memoId, fileId) {
        return this.http.get(this.MEMO_ATTACHMENT_DELETE_URL + memoId + "/" + fileId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    public deleteMemo(memoId) {
        return this.http.get(this.MEMO_DELETE_URL + memoId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }
}