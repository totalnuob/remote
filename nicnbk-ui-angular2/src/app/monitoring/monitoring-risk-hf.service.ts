import {CommonService} from "../common/common.service";
import {Injectable} from "@angular/core";
import {DATA_APP_URL} from "../common/common.service.constants";
import {FileUploadService} from "../upload/file.upload.service";
import {Http} from "@angular/http";
import {Observable} from "rxjs";

var fileSaver = require("file-saver");

@Injectable()
export class MonitoringRiskHedgeFundService extends CommonService {
    private MONITORING_RISK_BASE_URL = DATA_APP_URL + "monitoring/risk/";
    private MONITORING_RISK_MONTHLY_HF_GET_URL = this.MONITORING_RISK_BASE_URL + "hfMonthly/";
    private MONITORING_RISK_MONTHLY_HF_AVAILABLE_DATES_GET_URL = this.MONITORING_RISK_BASE_URL + "dateList/";
    private MONITORING_RISK_SUB_STRATEGY_UPLOAD_URL = this.MONITORING_RISK_BASE_URL + "strategy/upload/";
    private MONITORING_RISK_SUB_STRATEGY_EXPORT_URL = this.MONITORING_RISK_BASE_URL + "strategy/export/";
    private MONITORING_RISK_SUB_STRATEGY_DELETE_URL = this.MONITORING_RISK_BASE_URL + "strategy/delete/";
    private MONITORING_RISK_TOP_PORTFOLIO_UPLOAD_URL = this.MONITORING_RISK_BASE_URL + "topPortfolio/upload/";
    private MONITORING_RISK_TOP_PORTFOLIO_EXPORT_URL = this.MONITORING_RISK_BASE_URL + "topPortfolio/export/";
    private MONITORING_RISK_TOP_PORTFOLIO_DELETE_URL = this.MONITORING_RISK_BASE_URL + "topPortfolio/delete/";
    private MONITORING_RISK_MONTHLY_HF_SAVE_REPORT_URL = this.MONITORING_RISK_BASE_URL + "riskHFReport/save/";

    private MONITORING_RISK_RETURNS_UPLOAD_URL = this.MONITORING_RISK_BASE_URL + "returns/upload/";
    private MONITORING_RISK_RETURNS_CLASS_A_FILE_DELETE_URL = this.MONITORING_RISK_BASE_URL + "returns/classA/delete/";
    private MONITORING_RISK_RETURNS_CLASS_B_FILE_DELETE_URL = this.MONITORING_RISK_BASE_URL + "returns/classB/delete/";
    private MONITORING_RISK_RETURNS_CONS_FILE_DELETE_URL = this.MONITORING_RISK_BASE_URL + "returns/cons/delete/";


    constructor(
        private uploadService: FileUploadService,
        private http: Http)
    {
        super();
    }

    saveReport(report) {
        var body = JSON.stringify(report);
        return this.http.post(this.MONITORING_RISK_MONTHLY_HF_SAVE_REPORT_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getMonthlyHFRiskReport(selectedDate) {
        var body = JSON.stringify({"date": selectedDate});
        return this.http.post(this.MONITORING_RISK_MONTHLY_HF_GET_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    getAvailableDates(){
        return this.http.get(this.MONITORING_RISK_MONTHLY_HF_AVAILABLE_DATES_GET_URL, this.getOptionsWithCredentials())
                .map(this.extractData)
                .catch(this.handleErrorResponse);
    }

    postFiles(files) {
        return this.uploadService.postFiles(this.MONITORING_RISK_SUB_STRATEGY_UPLOAD_URL, [], files, null);
    }

    postFilesTopPortfolio(files) {
        return this.uploadService.postFiles(this.MONITORING_RISK_TOP_PORTFOLIO_UPLOAD_URL, [], files, null);
    }

    uploadReturns(files, data) {
        return this.uploadService.postFilesWithData(this.MONITORING_RISK_RETURNS_UPLOAD_URL, [], files, data);
    }

    deleteReturnsClassAFile(reportId){
        return this.http.get(this.MONITORING_RISK_RETURNS_CLASS_A_FILE_DELETE_URL + reportId, this.getOptionsWithCredentials())
                    .map(this.extractData)
                    .catch(this.handleErrorResponse);
    }
    deleteReturnsClassBFile(reportId){
        return this.http.get(this.MONITORING_RISK_RETURNS_CLASS_B_FILE_DELETE_URL + reportId, this.getOptionsWithCredentials())
                    .map(this.extractData)
                    .catch(this.handleErrorResponse);
    }
    deleteReturnsConsFile(reportId){
        return this.http.get(this.MONITORING_RISK_RETURNS_CONS_FILE_DELETE_URL + reportId, this.getOptionsWithCredentials())
                    .map(this.extractData)
                    .catch(this.handleErrorResponse);
    }

    deleteSubStrategy(selectedDate) {
        return this.http.get(this.MONITORING_RISK_SUB_STRATEGY_DELETE_URL + selectedDate, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    exportSubStrategy(selectedDate) {
        return this.makeFileRequest(this.MONITORING_RISK_SUB_STRATEGY_EXPORT_URL, selectedDate, "POST", selectedDate);
    }

    deleteTopPortfolio(selectedDate) {
        return this.http.get(this.MONITORING_RISK_TOP_PORTFOLIO_DELETE_URL + selectedDate, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleErrorResponse);
    }

    exportTopPortfolio(selectedDate) {
        return this.makeFileRequest(this.MONITORING_RISK_TOP_PORTFOLIO_EXPORT_URL, selectedDate, "POST", selectedDate);
    }

    public makeFileRequest(url, selectedDate, method?: string, body?): Observable<Response> {
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
                        fileSaver.saveAs(blob, "exported_portfolios.xlsx");
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
