import {Component, OnInit} from "@angular/core";
import {CommonFormViewComponent} from "../common/common.component";
import { Http, Response } from '@angular/http';

import {Router, ActivatedRoute} from '@angular/router';
import {PeriodicReportService} from "../reporting/periodic.report.service";
import {PeriodicReport} from "../reporting/model/periodic.report";
import {ErrorResponse} from "../common/error-response";
import {Subscription} from "rxjs/Subscription";
import {CommonNBReportingComponent} from "../reporting/common.nb.reporting.component";
import {OKResponse} from "../common/ok-response";
import {SaveResponse} from "../common/save-response";
import {LookupService} from "../common/lookup.service";
import {CurrencyRatesSearchResults} from "../common/model/currencyrates-search-results";
import {CurrencyRatesSearchParams} from "../common/model/currency-rates-search-params";

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/forkJoin';

import * as moment from "moment";
import {CurrencyRate} from "../common/model/currency,rate";
import {NewCurrencyRateParams} from "../common/model/new-currency-rate-params";
import {BaseDictionary} from "../common/model/base-dictionary";

declare var $:any

@Component({
    selector: 'currency-rates-lookup-values-nb-reporting',
    templateUrl: 'view/currency.rates.lookup.values.component.html',
    styleUrls: [],
    providers: [],
})
export class CurrencyRatesLookupValuesComponent extends CommonNBReportingComponent implements OnInit{

