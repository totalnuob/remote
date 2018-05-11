import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable}     from 'rxjs/Observable';

import {DATA_APP_URL} from "../common/common.service.constants";
import {CommonService} from "../common/common.service";
import {FileUploadService} from "../upload/file.upload.service";
import {CorpMeeting} from "./model/corp-meeting";

@Injectable()
export class CorpMeetingService extends CommonService {
    private CORP_MEETINGS_BASE_URL = DATA_APP_URL + "corpMeetings/";

    private CORP_MEETINGS_SAVE_URL = this.CORP_MEETINGS_BASE_URL + "save/";
    private CORP_MEETINGS_SEARCH_URL = this.CORP_MEETINGS_BASE_URL + "search/";
    private CORP_MEETINGS_GET_URL = this.CORP_MEETINGS_BASE_URL + "get/";
    private CORP_MEETINGS_DELETE_URL = this.CORP_MEETINGS_BASE_URL + "delete/";

    private MEETING_ATTACHMENT_UPLOAD_URL = this.CORP_MEETINGS_BASE_URL + "materials/upload/";
    private MEETING_ATTACHMENT_DELETE_URL = this.CORP_MEETINGS_BASE_URL + "materials/delete/";

    constructor( private http: Http,
                 private uploadService: FileUploadService) {
        super();
    }

    search(searchParam) {
        let body = JSON.stringify(searchParam);

        //console.log(body);
        return this.http.post(this.CORP_MEETINGS_SEARCH_URL, body, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    save(entity){
        let body = JSON.stringify(entity);

        console.log(body);
        return this.http.post(this.CORP_MEETINGS_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    get(id): Observable<CorpMeeting> {
        // TODO: check type and id
        return this.http.get(this.CORP_MEETINGS_GET_URL + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    postFiles(meetingId, params, files){
        return this.uploadService.postFiles(this.MEETING_ATTACHMENT_UPLOAD_URL + meetingId, [], files, null);
    }

    public deleteAttachment(meetingId, fileId) {
        return this.http.get(this.MEETING_ATTACHMENT_DELETE_URL + meetingId + "/" + fileId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    delete(id){
        return this.http.post(this.CORP_MEETINGS_DELETE_URL + id, null, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }
}