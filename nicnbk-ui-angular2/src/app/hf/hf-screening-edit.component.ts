import {Component,NgModule, OnInit, ViewChild} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {SelectComponent} from "ng2-select";
import {CommonFormViewComponent} from "../common/common.component";
import {EmployeeService} from "../employee/employee.service";
import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";
import {Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/forkJoin';
import {HedgeFundScreening} from "./model/hf.screening";
import {HedgeFundScreeningService} from "./hf.fund.screening.service";
import {HedgeFundScreeningSearchParams} from "./model/hf.screening-search-params";
import {HedgeFundScreeningParsedDataDateValue} from "./model/hf.screening.parsed.data.return";

declare var $:any
declare var Chart: any;

@Component({
    selector: 'hf-screening-edit',
    templateUrl: 'view/hf-screening-edit.component.html',
    styleUrls: [],
    providers: [],
})
@NgModule({
    imports: []
})
export class HFScreeningEditComponent extends CommonFormViewComponent implements OnInit{

    private sub: any;
    private screeningId: number;
    busy: Subscription;
    screening = new HedgeFundScreening;

    parsedDataReturns: HedgeFundScreeningParsedDataDateValue[];
    parsedDataAUM: HedgeFundScreeningParsedDataDateValue[];

    public uploadFile: any;
    private breadcrumbParams: string;
    private searchParams = new HedgeFundScreeningSearchParams();

    headerDatesReturns: Date[];
    headerDatesAUM: Date[];

    private moduleAccessChecker: ModuleAccessCheckerService;


    constructor(
        private route: ActivatedRoute,
        private screeningService: HedgeFundScreeningService,
        private router: Router
    ){
        super(router);
        this.moduleAccessChecker = new ModuleAccessCheckerService;
        this.sub = this.route
            .params
            .subscribe(params => {
                this.screeningId = +params['id'];
                this.breadcrumbParams = params['params'];
                if(this.screeningId > 0) {
                    this.busy = this.screeningService.get(this.screeningId)
                        .subscribe(
                            entity => {
                                this.screening = entity;
                                //console.log(this.screening);
                            },
                            (error: ErrorResponse) => {
                                this.errorMessage = "Error loading screening";
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, this.errorMessage);
                            }
                        );
                }else{
                }
            });

    }


    ngOnInit(): any {

        this.postAction(null, null);

        // TODO: exclude jQuery
        // datetimepicker
        $('#screeningDateDiv').datetimepicker({
            defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
    }

    save() {
        this.screening.date = $('#screeningDate').val();

        this.busy = this.screeningService.save(this.screening)
            .subscribe(
                response => {
                    this.screening.id = response.entityId;

                    if (this.uploadFile) {
                        this.busy = this.screeningService.postFiles(this.screening.id, [], [this.uploadFile])
                            .subscribe(
                                res => {
                                    // clear upload files list on view
                                    this.uploadFile = null;

                                    // update files list with new files
                                    this.screening.fileId = res.fileId;
                                    this.screening.fileName = res.fileName;

                                    this.busy = this.screeningService.get(this.screeningId)
                                        .subscribe(
                                            entity => {
                                                this.screening = entity;
                                                this.postAction("Successfully saved screening.", null);
                                            },
                                            (error: ErrorResponse) => {
                                                this.errorMessage = "Error re-loading screening after file upload";
                                                if(error && !error.isEmpty()){
                                                    this.processErrorMessage(error);
                                                }
                                                this.postAction(this.successMessage, this.errorMessage);
                                            }
                                        );

                                },
                                error => {
                                    var result = JSON.parse(error);
                                    var message = result != null && result.message != null && result.message.nameEn != null ? result.message.nameEn : null;
                                    this.uploadFile = null;
                                    this.postAction(null, message != null && message != null ? message : "Error uploading file");
                                });
                    } else {
                        this.postAction("Successfully saved screening.", null);
                    }

                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error saving screening";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, this.errorMessage);
                }
            );
    }

    deleteAttachment(fileId) {
        var confirmed = window.confirm("Are you sure want to delete");
        if(confirmed) {
            this.busy = this.screeningService.removeFile(this.screeningId, fileId)
                .subscribe(
                    entity => {
                        this.screening.fileId = null;
                        this.screening.fileName = null;
                        this.screening.parsedData = null;
                        this.postAction("Attachment deleted.", null);
                    },
                    (error:ErrorResponse) => {
                        this.errorMessage = "Failed to delete file";
                        if (error && !error.isEmpty()) {
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, this.errorMessage);
                    }
                );
        }
    }

    onFileChange(event) {
        var target = event.target || event.srcElement;
        var files = target.files;
        this.uploadFile = files[0];
        //this.uploadFiles.length = 0;
        //for (let i = 0; i < files.length; i++) {
        //    this.uploadFiles.push(files[i]);
        //}
    }



    public canEdit() {
        return this.moduleAccessChecker.checkAccessHedgeFundsEditor();
    }

    searchReturns(months){
        var date = null;
        if(months){
            // get max date
            if(this.headerDatesReturns != null && this.headerDatesReturns.length > 0) {
                var index = months > 0 ? this.headerDatesReturns.length - 1 : 0;
                date = this.headerDatesReturns[index];
            }
        }
        this.busy = this.screeningService.searchReturns(this.screeningId, months, date)
            .subscribe(
                searchResult  => {
                    //console.log(searchResult);
                    if(searchResult != null && searchResult.length > 0){
                        for(var i = 0; i < searchResult.length; i++) {
                            if(searchResult[i].dates != null) {
                                this.headerDatesReturns = searchResult[i].dates;
                                break;
                            }
                        }
                        this.parsedDataReturns = searchResult;
                    }

                },
                error =>  {
                    this.postAction(null, "Failed to search screening returns list");

                }
            );
    }

    searchAUMs(months){
        var date = null;
        if(months){
            // get max date
            if(this.headerDatesAUM != null && this.headerDatesAUM.length > 0) {
                var index = months > 0 ? this.headerDatesAUM.length - 1 : 0;
                date = this.headerDatesAUM[index];
            }
        }
        this.busy = this.screeningService.searchAUMs(this.screeningId, months, date)
            .subscribe(
                searchResult  => {
                    //console.log(searchResult);
                    if(searchResult != null && searchResult.length > 0){
                        for(var i = 0; i < searchResult.length; i++) {
                            if(searchResult[i].dates != null) {
                                this.headerDatesAUM = searchResult[i].dates;
                                break;
                            }
                        }
                        this.parsedDataAUM = searchResult;
                    }

                },
                error =>  {
                    this.postAction(null, "Failed to search screening AUM list");

                }
            );
    }

    getTotalParsedDataRecords(){
        return this.screening.parsedData != null ? this.screening.parsedData.length : 0;
    }

    getTotalParsedReturnsDataRecords(){
        return this.parsedDataReturns != null ? this.parsedDataReturns.length : 0;
    }

    getTotalParsedAUMDataRecords(){
        return this.parsedDataAUM != null ? this.parsedDataAUM.length : 0;
    }

}