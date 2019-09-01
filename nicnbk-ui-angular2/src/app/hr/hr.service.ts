import {CommonService} from "../common/common.service";
import {Injectable} from "@angular/core";
import {DATA_APP_URL} from "../common/common.service.constants";
import {FileUploadService} from "../upload/file.upload.service";
import { Observable }     from 'rxjs/Observable';
import {Http} from "@angular/http";
import {HRNews} from "./model/hr-news";

@Injectable()
export class HRService extends CommonService {
    private HR_BASE_URL = DATA_APP_URL + "hr/";

    private HR_DOCS_BASE_URL = this.HR_BASE_URL + "docs/";
    private HR_DOCS_GET_URL = this.HR_DOCS_BASE_URL + "get/";
    private HR_DOCS_UPLOAD_URL = this.HR_DOCS_BASE_URL + "upload/";
    private HR_DOCS_DELETE_URL = this.HR_DOCS_BASE_URL + "delete/";

    private HR_NEWS_BASE_URL = this.HR_BASE_URL + "news/";
    private NEWS_LIST_URL  = this.HR_NEWS_BASE_URL + "load/";
    private NEWS_LOAD_MORE_URL  = this.HR_NEWS_BASE_URL + "load/";
    private NEWS_GET_URL = this.HR_NEWS_BASE_URL + "get/";
    private NEWS_SAVE_URL = this.HR_NEWS_BASE_URL + "save/";


    constructor (
        private uploadService: FileUploadService,
        private http: Http)
    {
        super();
    }

    get() {
        return this.http.get(this.HR_DOCS_GET_URL, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    postFiles(files) {
        return this.uploadService.postFiles(this.HR_DOCS_UPLOAD_URL, [], files, null);
    }

    deleteDocument(fileId) {
        return this.http.delete(this.HR_DOCS_DELETE_URL + fileId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    loadNews(): Observable<HRNews[]> {
        return this.http.get(this.NEWS_LIST_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    loadMoreNews(pageSize, page){
        return this.http.get(this.NEWS_LOAD_MORE_URL + pageSize + "/" + page, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleError);
    }

    getNewsById(id): Observable<HRNews> {
        return this.http.get(this.NEWS_GET_URL + id, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    saveNews(entity){
        let body = JSON.stringify(entity);
        //console.log(body);
        return this.http.post(this.NEWS_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }
}