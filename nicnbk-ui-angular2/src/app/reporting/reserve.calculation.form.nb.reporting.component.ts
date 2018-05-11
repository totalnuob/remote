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
import {ReserveCalculationFormRecord} from "./model/reserve.calculation.form.record";

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/forkJoin';
import {ReserveCalculationSearchParams} from "./model/reserve-calculation-search-params";
import {ReserveCalculationSearchResults} from "./model/reserve-calculation-search-results";

declare var $:any

@Component({
    selector: 'reserve-calculation-form-nb-reporting',
    templateUrl: 'view/reserve.calculation.form.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class ReserveCalculationFormNBReportingComponent extends CommonNBReportingComponent implements OnInit {

    public sub: any;
    busy: Subscription;

    busyExport:Subscription;

    searchParams = new ReserveCalculationSearchParams();
    searchResult: ReserveCalculationSearchResults;

    private records: ReserveCalculationFormRecord[]
    private addedRecords: ReserveCalculationFormRecord[];

    private expenseTypeLookup: BaseDictionary[];
    private entityTypeLookup: BaseDictionary[];


    private exportDirectorLookup: BaseDictionary[];
    private exportDoerLookup: BaseDictionary[];
    private exportApproveListLookup: BaseDictionary[];

    private exportDirectorOption;
    private exportDoerOption;
    private exportApproveList = [];

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService,
        private lookupService: LookupService
    ){
        super(router, route, periodicReportService);

        this.records = [];
        this.addedRecords = [];

        Observable.forkJoin(
            // Load lookups
            this.lookupService.getReserveCalculationExpenseTypeLookup(),
            this.lookupService.getReserveCalculationEntityTypeLookup(),
            this.lookupService.getReserveCalculationExportSignerTypeLookup(),
            this.lookupService.getReserveCalculationExportDoerTypeLookup(),
            this.lookupService.getReserveCalculationExportApproveListTypeLookup()
            )
            .subscribe(
                ([data1, data2, data3, data4, data5]) => {
                    this.expenseTypeLookup = data1;
                    this.entityTypeLookup = data2;
                    this.exportDirectorLookup = data3;
                    this.exportDoerLookup = data4;
                    this.exportApproveListLookup =  data5;
                    this.sub = this.route
                        .params
                        .subscribe(params => {
                            if(params['params'] != null){
                                this.searchParams = JSON.parse(params['params']);
                                this.search();
                            } else {
                                this.search(0);
                            }
                        });

                    //this.busy = this.periodicReportService.getReserveCalculationFormData()
                    //    .subscribe(
                    //        response  => {
                    //            if(response){
                    //                this.records = response;
                    //                this.checkRecords();
                    //
                    //                if(this.exportDirectorLookup != null && this.exportDirectorLookup.length > 0){
                    //                    this.exportDirectorOption = this.exportDirectorLookup[0].code;
                    //                }
                    //
                    //                if(this.exportDoerLookup != null && this.exportDoerLookup.length > 0){
                    //                    this.exportDoerOption = this.exportDoerLookup[0].code;
                    //                }
                    //
                    //                if(this.exportApproveListLookup && this.exportApproveListLookup.length > 1) {
                    //                    this.exportApproveList.push(this.exportApproveListLookup[0].code);
                    //                    this.exportApproveList.push(this.exportApproveListLookup[1].code);
                    //                }
                    //                if(this.exportApproveListLookup && this.exportApproveListLookup.length > 2) {
                    //                    this.exportApproveList.push(this.exportApproveListLookup[2].code);
                    //                }
                    //                this.checkApproveList();
                    //            }
                    //        },
                    //        (error: ErrorResponse) => {
                    //            this.successMessage = null;
                    //            this.errorMessage = "Error loading data";
                    //            if(error && !error.isEmpty()){
                    //                this.processErrorMessage(error);
                    //            }
                    //            this.postAction(null, this.errorMessage);
                    //        }
                    //    );

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

    search(page?){
        this.searchParams.pageSize = 20;
        if(page) {
            this.searchParams.page = page;
        }

        this.busy = this.periodicReportService.searchReserveCalculations(this.searchParams)
            .subscribe(
                searchResult  => {
                    this.records = searchResult.records;
                    this.searchResult = searchResult;

                    this.checkRecords();

                    if(this.exportDirectorLookup != null && this.exportDirectorLookup.length > 0){
                        this.exportDirectorOption = this.exportDirectorLookup[0].code;
                    }

                    if(this.exportDoerLookup != null && this.exportDoerLookup.length > 0){
                        this.exportDoerOption = this.exportDoerLookup[0].code;
                    }

                    if(this.exportApproveListLookup && this.exportApproveListLookup.length > 1) {
                        this.exportApproveList.push(this.exportApproveListLookup[0].code);
                        this.exportApproveList.push(this.exportApproveListLookup[1].code);
                    }
                    if(this.exportApproveListLookup && this.exportApproveListLookup.length > 2) {
                        this.exportApproveList.push(this.exportApproveListLookup[2].code);
                    }
                    this.checkApproveList();

                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error searching records";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                    //alert(this.errorMessage);
                }
            );

    }

    checkRecords(){
        if(this.records != null && this.records.length > 0){
            for (var i = 0; i < this.records.length; i++) {
                if(this.records[i].currencyRate == null){
                    this.postAction(this.successMessage, "Record missing value for column 'Курс'. Please check currency rate for the record date.");
                    return;
                }
            }
        }
    }

    addRecord(){
        this.addedRecords.push(new ReserveCalculationFormRecord());

        // FIX, since ngFor rendering takes time, no element for datetimepicker() function call
        setTimeout(function(){
            $('#dateDivId').datetimepicker({
                //defaultDate: new Date(),
                format: 'DD-MM-YYYY'
            });


            $('#dateDivId2').datetimepicker({
                //defaultDate: new Date(),
                format: 'DD-MM-YYYY'
            });
        }, 500);

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

    editRecord(record){
        this.addedRecords = [];
        this.addedRecords.push(record);
    }

    showEditRecordButton(record){
        return record.canDelete;
    }

    removeSavedRecord(record){
        var confirmed = window.confirm("Are you sure want to delete record?");
        if(confirmed) {
            this.periodicReportService.deleteReserveCalculationFormDataRecord(record.id)
                .subscribe(
                    response => {
                        // get tarragon records
                        this.busy = this.periodicReportService.getReserveCalculationFormData()
                            .subscribe(
                                response  => {
                                    if(response){
                                        this.records = response;
                                        this.postAction("Record successfully deleted", null);
                                        this.checkRecords();
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

                        this.processErrorResponse(error);

                    }
                );
        }
    }

    saveAddedRecords(){

        if($('#dateInputId2').val() === '') {
            if (!confirm("Value 'Дата валютирования' is empty. Value 'Дата' will be used for export. Ok?")) {
                return;
            }
        }

        if(this.addedRecords != null){
            for(var i = 0; i < this.addedRecords.length; i++){
                this.addedRecords[i].date = $('#dateInputId').val();
                this.addedRecords[i].valueDate = $('#dateInputId2').val();

                if(this.addedRecords[i].date == null || this.addedRecords[i].date === ''){
                    this.postAction(null, "Missing value 'Дата'");
                    return;
                }

                if(this.addedRecords[i].amount) {
                    this.addedRecords[i].amount = Number(this.addedRecords[i].amount.toString().replace(/,/g, ''));
                }
                if(this.addedRecords[i].amountToSPV) {
                    this.addedRecords[i].amountToSPV = Number(this.addedRecords[i].amountToSPV.toString().replace(/,/g, ''));
                }
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
                                    this.postAction("Records successfully saved", null);
                                    this.checkRecords();
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

    public checkApproveListOption(item, checked){
        if(checked) {
            this.exportApproveList.push(item.code);
        }else{
            for (var i = 0; i < this.exportApproveList.length; i++) {
                if (this.exportApproveList[i] === item.code) {
                    this.exportApproveList.splice(i, 1);
                }
            }
        }

        this.checkApproveList();

    }

    public checkApproveList(){
        if(this.exportApproveList.length == 3) {
            // disable unchecked
            var elements = $('[name="approvelistCheckbox"]');
            for (var i = 0; i < elements.length; i++) {
                if(!elements[i].checked){
                    elements[i].disabled = true;
                }
            }
        }else{
            var elements = $('[name="approvelistCheckbox"]');
            for (var i = 0; i < elements.length; i++) {
                if(elements[i].disabled){
                    elements[i].disabled = false;
                }
            }
        }
    }

    public exportFAFToOperations(record){
        var fileName = record.date.replace(/-/g, "_") + "-Order $ " + record.amount + " TA-OA";

        var exportParams = {'director': this.exportDirectorOption, 'doer': this.exportDoerOption, 'approveList': this.exportApproveList};
        this.busyExport = this.periodicReportService.makeFileRequest(DATA_APP_URL + `periodicReport/reserveCalculation/export/${record.id}/${'OPs'}`,
            fileName, 'POST', exportParams)
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

    public exportFAFToSPV(record){
        console.log(record.recipient.code);
        var entity = record.recipient.code.startsWith('TARR') ? "Tarragon" : record.recipient.code.startsWith('SING') ? "Singularity" : "";
        var fileName = record.date.replace(/-/g, "_") + "-Order to $ " + (record.amountToSPV != null ? record.amountToSPV : record.amount) + " OA-" + entity;

        var exportParams = {'director': this.exportDirectorOption, 'doer': this.exportDoerOption, 'approveList': this.exportApproveList};
        this.busyExport = this.periodicReportService.makeFileRequest(DATA_APP_URL + `periodicReport/reserveCalculation/export/${record.id}/${'SPV'}`,
            fileName, 'POST', exportParams)
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

    public exportOrder(record){
        var fileName = record.date.replace(/-/g, "_") + "-Letter of Direction $ " + record.amount;
        var exportParams = {'director': this.exportDirectorOption, 'doer': this.exportDoerOption, 'approveList': this.exportApproveList};
        this.busyExport = this.periodicReportService.makeFileRequest(DATA_APP_URL + `periodicReport/reserveCalculation/export/${record.id}/${'ORDER'}`,
            fileName, 'POST', exportParams)
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

    public showDeleteRecordButton(record: ReserveCalculationFormRecord){
        //return true;
        return record.canDelete;
    }

    public showExportOrderButton(record: ReserveCalculationFormRecord){
        return record.expenseType.code === 'ADD' && (record.recipient.code.startsWith('TARR') || record.recipient.code.startsWith('SING')
            || record.recipient.code.startsWith('TERRA'));
    }

    public onNumberChange(record){
        if(record.amount != null && record.amount != 'undefined' && record.amount.toString().length > 0) {
            if(record.amount.toString()[record.amount.toString().length - 1] != '.' || record.amount.toString().split('.').length > 2){
                record.amount = record.amount.toString().replace(/,/g , '');
                record.amount = parseFloat(record.amount).toLocaleString('en', {maximumFractionDigits: 2});
            }
        }
        if(record.amountToSPV != null && record.amountToSPV != 'undefined' && record.amountToSPV.toString().length > 0) {
            if(record.amountToSPV.toString()[record.amountToSPV.toString().length - 1] != '.' || record.amountToSPV.toString().split('.').length > 2){
                record.amountToSPV = record.amountToSPV.toString().replace(/,/g , '');
                record.amountToSPV = parseFloat(record.amountToSPV).toLocaleString('en', {maximumFractionDigits: 2});
            }
        }
    }

}