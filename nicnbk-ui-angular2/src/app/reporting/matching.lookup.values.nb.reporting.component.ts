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
import {LookupService} from "../common/lookup.service";
import {CurrencyRatesSearchResults} from "../common/model/currencyrates-search-results";
import {CurrencyRatesSearchParams} from "../common/model/currency-rates-search-params";

import * as moment from "moment";
import {CurrencyRate} from "../common/model/currency,rate";
import {BaseDictionary} from "../common/model/base-dictionary";

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/forkJoin';
import {CommonMatchingNICReportingChartOfAccounts} from "./model/common.mathcing.nic.reporting.chart.of.accounts.";
import {NICReportingChartOfAccounts} from "./model/nic.reporting.chart.of.accounts.";

declare var $:any

@Component({
    selector: 'matching-lookup-values-nb-reporting',
    templateUrl: 'view/matching.lookup.values.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class MatchingLookupValuesNBReportingComponent extends CommonNBReportingComponent implements OnInit{

    busy: Subscription;
    selectedLookupName;
    selectedLookupValues;

    nicChartAccountsLookupValues;
    nbChartAccountsLookupValues;
    chartAccountsTypeLookupValues;

    nicChartAccountsSearchParams;
    nicChartAccountsSearchResult;

    selectedEditLookup;

    errorMessageSaveLookup;
    successMessageSaveLookup;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService,
        private lookupService: LookupService
    ){
        super(router, route, periodicReportService);

        this.nicChartAccountsSearchParams =  {};

        Observable.forkJoin(
            // Load lookups
            this.lookupService.getNICReportingChartOfAccounts(null),
            this.lookupService.getNBChartAccounts(),
            this.lookupService.getChartAccountsType()
            )
            .subscribe(
                ([data1, data2, data3]) => {
                    this.nicChartAccountsLookupValues = data1;
                    this.nbChartAccountsLookupValues = data2;
                    this.chartAccountsTypeLookupValues = data3;
                    console.log(data3);
                });

    }

    ngOnInit():void {
        $('#editModal').on('hidden.bs.modal', function () {
            $('#closeEditModalButton').click();
        });
    }

    selectLookup(value, page){
        if(value){
            this.selectedLookupName = value;
        }
        if(this.selectedLookupName){
            this.selectedLookupValues = null;
            if(this.selectedLookupName === 'NIC_CHART_ACCOUNTS'){
                //this.busy = this.lookupService.getNICReportingChartOfAccounts(null)
                //    .subscribe(
                //        result  => {
                //            this.selectedLookupValues = result;
                //        },
                //        (error: ErrorResponse) => {
                //            this.errorMessage = "Error loading lookup: NIC_CHART_ACCOUNTS";
                //            if(error && !error.isEmpty()){
                //                this.processErrorMessage(error);
                //            }
                //            this.postAction(null, null);
                //        }
                //    );
                if(page && !isNaN(page)){
                    this.nicChartAccountsSearchParams.page = page;
                }
                this.busy = this.lookupService.searchNICReportingChartOfAccounts(this.nicChartAccountsSearchParams)
                    .subscribe(
                        result  => {
                            this.nicChartAccountsSearchResult = result;
                            this.selectedLookupValues = result.chartOfAccounts;
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error loading lookup: NIC_CHART_ACCOUNTS";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, null);
                        }
                    );

            }else if(this.selectedLookupName === 'NIC_SINGULARITY_CHART_ACCOUNTS'){

                this.clearSearchForm();
                this.busy = this.lookupService.getNBReportingNICSingularityChartAccounts()
                    .subscribe(
                        result  => {
                            console.log(result);
                            this.selectedLookupValues = result;
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error loading lookup: NIC_SINGULARITY_CHART_ACCOUNTS";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, null);
                        }
                    );
            }else if(this.selectedLookupName === 'NIC_TARRAGON_CHART_ACCOUNTS'){
                this.clearSearchForm();
                this.busy = this.lookupService.getNBReportingNICTarragonChartAccounts()
                    .subscribe(
                        result  => {
                            this.selectedLookupValues = result;
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error loading lookup: NIC_TARRAGON_CHART_ACCOUNTS";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, null);
                        }
                    );
            }else if(this.selectedLookupName === 'NIC_TERRA_CHART_ACCOUNTS'){
                this.clearSearchForm();
                this.busy = this.lookupService.getNBReportingNICTerraChartAccounts()
                    .subscribe(
                        result  => {
                            this.selectedLookupValues = result;
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error loading lookup: NIC_TERRA_CHART_ACCOUNTS";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, null);
                        }
                    );
            }
        }
    }

    clearSearchForm(){
       this.nicChartAccountsSearchParams = {};
    }

    edit(item){
        this.errorMessageSaveLookup = null;
        this.successMessageSaveLookup = null;
        if(item){
            this.selectedEditLookup = item;
        }else {
            if(this.selectedLookupName === 'NIC_CHART_ACCOUNTS'){
                this.selectedEditLookup = new NICReportingChartOfAccounts();
            }else {
                this.selectedEditLookup = new CommonMatchingNICReportingChartOfAccounts();
            }
        }
    }

    closeEditModal(){
        console.log("closeEditModal");
        this.selectedEditLookup = null;
    }
    save(){
        if(this.selectedEditLookup == null){
            this.errorMessageSaveLookup = "Error saving lookup";
            return;
        }

        if(this.selectedLookupName === 'NIC_CHART_ACCOUNTS') {
            if (this.selectedEditLookup.nameRu == null || this.selectedEditLookup.nameRu.trim() === '') {
                this.errorMessageSaveLookup = "Name ru required.";
                this.successMessageSaveLookup = null;
                return;
            } else if (this.selectedEditLookup.nbchartOfAccounts == null || this.selectedEditLookup.nbchartOfAccounts.code == null ||
                this.selectedEditLookup.nbchartOfAccounts.code.trim() === '') {
                this.errorMessageSaveLookup = "NB Chart of accounts required.";
                return;
            }
        }else{
            if((this.selectedEditLookup.accountNumber == null || this.selectedEditLookup.accountNumber.trim() === '')
                && (this.selectedEditLookup.nameEn == null || this.selectedEditLookup.nameEn.trim() === '')){
                this.errorMessageSaveLookup = "'Account number' or 'Name En'  required.";
                return;
            }else if(this.selectedEditLookup.nicchartOfAccounts == null || this.selectedEditLookup.nicchartOfAccounts.code == null ||
                this.selectedEditLookup.nicchartOfAccounts.code.trim() === ''){
                this.errorMessageSaveLookup = "NIC Chart of accounts required.";
                return;
            }
//            else if(this.selectedEditLookup.positiveOnly && this.selectedEditLookup.negativeOnly){
//                this.errorMessageSaveLookup = "Cannot be positive only and negative only at the same time";
//                return;
//            }
        }

        if(this.selectedLookupName === 'NIC_CHART_ACCOUNTS'){

            this.busy = this.lookupService.saveNICChartAccounts(this.selectedEditLookup)
                .subscribe(
                    (saveResponse:SaveResponse) => {
                        if (saveResponse.status === 'SUCCESS') {
                            //console.log(saveResponse);
                            this.errorMessageSaveLookup = null;
                            this.successMessageSaveLookup = saveResponse.message.nameEn;

                            this.selectLookup(this.selectedLookupName, null);

                        } else {
                            if (saveResponse.message != null) {
                                var message = saveResponse.message.nameEn != null ? saveResponse.message.nameEn :
                                    saveResponse.message.nameRu != null ? saveResponse.message.nameRu : saveResponse.message.nameKz;
                                if (message != null && message != '') {
                                    this.postAction(null, message);
                                } else {
                                    this.postAction(null, "Error saving lookup value");
                                }
                                this.successMessageSaveLookup = null;
                            }
                        }
                    },
                    (error:ErrorResponse) => {
                        this.errorMessageSaveLookup = error && error.message ? error.message : "Error saving lookup value.";
                        this.successMessageSaveLookup = null;
                    }
                );
        }else {
            // Check duplicate
            if(this.selectedLookupValues && this.selectedLookupValues.length > 0) {
                for (var i = 0; i < this.selectedLookupValues.length; i++) {
                    if( this.selectedEditLookup.accountNumber != null && this.selectedEditLookup.accountNumber.trim() != ''){
                        if(this.selectedEditLookup.id != null && Number(this.selectedEditLookup.id) == Number(this.selectedLookupValues[i].id)){
                            continue;
                        }else if(this.selectedEditLookup.accountNumber === this.selectedLookupValues[i].accountNumber){
/*                            if(this.selectedEditLookup.positiveOnly != null && this.selectedEditLookup.positiveOnly &&
                                this.selectedLookupValues[i].negativeOnly != null && this.selectedLookupValues[i].negativeOnly){
                                // OK: positive and negative
                            }else if(this.selectedEditLookup.negativeOnly != null && this.selectedEditLookup.negativeOnly &&
                                this.selectedLookupValues[i].positiveOnly != null && this.selectedLookupValues[i].positiveOnly){
                                // OK: positive and negative
                            }else{
                                this.errorMessageSaveLookup = "Account number matches another record, 'Negative only' or " +
                                    " 'Positive only' must be selected and must be opposite to matching record.";
                                this.successMessageSaveLookup = null;
                                return;
                            }
*/
                        }

                    }else if( this.selectedEditLookup.nameEn != null && this.selectedEditLookup.nameEn.trim() != ''){
                        if(this.selectedEditLookup.id != null && Number(this.selectedEditLookup.id) == Number(this.selectedLookupValues[i].id)){
                            continue;
                        }else if(this.selectedEditLookup.nameEn === this.selectedLookupValues[i].nameEn){
/*                            if(this.selectedEditLookup.positiveOnly != null && this.selectedEditLookup.positiveOnly &&
                                this.selectedLookupValues[i].negativeOnly != null && this.selectedLookupValues[i].negativeOnly){
                                // OK: positive and negative
                                console.log("OK 1");
                            }else if(this.selectedEditLookup.negativeOnly != null && this.selectedEditLookup.negativeOnly &&
                                this.selectedLookupValues[i].positiveOnly != null && this.selectedLookupValues[i].positiveOnly){
                                // OK: positive and negative
                                console.log("OK 2");
                            }else{
                                this.errorMessageSaveLookup = "Name en matches another record, 'Negative only' or " +
                                    " 'Positive only' must be selected and must be opposite to matching record.";
                                this.successMessageSaveLookup = null;
                                return;
                            }
*/
                        }
                    }
                }

            }
            this.busy = this.lookupService.saveMatchingNICChartAccounts(this.selectedLookupName, this.selectedEditLookup)
                .subscribe(
                    (saveResponse:SaveResponse) => {
                        if (saveResponse.status === 'SUCCESS') {
                            //console.log(saveResponse);
                            this.errorMessageSaveLookup = null;
                            this.successMessageSaveLookup = saveResponse.message.nameEn;

                            this.selectLookup(this.selectedLookupName, null);

                        } else {
                            if (saveResponse.message != null) {
                                var message = saveResponse.message.nameEn != null ? saveResponse.message.nameEn :
                                    saveResponse.message.nameRu != null ? saveResponse.message.nameRu : saveResponse.message.nameKz;
                                if (message != null && message != '') {
                                    this.postAction(null, message);
                                } else {
                                    this.postAction(null, "Error saving lookup value");
                                }
                                this.successMessageSaveLookup = null;
                            }
                        }
                    },
                    (error:ErrorResponse) => {
                        this.errorMessageSaveLookup = error && error.message ? error.message : "Error saving lookup value.";
                        this.successMessageSaveLookup = null;
                    }
                );
        }
    }

    delete(item){
        //alert("Delete is not available. Please contact administrator.");
        if(confirm("Are you sure want to delete record?")){
            this.busy = this.lookupService.deleteMathcingLookupValue(this.selectedLookupName, item.id)
                .subscribe(
                    deleteResult  => {
                        if(deleteResult){
                            this.selectLookup(this.selectedLookupName, null);

                            this.errorMessage = null;
                            this.successMessage = "Successfully deleted record";
                            return;
                        }else{
                            this.successMessage = null;
                            this.errorMessage = "Failed to delete record";
                            return;
                        }
                    },
                    (error: ErrorResponse) => {
                        this.errorMessage = "Error deleting lookup value";
                        this.postAction(null, this.errorMessage);
                    }
                );
        }

    }
}