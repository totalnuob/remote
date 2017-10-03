import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable }     from 'rxjs/Observable';
import {CommonService} from "../common/common.service";
import {DATA_APP_URL} from "../common/common.service.constants";
import {FileUploadService} from "../upload/file.upload.service";
import {InputFilesNBReport} from "./model/input.files.nb.report";
import {PeriodicReportRecord} from "./model/periodic.report.record";
import {PeriodicReportRecordHolder} from "./model/periodic.report.record.holder";
import {OtherInfoNBReporting} from "./model/other.info.nb.reporting";
import {NICKMFReportingInfo} from "./model/nick.mf.reporting.info.nb.reporting";
import {NICKMFReportingInfoHolder} from "./model/nick.mf.reporting.info.holder.nb.reporting";

var fileSaver = require("file-saver");

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
    private PERIODIC_REPORT_GENERAL_LEDGER_BALANCE_URL = this.PERIODIC_REPORT_BASE_URL + "get/generalLedgerBalance/";

    private PERIODIC_REPORT_SINGULARITY_NOAL_A_URL = this.PERIODIC_REPORT_BASE_URL + "get/noalA/";
    private PERIODIC_REPORT_SINGULARITY_NOAL_B_URL = this.PERIODIC_REPORT_BASE_URL + "get/noalB/";
    private PERIODIC_REPORT_OTHER_INFO_URL = this.PERIODIC_REPORT_BASE_URL + "get/otherInfo/";
    private PERIODIC_REPORT_NICK_MF_REPORTING_INFO_URL = this.PERIODIC_REPORT_BASE_URL + "NICKMFReportingInfo/";
    private PERIODIC_REPORT_NICK_MF_REPORTING_INFO_PREVIOUS_MONTH_URL = this.PERIODIC_REPORT_BASE_URL + "NICKMFReportingInfoPreviousMonth/";

    private PERIODIC_REPORT_OTHER_INFO_UPLOAD_URL = this.PERIODIC_REPORT_BASE_URL + "otherInfo/save/";
    private PERIODIC_REPORT_NICK_MF_REPORTING_INFO_SAVE_URL = this.PERIODIC_REPORT_BASE_URL + "NICKMFReportingInfo/save/";

    private PERIODIC_REPORT_UPLOAD_URL = this.PERIODIC_REPORT_BASE_URL + "upload/";
    private PERIODIC_REPORT_UPLOAD_NONPARSED_URL = this.PERIODIC_REPORT_BASE_URL + "nonParsed/upload/";
    private MONTHLY_CASH_STATEMENT_FILE_DELETE_URL = this.PERIODIC_REPORT_BASE_URL + "monthlyCashStatementFile/delete/";

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

    postNonParsedFiles(reportId, file, fileType){
        console.log(reportId);
        return this.uploadService.postFiles(this.PERIODIC_REPORT_UPLOAD_NONPARSED_URL + reportId, null, file, fileType );
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

    getGeneralLedgerBalance(reportId): Observable<PeriodicReportRecordHolder>{
        return this.http.get(this.PERIODIC_REPORT_GENERAL_LEDGER_BALANCE_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }
    getSingularityNOALTrancheA(reportId): Observable<PeriodicReportRecordHolder>{
        return this.http.get(this.PERIODIC_REPORT_SINGULARITY_NOAL_A_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getSingularityNOALTrancheB(reportId): Observable<PeriodicReportRecordHolder>{
        return this.http.get(this.PERIODIC_REPORT_SINGULARITY_NOAL_B_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getOtherInfo(reportId): Observable<OtherInfoNBReporting>{
        return this.http.get(this.PERIODIC_REPORT_OTHER_INFO_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getNICKMFReportingInfo(reportId): Observable<NICKMFReportingInfoHolder>{
        return this.http.get(this.PERIODIC_REPORT_NICK_MF_REPORTING_INFO_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getNICKMFReportingInfoPreviousMonth(reportId): Observable<NICKMFReportingInfoHolder>{
        return this.http.get(this.PERIODIC_REPORT_NICK_MF_REPORTING_INFO_PREVIOUS_MONTH_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    saveNICKMFReportingInfo(records){
        //let body = JSON.stringify(dto);
        //console.log(body);
        return this.http.post(this.PERIODIC_REPORT_NICK_MF_REPORTING_INFO_SAVE_URL, records, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    saveOtherInfo(dto){
        let body = JSON.stringify(dto);
        //console.log(body);
        return this.http.post(this.PERIODIC_REPORT_OTHER_INFO_UPLOAD_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }


    public deleteMonthlyCashStatementFile(reportId) {
        return this.http.get(this.MONTHLY_CASH_STATEMENT_FILE_DELETE_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }
}