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

declare var $:any

@Component({
    selector: 'currency-rates-lookup-values-nb-reporting',
    templateUrl: 'view/currency.rates.lookup.values.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class CurrencyRatesLookupValuesNBReportingComponent extends CommonNBReportingComponent implements OnInit{

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService,
        private lookupService: LookupService
    ){
        super(router, route, periodicReportService);
    }

    errorMessageSaveCurrencyRate;
    successMessageSaveCurrencyRate;

    busy: Subscription;
    searchResults: CurrencyRatesSearchResults;
    searchParams: CurrencyRatesSearchParams;
    selectedCurrencyRate = new CurrencyRate();

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

        this.search(0);
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
                    console.log(searchResult);
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
            this.selectedCurrencyRate = new CurrencyRate("USD");
        }

    }

    save(){
        this.selectedCurrencyRate.date = $('#valueDate').val();

        // TODO: check required fields
        if(!this.selectedCurrencyRate.date){
            this.successMessageSaveCurrencyRate = null;
            this.errorMessageSaveCurrencyRate = "Date required.";
            return false;
        }

        if(!this.selectedCurrencyRate.value){
            this.successMessageSaveCurrencyRate = null;
            this.errorMessageSaveCurrencyRate = "Value required.";
            return false;
        }else if(this.selectedCurrencyRate.value <= 0){
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
                        console.log(saveResponse);

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

    delete(item){
        if(confirm("Are you sure want to delete record?")){
            this.busy = this.lookupService.deleteCurrencyRates(item.id)
                .subscribe(
                    deleteResult  => {
                        if(deleteResult){
                            console.log(1);
                            this.errorMessage = null;
                            this.successMessage = "Successfully deleted record";

                            // TODO: request params

                            this.search(0);
                            return;
                        }else{
                            console.log(2);
                            this.successMessage = null;
                                this.errorMessage = "Failed to delete record";
                            return;
                        }
                    },
                    (error: ErrorResponse) => {
                        this.errorMessage = "Error deleting currency rate";
                        this.postAction(null, this.errorMessage);
                    }
                );
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
}