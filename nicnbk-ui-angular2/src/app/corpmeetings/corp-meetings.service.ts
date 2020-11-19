import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable}     from 'rxjs/Observable';

import {DATA_APP_URL} from "../common/common.service.constants";
import {CommonService} from "../common/common.service";
import {FileUploadService} from "../upload/file.upload.service";
import {CorpMeeting} from "./model/corp-meeting";
import {ICMeetingTopic} from "./model/ic-meeting-topic";
import {ICMeeting} from "./model/ic-meeting";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";

var fileSaver = require("file-saver");

@Injectable()
export class CorpMeetingService extends CommonService {
    private CORP_MEETINGS_BASE_URL = DATA_APP_URL + "corpMeetings/";

    private CORP_MEETINGS_SAVE_URL = this.CORP_MEETINGS_BASE_URL + "save/";
    private CORP_MEETINGS_SEARCH_URL = this.CORP_MEETINGS_BASE_URL + "search/";
    private CORP_MEETINGS_GET_URL = this.CORP_MEETINGS_BASE_URL + "get/";
    private CORP_MEETINGS_DELETE_URL = this.CORP_MEETINGS_BASE_URL + "delete/";

    private CORP_MEETINGS_AVAILABLE_APPROVE_LIST_URL = this.CORP_MEETINGS_BASE_URL + "availableApproveList/";


    private MEETING_ATTACHMENT_UPLOAD_URL = this.CORP_MEETINGS_BASE_URL + "materials/upload/";
    private MEETING_ATTACHMENT_DELETE_URL = this.CORP_MEETINGS_BASE_URL + "materials/delete/";
    private MEETING_EXPLANATORY_NOTE_DELETE_URL = this.CORP_MEETINGS_BASE_URL + "explanatoryNote/delete/";
    private MEETING_EXPLANATORY_NOTE_UPD_DELETE_URL = this.CORP_MEETINGS_BASE_URL + "explanatoryNoteUpd/delete/";

    private IC_MEETING_AGENDA_DELETE_URL = this.CORP_MEETINGS_BASE_URL + "ICMeeting/agenda/delete/";
    private IC_MEETING_UNLOCK_FOR_FINALIZE_URL = this.CORP_MEETINGS_BASE_URL + "ICMeeting/unlockForFinalize/";
    private IC_MEETING_VOTE_URL = this.CORP_MEETINGS_BASE_URL + "ICMeeting/vote/";


    private IC_MEETINGS_GET_ALL_URL = this.CORP_MEETINGS_BASE_URL + "ICMeeting/getAllShort/";
    private IC_MEETINGS_GET_URL = this.CORP_MEETINGS_BASE_URL + "ICMeeting/get/";

    private IC_MEETINGS_SEARCH_URL = this.CORP_MEETINGS_BASE_URL + "ICMeeting/search/";
    private IC_MEETING_TOPICS_SEARCH_URL = this.CORP_MEETINGS_BASE_URL + "ICMeetingTopic/search/";

    private IC_MEETING_SAVE_URL = this.CORP_MEETINGS_BASE_URL + "ICMeeting/save/";
    private IC_MEETING_PROTOCOL_ATTACHMENT_UPLOAD_URL = this.CORP_MEETINGS_BASE_URL + "ICMeeting/protocol/upload/";
    private IC_MEETING_PROTOCOL_ATTACHMENT_DELETE_URL = this.CORP_MEETINGS_BASE_URL + "ICMeeting/protocol/delete/";

    private IC_MEETING_TOPIC_GET_URL = this.CORP_MEETINGS_BASE_URL + "ICMeetingTopic/get/";
    private IC_MEETING_TOPIC_SAVE_URL = this.CORP_MEETINGS_BASE_URL + "ICMeetingTopic/save/";
    private IC_MEETING_TOPIC_SAVE_UPDATE_URL = this.CORP_MEETINGS_BASE_URL + "ICMeetingTopic/saveUpdate/";

    private IC_MEETING_TOPIC_DELETE_URL = this.CORP_MEETINGS_BASE_URL + "ICMeetingTopic/delete/";
    private IC_MEETING_TOPIC_APPROVE_URL = this.CORP_MEETINGS_BASE_URL + "ICMeetingTopic/approve/";

