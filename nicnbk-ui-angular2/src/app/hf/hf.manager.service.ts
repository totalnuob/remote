import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable }     from 'rxjs/Observable';

import {DATA_APP_URL} from "../common/common.service.constants";
import {CommonService} from "../common/common.service";
import {HFManager} from "./model/hf.manager";
import {HFResearch} from "./model/hf.research-form";
import {HFResearchPage} from "./model/hf.research-page";
import {FileUploadService} from "../upload/file.upload.service";

@Injectable()
export class HFManagerService extends CommonService{

    private HF_BASE_URL = DATA_APP_URL + "hf/manager/";
    private HF_RESEARCH_URL = this.HF_BASE_URL + "research/";
    private HF_RESEARCH_PAGE_URL = this.HF_RESEARCH_URL + "page/";


    private HF_MANAGER_SAVE_URL = this.HF_BASE_URL + "save/";
    private HF_MANAGER_GET_URL = this.HF_BASE_URL + "get/";
    private HF_MANAGER_SEARCH_URL = this.HF_BASE_URL + "search/";
    private HF_MANAGER_LIST_URL = this.HF_BASE_URL + "all/";
    private HF_MANAGER_INVESTED_URL = this.HF_BASE_URL + "invested/";


    private HF_MANAGER_RESEARCH_SAVE_URL = this.HF_RESEARCH_URL + "save/";
    private HF_MANAGER_RESEARCH_GET_URL = this.HF_RESEARCH_URL + "get/";


    private HF_MANAGER_RESEARCH_PAGE_GET_URL = this.HF_RESEARCH_PAGE_URL + "get/";
    private HF_MANAGER_RESEARCH_PAGE_SAVE_URL = this.HF_RESEARCH_PAGE_URL + "save/";
    private HF_MANAGER_RESEARCH_PAGE_DELETE_URL = this.HF_RESEARCH_PAGE_URL + "delete/";

    private HF_MANAGER_RESEARCH_PAGE_ATTACHMENT_URL = this.HF_RESEARCH_PAGE_URL + "attachment/";
    private HF_MANAGER_RESEARCH_PAGE_ATTACHMENT_DELETE_URL = this.HF_MANAGER_RESEARCH_PAGE_ATTACHMENT_URL + "delete/";
    private HF_MANAGER_RESEARCH_PAGE_ATTACHMENT_UPLOAD_URL = this.HF_MANAGER_RESEARCH_PAGE_ATTACHMENT_URL + "upload/";

    constructor (
        private http: Http,
        private uploadService: FileUploadService)
    {
        super();
    }

    save(entity){

        let body = JSON.stringify(entity);

        return this.http.post(this.HF_MANAGER_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    researchSave(entity){
        let body = JSON.stringify(entity);

        return this.http.post(this.HF_MANAGER_RESEARCH_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    get(id): Observable<HFManager> {
        // TODO: check id
        return this.http.get(this.HF_MANAGER_GET_URL + "/" + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getResearch(id): Observable<HFResearch> {
        return this.http.get(this.HF_MANAGER_RESEARCH_GET_URL + "/" + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    search(searchParams){
        let body = JSON.stringify(searchParams);

        //console.log(body);
        return this.http.post(this.HF_MANAGER_SEARCH_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getManagers(){
        return this.http.get(this.HF_MANAGER_LIST_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleError);
    }

    findInvestedFunds(){
        return this.http.get(this.HF_MANAGER_INVESTED_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleError);
    }

    getResearchPage(id): Observable<HFResearchPage> {
        return this.http.get(this.HF_MANAGER_RESEARCH_PAGE_GET_URL + "/" + id, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    saveResearchPage(entity){
        let body = JSON.stringify(entity);

        return this.http.post(this.HF_MANAGER_RESEARCH_PAGE_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    public deleteResearchPage(pageId){
        return this.http.get(this.HF_MANAGER_RESEARCH_PAGE_DELETE_URL + pageId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    postFiles(pageId, params, files){
        return this.uploadService.postFiles(this.HF_MANAGER_RESEARCH_PAGE_ATTACHMENT_UPLOAD_URL + pageId, [], files, null);
    }

    public deleteAttachment(pageId, fileId) {
        return this.http.get(this.HF_MANAGER_RESEARCH_PAGE_ATTACHMENT_DELETE_URL + pageId + "/" + fileId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

}