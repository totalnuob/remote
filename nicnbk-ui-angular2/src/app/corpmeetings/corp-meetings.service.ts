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

@Injectable()
export class CorpMeetingService extends CommonService {
    private CORP_MEETINGS_BASE_URL = DATA_APP_URL + "corpMeetings/";

    private CORP_MEETINGS_SAVE_URL = this.CORP_MEETINGS_BASE_URL + "save/";
    private CORP_MEETINGS_SEARCH_URL = this.CORP_MEETINGS_BASE_URL + "search/";
    private CORP_MEETINGS_GET_URL = this.CORP_MEETINGS_BASE_URL + "get/";
    private CORP_MEETINGS_DELETE_URL = this.CORP_MEETINGS_BASE_URL + "delete/";

    private MEETING_ATTACHMENT_UPLOAD_URL = this.CORP_MEETINGS_BASE_URL + "materials/upload/";
    private MEETING_ATTACHMENT_DELETE_URL = this.CORP_MEETINGS_BASE_URL + "materials/delete/";

    private IC_MEETINGS_GET_ALL_URL = this.CORP_MEETINGS_BASE_URL + "ICMeeting/getAll/";

    private IC_MEETINGS_SEARCH_URL = this.CORP_MEETINGS_BASE_URL + "ICMeeting/search/";
    private IC_MEETING_TOPICS_SEARCH_URL = this.CORP_MEETINGS_BASE_URL + "ICMeetingTopic/search/";

    private IC_MEETING_SAVE_URL = this.CORP_MEETINGS_BASE_URL + "ICMeeting/save/";
    private IC_MEETING_PROTOCOL_ATTACHMENT_UPLOAD_URL = this.CORP_MEETINGS_BASE_URL + "ICMeeting/protocol/upload/";
    private IC_MEETING_PROTOCOL_ATTACHMENT_DELETE_URL = this.CORP_MEETINGS_BASE_URL + "ICMeeting/protocol/delete/";

    private IC_MEETING_TOPIC_GET_URL = this.CORP_MEETINGS_BASE_URL + "ICMeetingTopic/get/";
    private IC_MEETING_TOPIC_SAVE_URL = this.CORP_MEETINGS_BASE_URL + "ICMeetingTopic/save/";
    private IC_MEETING_TOPIC_DELETE_URL = this.CORP_MEETINGS_BASE_URL + "ICMeetingTopic/delete/";
    private IC_MEETINGS_DELETE_URL = this.CORP_MEETINGS_BASE_URL + "ICMeeting/delete/";




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

    searchICMeetingTopics(searchParam) {
        let body = JSON.stringify(searchParam);

        //console.log(body);
        return this.http.post(this.IC_MEETING_TOPICS_SEARCH_URL, body, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }


    save(entity){
        let body = JSON.stringify(entity);

        return this.http.post(this.CORP_MEETINGS_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    saveICMeetingTopic(entity){
        let body = JSON.stringify(entity);
        return this.http.post(this.IC_MEETING_TOPIC_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    saveICMeeting(entity){
        let body = JSON.stringify(entity);

        return this.http.post(this.IC_MEETING_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    deleteICMeeting(id){
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

    getICMeetingTopic(id): Observable<ICMeetingTopic> {
        return this.http.get(this.IC_MEETING_TOPIC_GET_URL + id, this.getOptionsWithCredentials())
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


    deleteAttachment(meetingId, fileId) {
        return this.http.get(this.MEETING_ATTACHMENT_DELETE_URL + meetingId + "/" + fileId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    deleteICMeetingTopicAttachment(meetingId, fileId) {
        return this.http.get(this.MEETING_ATTACHMENT_DELETE_URL + meetingId + "/" + fileId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    delete(id){
        return this.http.post(this.CORP_MEETINGS_DELETE_URL + id, null, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    deleteICMeetingTopic(id){
        return this.http.post(this.IC_MEETING_TOPIC_DELETE_URL + id, null, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }
}