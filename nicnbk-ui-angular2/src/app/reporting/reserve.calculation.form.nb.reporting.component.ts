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
import {GeneratedGLFormRecord} from "./model/generated.form.record";
import {TarragonNICReportingChartOfAccounts} from "./model/tarragon,.nic.reporting.chart.of.accounts.";
import {PEGeneralLedgerFormDataHolder} from "./model/pe.general.ledger.form.data.holder.nb.reporting";
import {PEGeneralLedgerFormDataRecord} from "./model/pe.general.ledger.form.data.record";
import readConfigFile = ts.readConfigFile;
import {ReserveCalculationFormRecord} from "./model/reserve.calculation.form.record";

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/forkJoin';

declare var $:any

@Component({
    selector: 'reserve-calculation-form-nb-reporting',
    templateUrl: 'view/reserve.calculation.form.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class ReserveCalculationFormNBReportingComponent extends CommonNBReportingComponent implements OnInit {

    private reportId;

    busy: Subscription;

    private records: ReserveCalculationFormRecord[]
    private addedRecords: ReserveCalculationFormRecord[];

    private expenseTypeLookup: BaseDictionary[];
    private entityTypeLookup: BaseDictionary[];


    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService,
        private lookupService: LookupService
    ){
        super(router, route, periodicReportService);

        Observable.forkJoin(
            // Load lookups
            this.lookupService.getReserveCalculationExpenseTypeLookup(),
            this.lookupService.getReserveCalculationEntityTypeLookup()
            )
            .subscribe(
                ([data1, data2]) => {
                    this.expenseTypeLookup = data1;
                    this.entityTypeLookup = data2;
                    //console.log(this.expenseTypeLookup);
                    //console.log(this.entityTypeLookup);

                    this.records = [];
                    this.addedRecords = [];

                    this.busy = this.periodicReportService.getReserveCalculationFormData()
                        .subscribe(
                            response  => {
                                if(response){
                                    this.records = response;
                                }
                            },
                            (error: ErrorResponse) => {
                                this.successMessage = null;
                                this.errorMessage = "Error loading data";
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, this.errorMessage);
                            }
                        );

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



    addRecord(){
        this.addedRecords.push(new ReserveCalculationFormRecord());
        $('.datepicker').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
    }


    removeRecord(addedRecord){
        var confirmed = window.confirm("Are you sure want to delete record?");
        if(confirmed) {
            if (this.addedRecords) {
                for (var i = this.addedRecords.length; i--;) {
                    if (this.addedRecords[i] === addedRecord) {
                        this.addedRecords.splice(i, 1);
                    }
                }
            }
        }
    }

    removeSavedRecord(record){
        var confirmed = window.confirm("Are you sure want to delete record?");
        if(confirmed) {
            this.periodicReportService.deleteReserveCalculationFormDataRecord(record.addedRecordId)
                .subscribe(
                    response => {
                        // get tarragon records
                        this.busy = this.periodicReportService.getReserveCalculationFormData()
                            .subscribe(
                                response  => {
                                    if(response){
                                        this.records = response;
                                        this.postAction("Record successfully deleted", this.errorMessage);
                                    }
                                },
                                (error: ErrorResponse) => {
                                    this.successMessage = null;
                                    this.errorMessage = "Error loading data";
                                    if(error && !error.isEmpty()){
                                        this.processErrorMessage(error);
                                    }
                                    this.postAction(null, this.errorMessage);
                                }
                            );

                    },
                    (error:ErrorResponse) => {
                        this.errorMessage = "Error deleting record";
                        if (error && !error.isEmpty()) {
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, this.errorMessage);
                    }
                );
        }
    }

    saveAddedRecords(){

        // TODO: add validation

        if(this.addedRecords != null){
            for(var i = 0; i < this.addedRecords.length; i++){
                this.addedRecords[i].date = $('#dateVal').val();
            }
        }

        this.periodicReportService.saveReserveCalculationFormData(this.addedRecords)
            .subscribe(
                response  => {
                    this.errorMessage = null;
                    this.addedRecords = [];

                    this.busy = this.periodicReportService.getReserveCalculationFormData()
                        .subscribe(
                            response  => {
                                if(response){
                                    this.records = response;
                                    this.postAction("Records successfully saved", this.errorMessage);
                                }
                            },
                            (error: ErrorResponse) => {
                                this.successMessage = null;
                                this.errorMessage = "Error loading records";
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, this.errorMessage);
                            }
                        );
                },
                (error: ErrorResponse) => {
                    this.successMessage = null;
                    this.errorMessage = "Error saving new records";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }else {
                        this.postAction(null, this.errorMessage);
                    }
                }
            )
    }

}