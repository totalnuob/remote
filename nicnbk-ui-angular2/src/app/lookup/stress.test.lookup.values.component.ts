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
import {StressTestSearchResults} from "../common/model/stress-test-search-results";
import {StressTestSearchParams} from "../common/model/stress-test-search-params";
import {StressTestValue} from "../common/model/stress-test.value";

declare var $:any

@Component({
    selector: 'currency-rates-lookup-values-nb-reporting',
    templateUrl: 'view/stress.test.lookup.values.component.html',
    styleUrls: [],
    providers: [],
})
export class StressTestLookupValuesComponent extends CommonNBReportingComponent implements OnInit{

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

    errorMessageSaveStressTest;
    successMessageSaveStressTest;

    stressTestUploadModalSuccessMessage;
    stressTestUploadModalErrorMessage;

    uploadPortfolioVarCode;
    uploadedValues;

    busy: Subscription;
    portfolioVarTypes = [];
    searchResults: StressTestSearchResults;
    searchParams = new StressTestSearchParams();
    selectedStressTest = new StressTestValue();

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
            this.lookupService.getPortfolioVarTypeList()
        )
            .subscribe(
                ([data1]) => {
                    this.portfolioVarTypes = data1;

                    this.search(0);
                });

    }

    search(page){

        if(this.searchParams == null){
            this.searchParams = new StressTestSearchParams();
        }
        //console.log(this.searchParams);
        // TODO: as parameter?
        this.searchParams.pageSize = 20;

        this.searchParams.page = page;

        this.searchParams.fromDate = $('#fromDate').val();
        this.searchParams.toDate = $('#toDate').val();

        this.busy = this.lookupService.getStressTests(this.searchParams)
            .subscribe(
                searchResult  => {
                    this.searchResults = searchResult;
                    //console.log(searchResult);
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error searching stress tests";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                    //alert(this.errorMessage);
                }
            );

    }

    clearSearchForm(){
        this.searchParams = new StressTestSearchParams();
    }

    edit(item){
        this.errorMessageSaveStressTest = null;
        this.errorMessageSaveStressTest = null;
        this.successMessageSaveStressTest = null;
        if(item){
            this.selectedStressTest = item;
        }else{
        }
    }

    save(){
        this.selectedStressTest.date = $('#valueDate').val();

        // TODO: check required fields
        if(!this.selectedStressTest.date){
            this.successMessageSaveStressTest = null;
            this.errorMessageSaveStressTest = "Date required.";
            return false;
        }


        if(!this.selectedStressTest.name){
            this.successMessageSaveStressTest = null;
            this.errorMessageSaveStressTest = "Name required.";
            return false;
        }

        if(!this.selectedStressTest.value){
            this.successMessageSaveStressTest = null;
            this.errorMessageSaveStressTest = "Value required.";
            return false;
        }

        this.busy = this.lookupService.saveStressTest(this.selectedStressTest)
            .subscribe(

                (saveResponse: SaveResponse) => {
                    if(saveResponse.status === 'SUCCESS' ){
                        //console.log(saveResponse);

                        // TODO: Search params
                        this.search(0);

                        this.successMessageSaveStressTest = saveResponse.message.nameEn;
                        this.errorMessageSaveStressTest = null;

                    }else{
                        if(saveResponse.message != null){
                            var message = saveResponse.message.nameEn != null ? saveResponse.message.nameEn :
                                saveResponse.message.nameRu != null ? saveResponse.message.nameRu : saveResponse.message.nameKz;
                            if(message != null && message != ''){
                                this.postAction(null, message);
                            }else{
                                this.postAction(null, "Error saving stress tests");
                            }
                        }
                    }
                },
                (error: ErrorResponse) => {
                    this.successMessageSaveStressTest = null;
                    this.errorMessageSaveStressTest = error && error.message ? error.message : "Error saving stress tests.";
                    //this.processErrorMessage(error);
                }
            );
    }

    closePortfolioVarUploadModal(){
        this.uploadedValues = "";
        this.uploadPortfolioVarCode = null;
        this.stressTestUploadModalSuccessMessage = null;
        this.stressTestUploadModalErrorMessage = null;
    }

    closeStressTestEditModal(){
        this.selectedStressTest = new StressTestValue();
        this.successMessageSaveStressTest = null;
        this.errorMessageSaveStressTest = null;
    }

    // parsePortfolioVarValues(){
    //     var portfolioVars = [];
    //     if(this.uploadPortfolioVarCode == null || this.uploadPortfolioVarCode === ''){
    //         this.portfolioVarUploadModalSuccessMessage = null;
    //         this.portfolioVarUploadModalErrorMessage = "Portfolio VaR required";
    //         return;
    //     }
    //     if(this.uploadedValues == null || this.uploadedValues.trim() === ''){
    //         this.portfolioVarUploadModalSuccessMessage = null;
    //         this.portfolioVarUploadModalErrorMessage = "Values required";
    //         return;
    //     }
    //     var rows = this.uploadedValues.split("\n");
    //     for(var i = 0; i < rows.length; i++){
    //         if(rows[i].trim() === ""){
    //             continue;
    //         }
    //         var row = rows[i].split("\t");
    //         if(row.length != 2){
    //             this.portfolioVarUploadModalSuccessMessage = null;
    //             this.portfolioVarUploadModalErrorMessage = "Invalid format. Expected: date [tab] return";
    //             return;
    //         }
    //         if(row[0] == null || row[0] === 'undefined' || row[0].split(".").length != 3){
    //             this.portfolioVarUploadModalSuccessMessage = null;
    //             this.portfolioVarUploadModalErrorMessage = "Invalid format - date";
    //             return;
    //         }
    //
    //         var day = row[0].split(".")[0];
    //         var month = row[0].split(".")[1];
    //         var year = row[0].split(".")[2];
    //
    //         //var index_value = row[1].replace(/,/g, '.');
    //         //index_value = index_value.replace('%', '');
    //
    //         var value = row[1].replace(/,/g, '.');
    //         value = value.replace('%', '');
    //
    //         var portfolioVar =  new BaseDictionary();
    //         portfolioVar.code = this.uploadPortfolioVarCode;
    //         portfolioVars.push({"date": day + '-' + month + '-' + year, /*"indexValue": parseFloat(Number(index_value)).toFixed(4),*/
    //             "value": parseFloat(Number(value)),"benchmark":portfolioVar});
    //     }
    //
    //     this.busy = this.lookupService.saveBenchmarksList(benchmarks)
    //         .subscribe(
    //             (saveResponse: SaveResponse) => {
    //                 if(saveResponse.status === 'SUCCESS' ){
    //                     this.search(0);
    //                     this.benchmarkUploadModalSuccessMessage = saveResponse.message.nameEn;
    //                     this.benchmarkUploadModalErrorMessage = null;
    //
    //                 }else{
    //                     if(saveResponse.message != null){
    //                         var message = saveResponse.message.nameEn != null ? saveResponse.message.nameEn :
    //                             saveResponse.message.nameRu != null ? saveResponse.message.nameRu : saveResponse.message.nameKz;
    //                         if(message != null && message != ''){
    //                             this.postAction(null, message);
    //                         }else{
    //                             this.postAction(null, "Error saving benchmark list");
    //                         }
    //                     }
    //                 }
    //             },
    //             (error: ErrorResponse) => {
    //                 this.benchmarkUploadModalSuccessMessage = null;
    //                 this.benchmarkUploadModalErrorMessage = error && error.message ? error.message : "Error saving benchmark list.";
    //                 //this.processErrorMessage(error);
    //             }
    //         );
    //
    // }
}