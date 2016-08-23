import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable}     from 'rxjs/Observable';

import {DATA_APP_URL} from "../common/common.service.constants";
import {CommonService} from "../common/common.service";
import {TripMemo} from "./model/trip-memo";
import {FileUploadService} from "../upload/file.upload.service";

@Injectable()
export class TripMemoService extends CommonService {
    private TRIP_MEMO_BASE_URL = DATA_APP_URL + "bt/";

    private TRIP_MEMO_SAVE_URL = this.TRIP_MEMO_BASE_URL + "save/";
    private TRIP_MEMO_SEARCH_URL = this.TRIP_MEMO_BASE_URL + "search/";
    private TRIP_MEMO_GET_URL = this.TRIP_MEMO_BASE_URL + "get/";
    private TRIP_MEMO_ATTACHMENT_URL = this.TRIP_MEMO_BASE_URL + "attachment/";
    private TRIP_MEMO_ATTACHMENT_DELETE_URL = this.TRIP_MEMO_ATTACHMENT_URL + "delete/";
    private TRIP_MEMO_ATTACHMENT_UPLOAD_URL = this.TRIP_MEMO_ATTACHMENT_URL + "upload/";

    constructor( private http: Http,
                 private uploadService: FileUploadService) {
        super();
    }

    search(searchParam) {
        let body = JSON.stringify(searchParam);
        let headers = new Headers({'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});

        //console.log(body);
        return this.http.post(this.TRIP_MEMO_SEARCH_URL, body, options)
            .map(this.extractData)
            .catch(this.handleError);
    }

    saveBt(entity) {
        return this.save(this.TRIP_MEMO_SAVE_URL, entity);
    }

    private save(URL, entity){
        let body = JSON.stringify(entity);
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        //console.log(body);
        return this.http.post(URL, body, options)
            .map(this.extractData)
            .catch(this.handleError);
    }
    postFiles(tripMemoId, params, files) {
        return this.uploadService.postFiles(this.TRIP_MEMO_ATTACHMENT_UPLOAD_URL + tripMemoId, [], files);
    }

    get(id): Observable<any> {
        // TODO: check type and id

        return this.http.get(this.TRIP_MEMO_GET_URL + id)
            .map(this.extractData)
            .catch(this.handleError);
    }
    public deleteAttachment(tripMemoId, fileId) {
        return this.http.get(this.TRIP_MEMO_ATTACHMENT_DELETE_URL + tripMemoId + "/" + fileId)
            .map(this.extractData)
            .catch(this.handleError);
    }
}