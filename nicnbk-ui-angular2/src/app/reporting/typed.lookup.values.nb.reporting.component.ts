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

declare var $:any

@Component({
    selector: 'typed-lookup-values-nb-reporting',
    templateUrl: 'view/typed.lookup.values.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class TypedLookupValuesNBReportingComponent extends CommonNBReportingComponent implements OnInit{

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService,
        private lookupService: LookupService
    ){
        super(router, route, periodicReportService);
    }

    busy: Subscription;
    selectedLookupName;
    selectedLookupValues: BaseDictionary[];

    selectedEditLookup: BaseDictionary;

    errorMessageSaveLookup;
    successMessageSaveLookup;

    ngOnInit():void {
    }

    lookupWithParent(){
        return this.selectedLookupName != "NB_CHART_ACCOUNTS";
    }

    selectLookup(value){
        this.errorMessage = null;
        this.successMessage = null;
        if(value){
            this.selectedLookupName = value;
        }
        if(this.selectedLookupName){
            if(this.selectedLookupName === 'PE_BALANCE_TYPE'){
                this.busy = this.lookupService.getNBReportingTarragonBalanceType()
                    .subscribe(
                        result  => {
                            this.selectedLookupValues = result;
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error loading lookup";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, null);
                        }
                    );

            }else if(this.selectedLookupName === 'PE_OPS_TYPE'){
                this.busy = this.lookupService.getNBReportingTarragonOperationsType()
                    .subscribe(
                        result  => {
                            this.selectedLookupValues = result;
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error loading lookup";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, null);
                        }
                    );
            }else if(this.selectedLookupName === 'PE_CASHFLOW_TYPE'){
                this.busy = this.lookupService.getNBReportingTarragonCashflowsType()
                    .subscribe(
                        result  => {
                            this.selectedLookupValues = result;
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error loading lookup";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, null);
                        }
                    );
            }else if(this.selectedLookupName === 'HF_CHART_ACCOUNTS_TYPE'){
                this.busy = this.lookupService.getNBReportingSingularityChartAccountsType()
                    .subscribe(
                        result  => {
                            this.selectedLookupValues = result;
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error loading lookup";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, null);
                        }
                    );
            }else if(this.selectedLookupName === 'NB_CHART_ACCOUNTS'){
                this.busy = this.lookupService.getNBChartAccounts()
                    .subscribe(
                        result  => {
                            this.selectedLookupValues = result;
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error loading lookup";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, null);
                        }
                    );
            //}else if(this.selectedLookupName === 'RE_CHART_ACCOUNTS_TYPE'){
            //    this.busy = this.lookupService.getNBReportingTerraChartAccountsType()
            //        .subscribe(
            //            result  => {
            //                this.selectedLookupValues = result;
            //            },
            //            (error: ErrorResponse) => {
            //                this.errorMessage = "Error loading lookup";
            //                if(error && !error.isEmpty()){
            //                    this.processErrorMessage(error);
            //                }
            //                this.postAction(null, null);
            //            }
            //        );
            }else if(this.selectedLookupName === 'RE_BALANCE_TYPE'){
                this.busy = this.lookupService.getNBReportingTerraBalanceType()
                    .subscribe(
                        result  => {
                            this.selectedLookupValues = result;
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error loading lookup";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, null);
                        }
                    );
            }
            else if(this.selectedLookupName === 'RE_PROFIT_LOSS_TYPE'){
                this.busy = this.lookupService.getNBReportingTerraProfitLossType()
                    .subscribe(
                        result  => {
                            this.selectedLookupValues = result;
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error loading lookup";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null, null);
                        }
                    );
            }
        }
    }

    edit(item){
        this.errorMessageSaveLookup = null;
        this.successMessageSaveLookup = null;
        if(item){
            this.selectedEditLookup = item;
            if(this.selectedLookupName != 'NB_CHART_ACCOUNTS' && this.selectedEditLookup.parent == null) {
                this.selectedEditLookup.parent = new BaseDictionary;
            }
        }else {
            this.selectedEditLookup = new BaseDictionary();
            if(this.selectedLookupName != 'NB_CHART_ACCOUNTS') {
                this.selectedEditLookup.parent = new BaseDictionary;
            }
        }

    }

    delete(item){
        if(confirm("Are you sure want to delete record?")){
            this.busy = this.lookupService.deleteTypedLookupValue(this.selectedLookupName, item.id)
                .subscribe(
                    deleteResult  => {
                        if(deleteResult){
                            // TODO: request params
                            this.selectLookup(this.selectedLookupName);

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

    save(){
        if(this.selectedEditLookup == null){
            this.errorMessageSaveLookup = "Error saving lookup";
            return;
        }
        if(this.selectedLookupName != 'NB_CHART_ACCOUNTS') {
            if (this.selectedEditLookup.code == null || this.selectedEditLookup.code.trim() === '') {
                this.errorMessageSaveLookup = "Code required.";
                return;
            } else if (this.selectedEditLookup.nameEn == null || this.selectedEditLookup.nameEn.trim() === '') {
                this.errorMessageSaveLookup = "Name EN required.";
                return;
            }
        }else {
            if (this.selectedEditLookup.code == null || this.selectedEditLookup.code.trim() === '') {
                this.errorMessageSaveLookup = "Code required.";
                return;
            }
            if (this.selectedEditLookup.nameRu == null || this.selectedEditLookup.nameRu.trim() === '') {
                this.errorMessageSaveLookup = "Name RU required.";
                return;
            }
        }

        // Check self parent
        if(this.selectedEditLookup.parent != null &&
            this.selectedEditLookup.code === this.selectedEditLookup.parent.code){
            this.errorMessageSaveLookup = "Cannot be self parent";
            this.successMessageSaveLookup = null;
            return;
        }

        if(this.selectedEditLookup.parent != null && this.selectedEditLookup.parent.code === 'NONE'){
            this.selectedEditLookup.parent = new BaseDictionary;
        }

        // Check duplicate code
        if(this.selectedEditLookup.id == null && this.selectedLookupValues && this.selectedLookupValues.length > 0) {
            for (var i = 0; i < this.selectedLookupValues.length; i++) {
                if(this.selectedLookupValues[i].code === this.selectedEditLookup.code){
                    this.errorMessageSaveLookup = "Duplicate code.";
                    this.successMessageSaveLookup = null;
                    return;
                }
            }

        }

        this.busy = this.lookupService.saveLookupValue(this.selectedLookupName, this.selectedEditLookup)
            .subscribe(
                (saveResponse: SaveResponse) => {
                    if(saveResponse.status === 'SUCCESS' ){
                        this.errorMessageSaveLookup = null;
                        this.successMessageSaveLookup = saveResponse.message.nameEn;
                        this.selectedEditLookup.id = saveResponse.entityId;

                        this.selectLookup(this.selectedLookupName);

                    }else{
                        if(saveResponse.message != null){
                            var message = saveResponse.message.nameEn != null ? saveResponse.message.nameEn :
                                saveResponse.message.nameRu != null ? saveResponse.message.nameRu : saveResponse.message.nameKz;
                            if(message != null && message != ''){
                                this.postAction(null, message);
                            }else{
                                this.postAction(null, "Error saving lookup value");
                            }
                            this.successMessageSaveLookup = null;
                        }
                    }
                },
                (error: ErrorResponse) => {
                    this.errorMessageSaveLookup = error && error.message ? error.message : "Error saving lookup value.";
                    this.successMessageSaveLookup = null;
                }
            );
    }
}