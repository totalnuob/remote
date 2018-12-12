import {Component, OnInit} from "@angular/core";
import {Router, ActivatedRoute} from '@angular/router';
import {Subscription} from "rxjs/Subscription";
import {ErrorResponse} from "../common/error-response";
import {PeriodicReportService} from "./periodic.report.service";
import {OtherInfoNBReporting} from "./model/other.info.nb.reporting";
import {CommonNBReportingComponent} from "./common.nb.reporting.component";
import {DATA_APP_URL} from "../common/common.service.constants";
import {LookupService} from "../common/lookup.service";
import {PeriodicReport} from "./model/periodic.report";
import {isNumeric} from "rxjs/util/isNumeric";
import {InputFilesNBReport} from "./model/input.files.nb.report";
import {SaveResponse} from "../common/save-response";

declare var $:any

var fileSaver = require("file-saver");

@Component({
    selector: 'generated-reports-nb-reporting',
    templateUrl: 'view/generated.reports.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class GeneratedReportsNBReportingComponent extends CommonNBReportingComponent implements OnInit {

    private sub: any;
    private reportId;

    busy: Subscription;
    busyFinal: Subscription;
    private report: InputFilesNBReport;
    private periodicReport: PeriodicReport;


    tabOption;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService
    ){
        super(router, route, periodicReportService);
        this.periodicReport = new PeriodicReport();

        this.sub = this.route
            .params
            .subscribe(params => {
                this.reportId = +params['id'];

                if(this.reportId > 0){
                    // load report data

                    //this.report = new InputFilesNBReport();
                    //this.report.reportId = this.reportId;

                    this.busy = this.periodicReportService.loadInputFilesList(this.reportId)
                        .subscribe(
                            response  => {
                                this.report = new InputFilesNBReport();
                                this.convertToViewModel(response.files);

                                this.periodicReport = response.report;

                                console.log(response);
                            },
                            (error: ErrorResponse) => {
                                this.successMessage = null;
                                this.errorMessage = "Error loading report files";
                                this.report = null;
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, null);
                            }
                        )

                }else{
                    // TODO: ??
                    console.log("No report id")
                }
            });

    }

    ngOnInit(): any {
        this.sub = this.route.params.subscribe(params => {
            this.tabOption = params['showInputList'];
        });
    }

    public saveInterestRate(){
        this.busy = this.periodicReportService.saveKZTReportForm13InterestRate(this.reportId, this.periodicReport.interestRate)
            .subscribe(
                (response: SaveResponse)  => {
                    console.log(response);
                    if (response && response.status != 'FAIL') {
                        this.postAction("Successfully saved interest rate", null);
                    } else if (response && response.status === 'FAIL') {
                        var errorMessage = response.message != null && response.message.nameEn != null ? response.message.nameEn : "Error saving interest rate";
                        this.postAction(null, errorMessage);
                    } else {
                        this.postAction(null, "Unexptected return from server.");
                        return;
                    }
                },
                (error: ErrorResponse) => {
                    this.successMessage = null;
                    this.errorMessage = "Error saving interest rate (report form 13)";
                    this.report = null;
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            )
    }

    markAsFinal(){
        if(confirm("Are you sure want to mark report 'FINAL'? You will not be able to edit any data.")) {
            this.busyFinal = this.periodicReportService.markReportAsFinal(this.reportId)
                .subscribe(
                    response => {
                        if (response && response.status != 'FAIL') {
                            this.postAction("Successfully marked report as final (SUBMITTED)", null);
                            this.periodicReport.status = "SUBMITTED";
                        } else if (response && response.status === 'FAIL') {
                            var errorMessage = response.message != null && response.message.nameEn != null ? response.message.nameEn : "Error marking report as final";
                            this.postAction(null, errorMessage);
                        } else {
                            this.postAction(null, "Unexptected return from server.");
                            return;
                        }
                    },
                    (error: ErrorResponse) => {
                        this.successMessage = null;
                        this.errorMessage = "Error marking report as final";
                        this.report = null;
                        if (error && !error.isEmpty()) {
                            this.processErrorMessage(error);
                        }else {
                            this.postAction(null, this.errorMessage);
                        }
                    }
                )
        }

    }

    getReportsTabClass(){
        var clazz = 'active';
        if(this.tabOption === 'INPUT'){
            clazz = '';
        }
        return clazz;
    }

    getInputTabClass(){
        var clazz = '';
        if(this.tabOption === 'INPUT'){
            clazz = 'active';
        }
        return clazz;
    }

    getReportsTabDivClass(){
        var clazz = 'tab-pane fade in active';
        if(this.tabOption === 'INPUT'){
            clazz = 'tab-pane fade';
        }
        return clazz;
    }

    getInputTabDivClass(){
        var clazz = 'tab-pane fade';
        if(this.tabOption === 'INPUT'){
            clazz = 'tab-pane fade in active';
        }
        return clazz;
    }

    convertToViewModel(filesList){
        if(filesList){
            for(var i = 0; i < filesList.length; i++){
                if(filesList[i].type == 'NB_REP_T1'){
                    this.report.tarragonScheduleInvestmentFileId = filesList[i].id;
                    this.report.tarragonScheduleInvestmentFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_T2'){
                    this.report.tarragonStatementAssetsFileId = filesList[i].id;
                    this.report.tarragonStatementAssetsFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_T3'){
                    this.report.tarragonStatementCashflowsFileId = filesList[i].id;
                    this.report.tarragonStatementCashflowsFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_T4'){
                    this.report.tarragonStatementChangesFileId = filesList[i].id;
                    this.report.tarragonStatementChangesFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_SGL'){
                    this.report.singularityGeneralLedgerFileId = filesList[i].id;
                    this.report.singularityGeneralLedgerFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_SNA'){
                    this.report.singularityNOALTrancheAFileId = filesList[i].id;
                    this.report.singularityNOALTrancheAFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_SNB'){
                    this.report.singularityNOALTrancheBFileId = filesList[i].id;
                    this.report.singularityNOALTrancheBFileName = filesList[i].fileName;
                } else if(filesList[i].type == 'NB_REP_CMB'){
                    this.report.terraCombinedFileId = filesList[i].id;
                    this.report.terraCombinedFileName = filesList[i].fileName;
                }
            }
        }
    }

    // TODO: separate REST Service for file downloads etc and separate service
    download(fileType, fileId, fileName) {

        // Xhr creates new context so we need to create reference to this
        let self = this;

        // Status flag used in the template.
        //this.pending = true;

        // Create the Xhr request object
        let xhr = new XMLHttpRequest();
        xhr.withCredentials = true; // send auth token with the request
        // TODO: url const
        let url =  DATA_APP_URL + `files/download/${fileType}/${fileId}`;
        xhr.open('GET', url, true);
        xhr.responseType = 'blob';

        // Xhr callback when we get a result back
        // We are not using arrow function because we need the 'this' context
        xhr.onreadystatechange = function() {

            // We use setTimeout to trigger change detection in Zones
            setTimeout( () => {
                //self.pending = false;
            }, 0);

            // If we get an HTTP status OK (200), save the file using fileSaver
            if(xhr.readyState === 4 && xhr.status === 200) {
                var blob = new Blob([this.response], {type: this.response.type});
                fileSaver.saveAs(blob, fileName);

            }
        };

        // Start the Ajax request
        xhr.send();
    }

    export(formType, formName) {
        this.busy = this.periodicReportService.makeFileRequest(DATA_APP_URL + `periodicReport/export/${this.reportId}/${formType}`, formName)
            .subscribe(
                response  => {
                    //console.log("ok");
                },
                error => {
                    //console.log("fails")
                    this.postAction(null, "Error exporting data");
                }
            );
    }

    exportAll(){
        this.busy = this.periodicReportService.makeFileRequest(DATA_APP_URL + `periodicReport/exportAll/${this.reportId}/`, 'reports_kzt')
            .subscribe(
                response  => {
                    //console.log("ok");
                },
                error => {
                    //console.log("fails")
                    this.postAction(null, "Error exporting data");
                }
            );
    }

}