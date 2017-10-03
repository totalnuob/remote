import {Component, OnInit} from "@angular/core";
import {Router, ActivatedRoute} from '@angular/router';
import {Subscription} from "rxjs/Subscription";
import {ErrorResponse} from "../common/error-response";
import {PeriodicReportService} from "./periodic.report.service";
import {OtherInfoNBReporting} from "./model/other.info.nb.reporting";
import {CommonNBReportingComponent} from "./common.nb.reporting.component";
import {DATA_APP_URL} from "../common/common.service.constants";


declare var $:any

@Component({
    selector: 'other-info-input-nb-reporting',
    templateUrl: 'view/other.info.input.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class OtherInfoInputNBReportingComponent extends CommonNBReportingComponent implements OnInit {

    private sub: any;
    private reportId;

    busy: Subscription;


    otherInfo: OtherInfoNBReporting;
    private monthlyCashStatementFile;

    private otherInfoSaved;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService
    ){
        super(router, route, periodicReportService);

        this.otherInfo = new OtherInfoNBReporting;
        this.otherInfoSaved = false;

        this.sub = this.route
            .params
            .subscribe(params => {
                this.reportId = +params['id'];
                console.log(this.reportId);
                if(this.reportId > 0){
                    // load report data

                    this.otherInfo.report.id = this.reportId;
                    this.busy = this.periodicReportService.getOtherInfo(this.reportId)
                        .subscribe(
                            response  => {
                                if(response){
                                    //console.log(response);
                                    this.otherInfo = response;
                                    if(this.otherInfo.closingBalance > 0 && this.otherInfo.exchangeRate > 0) {
                                        this.otherInfoSaved = true;
                                    }
                                }
                            },
                            (error: ErrorResponse) => {
                                this.successMessage = null;
                                this.errorMessage = "Error loading Other Info";
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, null);
                            }
                        );
                }else{
                    // TODO: ??
                    console.log("No report id")
                }
            });
    }

    ngOnInit(): any {
        this.postAction(null, null);
        // TODO: exclude jQuery
        // datetimepicker
        $('#exchangeRateDateDiv').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
    }

    save(){
        if(this.otherInfo.closingBalance > 0 && this.otherInfo.exchangeRate > 0) {
            this.otherInfoSaved = true;
        }else{
            this.postAction(null, 'Please fill out required fields');
            return;
        }
        this.otherInfo.exchangeRateDate = $('#exchangeRateDate').val();

        this.periodicReportService.saveOtherInfo(this.otherInfo)
            .subscribe(
                response  => {
                    var creationDate = response.creationDate;

                    this.successMessage = "Successfully saved information";
                    this.errorMessage = null;
                },
                (error: ErrorResponse) => {
                    this.successMessage = null;
                    this.errorMessage = "Error saving information";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            )
    }

    onFileChange(event) {
        var target = event.target || event.srcElement;
        var files = target.files;

        this.monthlyCashStatementFile = files;
    }

    uploadFile(){
        this.busy = this.periodicReportService.postNonParsedFiles(this.reportId, this.monthlyCashStatementFile, 'NB_REP_MCS').subscribe(
            res => {
                // clear upload file on view
                this.monthlyCashStatementFile = null;
                // set file id
                this.otherInfo.monthlyCashStatementFileId = res.fileId;
                this.otherInfo.monthlyCashStatementFileName = res.fileName;
                this.postAction(res != null && res.messageEn != null ? res.messageEn : "Successfully uploaded file - Monthly Cash flows Statement", null);
            },
            error => {
                var result = JSON.parse(error);
                var message = result != null && result.messageEn != null ? result.messageEn : null;
                this.monthlyCashStatementFile = null;
                this.postAction(null, message != null && message != null ? message : "Error uploading file");
            });
    }

    clearFile(){
        this.monthlyCashStatementFile = null;
    }

    deleteMonthlyCashStatementFile(){
        var confirmed = window.confirm("Are you sure want to delete");
        if(confirmed) {
            this.periodicReportService.deleteMonthlyCashStatementFile(this.otherInfo.report.id)
                .subscribe(
                    response => {
                        this.otherInfo.monthlyCashStatementFileId = null;
                        this.otherInfo.monthlyCashStatementFileName = null;

                        this.postAction("'Monthly Cash Statement' file deleted.", null);
                    },
                    (error: ErrorResponse) => {
                        this.errorMessage = "Error deleting 'Monthly Cash Statement' file";
                        if(error && !error.isEmpty()){
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, null);
                    }
                );
        }
    }
}