    private IC_MEETINGS_DELETE_URL = this.CORP_MEETINGS_BASE_URL + "ICMeeting/delete/";
    private IC_MEETING_CLOSE_URL = this.CORP_MEETINGS_BASE_URL + "ICMeeting/close/";
    private IC_MEETING_REOPEN_URL = this.CORP_MEETINGS_BASE_URL + "ICMeeting/reopen/";




    constructor( private http: Http,
                 private moduleAccessChecker: ModuleAccessCheckerService,
                 private uploadService: FileUploadService) {
        super();
    }

    public checkICMeetingTopicEditAccess(type){
        if(this.moduleAccessChecker.checkAccessAdmin() || this.moduleAccessChecker.checkAccessCorpMeetingsEdit()){
           return true;
        }
        if(this.moduleAccessChecker.checkAccessICMember()) {
            if (this.moduleAccessChecker.checkAccessHedgeFundsEditor()) {
                return type != null && type === "HF";
            } else if (this.moduleAccessChecker.checkAccessPrivateEquityEditor()) {
                return type != null && type === "PE";
            } else if (this.moduleAccessChecker.checkAccessRealEstateEditor()) {
                return type != null && type === "RE";
            } else if (this.moduleAccessChecker.checkAccessStrategyRisksEditor()) {
                return type != null && type === "SRM";
            } else if (this.moduleAccessChecker.checkAccessReportingEditor()) {
                return type != null && type === "REP";
            }else if (this.moduleAccessChecker.checkAccessStrategyEditor()) {
                return type != null && type === "STRTG";
            }else if (this.moduleAccessChecker.checkAccessRisksEditor()) {
                return type != null && type === "RISKS";
            }
        }
        return false;
    }

