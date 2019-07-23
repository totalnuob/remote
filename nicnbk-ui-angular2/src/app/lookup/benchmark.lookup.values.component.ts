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

    benchmarkUploadModalSuccessMessage;
    benchmarkUploadModalErrorMessage

    uploadBenchmarkCode;
    uploadedValues;

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
        //if(!this.selectedBenchmark.indexValue){
        //    this.successMessageSaveBenchmark = null;
        //    this.errorMessageSaveBenchmark = "Index value required.";
        //    return false;
        //}

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

    closeBenchmarkUploadModal(){
        this.uploadedValues = "";
        this.uploadBenchmarkCode = null;
        this.benchmarkUploadModalSuccessMessage = null;
        this.benchmarkUploadModalErrorMessage = null;
    }

    closeBenchmarkEditModal(){
        this.selectedBenchmark = new BenchmarkValue();
        this.errorMessageSaveBenchmark = null;
        this.successMessageSaveBenchmark = null;
    }

    parseBenchmarkValues(){
        var benchmarks = [];
        if(this.uploadBenchmarkCode == null || this.uploadBenchmarkCode === ''){
            this.benchmarkUploadModalSuccessMessage = null;
            this.benchmarkUploadModalErrorMessage = "Benchmark required";
            return;
        }
        if(this.uploadedValues == null || this.uploadedValues.trim() === ''){
            this.benchmarkUploadModalSuccessMessage = null;
            this.benchmarkUploadModalErrorMessage = "Values required";
            return;
        }
        var rows = this.uploadedValues.split("\n");
        for(var i = 0; i < rows.length; i++){
            if(rows[i].trim() === ""){
                continue;
            }
            var row = rows[i].split("\t");
            if(row.length != 2){
                this.benchmarkUploadModalSuccessMessage = null;
                this.benchmarkUploadModalErrorMessage = "Invalid format. Expected: date [tab] return";
                return;
            }
            if(row[0] == null || row[0] === 'undefined' || row[0].split(".").length != 3){
                this.benchmarkUploadModalSuccessMessage = null;
                this.benchmarkUploadModalErrorMessage = "Invalid format - date";
                return;
            }

            var day = row[0].split(".")[0];
            var month = row[0].split(".")[1];
            var year = row[0].split(".")[2];

            //var index_value = row[1].replace(/,/g, '.');
            //index_value = index_value.replace('%', '');

            var return_value = row[1].replace(/,/g, '.');
            return_value = return_value.replace('%', '');

            var benchmark =  new BaseDictionary();
            benchmark.code = this.uploadBenchmarkCode;
            benchmarks.push({"date": day + '-' + month + '-' + year, /*"indexValue": parseFloat(Number(index_value)).toFixed(4),*/
                "returnValue": parseFloat(Number(return_value)).toFixed(4),"benchmark":benchmark});
        }

        //console.log(benchmarks);

        this.busy = this.lookupService.saveBenchmarksList(benchmarks)
            .subscribe(
                (saveResponse: SaveResponse) => {
                    if(saveResponse.status === 'SUCCESS' ){
                        this.search(0);
                        this.benchmarkUploadModalSuccessMessage = saveResponse.message.nameEn;
                        this.benchmarkUploadModalErrorMessage = null;

                    }else{
                        if(saveResponse.message != null){
                            var message = saveResponse.message.nameEn != null ? saveResponse.message.nameEn :
                                saveResponse.message.nameRu != null ? saveResponse.message.nameRu : saveResponse.message.nameKz;
                            if(message != null && message != ''){
                                this.postAction(null, message);
                            }else{
                                this.postAction(null, "Error saving benchmark list");
                            }
                        }
                    }
                },
                (error: ErrorResponse) => {
                    this.benchmarkUploadModalSuccessMessage = null;
                    this.benchmarkUploadModalErrorMessage = error && error.message ? error.message : "Error saving benchmark list.";
                    //this.processErrorMessage(error);
                }
            );

    }

    checkYTD(){
        var date = moment($('#valueDate').val(), "DD-MM-YYYY");
        //console.log(date.toDate());
        if(date.toDate().getMonth() == 11 && date.toDate().getDate() == 31){
            return true;
        }else{
        }
        return false;
    }
}