import {Component, OnInit} from "@angular/core";
import {Router, ActivatedRoute} from '@angular/router';
import {Subscription} from "rxjs/Subscription";
import {ErrorResponse} from "../common/error-response";
import {PeriodicReportService} from "./periodic.report.service";
import {CommonNBReportingComponent} from "./common.nb.reporting.component";
import {DATA_APP_URL} from "../common/common.service.constants";
import {LookupService} from "../common/lookup.service";
import {BaseDictionary} from "../common/model/base-dictionary";
import {NICReportingChartOfAccounts} from "./model/nic.reporting.chart.of.accounts.";
import {PeriodicReport} from "./model/periodic.report";
import {isNumeric} from "rxjs/util/isNumeric";

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/forkJoin';
import {ListResponse} from "./../common/list.response.ts";
import {TerraNICReportingChartOfAccounts} from "./model/terra,.nic.reporting.chart.of.accounts.";
import {REGeneralLedgerFormDataHolder} from "./model/re.general.ledger.form.data.holder.nb.reporting";
import {REGeneralLedgerFormDataRecord} from "./model/re.general.ledger.form.data.record";
import {TerraGeneratedGLFormRecord} from "./model/terra.generated.gl.form.record";

declare var $:any

@Component({
    selector: 'terra-generated-form-nb-reporting',
    templateUrl: 'view/terra.generated.form.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class TerraGeneratedFormNBReportingComponent extends CommonNBReportingComponent implements OnInit {

    private sub: any;
    private reportId;

    busy: Subscription;

    private records: TerraGeneratedGLFormRecord[];
    private terraNICReportingChartOfAccounts: TerraNICReportingChartOfAccounts[];
    private addedRecordsHolder: REGeneralLedgerFormDataHolder;

    private availableFundList: string[];

    totalAssetsSum = 0.0;
    totalOtherSum = 0.0;

    recordsValid = true;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService,
        private lookupService: LookupService
    ){
        super(router, route, periodicReportService);

        //this.loadLookups();
        //

        this.records = [];
        this.addedRecordsHolder = new REGeneralLedgerFormDataHolder();
        this.addedRecordsHolder.records = [];

        Observable.forkJoin(
            // Load lookups
            this.lookupService.getAddableTerraNICReportingChartOfAccounts()
            )
            .subscribe(
                ([data]) => {
                    this.terraNICReportingChartOfAccounts = data;

                    this.sub = this.route
                        .params
                        .subscribe(params => {
                            this.reportId = +params['id'];
                            if (this.reportId > 0) {
                                // load report data
                                this.addedRecordsHolder.report = new PeriodicReport();
                                this.addedRecordsHolder.report.id = this.reportId;

                                this.busy = this.periodicReportService.getGeneratedTerraForm(this.reportId)
                                    .subscribe(
                                        (response: ListResponse) => {
                                            if (response) {
                                                this.records = response.records;
                                                if (response.status === 'FAIL') {
                                                    if(response.message != null){
                                                        this.errorMessage = response.message.nameEn ? response.message.nameEn :
                                                            response.message.nameKz ? response.message.nameKz : response.message.nameRu ? response.message.nameRu : null;
                                                    }
                                                    if(this.errorMessage == null){
                                                        this.errorMessage = "Error loading Terra General Ledger data";
                                                    }
                                                    this.recordsValid = false;
                                                    this.postAction(null, this.errorMessage);
                                                } else {
                                                    this.checkRecords();
                                                    this.updateAvailableFundList();
                                                }
                                            }
                                        },
                                        (error:ErrorResponse) => {
                                            this.processErrorResponse(error);
                                            //this.successMessage = null;
                                            //this.errorMessage = "Error loading data";
                                            //if(error && !error.isEmpty()){
                                            //    this.processErrorMessage(error);
                                            //}
                                            //this.postAction(null, this.errorMessage);
                                        }
                                    );
                            } else {
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

    updateAvailableFundList(){
        this.availableFundList = [];
        if(this.records != null && this.records.length > 0){
            for(var i = 0; i < this.records.length; i++){
                if(this.records[i].nbAccountNumber === '2033.010'){
                    if(this.records[i].chartAccountsLongDescription != null){
                        this.availableFundList.push(this.records[i].subscriptionRedemptionEntity);
                    }
                }
            }
            this.availableFundList.sort();
        }

    }

    addRecord(){
        this.addedRecordsHolder.records.push(new REGeneralLedgerFormDataRecord());
    }

    copyFromPrevious(){
        this.busy = this.periodicReportService.getGeneratedTerraFormDataFromPreviousMonth(this.reportId)
            .subscribe(
                response  => {
                    console.log(response);
                    if(response && response.length > 0) {
                        for (var i = 0; i < response.length; i++) {
                            let record = new REGeneralLedgerFormDataRecord();
                            //if(response[i].acronym === 'TARRAGON'){
                            //    record.tranche = 1;
                            //}else if(response[i].acronym === 'TARRAGON B'){
                            //    record.tranche = 2;
                            //}
                            record.financialStatementCategory = response[i].financialStatementCategory;
                            record.terraNICChartOfAccountsName = response[i].chartAccountsLongDescription;

                            if(!this.availableFundList.includes(response[i].subscriptionRedemptionEntity) &&
                                response[i].subscriptionRedemptionEntity != null && response[i].subscriptionRedemptionEntity.trim() != ''){
                                this.availableFundList.push(response[i].subscriptionRedemptionEntity);
                            }
                            record.entityName = response[i].subscriptionRedemptionEntity;

                            record.nbAccountNumber = response[i].nbAccountNumber;
                            record.nicAccountName = response[i].nicAccountName;
                            record.glaccountBalance = response[i].glaccountBalance;

                            this.onNumberChange(record);

                            this.addedRecordsHolder.records.push(record);

                            //this.nbChartOfAccountsChanged(response.records[i], true);
                        }
                        this.postAction("Successfully loaded " + response.length + " records from previous month", null);
                    }else {
                        this.postAction("No records were loaded from previous month", null);
                    }
                },
                (error: ErrorResponse) => {
                    this.processErrorResponse(error);
                    //this.successMessage = null;
                    //this.errorMessage = "Error loading Tarragon GL added data from previous month";
                    //if(error && !error.isEmpty()){
                    //    this.processErrorMessage(error);
                    //}
                    //this.postAction(this.successMessage, this.errorMessage);
                }
            );
    }

    chartOfAccountsLongDescriptionChanged(addedRecord){
        for(var i = 0; i < this.terraNICReportingChartOfAccounts.length; i++){
            if(this.terraNICReportingChartOfAccounts[i].terraChartOfAccountsName == addedRecord.terraNICChartOfAccountsName){
                addedRecord.nbAccountNumber = this.terraNICReportingChartOfAccounts[i].nicchartOfAccounts.nbchartOfAccounts.code;
                addedRecord.nicAccountName = this.terraNICReportingChartOfAccounts[i].nicchartOfAccounts.nameRu;
            }
        }
        if(addedRecord.nbAccountNumber != '6150.030'){
            addedRecord.entityName = null;
        }
    }


    removeRecord(addedRecord){
        var confirmed = window.confirm("Are you sure want to delete record?");
        if(confirmed) {
            if (this.addedRecordsHolder.records) {
                for (var i = this.addedRecordsHolder.records.length; i--;) {
                    if (this.addedRecordsHolder.records[i] === addedRecord) {
                        this.addedRecordsHolder.records.splice(i, 1);
                    }
                }
            }
        }
    }


    removeSavedRecord(record){
        var confirmed = window.confirm("Are you sure want to delete record?");
        if(confirmed) {
            this.periodicReportService.deleteRealEstateGeneralLedgerFormDataRecord(record.addedRecordId)
                .subscribe(
                    response => {
                        this.successMessage ="Successfully deleted record";
                        // get tarragon records
                        this.busy = this.periodicReportService.getGeneratedTerraForm(this.reportId)
                            .subscribe(
                                (response: ListResponse) => {
                                    if (response) {
                                        this.records = response.records;
                                        if (response.status === 'FAIL') {
                                            if (response.message != null) {
                                                this.errorMessage = response.message.nameEn ? response.message.nameEn :
                                                    response.message.nameKz ? response.message.nameKz : response.message.nameRu ? response.message.nameRu : null;
                                            }
                                            if (this.errorMessage == null) {
                                                this.errorMessage = "Error loading Terra General Ledger data";
                                            }
                                            this.recordsValid = false;
                                            this.postAction(this.successMessage, this.errorMessage);
                                        } else {
                                            this.checkRecords();
                                            this.updateAvailableFundList();
                                            this.postAction(this.successMessage, this.errorMessage);
                                        }
                                    }
                                },
                                (error: ErrorResponse) => {
                                    this.processErrorResponse(error, "Error loading Terra General Ledger data");
                                    //this.errorMessage = "Error loading Tarragon General Ledger data";
                                    //if(error && !error.isEmpty()){
                                    //    this.processErrorMessage(error);
                                    //}
                                    //this.postAction(this.successMessage, this.errorMessage);
                                }
                            );

                    },
                    (error: ErrorResponse) => {
                        this.processErrorResponse(error);
                        //this.errorMessage = "Error deleting record";
                        //if (error && !error.isEmpty()) {
                        //    console.log("Error");
                        //    this.processErrorMessage(error);
                        //}
                        //this.postAction(null, this.errorMessage);
                    }
                );
        }
    }

    saveAddedRecords(){

        // TODO: add validation

        let confirmed = false;
        for(var i = 0; i < this.addedRecordsHolder.records.length; i++) {
            this.addedRecordsHolder.records[i].glaccountBalance = Number(this.addedRecordsHolder.records[i].glaccountBalance.toString().replace(/,/g , ''));

            if (this.addedRecordsHolder.records[i].financialStatementCategory === 'A') {
                if (this.addedRecordsHolder.records[i].terraNICChartOfAccountsName == 'Capital call cash adjustment') {
                    if (this.addedRecordsHolder.records[i].glaccountBalance > 0) {
                        if (confirm(" Do you want to add 'Capital call capital adjustment' with opposite account balance?")) {
                            confirmed = true;
                            break;
                        }else{
                            confirmed = false;
                            break;
                        }
                    }
                }
            }
        }


        if(confirmed) {
            for (var i = 0; i < this.addedRecordsHolder.records.length; i++) {
                if (this.addedRecordsHolder.records[i].financialStatementCategory === 'A') {
                    if (this.addedRecordsHolder.records[i].terraNICChartOfAccountsName == 'Capital call cash adjustment') {
                        if (this.addedRecordsHolder.records[i].glaccountBalance > 0) {
                            let record = new REGeneralLedgerFormDataRecord();
                            //record.tranche = this.addedRecordsHolder.records[i].tranche;
                            record.financialStatementCategory = 'E';
                            record.terraNICChartOfAccountsName = 'Capital call capital adjustment';
                            record.glaccountBalance = 0 - Number(this.addedRecordsHolder.records[i].glaccountBalance);
                            this.chartOfAccountsLongDescriptionChanged(record);
                            this.addedRecordsHolder.records.push(record);
                        }
                    }
                }
            }
        }

        this.periodicReportService.saveRealEstateGeneralLedgerFormData(this.addedRecordsHolder)
            .subscribe(
                response  => {
                    var creationDate = response.creationDate;
                    this.errorMessage = null;

                    this.addedRecordsHolder.records = [];

                    this.busy = this.periodicReportService.getGeneratedTerraForm(this.reportId)
                        .subscribe(
                            (response: ListResponse) => {
                                if (response) {
                                    this.records = response.records;
                                    if (response.status === 'FAIL') {
                                        if (response.message != null) {
                                            this.errorMessage = response.message.nameEn ? response.message.nameEn :
                                                response.message.nameKz ? response.message.nameKz : response.message.nameRu ? response.message.nameRu : null;
                                        }
                                        if (this.errorMessage == null) {
                                            this.errorMessage = "Error loading Terra General Ledger data";
                                        }
                                        this.recordsValid = false;
                                        this.postAction(this.successMessage, this.errorMessage);
                                    } else {
                                        this.checkRecords();
                                        this.updateAvailableFundList();
                                    }
                                }
                            },
                            (error: ErrorResponse) => {
                                this.processErrorResponse(error);
                                //this.successMessage = null;
                                //this.errorMessage = "Error loading records";
                                //if(error && !error.isEmpty()){
                                //    this.processErrorMessage(error);
                                //}
                                //this.postAction(null, this.errorMessage);
                            }
                        );
                },
                (error: ErrorResponse) => {
                    this.processErrorResponse(error);
                    //this.successMessage = null;
                    //this.errorMessage = "Error saving new records";
                    //if(error && !error.isEmpty()){
                    //    this.processErrorMessage(error);
                    //}else {
                    //    this.postAction(null, this.errorMessage);
                    //}
                }
            )
    }


    //saveEditAccountBalance(record){
    //    let updateDto = {"reportId": this.reportId,"fundName": record.chartAccountsLongDescription,
    //        "accountBalance": record.glaccountBalance.toString().replace(/,/g , '')};
    //    console.log(updateDto);
    //    this.busy = this.periodicReportService.updateTerraInvestment(updateDto)
    //        .subscribe(
    //            response  => {
    //                if(response){
    //                    this.postAction("Record successfully updated", null);
    //                    this.checkRecords();
    //                    record.editing = false;
    //                }
    //            },
    //            (error: ErrorResponse) => {
    //                this.processErrorResponse(error);
    //                //this.successMessage = null;
    //                //this.errorMessage = "Error updating account balance";
    //                //if(error && !error.isEmpty()){
    //                //    this.processErrorMessage(error);
    //                //}
    //                //this.postAction(null, this.errorMessage);
    //            }
    //        );
    //}

    showNextButton(){
        var diff = this.totalAssetsSum + this.totalOtherSum;
        if(diff > 2 || diff < -2){
            return false;
        }else if(!this.recordsValid){
            return false;
        }
        return true;
    }

    checkRecords(){
        if(this.records != null){
            var assetsSum = 0.0;
            var otherSum = 0.0;
            for(var i = 0; i < this.records.length; i++){
                if(this.records[i].nbAccountNumber == null){
                    this.postAction(this.successMessage, "NB Account number is missing for record '" + this.records[i].chartAccountsLongDescription + "'");
                    this.recordsValid = false;
                    return;
                }
                if(!this.isRecordExcluded(this.records[i])) {
                    if (isNumeric(this.records[i].glaccountBalance) && this.records[i].financialStatementCategory === 'A') {
                        assetsSum += Number(this.records[i].glaccountBalance);
                    } else {
                        otherSum += Number(this.records[i].glaccountBalance);
                    }
                }

                if(this.records[i].nbAccountNumber  == null && this.records[i].chartAccountsLongDescription != null){
                    this.postAction(null, "Record '" + this.records[i].chartAccountsLongDescription +"' is missing 'NB Account Number' field value (see below).");
                    this.recordsValid = false;
                }
            }

            this.totalAssetsSum = assetsSum;
            this.totalOtherSum = otherSum;

            var diff = assetsSum + otherSum;
            if(diff > 2 || diff < -2){
                this.postAction(this.successMessage, "Total Assets = " + assetsSum.toFixed(2) + ". total L, E, X, I = " + otherSum.toFixed(2) + ". Sum = " + diff.toFixed(2));
                this.recordsValid = false;
            }else{
                this.errorMessage = null;
                this.recordsValid = true;
            }
        }
    }

    public onNumberChange(record){
        if(record.glaccountBalance != null && record.glaccountBalance.toString().length > 0) {
            if(record.glaccountBalance.toString()[record.glaccountBalance.toString().length - 1] != '.' || record.glaccountBalance.toString().split('.').length > 2){
                record.glaccountBalance = record.glaccountBalance.toString().replace(/,/g , '');
                if(record.glaccountBalance != '-') {
                    record.glaccountBalance = parseFloat(record.glaccountBalance).toLocaleString('en', {maximumFractionDigits: 2});
                }
            }
        }
    }

    excludeGeneratedRecord(record){
        if(confirm("Are you sure want to exclude record?")){
            console.log(record);
            var params = {"recordId": record.id, "name": record.chartAccountsLongDescription, "type": record.type}
            this.busy = this.periodicReportService.includeExcludeTerraGeneralLedgerRecord(params)
                .subscribe(
                    response => {
                        if (response) {
                            var value = record.excludeFromTerraCalculation != null ? record.excludeFromTerraCalculation : false;
                            record.excludeFromTerraCalculation = !value;
                            this.checkRecords();
                        }else{
                            this.postAction(null, "Failed to exclude record");
                        }
                    },
                    (error: ErrorResponse) => {
                        this.postAction(null, "Failed to exclude record");
                    }
                );
        }
    }

    includeGeneratedRecord(record){
        if(confirm("Are you sure want to include record?")){
            console.log(record);
            var params = {"recordId": record.id, "name": record.chartAccountsLongDescription, "type": record.type}
            this.busy = this.periodicReportService.includeExcludeTerraGeneralLedgerRecord(params)
                .subscribe(
                    response => {
                        if (response) {
                            var value = record.excludeFromTerraCalculation != null ? record.excludeFromTerraCalculation : false;
                            record.excludeFromTerraCalculation = !value;
                            this.checkRecords();
                        }else{
                            this.postAction(null, "Failed to include record");
                        }
                    },
                    (error: ErrorResponse) => {
                        this.postAction(null, "Failed to include record");
                    }
                );
        }
    }

    isRecordExcluded(record){
        return record.excludeFromTerraCalculation;
    }
}