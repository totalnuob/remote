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
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";

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

    editSuccessMessage: string;
    editErrorMessage: string;

    searchParams = new ReserveCalculationSearchParams();
    searchResult: ReserveCalculationSearchResults;
    totalSumRecord = {"amount": 0, "amountToSPV": 0};

    private records: ReserveCalculationFormRecord[]
    //private addedRecords: ReserveCalculationFormRecord[];

    private editedRecord: ReserveCalculationFormRecord;

    private expenseTypeLookup: BaseDictionary[];
    private entityTypeLookup: BaseDictionary[];
    private entityTypeLookupNonDeleted: BaseDictionary[];


    private exportDirectorLookup: BaseDictionary[];
    private exportDoerLookup: BaseDictionary[];
    private exportApproveListLookup: BaseDictionary[];

    private exportDirectorOption;
    private exportDoerOption;
    private exportApproveList = [];

    public uploadFiles: Array<any> = [];

    private moduleAccessChecker: ModuleAccessCheckerService;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService,
        private lookupService: LookupService
    ){
        super(router, route, periodicReportService);

        this.moduleAccessChecker = new ModuleAccessCheckerService;

        this.records = [];
        this.searchParams = new ReserveCalculationSearchParams();

        this.busy = Observable.forkJoin(
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
                    if(this.entityTypeLookup != null){
                        this.entityTypeLookupNonDeleted = [];
                        for(var i = 0; i < this.entityTypeLookup.length; i++){
                            if(this.entityTypeLookup[i].deleted == null || !this.entityTypeLookup[i].deleted){
                                this.entityTypeLookupNonDeleted.push(this.entityTypeLookup[i]);
                            }
                        }
                    }
                    console.log(this.entityTypeLookupNonDeleted);
                    this.exportDirectorLookup = data3;
                    this.exportDoerLookup = data4;
                    this.exportApproveListLookup =  data5;
                    if(this.exportApproveListLookup != null && this.exportApproveListLookup.length > 1){
                        this.exportApproveList.push(this.exportApproveListLookup[1].code);
                    }
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
        $('#fromDateDTPickeer').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
        $('#untilDateDTPickeer').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
    }

    clearSearchForm(){
        this.searchParams = new ReserveCalculationSearchParams();
    }

    search(page?){
        this.searchParams.pageSize = 20;
        if(page) {
            this.searchParams.page = page;
        }else{
            this.searchParams.page = 0;
        }
        if(this.searchParams.expenseType != null && this.searchParams.expenseType === 'NONE'){
            this.searchParams.expenseType = null;
        }
        if(this.searchParams.sourceType != null && this.searchParams.sourceType === 'NONE'){
            this.searchParams.sourceType = null;
        }
        if(this.searchParams.destinationType != null && this.searchParams.destinationType === 'NONE'){
            this.searchParams.destinationType = null;
        }
        this.searchParams.dateFrom = $('#fromDate').val();
        this.searchParams.dateTo = $('#toDate').val();
        console.log(this.searchParams);
        this.busy = this.periodicReportService.searchReserveCalculations(this.searchParams)
            .subscribe(
                searchResult  => {
                    this.records = searchResult.records;
                    this.totalSumRecord = {"amount": 0, "amountToSPV": 0};
                    if(this.records != null){
                        for(var i = 0; i < this.records.length; i++){
                            this.totalSumRecord.amount += Number(this.records[i].amount);
                            this.totalSumRecord.amountToSPV += Number(this.records[i].amountToSPV);
                            this.onNumberChange(this.records[i]);
                        }
                        this.onNumberChange(this.totalSumRecord);
                    }
                    this.searchResult = searchResult;

                    this.checkRecords();
                    if(this.exportDirectorLookup != null && this.exportDirectorLookup.length > 0){
                        this.exportDirectorOption = this.exportDirectorLookup[0].code;
                    }

                    if(this.exportDoerLookup != null && this.exportDoerLookup.length > 0){
                        this.exportDoerOption = this.exportDoerLookup[0].code;
                    }

                    //if(this.exportApproveListLookup && this.exportApproveListLookup.length > 1) {
                    //    this.exportApproveList.push(this.exportApproveListLookup[0].code);
                    //    this.exportApproveList.push(this.exportApproveListLookup[1].code);
                    //}
                    //if(this.exportApproveListLookup && this.exportApproveListLookup.length > 2) {
                    //    this.exportApproveList.push(this.exportApproveListLookup[2].code);
                    //}
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
                    this.postAction(this.successMessage, "Record missing value for column '????????'. Please check currency rate for the record date.");
                    return;
                }
            }
        }
    }

    addRecord(){
        this.editedRecord = new ReserveCalculationFormRecord();
        this.editedRecord.canDelete = true;
        if(this.entityTypeLookup)

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

    //removeRecord(addedRecord){
    //    var confirmed = window.confirm("Are you sure want to delete record?");
    //    if(confirmed) {
    //        this.addedRecord = null;
    //    }
    //}

    editRecord(record){
        this.editedRecord = record;

        console.log(this.editedRecord.files);
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

    saveAddedRecord(){

        if($('#dateInputId2').val() === '') {
            if (!confirm("Value '???????? ??????????????????????????' is empty. Value '????????' will be used for export. Ok?")) {
                return;
            }
        }

        if(this.editedRecord != null){
            this.editedRecord.date = $('#dateInputId').val();
            this.editedRecord.valueDate = $('#dateInputId2').val();

            if(this.editedRecord.date == null || this.editedRecord.date === ''){
                this.postAction(null, "Missing value '????????'");
                return;
            }

            if(this.editedRecord.amount) {
                this.editedRecord.amount = Number(this.editedRecord.amount.toString().replace(/,/g, ''));
            }
            if(this.editedRecord.amountToSPV) {
                this.editedRecord.amountToSPV = Number(this.editedRecord.amountToSPV.toString().replace(/,/g, ''));
            }
            if(this.editedRecord.currencyRate) {
                this.editedRecord.currencyRate = Number(this.editedRecord.currencyRate.toString().replace(/,/g, ''));
            }
            if(this.editedRecord.amountKZT) {
                this.editedRecord.amountKZT = Number(this.editedRecord.amountKZT.toString().replace(/,/g, ''));
            }
        }

        this.periodicReportService.saveReserveCalculationRecord(this.editedRecord)
            .subscribe(
                response  => {

                    this.editedRecord.id = response.entityId;
                    this.editErrorMessage = null;
                    this.editSuccessMessage = "Record successfully saved";
                    this.onNumberChange(this.editedRecord);

                    if(this.uploadFiles != null && this.uploadFiles.length > 0) {
                        // TODO: refactor
                        this.periodicReportService.postReserveCalculationFiles(response.entityId, this.uploadFiles).subscribe(
                            res => {
                                // clear upload files list on view
                                this.uploadFiles = [];
                                for (var i = 0; i < res.length; i++) {
                                    this.editedRecord.files.push(res[i]);
                                }

                                //this.editedRecord = null;
                                //this.search(0);
                            },
                            (error: ErrorResponse) => {
                                this.errorMessage = "Error uploading attachments";
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, null);
                            });
                    }else{
                        this.errorMessage = null;
                        //this.editedRecord = null;
                        //this.search(0);
                    }

                },
                (error: ErrorResponse) => {
                    this.editSuccessMessage = null;
                    this.editErrorMessage = "Error saving new record";
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
        fileName = fileName.replace(".", ",");
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
        fileName = fileName.replace(".", ",");
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

    exportAdmFeeFAFToSPV(record){
        //var entity = record.recipient.code.startsWith('TARR') ? "Tarragon" : record.recipient.code.startsWith('SING') ? "Singularity" : "";
        var fileName = record.date.replace(/-/g, "_") + "-Order to BONY $ " + (record.amountToSPV != null ? record.amountToSPV : record.amount) ;
        fileName = fileName.replace(".", ",");
        var exportParams = {'director': this.exportDirectorOption, 'doer': this.exportDoerOption, 'approveList': this.exportApproveList};
        this.busyExport = this.periodicReportService.makeFileRequest(DATA_APP_URL + `periodicReport/reserveCalculation/export/${record.id}/${'ADM_FEE_SPV'}`,
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
        fileName = fileName.replace(".", ",");
        console.log(this.exportApproveList);
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
                if(record.amount != '-'){
                    record.amount = parseFloat(record.amount).toLocaleString('en', {maximumFractionDigits: 2});
                }
            }
        }
        if(record.amountToSPV != null && record.amountToSPV != 'undefined' && record.amountToSPV.toString().length > 0) {
            if(record.amountToSPV.toString()[record.amountToSPV.toString().length - 1] != '.' || record.amountToSPV.toString().split('.').length > 2){
                record.amountToSPV = record.amountToSPV.toString().replace(/,/g , '');
                if(record.amountToSPV != '-'){
                    record.amountToSPV = parseFloat(record.amountToSPV).toLocaleString('en', {maximumFractionDigits: 2});
                }

            }
        }
        if(record.currencyRate != null && record.currencyRate != 'undefined' && record.currencyRate.toString().length > 0) {
            if(record.currencyRate.toString()[record.currencyRate.toString().length - 1] != '.' || record.currencyRate.toString().split('.').length > 2){
                record.currencyRate = record.currencyRate.toString().replace(/,/g , '');
                if(record.currencyRate != '-'){
                    record.currencyRate = parseFloat(record.currencyRate).toLocaleString('en', {maximumFractionDigits: 2});
                }

            }
        }
        if(record.amountKZT != null && record.amountKZT != 'undefined' && record.amountKZT.toString().length > 0) {
            if(record.amountKZT.toString()[record.amountKZT.toString().length - 1] != '.' || record.amountKZT.toString().split('.').length > 2){
                record.amountKZT = record.amountKZT.toString().replace(/,/g , '');
                if(record.amountKZT != '-'){
                    record.amountKZT = parseFloat(record.amountKZT).toLocaleString('en', {maximumFractionDigits: 2});
                }

            }
        }
    }

    onFileChange(event) {
        var target = event.target || event.srcElement;
        var files = target.files;
        //this.uploadFiles.length = 0;
        this.uploadFiles = this.uploadFiles == null? [] : this.uploadFiles;
        for (let i = 0; i < files.length; i++) {
            this.uploadFiles.push(files[i]);
        }
    }

    closeEditRecordModal(){
        this.editErrorMessage = null;
        this.editSuccessMessage = null;
        this.editedRecord = new ReserveCalculationFormRecord();
        this.uploadFiles = [];
        this.search(0);
    }


    deleteAttachment(recordId, fileId){
        if(!this.moduleAccessChecker.checkAccessReportingEditor()){
            return;
        }
        var confirmed = window.confirm("Are you sure want to delete");
        if(confirmed) {
            this.periodicReportService.safeDeleteAttachment(recordId, fileId)
                .subscribe(
                    response => {
                        for(var i = this.editedRecord.files.length - 1; i >= 0; i--) {
                            if(this.editedRecord.files[i].id === fileId) {
                                this.editedRecord.files.splice(i, 1);
                            }
                        }

                        this.editErrorMessage = null;
                        this.editSuccessMessage = "Attachment deleted";
                    },
                    (error: ErrorResponse) => {
                        this.editErrorMessage = "Error deleting attachment";
                        if(error && !error.isEmpty()){
                            this.processErrorMessage(error);
                        }
                    }
                );
        }
    }

}