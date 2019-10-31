import {Component} from "@angular/core";
import {CommonFormViewComponent} from "../common/common.component";
import {Router, ActivatedRoute} from '@angular/router';
import {InputFilesNBReport} from "./model/input.files.nb.report";
import {Subscription} from "rxjs/Subscription";
import {PeriodicReportService} from "./periodic.report.service";
import {LookupService} from "../common/lookup.service";
import {ErrorResponse} from "../common/error-response";
import {DATA_APP_URL} from "../common/common.service.constants";
import {PeriodicReportRecordHolder} from "./model/periodic.report.record.holder";
import {PeriodicReportTarragonStatementBalanceOperationsRecordHolder} from "./model/periodic.report.tarragon.statement.balance.operations.record.holder";
import {CommonNBReportingComponent} from "./common.nb.reporting.component";

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/forkJoin';

@Component({
    selector: 'schedule.investments.nb.reporting',
    templateUrl: 'view/statement.balance.operations.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class StatementBalanceOperationsNBReportingComponent extends CommonNBReportingComponent {

    private sub: any;
    private reportId;

    trancheTypes;

    recordsHolder: PeriodicReportTarragonStatementBalanceOperationsRecordHolder;
    report;
    prevPageSource;

    busy: Subscription;

    records = {};


    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService,
        private lookupService: LookupService
    ){
        super(router, route, periodicReportService);
        Observable.forkJoin(
                    // Load lookups
                    this.lookupService.getPETrancheTypes())
                           .subscribe(
                               ([data]) => {
                                   this.trancheTypes = data;

                                   this.sub = this.route
                                               .params
                                               .subscribe(params => {
                                                   this.reportId = +params['id'];
                                                   this.prevPageSource = params['prevPageSource'];

                                                   if(this.reportId > 0){
                                                       // load report data

                                                       this.busy = this.periodicReportService.getStatementBalanceOperations(this.reportId)
                                                           .subscribe(
                                                               response  => {
                                                                   console.log(response);
                                                                   this.recordsHolder = response;
                                                                   this.report = response.report;
                                                               },
                                                               (error: ErrorResponse) => {
                                                                   this.successMessage = null;
                                                                   this.errorMessage = "Error loading statement of balance and operations";
                                                                   if(error && !error.isEmpty()){
                                                                       this.processErrorMessage(error);
                                                                   }
                                                                   this.postAction(null, null);
                                                               }
                                                           );
                                                   }else{
                                                       // TODO: ??
                                                   }
                                               });
                           });

    }



    getBalanceRecordsByTrancheName(trancheName){
        if(this.recordsHolder != null && this.recordsHolder.balanceRecords != null){
            for(var i = 0; i < this.recordsHolder.balanceRecords.length; i++){
                if(this.recordsHolder.balanceRecords[i].trancheType != null && this.recordsHolder.balanceRecords[i].trancheType.nameEn === trancheName){
                    return this.recordsHolder.balanceRecords[i].records;
                }
            }
        }
        return [];
    }

    getOperationsRecordsByTrancheName(trancheName){
        if(this.recordsHolder != null && this.recordsHolder.operationsRecords != null){
            for(var i = 0; i < this.recordsHolder.operationsRecords.length; i++){
                if(this.recordsHolder.operationsRecords[i].trancheType != null && this.recordsHolder.operationsRecords[i].trancheType.nameEn === trancheName){
                    return this.recordsHolder.operationsRecords[i].records;
                }
            }
        }
        return [];
    }
}