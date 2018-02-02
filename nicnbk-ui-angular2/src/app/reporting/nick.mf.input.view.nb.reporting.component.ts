import {Component, OnInit} from "@angular/core";
import {Router, ActivatedRoute} from '@angular/router';
import {Subscription} from "rxjs/Subscription";
import {ErrorResponse} from "../common/error-response";
import {PeriodicReportService} from "./periodic.report.service";
import {OtherInfoNBReporting} from "./model/other.info.nb.reporting";
import {CommonNBReportingComponent} from "./common.nb.reporting.component";
import {DATA_APP_URL} from "../common/common.service.constants";
import {NICKMFReportingInfo} from "./model/nick.mf.reporting.info.nb.reporting";
import {LookupService} from "../common/lookup.service";
import {BaseDictionary} from "../common/model/base-dictionary";
import {NICReportingChartOfAccounts} from "./model/nic.reporting.chart.of.accounts.";
import {NICKMFReportingInfoHolder} from "./model/nick.mf.reporting.info.holder.nb.reporting";
import {PeriodicReport} from "./model/periodic.report";
import {isNumeric} from "rxjs/util/isNumeric";

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/forkJoin';

declare var $:any

@Component({
    selector: 'mick-mf-input-view-nb-reporting',
    templateUrl: 'view/nick.mf.input.view.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class NICKMFInputViewNBReportingComponent extends CommonNBReportingComponent implements OnInit {

    private sub: any;
    private reportId;

    busy: Subscription;

    private data: NICKMFReportingInfoHolder;
    private report: PeriodicReport;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService
    ){
        super(router, route, periodicReportService);

        this.data = new NICKMFReportingInfoHolder();
        this.data.records = [];

        // load data
        this.sub = this.route
            .params
            .subscribe(params => {
                this.reportId = +params['id'];
                if(this.reportId > 0){
                    // load report data

                    this.data.report = new PeriodicReport();
                    this.data.report.id = this.reportId;

                    this.busy = this.periodicReportService.getNICKMFReportingInfo(this.reportId)
                        .subscribe(
                            response  => {
                                if(response){
                                    this.data.records = response.records;
                                    this.report = response.report;
                                }
                            },
                            (error: ErrorResponse) => {
                                this.successMessage = null;
                                this.errorMessage = "Error loading NICK MF data";
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                }
                                this.postAction(this.successMessage, this.errorMessage);
                            }
                        );
                }else{
                    // TODO: ??
                    console.log("No report id")
                }
            });

    }

    ngOnInit(): any {
    }

    calculateTotal(){
        if(this.data.records){
            var sum = 0.0;
            for(var i = 0; i < this.data.records.length; i++){
                if(isNumeric(this.data.records[i].accountBalance)){
                    sum += Number(this.data.records[i].accountBalance);
                }else{
                }
            }
            return sum;
        }
        return 0;
    }

}