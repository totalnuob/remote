import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable }     from 'rxjs/Observable';
import {CommonService} from "../common/common.service";
import {DATA_APP_URL} from "../common/common.service.constants";
import {FileUploadService} from "../upload/file.upload.service";
import {InputFilesNBReport} from "./model/input.files.nb.report";
import {PeriodicReportRecord} from "./model/periodic.report.record";
import {PeriodicReportRecordHolder} from "./model/periodic.report.record.holder";

@Injectable()
export class PeriodicReportService extends CommonService{
    constructor (
        private http: Http,
        private uploadService: FileUploadService
    ) {
        super();
    }

    private PERIODIC_REPORT_BASE_URL = DATA_APP_URL + "periodicReport/";
    private PERIODIC_REPORT_GET_URL  = this.PERIODIC_REPORT_BASE_URL + "get/";
    private PERIODIC_REPORT_LIST_URL  = this.PERIODIC_REPORT_BASE_URL + "getAll/";
    private PERIODIC_REPORT_SAVE_URL = this.PERIODIC_REPORT_BASE_URL + "save/";
    private PERIODIC_REPORT_FILE_LIST_URL  = this.PERIODIC_REPORT_BASE_URL + "inputFilesList/";

    private PERIODIC_REPORT_SCHEDULE_INVESTMENTS_URL  = this.PERIODIC_REPORT_BASE_URL + "get/scheduleInvestments/";
    private PERIODIC_REPORT_STATEMENT_BALANCE_OPERATIONS_URL  = this.PERIODIC_REPORT_BASE_URL + "get/balanceOperations/";
    private PERIODIC_REPORT_STATEMENT_CASHFLOWS_URL = this.PERIODIC_REPORT_BASE_URL + "get/cashflows/";
    private PERIODIC_REPORT_STATEMENT_CHANGES_URL = this.PERIODIC_REPORT_BASE_URL + "get/changes/";

    private PERIODIC_REPORT_UPLOAD_URL = this.PERIODIC_REPORT_BASE_URL + "upload/";

    loadAll(): Observable<any[]> {
        //return Promise.resolve(NEWS);

        return this.http.get(this.PERIODIC_REPORT_LIST_URL, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    save(entity){
        let body = JSON.stringify(entity);
        //console.log(body);
        return this.http.post(this.PERIODIC_REPORT_SAVE_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    get(reportId):  Observable<InputFilesNBReport>{
        return this.http.get(this.PERIODIC_REPORT_GET_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    loadInputFilesList(reportId): Observable<any[]> {

        return this.http.get(this.PERIODIC_REPORT_FILE_LIST_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractDataList)
            .catch(this.handleErrorResponse);
    }

    postFiles(reportId, file, fileType){
        console.log(reportId);
        return this.uploadService.postFiles(this.PERIODIC_REPORT_UPLOAD_URL + reportId, null, file, fileType );
    }

    getScheduleInvestments(reportId): Observable<PeriodicReportRecordHolder>{
        return this.http.get(this.PERIODIC_REPORT_SCHEDULE_INVESTMENTS_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getStatementBalanceOperations(reportId): Observable<PeriodicReportRecordHolder>{
        return this.http.get(this.PERIODIC_REPORT_STATEMENT_BALANCE_OPERATIONS_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getStatementCashflows(reportId): Observable<PeriodicReportRecordHolder>{
        return this.http.get(this.PERIODIC_REPORT_STATEMENT_CASHFLOWS_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getStatementChanges(reportId): Observable<PeriodicReportRecordHolder>{
        return this.http.get(this.PERIODIC_REPORT_STATEMENT_CHANGES_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }
}