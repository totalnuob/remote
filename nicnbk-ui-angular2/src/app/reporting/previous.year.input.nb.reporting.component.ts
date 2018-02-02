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
import {ConsolidatedBalanceFormRecord} from "./model/consolidated.balance.form.record";
import {PreviousYearInputRecord} from "./model/previous.year.input.record";

declare var $:any

@Component({
    selector: 'previous-year-input-nb-reporting',
    templateUrl: 'view/previous.year.input.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class PreviousYearInputNBReportingComponent extends CommonNBReportingComponent implements OnInit {

    private sub: any;
    private reportId;

    busy: Subscription;

    //private nbChartOfAccounts: BaseDictionary[];
    private nicReportingChartOfAccounts: NICReportingChartOfAccounts[];

    private records: PreviousYearInputRecord[];

    private recordsValid: boolean;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private lookupService: LookupService,
        private periodicReportService: PeriodicReportService
    ){
        super(router, route, periodicReportService);

        this.records = [];

        Observable.forkJoin(
            // Load lookups
            //this.lookupService.getNBChartOfAccounts(),
            this.lookupService.getNICReportingChartOfAccounts(null)
            )
            .subscribe(
                ([data2]) => {
                    //this. nbChartOfAccounts = data1;
                    this.nicReportingChartOfAccounts = data2;
                    //console.log(this.nicReportingChartOfAccounts);

                    // load data
                    this.sub = this.route
                        .params
                        .subscribe(params => {
                            this.reportId = +params['id'];
                            if(this.reportId > 0){
                                // load report data

                                this.busy = this.periodicReportService.getPreviousYearInput(this.reportId)
                                    .subscribe(
                                        response  => {
                                            if(response){
                                                this.records = response;
                                                this.checkRecords();
                                            }
                                        },
                                        (error: ErrorResponse) => {
                                            this.processErrorResponse(error);
                                            //this.successMessage = null;
                                            //this.errorMessage = "Error loading data";
                                            //if(error && !error.isEmpty()){
                                            //    this.processErrorMessage(error);
                                            //}
                                            //this.postAction(this.successMessage, this.errorMessage);
                                        }
                                    );
                            }else{
                                // TODO: ??
                                console.log("No report id")
                            }
                        });

                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error loading lookups";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, this.errorMessage);
                }
            );

    }

    ngOnInit(): any {
    }

    save(){
        this.periodicReportService.savePreviousYearInput(this.records, this.reportId)
            .subscribe(
                response  => {
                    var creationDate = response.creationDate;
                    this.checkRecords();

                    this.successMessage = "Successfully saved records";
                    this.postAction(this.successMessage, this.errorMessage);
                },
                (error: ErrorResponse) => {
                    this.processErrorMessage(error);
                    //this.successMessage = null;
                    //this.errorMessage = "Error saving records";
                    //if(error && !error.isEmpty()){
                    //    this.processErrorMessage(error);
                    //}
                    //this.postAction(this.successMessage, this.errorMessage);
                }
            )
    }

    addRecord(){
        let newItem = new PreviousYearInputRecord(this.reportId);
        this.records.push(newItem);
        this.successMessage = null;
    }

    removeRecord(record){
        var confirmed = window.confirm("Are you sure want to delete record?");
        if(confirmed) {
            if (this.records) {
                for (var i = this.records.length; i--;) {
                    if (this.records[i] === record) {
                        this.records.splice(i, 1);
                    }
                }
            }
        }
    }

    copyFromPrevious(){
        this.busy = this.periodicReportService.getPreviousYearInputDataFromPreviousMonth(this.reportId)
            .subscribe(
                response  => {
                    console.log(response);
                    if(response && response.length > 0) {
                        for (var i = 0; i < response.length; i++) {
                            this.records.push(response[i]);
                        }
                        this.postAction("Successfully loaded " + response.length + " records from previous month", null);
                    }else {
                        this.postAction("No records were loaded from previous month", null);
                    }
                },
                (error: ErrorResponse) => {
                    this.processErrorResponse(error);
                    //this.successMessage = null;
                    //this.errorMessage = "Error loading previous year input data from previous month";
                    //if(error && !error.isEmpty()){
                    //    this.processErrorMessage(error);
                    //}
                    //this.postAction(this.successMessage, this.errorMessage);
                }
            );
    }

    public showNextButton(){
        return this.recordsValid;
    }

    public checkRecords(){

        var check5440_010 = false;
        //var name5440_010 = 'Резерв по переоценке финансовых инвестиций, имеющихся в наличии для продажи (прошлый год)';

        var check5520_010 = false;
        //var name5520_010 = 'Нераспределенная прибыль (непокрытый убыток) прошлых лет';
        if (this.records) {
            for (var i = this.records.length; i--;) {
                if (this.records[i].chartOfAccounts != null && this.records[i].chartOfAccounts.code.startsWith('5440.010')){
                    check5440_010 = true;
                }else if (this.records[i].chartOfAccounts != null && this.records[i].chartOfAccounts.code.startsWith('5520.010')){
                    check5520_010 = true;
                }
            }
        }


        if(!check5440_010){
            this.errorMessage = "Missing '5440.010' record.";
        }else if(!check5520_010){
            this.errorMessage = "Missing '5520.010' record.";
        }

        this.recordsValid = (check5440_010 && check5520_010);

        if(this.recordsValid){
            this.errorMessage = null;
        }
    }

}