    search(searchParam) {
        let body = JSON.stringify(searchParam);

        //console.log(body);
        return this.http.post(this.CORP_MEETINGS_SEARCH_URL, body, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    searchICMeetings(searchParam) {
        let body = JSON.stringify(searchParam);

        //console.log(body);
        return this.http.post(this.IC_MEETINGS_SEARCH_URL, body, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    getAllICMeetings(): Observable<ICMeeting[]> {
        return this.http.get(this.IC_MEETINGS_GET_ALL_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    searchICMeetingTopics(searchParam):  Observable<ICMeetingTopic[]> {
        let body = JSON.stringify(searchParam);

        //console.log(body);
        return this.http.post(this.IC_MEETING_TOPICS_SEARCH_URL, body, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }


    save(entity): Observable<any>{
        let body = JSON.stringify(entity);

        return this.http.post(this.CORP_MEETINGS_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }


    saveICMeetingTopic(entity, files): Observable<any>{
        const formData = new FormData();
        formData.append('data', JSON.stringify(entity));
        for (let i = 0; i < files.length; i++) {
            formData.append("file", files[i].file);
        }
        return this.http.post(this.IC_MEETING_TOPIC_SAVE_URL, formData, this.getHeadersMultipartOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    saveICMeetingTopicUpdate(entity, materials, explanatoryNote): Observable<any>{
        console.log(entity);
        return this.uploadService.postJsonWithFiles(this.IC_MEETING_TOPIC_SAVE_UPDATE_URL, [], entity, materials, 'file',
                                                                explanatoryNote, 'exp_note');
    }

    saveICMeetingTopicWithFiles(entity, files, exp_notes){
        return this.uploadService.postJsonWithFiles(this.IC_MEETING_TOPIC_SAVE_URL, [], entity, files, 'file',
                                                        exp_notes, 'exp_note');
    }

    saveICMeeting(entity, files): Observable<any>{
        //let body = JSON.stringify(entity);
        //return this.http.post(this.IC_MEETING_SAVE_URL, body, this.getOptionsWithCredentials())
        //    .map(this.extractData)
        //    .catch(this.handleErrorResponse);
        return this.uploadService.postJsonWithFiles(this.IC_MEETING_SAVE_URL, [], entity, files, 'file',
                                                                null, null);
    }

    postFiles(meetingId, params, files){
            return this.uploadService.postFiles(this.MEETING_ATTACHMENT_UPLOAD_URL + meetingId, [], files, null);
        }

    deleteICMeeting(id): Observable<any>{
        return this.http.post(this.IC_MEETINGS_DELETE_URL + id, null, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }


    get(id): Observable<CorpMeeting> {
        // TODO: check type and id
        return this.http.get(this.CORP_MEETINGS_GET_URL + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getICMeeting(id): Observable<ICMeeting> {
        // TODO: check type and id
        return this.http.get(this.IC_MEETINGS_GET_URL + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getICMeetingTopic(id): Observable<ICMeetingTopic> {
        return this.http.get(this.IC_MEETING_TOPIC_GET_URL + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    approveICMeetingTopic(topicId): Observable<any> {
        return this.http.post(this.IC_MEETING_TOPIC_APPROVE_URL + topicId, {}, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    postFiles(meetingId, params, files){
        return this.uploadService.postFiles(this.MEETING_ATTACHMENT_UPLOAD_URL + meetingId, [], files, null);
    }

    postICMeetingProtocolFiles(meetingId, params, file){
        return this.uploadService.postFiles(this.IC_MEETING_PROTOCOL_ATTACHMENT_UPLOAD_URL + meetingId, [], file, null);
    }

    deleteICMeetingProtocolAttachment(meetingId, fileId) {
        return this.http.delete(this.IC_MEETING_PROTOCOL_ATTACHMENT_DELETE_URL + meetingId + "/" + fileId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }


    //deleteAttachment(meetingId, fileId) {
    //    return this.http.get(this.MEETING_ATTACHMENT_DELETE_URL + meetingId + "/" + fileId, this.getOptionsWithCredentials())
    //        .map(this.extractData)
    //        .catch(this.handleErrorResponse);
    //}

    deleteICMeetingTopicAttachment(meetingId, fileId): Observable<any> {
        return this.http.get(this.MEETING_ATTACHMENT_DELETE_URL + meetingId + "/" + fileId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    deleteICMeetingTopicExplanatoryNote(id) {
        return this.http.delete(this.MEETING_EXPLANATORY_NOTE_DELETE_URL + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    deleteICMeetingTopicExplanatoryNoteUpd(id) {
        return this.http.delete(this.MEETING_EXPLANATORY_NOTE_UPD_DELETE_URL + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    deleteICMeetingAgenda(icMeetingId): Observable<any> {
        return this.http.delete(this.IC_MEETING_AGENDA_DELETE_URL + icMeetingId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    unlockICMeetingForFinalize(icMeetingId): Observable<any> {
        return this.http.post(this.IC_MEETING_UNLOCK_FOR_FINALIZE_URL + icMeetingId, null, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    voteICMeetingTopics(votes): Observable<any> {
        let data = JSON.stringify(votes);
        return this.http.post(this.IC_MEETING_VOTE_URL, data, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    delete(id){
        return this.http.post(this.CORP_MEETINGS_DELETE_URL + id, null, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getAvailableApproveList(): Observable<any[]> {
            return this.http.get(this.CORP_MEETINGS_AVAILABLE_APPROVE_LIST_URL, this.getOptionsWithCredentials())
                .map(this.extractData)
                .catch(this.handleErrorResponse);
        }
    deleteICMeetingTopic(id): Observable<any>{
        return this.http.post(this.IC_MEETING_TOPIC_DELETE_URL + id, null, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    closeICMeeting(id): Observable<any>{
        return this.http.post(this.IC_MEETING_CLOSE_URL + id, null, this.getOptionsWithCredentials())
                .map(this.extractData)
                .catch(this.handleErrorResponse);
    }
    reopenICMeeting(id): Observable<any>{
        return this.http.post(this.IC_MEETING_REOPEN_URL + id, null, this.getOptionsWithCredentials())
                .map(this.extractData)
                .catch(this.handleErrorResponse);
    }


    public makeFileRequest(url, fileName, method?: string, body?): Observable<Response> {
        return Observable.fromPromise(new Promise((resolve, reject) => {
            let xhr = new XMLHttpRequest();
            xhr.withCredentials = true; // send auth token with the request
            xhr.open('GET', url, true);
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
                        resolve(JSON.parse("{\"message\" : \"OK\"}"));
                        var blob = new Blob([this.response], {type: this.response.type});
                        fileSaver.saveAs(blob, fileName);
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
    }
}