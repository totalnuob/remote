import {CommonService} from "../common/common.service";
import {Injectable} from "@angular/core";
import {DATA_APP_URL} from "../common/common.service.constants";
import {FileUploadService} from "../upload/file.upload.service";
import {Http} from "@angular/http";

@Injectable()
export class HrDocsService extends CommonService {
    private HR_DOCS_BASE_URL = DATA_APP_URL + "hr/docs/";
    private HR_DOCS_GET_URL = this.HR_DOCS_BASE_URL + "get/";
    private HR_DOCS_UPLOAD_URL = this.HR_DOCS_BASE_URL + "upload/";
    private HR_DOCS_DELETE_URL = this.HR_DOCS_BASE_URL + "delete/";

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
}