    public sub: any;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService,
        private lookupService: LookupService
    ){
        super(router, route, periodicReportService);

        this.sub = this.route
            .params
            .subscribe(params => {
                if(params['params'] != null) {
                    this.newCurrencyRateParams = JSON.parse(params['params']);
                    console.log(this.newCurrencyRateParams);
                }
            });
    }

    errorMessageSaveCurrencyRate;
    successMessageSaveCurrencyRate;

    currencyUploadModalSuccessMessage;
    currencyUploadModalErrorMessage

    busy: Subscription;
    currencyList = [];
    searchResults: CurrencyRatesSearchResults;
    searchParams = new CurrencyRatesSearchParams();
    newCurrencyRateParams = new NewCurrencyRateParams();
    selectedCurrencyRate = new CurrencyRate();

    uploadedValues;
    uploadCurrencyCode;

    ngOnInit():void {
        $('#fromDateDTPickeer').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
        $('#untilDateDTPickeer').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        $('#valueDateDTPickeer').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        Observable.forkJoin(
            // Load lookups
            this.lookupService.getCurrencyList()
            )
            .subscribe(
                ([data1]) => {
                    this.currencyList = data1;

                    this.search(0);

                    if(this.newCurrencyRateParams && this.newCurrencyRateParams.date != null && this.newCurrencyRateParams.currency != null) {
                        console.log("auto click button")
                        document.getElementById("openCurrencyModalButton").click();
                    }
            });

    }


    search(page){

        if(this.searchParams == null){
            this.searchParams = new CurrencyRatesSearchParams;
        }
        //console.log(this.searchParams);
        // TODO: as parameter?
        this.searchParams.pageSize = 20;

        this.searchParams.page = page;

        this.searchParams.fromDate = $('#fromDate').val();
        this.searchParams.toDate = $('#toDate').val();

        this.busy = this.lookupService.getCurrencyRates(this.searchParams)
            .subscribe(
                searchResult  => {
                    this.searchResults = searchResult;
                    //console.log(searchResult);
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error searching currency rates";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                    //alert(this.errorMessage);
                }
            );

    }

    clearSearchForm(){
        this.searchParams = new CurrencyRatesSearchParams;
    }

    edit(item){
        this.errorMessageSaveCurrencyRate = null;
        this.successMessageSaveCurrencyRate = null;
        if(item){
            this.selectedCurrencyRate = item;
        }else{
            if(this.newCurrencyRateParams){
                this.selectedCurrencyRate = new CurrencyRate(this.newCurrencyRateParams.currency);
                this.selectedCurrencyRate.date = this.newCurrencyRateParams.date;
            }else {
                this.selectedCurrencyRate = new CurrencyRate("USD");
            }

        }

    }

    save(){
        this.selectedCurrencyRate.date = $('#valueDate').val();

        // Check required fields
        if(!this.selectedCurrencyRate.date){
            this.successMessageSaveCurrencyRate = null;
            this.errorMessageSaveCurrencyRate = "Date required.";
            return false;
        }

        if(!this.selectedCurrencyRate.value && !this.selectedCurrencyRate.valueUSD){
            this.successMessageSaveCurrencyRate = null;
            this.errorMessageSaveCurrencyRate = "Value KZT or USD required.";
            return false;
        }
        if((this.selectedCurrencyRate.value && this.selectedCurrencyRate.value <= 0) ||
            (this.selectedCurrencyRate.valueUSD && this.selectedCurrencyRate.valueUSD <= 0)){
            this.errorMessageSaveCurrencyRate = "Value must be positive";
            return false;
        }

        if(!this.selectedCurrencyRate.currency){
            this.successMessageSaveCurrencyRate = null;
            this.errorMessageSaveCurrencyRate = "Currency required.";
            return false;
        }

        if(this.selectedCurrencyRate.averageValue && this.selectedCurrencyRate.averageValue <= 0){
            this.errorMessageSaveCurrencyRate = "Average Value must be positive";
            return false;
        }

        if(this.selectedCurrencyRate.averageValueYear && this.selectedCurrencyRate.averageValueYear <= 0){
            this.errorMessageSaveCurrencyRate = "Average Value Year must be positive";
            return false;
        }

        this.busy = this.lookupService.saveCurrencyRates(this.selectedCurrencyRate)
            .subscribe(

                (saveResponse: SaveResponse) => {
                    if(saveResponse.status === 'SUCCESS' ){
                        //console.log(saveResponse);

                        // TODO: Search params
                        this.search(0);

                        this.successMessageSaveCurrencyRate = saveResponse.message.nameEn;

                    }else{
                        if(saveResponse.message != null){
                            var message = saveResponse.message.nameEn != null ? saveResponse.message.nameEn :
                                saveResponse.message.nameRu != null ? saveResponse.message.nameRu : saveResponse.message.nameKz;
                            if(message != null && message != ''){
                                this.postAction(null, message);
                            }else{
                                this.postAction(null, "Error saving currency rate");
                            }
                        }
                    }
                },
                (error: ErrorResponse) => {
                    this.errorMessageSaveCurrencyRate = error && error.message ? error.message : "Error saving currency rate.";
                    //this.processErrorMessage(error);
                }
            );
    }

    //delete(item){
    //    if(confirm("Are you sure want to delete record?")){
    //        this.busy = this.lookupService.deleteCurrencyRates(item.id)
    //            .subscribe(
    //                deleteResult  => {
    //                    if(deleteResult){
    //                        console.log(1);
    //                        this.errorMessage = null;
    //                        this.successMessage = "Successfully deleted record";
    //
    //                        // TODO: request params
    //
    //                        this.search(0);
    //                        return;
    //                    }else{
    //                        console.log(2);
    //                        this.successMessage = null;
    //                            this.errorMessage = "Failed to delete record";
    //                        return;
    //                    }
    //                },
    //                (error: ErrorResponse) => {
    //                    this.errorMessage = "Error deleting currency rate";
    //                    this.postAction(null, this.errorMessage);
    //                }
    //            );
    //    }
    //}

    currencyChanged(){
        if(this.selectedCurrencyRate.currency != null && this.selectedCurrencyRate.currency.code === 'USD'){
            this.selectedCurrencyRate.valueUSD = null;
        }
    }

    checkAverageYearValue(){
        //var
        //var date = new Date($('#valueDate').val());
        var date = moment($('#valueDate').val(), "DD-MM-YYYY");
        //console.log(date.toDate());
        if(date.toDate().getMonth() == 11 && date.toDate().getDate() == 31){
            return true;
        }
        return false;
    }

    checkAverageValue(){
        var date = moment($('#valueDate').val(), "DD-MM-YYYY");
        //console.log(date.toDate());
        var lastDay =  new Date(date.toDate().getFullYear(), date.toDate().getMonth()+1, 0).getDate();
        if(date.toDate().getDate() == lastDay){
            return true;
        }
        return false;
    }

    closeCurrencyUploadModal(){
        this.uploadedValues = "";
        this.uploadCurrencyCode = null;
        this.currencyUploadModalSuccessMessage = null;
        this.currencyUploadModalErrorMessage = null;
    }

    parseCurrencyValues(){
        var currencies = [];
        if(this.uploadCurrencyCode == null || this.uploadCurrencyCode === ''){
            this.currencyUploadModalSuccessMessage = null;
            this.currencyUploadModalErrorMessage = "Currency required";
            return;
        }else if(this.uploadCurrencyCode === 'USD'){
            this.currencyUploadModalSuccessMessage = null;
            this.currencyUploadModalErrorMessage = "Cannot choose USD";
            return;
        }
        if(this.uploadedValues == null || this.uploadedValues.trim() === ''){
            this.currencyUploadModalSuccessMessage = null;
            this.currencyUploadModalErrorMessage = "Values required";
            return;
        }
        var rows = this.uploadedValues.split("\n");
        for(var i = 0; i < rows.length; i++){
            if(rows[i].trim() === ""){
                continue;
            }
            var row = rows[i].split("\t");
            if(row.length != 2){
                this.currencyUploadModalSuccessMessage = null;
                this.currencyUploadModalErrorMessage = "Invalid format";
                return;
            }
            if(row[0] == null || row[0] === 'undefined' || row[0].split(".").length != 3){
                this.currencyUploadModalSuccessMessage = null;
                this.currencyUploadModalErrorMessage = "Invalid format of date";
                return;
            }

            var day = row[0].split(".")[0];
            var month = row[0].split(".")[1];
            var year = row[0].split(".")[2];

            var value = row[1].replace(/,/g, '.');
            value = value.replace('%', '');
            var currency =  new BaseDictionary();
            currency.code = this.uploadCurrencyCode;
            currencies.push({"date": day + '-' + month + '-' + year, "valueUSD": parseFloat(Number(value)).toFixed(4), "currency":currency});
        }

        console.log(currencies);

        this.busy = this.lookupService.saveCurrencyRatesList(currencies)
            .subscribe(
                (saveResponse: SaveResponse) => {
                    if(saveResponse.status === 'SUCCESS' ){
                        this.search(0);
                        this.currencyUploadModalSuccessMessage = saveResponse.message.nameEn;
                        this.currencyUploadModalErrorMessage = null;

                    }else{
                        if(saveResponse.message != null){
                            var message = saveResponse.message.nameEn != null ? saveResponse.message.nameEn :
                                saveResponse.message.nameRu != null ? saveResponse.message.nameRu : saveResponse.message.nameKz;
                            if(message != null && message != ''){
                                this.postAction(null, message);
                            }else{
                                this.postAction(null, "Error saving currency rate list");
                            }
                        }
                    }
                },
                (error: ErrorResponse) => {
                    this.currencyUploadModalErrorMessage = error && error.message ? error.message : "Error saving currency rate list.";
                    //this.processErrorMessage(error);
                }
            );

    }

    checkAverageYearValue(){
        var date = moment($('#valueDate').val(), "DD-MM-YYYY");
        //console.log(date.toDate());
        if(date.toDate().getMonth() == 11 && date.toDate().getDate() == 31){
            // DECEMBER 31
            return true;
        }
        return false;
    }

    checkAverageValue(){
        var date = moment($('#valueDate').val(), "DD-MM-YYYY");
        //console.log(date.toDate());
        var lastDay =  new Date(date.toDate().getFullYear(), date.toDate().getMonth()+1, 0).getDate();
        if(date.toDate().getDate() == lastDay){
            // Last day of month
            return true;
        }
        return false;
    }

    getNonUSDCurrencyList(){
        var rates = [];
        for(var i = 0; i < this.currencyList.length; i ++){
            if(this.currencyList[i].code != 'USD'){
                rates.push(this.currencyList[i]);
            }
        }
        return rates;
    }
}