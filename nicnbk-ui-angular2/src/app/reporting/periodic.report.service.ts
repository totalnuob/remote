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
import {GeneratedGLFormRecord} from "./model/generated.form.record";
import {ConsolidatedBalanceFormRecord} from "./model/consolidated.balance.form.record";
import {PreviousYearInputRecord} from "./model/previous.year.input.record";
import {ConsolidatedIncomeExpenseFormRecord} from "./model/consolidated.income.expense.form.record";
import {ConsolidatedTotalIncomeFormRecord} from "./model/consolidated.total.income.form.record";
import {ListResponse} from "./../common/list.response.ts";
import {ConsolidatedKZTForm8Record} from "./model/consolidated.kzt.form.8.record";
import {ConsolidatedKZTForm10Record} from "./model/consolidated.kzt.form.10.record";
import {ConsolidatedKZTForm14Record} from "./model/consolidated.kzt.form.14.record";
import {ConsolidatedKZTForm13Record} from "./model/consolidated.kzt.form.13.record";
import {ConsolidatedKZTForm7Record} from "./model/consolidated.kzt.form.7.record";
import {ReserveCalculationFormRecord} from "./model/reserve.calculation.form.record";
import {ConsolidatedBalanceForm19Record} from "./model/consolidated.balance.form.19.record";
import {ConsolidatedBalanceForm22Record} from "./model/consolidated.balance.form.22.record";
import {NEWS} from "../news/model/mock-news";
import {PeriodicReport} from "./model/periodic.report";
import {ConsolidatedKZTForm6Record} from "./model/consolidated.kzt.form.6.record";
import {OKResponse} from "../common/ok-response";
import {ReserveCalculationSearchResults} from "./model/reserve-calculation-search-results";

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
    private PERIODIC_REPORT_SINGULARITY_ADJUSTMENTS_URL = this.PERIODIC_REPORT_BASE_URL + "saveSingularityAdjustments/";

    private PERIODIC_REPORT_SINGULARITY_NOAL_A_URL = this.PERIODIC_REPORT_BASE_URL + "get/noalA/";
    private PERIODIC_REPORT_SINGULARITY_NOAL_B_URL = this.PERIODIC_REPORT_BASE_URL + "get/noalB/";
    private PERIODIC_REPORT_OTHER_INFO_URL = this.PERIODIC_REPORT_BASE_URL + "get/otherInfo/";
    private PERIODIC_REPORT_NICK_MF_REPORTING_INFO_URL = this.PERIODIC_REPORT_BASE_URL + "NICKMFReportingInfo/";
    private PERIODIC_REPORT_NICK_MF_REPORTING_INFO_PREVIOUS_MONTH_URL = this.PERIODIC_REPORT_BASE_URL + "NICKMFReportingInfoPreviousMonth/";

    private PERIODIC_REPORT_SINGULAR_GENERATED_FORM_URL = this.PERIODIC_REPORT_BASE_URL + "singularGeneratedForm/";
    private PERIODIC_REPORT_TARRAGON_GENERATED_FORM_URL = this.PERIODIC_REPORT_BASE_URL + "tarragonGeneratedForm/";
    private PERIODIC_REPORT_TARRAGON_GENERATED_FROM_PREV_MONTH_URL = this.PERIODIC_REPORT_BASE_URL + "tarragonGeneratedFormDataFromPreviousMonth/";

    private PERIODIC_REPORT_OTHER_INFO_UPLOAD_URL = this.PERIODIC_REPORT_BASE_URL + "otherInfo/save/";
    private PERIODIC_REPORT_NICK_MF_REPORTING_INFO_SAVE_URL = this.PERIODIC_REPORT_BASE_URL + "NICKMFReportingInfo/save/";
    private PERIODIC_REPORT_PE_GENERAL_LEDGER_FORM_DATA_SAVE_URL = this.PERIODIC_REPORT_BASE_URL + "PEGeneralLedgerFormData/save/";
    private PERIODIC_REPORT_PE_GENERAL_LEDGER_FORM_DATA_DELETE_RECORD_URL = this.PERIODIC_REPORT_BASE_URL + "PEGeneralLedgerFormData/delete/";
    private PERIODIC_REPORT_UPDATE_TARRAGON_INVESTMENT_URL = this.PERIODIC_REPORT_BASE_URL + "updatedTarragonInvestment/";


    private PERIODIC_REPORT_UPLOAD_URL = this.PERIODIC_REPORT_BASE_URL + "upload/";
    private PERIODIC_REPORT_UPLOAD_NONPARSED_URL = this.PERIODIC_REPORT_BASE_URL + "nonParsed/upload/";
    private MONTHLY_CASH_STATEMENT_FILE_DELETE_URL = this.PERIODIC_REPORT_BASE_URL + "monthlyCashStatementFile/delete/";

    private PERIODIC_REPORT_CONSOLIDATED_BALANCE_USD_FORM_URL = this.PERIODIC_REPORT_BASE_URL + "consolidatedBalanceUSDForm/";
    private PERIODIC_REPORT_CONSOLIDATED_INCOME_EXPENSE_USD_FORM_URL = this.PERIODIC_REPORT_BASE_URL + "consolidatedIncomeExpenseUSDForm/";
    private PERIODIC_REPORT_CONSOLIDATED_TOTAL_INCOME_USD_FORM_URL = this.PERIODIC_REPORT_BASE_URL + "consolidatedTotalIncomeUSDForm/";

    private PERIODIC_REPORT_PREVIOUS_YEAR_INPUT_URL = this.PERIODIC_REPORT_BASE_URL + "previousYearInput/";
    private PERIODIC_REPORT_PREVIOUS_YEAR_INPUT_PREV_MONTH_URL = this.PERIODIC_REPORT_BASE_URL + "previousYearInputPrevMonth/";

    private PERIODIC_REPORT_DELETE_FILE_URL = this.PERIODIC_REPORT_BASE_URL + "deleteFile/";

    private PERIODIC_REPORT_MARK_REPORT_FINAL_URL = this.PERIODIC_REPORT_BASE_URL + "markReportFinal/";

    private PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_6_URL = this.PERIODIC_REPORT_BASE_URL + "consolidatedBalanceKZTForm6/";
    private PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_8_URL = this.PERIODIC_REPORT_BASE_URL + "consolidatedBalanceKZTForm8/";
    private PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_10_URL = this.PERIODIC_REPORT_BASE_URL + "consolidatedBalanceKZTForm10/";
    private PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_14_URL = this.PERIODIC_REPORT_BASE_URL + "consolidatedBalanceKZTForm14/";
    private PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_13_URL = this.PERIODIC_REPORT_BASE_URL + "consolidatedBalanceKZTForm13/";
    private PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_7_URL = this.PERIODIC_REPORT_BASE_URL + "consolidatedBalanceKZTForm7/";
    private PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_1_URL = this.PERIODIC_REPORT_BASE_URL + "consolidatedBalanceKZTForm1/";
    private PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_2_URL = this.PERIODIC_REPORT_BASE_URL + "consolidatedBalanceKZTForm2/";
    private PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_3_URL = this.PERIODIC_REPORT_BASE_URL + "consolidatedBalanceKZTForm3/";
    private PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_19_URL = this.PERIODIC_REPORT_BASE_URL + "consolidatedBalanceKZTForm19/";
    private PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_22_URL = this.PERIODIC_REPORT_BASE_URL + "consolidatedBalanceKZTForm22/";

    private PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_13_INTEREST_RATE_URL = this.PERIODIC_REPORT_BASE_URL + "saveKZTReportForm13InterestRate/";

    private PERIODIC_REPORT_RESERVE_CALCULATION_FORM_URL = this.PERIODIC_REPORT_BASE_URL + "reserveCalculation/";
    private PERIODIC_REPORT_RESERVE_CALCULATION_DELETE_RECORD_URL = this.PERIODIC_REPORT_BASE_URL + "reserveCalculation/delete/";
    private PERIODIC_REPORT_RESERVE_CALCULATION_SAVE_URL = this.PERIODIC_REPORT_BASE_URL + "reserveCalculationSave/";
    private PERIODIC_REPORT_RESERVE_CALCULATION_SEARCH_URL = this.PERIODIC_REPORT_BASE_URL + "searchReserveCalculations/";


    loadAll(): Observable<PeriodicReport[]> {
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

    get(reportId):  Observable<PeriodicReport>{
        return this.http.get(this.PERIODIC_REPORT_GET_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    loadInputFilesList(reportId): Observable<any> {

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

    saveSingularityAdjustments(entity){
        let body = JSON.stringify(entity);
        //console.log(body);
        return this.http.post(this.PERIODIC_REPORT_SINGULARITY_ADJUSTMENTS_URL + entity.reportId , body, this.getOptionsWithCredentials())
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

    getGeneratedSingularForm(reportId): Observable<ListResponse>{
        return this.http.get(this.PERIODIC_REPORT_SINGULAR_GENERATED_FORM_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getGeneratedTarragonForm(reportId): Observable<ListResponse>{
        return this.http.get(this.PERIODIC_REPORT_TARRAGON_GENERATED_FORM_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getNICKMFReportingInfoPreviousMonth(reportId): Observable<NICKMFReportingInfoHolder>{
        return this.http.get(this.PERIODIC_REPORT_NICK_MF_REPORTING_INFO_PREVIOUS_MONTH_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getGeneratedTarragonFormDataFromPreviousMonth(reportId): Observable<GeneratedGLFormRecord[]>{
        return this.http.get(this.PERIODIC_REPORT_TARRAGON_GENERATED_FROM_PREV_MONTH_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getConsolidatedBalanceUSDForm(reportId): Observable<ListResponse>{
        return this.http.get(this.PERIODIC_REPORT_CONSOLIDATED_BALANCE_USD_FORM_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getConsolidatedBalanceKZTForm1(reportId): Observable<ListResponse>{
        return this.http.get(this.PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_1_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getConsolidatedKZTForm2(reportId): Observable<ListResponse>{
        return this.http.get(this.PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_2_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getConsolidatedKZTForm3(reportId): Observable<ListResponse>{
        return this.http.get(this.PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_3_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getConsolidatedKZTForm8(reportId): Observable<ListResponse>{
        return this.http.get(this.PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_8_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getConsolidatedKZTForm6(reportId): Observable<ListResponse>{
        return this.http.get(this.PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_6_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getConsolidatedKZTForm14(reportId): Observable<ListResponse>{
        return this.http.get(this.PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_14_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getConsolidatedKZTForm10(reportId): Observable<ListResponse>{
        return this.http.get(this.PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_10_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getConsolidatedKZTForm13(reportId): Observable<ListResponse>{
        return this.http.get(this.PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_13_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    saveKZTReportForm13InterestRate(reportId, value): Observable<any>{
        return this.http.post(this.PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_13_INTEREST_RATE_URL + reportId, value, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getConsolidatedKZTForm7(reportId): Observable<ListResponse>{
        return this.http.get(this.PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_7_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getConsolidatedKZTForm19(reportId): Observable<ListResponse>{
        return this.http.get(this.PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_19_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getConsolidatedKZTForm22(reportId): Observable<ListResponse>{
        return this.http.get(this.PERIODIC_REPORT_CONSOLIDATED_KZT_FORM_22_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getConsolidatedIncomeExpenseUSDForm(reportId): Observable<ConsolidatedIncomeExpenseFormRecord[]>{
        return this.http.get(this.PERIODIC_REPORT_CONSOLIDATED_INCOME_EXPENSE_USD_FORM_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getConsolidatedTotalIncomeUSDForm(reportId): Observable<ConsolidatedTotalIncomeFormRecord[]>{
        return this.http.get(this.PERIODIC_REPORT_CONSOLIDATED_TOTAL_INCOME_USD_FORM_URL + reportId, this.getOptionsWithCredentials())
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

    savePEGeneralLedgerFormData(records){
        //let body = JSON.stringify(dto);
        //console.log(body);
        return this.http.post(this.PERIODIC_REPORT_PE_GENERAL_LEDGER_FORM_DATA_SAVE_URL, records, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getReserveCalculationFormData(): Observable<ReserveCalculationFormRecord[]>{
        return this.http.get(this.PERIODIC_REPORT_RESERVE_CALCULATION_FORM_URL, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    searchReserveCalculations(searchParams): Observable<ReserveCalculationSearchResults>{
        let body = JSON.stringify(searchParams);
        return this.http.post(this.PERIODIC_REPORT_RESERVE_CALCULATION_SEARCH_URL, body,this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    public deleteReserveCalculationFormDataRecord(recordId) {
        return this.http.get(this.PERIODIC_REPORT_RESERVE_CALCULATION_DELETE_RECORD_URL + "/" + recordId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    saveReserveCalculationFormData(records){
        let body = JSON.stringify(records);
        //console.log(body);
        return this.http.post(this.PERIODIC_REPORT_RESERVE_CALCULATION_SAVE_URL, body, this.getOptionsWithCredentials())
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

    public deletePEGeneralLedgerFormDataRecord(recordId) {
        return this.http.get(this.PERIODIC_REPORT_PE_GENERAL_LEDGER_FORM_DATA_DELETE_RECORD_URL + "/" + recordId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    updateTarragonInvestment(data){
        let body = JSON.stringify(data);
        //console.log(body);
        return this.http.post(this.PERIODIC_REPORT_UPDATE_TARRAGON_INVESTMENT_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getPreviousYearInput(reportId): Observable<PreviousYearInputRecord[]>{
        return this.http.get(this.PERIODIC_REPORT_PREVIOUS_YEAR_INPUT_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getPreviousYearInputDataFromPreviousMonth(reportId): Observable<PreviousYearInputRecord[]>{
        return this.http.get(this.PERIODIC_REPORT_PREVIOUS_YEAR_INPUT_PREV_MONTH_URL + reportId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    savePreviousYearInput(records, reportId){
        let body = JSON.stringify(records);
        //console.log(body);
        return this.http.post(this.PERIODIC_REPORT_PREVIOUS_YEAR_INPUT_URL + reportId, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    public deleteFile(fileId) {
        return this.http.get(this.PERIODIC_REPORT_DELETE_FILE_URL + fileId, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    public markReportAsFinal(reportId): Observable<OKResponse>{
        return this.http.post(this.PERIODIC_REPORT_MARK_REPORT_FINAL_URL + reportId, null, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    public makeFileRequest(url, fileName, method?: string, body?): Observable<Response> {
        return Observable.fromPromise(new Promise((resolve, reject) => {
                let xhr = new XMLHttpRequest();
                xhr.withCredentials = true; // send auth token with the request
                // TODO: url const
                //let url =  DATA_APP_URL + `periodicReport/export/${this.reportId}/${'KZT_FORM_1'}`;

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
            //.map(this.extractData);
            //.catch(this.handleErrorResponse);
    }
}