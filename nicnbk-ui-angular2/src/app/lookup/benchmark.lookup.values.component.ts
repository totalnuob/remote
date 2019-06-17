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

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/forkJoin';

import * as moment from "moment";
import {BaseDictionary} from "../common/model/base-dictionary";
import {BenchmarkSearchParams} from "../common/model/benchmark-search-params";
import {BenchmarkSearchResults} from "../common/model/benchmark-search-results";
import {BenchmarkValue} from "../common/model/benchmark.value";

declare var $:any

@Component({
    selector: 'currency-rates-lookup-values-nb-reporting',
    templateUrl: 'view/benchmark.lookup.values.component.html',
    styleUrls: [],
    providers: [],
})
export class BenchmarkLookupValuesComponent extends CommonNBReportingComponent implements OnInit{

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
                }
            });
    }

    errorMessageSaveBenchmark;
    successMessageSaveBenchmark;

    busy: Subscription;
    benchmarkTypes = [];
    searchResults: BenchmarkSearchResults;
    searchParams = new BenchmarkSearchParams();
    selectedBenchmark = new BenchmarkValue();

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
            this.lookupService.getBenchmarkTypeList()
            )
            .subscribe(
                ([data1]) => {
                    this.benchmarkTypes = data1;

                    this.search(0);
            });

    }


    search(page){

        if(this.searchParams == null){
            this.searchParams = new BenchmarkSearchParams();
        }
        //console.log(this.searchParams);
        // TODO: as parameter?
        this.searchParams.pageSize = 20;

        this.searchParams.page = page;

        this.searchParams.fromDate = $('#fromDate').val();
        this.searchParams.toDate = $('#toDate').val();

        this.busy = this.lookupService.getBenchmarks(this.searchParams)
            .subscribe(
                searchResult  => {
                    this.searchResults = searchResult;
                    //console.log(searchResult);
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error searching benchmarks";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                    //alert(this.errorMessage);
                }
            );

    }

    clearSearchForm(){
        this.searchParams = new BenchmarkSearchParams();
    }

    edit(item){
        this.errorMessageSaveBenchmark = null;
        this.successMessageSaveBenchmark = null;
        if(item){
            this.selectedBenchmark = item;
        }else{

        }

    }

    save(){
        this.selectedBenchmark.date = $('#valueDate').val();

        // TODO: check required fields
        if(!this.selectedBenchmark.date){
            this.successMessageSaveBenchmark = null;
            this.errorMessageSaveBenchmark = "Date required.";
            return false;
        }


        if(!this.selectedBenchmark.benchmark){
            this.successMessageSaveBenchmark = null;
            this.errorMessageSaveBenchmark = "Type required.";
            return false;
        }

        if(!this.selectedBenchmark.returnValue){
            this.successMessageSaveBenchmark = null;
            this.errorMessageSaveBenchmark = "Return value required.";
            return false;
        }
        if(!this.selectedBenchmark.indexValue){
            this.successMessageSaveBenchmark = null;
            this.errorMessageSaveBenchmark = "Index value required.";
            return false;
        }

        this.busy = this.lookupService.saveBenchmark(this.selectedBenchmark)
            .subscribe(

                (saveResponse: SaveResponse) => {
                    if(saveResponse.status === 'SUCCESS' ){
                        //console.log(saveResponse);

                        // TODO: Search params
                        this.search(0);

                        this.successMessageSaveBenchmark = saveResponse.message.nameEn;
                        this.errorMessageSaveBenchmark = null;

                    }else{
                        if(saveResponse.message != null){
                            var message = saveResponse.message.nameEn != null ? saveResponse.message.nameEn :
                                saveResponse.message.nameRu != null ? saveResponse.message.nameRu : saveResponse.message.nameKz;
                            if(message != null && message != ''){
                                this.postAction(null, message);
                            }else{
                                this.postAction(null, "Error saving benchmark");
                            }
                        }
                    }
                },
                (error: ErrorResponse) => {
                    this.successMessageSaveBenchmark = null;
                    this.errorMessageSaveBenchmark = error && error.message ? error.message : "Error saving benchmark.";
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

}