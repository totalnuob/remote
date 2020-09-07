import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable}     from 'rxjs/Observable';

import {DATA_APP_URL} from "../common/common.service.constants";
import {CommonService} from "../common/common.service";
import {TripMemo} from "./model/trip-memo";
import {FileUploadService} from "../upload/file.upload.service";
import {get} from "http";

var fileSaver = require("file-saver");

@Injectable()
export class TripMemoService extends CommonService {
    private TRIP_MEMO_BASE_URL = DATA_APP_URL + "bt/";

    private TRIP_MEMO_SAVE_URL = this.TRIP_MEMO_BASE_URL + "save/";
    private TRIP_MEMO_SEARCH_URL = this.TRIP_MEMO_BASE_URL + "search/";
    private TRIP_MEMO_GET_URL = this.TRIP_MEMO_BASE_URL + "get/";
    private TRIP_MEMO_ATTACHMENT_URL = this.TRIP_MEMO_BASE_URL + "attachment/";
    private TRIP_MEMO_ATTACHMENT_DELETE_URL = this.TRIP_MEMO_ATTACHMENT_URL + "delete/";
    private TRIP_MEMO_ATTACHMENT_UPLOAD_URL = this.TRIP_MEMO_ATTACHMENT_URL + "upload/";
    private TRIP_MEMO_EXPORT_URL = this.TRIP_MEMO_BASE_URL + "export/";

    constructor( private http: Http,
                 private uploadService: FileUploadService) {
        super();
    }

    search(searchParam) {
        let body = JSON.stringify(searchParam);

        //console.log(body);
        return this.http.post(this.TRIP_MEMO_SEARCH_URL, body, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    saveBt(entity) {
        return this.save(this.TRIP_MEMO_SAVE_URL, entity);
    }

    private save(URL, entity){
        let body = JSON.stringify(entity);

        //console.log(body);
        return this.http.post(URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }
    postFiles(tripMemoId, params, files) {
        return this.uploadService.postFiles(this.TRIP_MEMO_ATTACHMENT_UPLOAD_URL + tripMemoId, [], files, null);
    }

    get(id): Observable<any> {
        // TODO: check type and id

        return this.http.get(this.TRIP_MEMO_GET_URL + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }
    public deleteAttachment(tripMemoId, fileId) {
        return this.http.get(this.TRIP_MEMO_ATTACHMENT_DELETE_URL + tripMemoId + "/" + fileId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    export(searchParam) {
        return this.makeFileRequest(this.TRIP_MEMO_EXPORT_URL, searchParam, "POST", searchParam);
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
                        fileSaver.saveAs(blob, "exported_trip_memos.xlsx");
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
}