import {Component, OnInit} from "@angular/core";
import {CommonFormViewComponent} from "../common/common.component";
import { Http, Response } from '@angular/http';

import {Router, ActivatedRoute} from '@angular/router';
import {PeriodicReportService} from "./periodic.report.service";
import {PeriodicReport} from "./model/periodic.report";
import {ErrorResponse} from "../common/error-response";
import {Subscription} from "rxjs/Subscription";
import {CommonNBReportingComponent} from "./common.nb.reporting.component";
import {OKResponse} from "../common/ok-response";
import {SaveResponse} from "../common/save-response";

declare var $:any

@Component({
    selector: 'main-nb-reporting',
    templateUrl: 'view/main.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class MainNBReportingComponent extends CommonNBReportingComponent implements OnInit{

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService
    ){
        super(router, route, periodicReportService);
    }
    private reportMonth;
    private reportYear;

    private report;
    private reportList: PeriodicReport[];

    busy, busyCreate: Subscription;

    ngOnInit():void {
        this.busy = this.periodicReportService.loadAll()
            .subscribe(
                response  => {
                    this.reportList = response;
                },
                (error: ErrorResponse) => {
                    this.processErrorResponse(error);
                }
            )
    }

    canEdit(){
        // TODO: check rights
        return true;
    }

    createNewReport(){
        this.report = new PeriodicReport();
        this.report.reportDate = '01-' + this.reportMonth + '-' + this.reportYear;
        if(this.reportList && this.reportList.length > 0){
            for(var i = 0; i < this.reportList.length; i++){
                if(this.report.reportDate.substring(3) === this.reportList[i].reportDate.substring(3)){
                    this.errorMessage = "Report date already exists";
                    this.report = null;
                    return false;
                }
            }
        }

        // TODO: refactor
        this.report.type = 'NB_NICK';
        this.report.status = 'NEW';

        this.busyCreate = this.periodicReportService.save(this.report)
            .subscribe(
                (response: SaveResponse)  => {
                    this.report.id = response.entityId;
                    this.successMessage = response.message.nameEn != null ? response.message.nameEn : "Successfully saved report";
                    this.errorMessage = null;
                    this.reportMonth = null;
                    this.reportYear = null;

                    this.postAction(this.successMessage, this.errorMessage);
                },
                (error) => {
                    this.processErrorResponse(error);
                }
            )
    }

    closeModal(){
        //console.log("close modal");
        $('#newReportModal').modal('toggle');

        this.reportMonth = null;
        this.reportYear = null;
        this.errorMessage = null;
        this.successMessage = null;


        if(this.report != null && this.report.id > 0){
            //console.log("reloading list...");
            this.busy = this.periodicReportService.loadAll()
                .subscribe(
                    response  => {
                        this.reportList = response;
                        this.report = null;
                    },
                    (error: ErrorResponse) => {
                        this.processErrorResponse(error);
                        //this.successMessage = null;
                        //this.errorMessage = "Error loading reports";
                        //this.report = null;
                        //if(error && !error.isEmpty()){
                        //    this.processErrorMessage(error);
                        //}
                        //this.postAction(null, null);
                    }
                )
        }
        this.report = null;
    }

    navigate(reportId){
        this.router.navigate(['/reporting/NBReporting/inputFileUpload/', reportId]);
    }

}