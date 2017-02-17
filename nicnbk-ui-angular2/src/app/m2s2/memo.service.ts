import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable }     from 'rxjs/Observable';

import {DATA_APP_URL} from "../common/common.service.constants";
import {CommonService} from "../common/common.service";
import {Memo} from "./model/memo";
import {FileUploadService} from "../upload/file.upload.service";

@Injectable()
export class MemoService extends CommonService{

    private MEMO_BASE_URL = DATA_APP_URL + "m2s2/";

    private PE_MEMO_SAVE_URL = this.MEMO_BASE_URL + "PE/save/";
    private PE_MEMO_SEARCH_URL = this.MEMO_BASE_URL + "PE/search/";

    private HF_MEMO_SAVE_URL = this.MEMO_BASE_URL + "HF/save/";
    private HF_MEMO_SEARCH_URL = this.MEMO_BASE_URL + "HF/search/";

    private RE_MEMO_SAVE_URL = this.MEMO_BASE_URL + "RE/save/";
    private GN_MEMO_SAVE_URL = this.MEMO_BASE_URL + "GN/save/";


    private MEMO_SEARCH_URL = this.MEMO_BASE_URL + "search/";
    private MEMO_GET_URL = this.MEMO_BASE_URL + "get/";

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
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        //console.log(body);
        return this.http.post(this.MEMO_SEARCH_URL, body, options)
            .map(this.extractData)
            .catch(this.handleError);
    }

    searchPE(searchParam){
        let body = JSON.stringify(searchParam);
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        //console.log(body);
        return this.http.post(this.PE_MEMO_SEARCH_URL, body, options)
            .map(this.extractData)
            .catch(this.handleError);
    }

    savePE(entity){
        return this.save(this.PE_MEMO_SAVE_URL, entity);
    }

    searchHF(searchParam){
        let body = JSON.stringify(searchParam);
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        //console.log(body);
        return this.http.post(this.HF_MEMO_SEARCH_URL, body, options)
            .map(this.extractData)
            .catch(this.handleError);
    }


    saveHF(entity){
        return this.save(this.HF_MEMO_SAVE_URL, entity);
    }
    saveRE(entity){
        return this.save(this.RE_MEMO_SAVE_URL, entity);
    }
    saveGeneral(entity){
        return this.save(this.GN_MEMO_SAVE_URL, entity);
    }

    private save(URL, entity){

        let body = JSON.stringify(entity);
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({headers: headers});

        return this.http.post(URL, body, options)
            .map(this.extractData)
            .catch(this.handleError);
    }

    postFiles(memoId, params, files){
        return this.uploadService.postFiles(this.MEMO_ATTACHMENT_UPLOAD_URL + memoId, [], files);
    }

    get(type, id): Observable<any> {
        // TODO: check type and id

        return this.http.get(this.MEMO_GET_URL + type + "/" + id)
            .map(this.extractData)
            .catch(this.handleError);
    }

    public deleteAttachment(memoId, fileId) {
        return this.http.get(this.MEMO_ATTACHMENT_DELETE_URL + memoId + "/" + fileId)
            .map(this.extractData)
            .catch(this.handleError);
    }

}