import {Component, OnInit} from "@angular/core";
import {CommonFormViewComponent} from "../common/common.component";
import { Http, Response } from '@angular/http';

import {Router, ActivatedRoute} from '@angular/router';
import {PeriodicReportService} from "./periodic.report.service";
import {PeriodicReport} from "./model/periodic.report";
import {ErrorResponse} from "../common/error-response";
import {Subscription} from "rxjs/Subscription";

declare var $:any

@Component({
    selector: 'main-nb-reporting',
    templateUrl: 'view/main.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class MainNBReportingComponent extends CommonFormViewComponent implements OnInit{

    constructor(
        private router: Router,
        private periodicReportService: PeriodicReportService
    ){
        super(router);
    }
    private reportMonth;
    private reportYear;

    private report;
    private reportList: PeriodicReport[];

    busy: Subscription;

    ngOnInit():void {
        this.busy = this.periodicReportService.loadAll()
            .subscribe(
                response  => {
                    this.reportList = response;
                },
                (error: ErrorResponse) => {
                    this.successMessage = null;
                    this.errorMessage = "Error loading reports";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            )
    }

    canEdit(){
        // TODO: check rights
        return true;
    }

    createNewReport(){
        console.log(this.reportMonth);
        console.log(this.reportYear);

        this.report = new PeriodicReport();
        this.report.reportDate = '01-' + this.reportMonth + '-' + this.reportYear;

        if(this.reportList && this.reportList.length > 0){
            for(var i = 0; i < this.reportList.length; i++){
                if(this.report.reportDate === this.reportList[i].reportDate){
                    this.errorMessage = "Report date already exists";
                    this.report = null;
                    return false;
                }
            }
        }

        // TODO: refactor
        this.report.type = 'NB_NICK';
        this.report.status = 'NEW';

        console.log(this.report);
        this.periodicReportService.save(this.report)
            .subscribe(
                response  => {
                    this.report.id = response.entityId;
                    var creationDate = response.creationDate;

                    this.successMessage = "New Report Created";
                    this.errorMessage = null;
                    this.reportMonth = null;
                    this.reportYear = null;
                },
                (error: ErrorResponse) => {
                    this.successMessage = null;
                    this.errorMessage = "Error creating new report";
                    this.report = null;
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
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
                        this.successMessage = null;
                        this.errorMessage = "Error loading reports";
                        this.report = null;
                        if(error && !error.isEmpty()){
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, null);
                    }
                )
        }
        this.report = null;
    }

    public getReportDateShortFormatted(reportDate){
        if(reportDate){
            var monthNum = reportDate.split("-")[1];
            var yearNum = reportDate.split("-")[2];
            if(monthNum === '01'){
                return 'JAN ' + yearNum;
            }else if(monthNum === '02'){
                return 'FEB ' + yearNum;
            }else if(monthNum === '03'){
                return 'MAR ' + yearNum;
            }else if(monthNum === '04'){
                return 'APR ' + yearNum;
            }else if(monthNum === '05'){
                return 'MAY ' + yearNum;
            }else if(monthNum === '06'){
                return 'JUN ' + yearNum;
            }else if(monthNum === '07'){
                return 'JUL ' + yearNum;
            }else if(monthNum === '08'){
                return 'AUG ' + yearNum;
            }else if(monthNum === '09'){
                return 'SEP ' + yearNum;
            }else if(monthNum === '10'){
                return 'OCT ' + yearNum;
            }else if(monthNum === '11'){
                return 'NOV ' + yearNum;
            }else if(monthNum === '12'){
                return 'DEC ' + yearNum;
            }
        }
        return "";
    }

    navigate(reportId){
        this.router.navigate(['/reporting/NBReporting/inputFileUpload/', reportId]);
    }

}