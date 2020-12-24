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
import {PortfolioVarSearchResults} from "../common/model/portfolio-var-search-results";
import {PortfolioVarValue} from "../common/model/portfolio-var.value";
import {PortfolioVarSearchParams} from "../common/model/portfolio-var-search-params";

declare var $:any

@Component({
    selector: 'currency-rates-lookup-values-nb-reporting',
    templateUrl: 'view/portfolio.var.lookup.values.component.html',
    styleUrls: [],
    providers: [],
})
export class PortfolioVarLookupValuesComponent extends CommonNBReportingComponent implements OnInit{

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

    errorMessageSavePortfolioVar;
    successMessageSavePortfolioVar;

    portfolioVarUploadModalSuccessMessage;
    portfolioVarUploadModalErrorMessage

    uploadPortfolioVarCode;
    uploadedValues;

    busy: Subscription;
    portfolioVarTypes = [];
    searchResults: PortfolioVarSearchResults;
    searchParams = new PortfolioVarSearchParams();
    selectedPortfolioVar = new PortfolioVarValue();

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
            this.searchParams = new PortfolioVarSearchParams();
        }
        //console.log(this.searchParams);
        // TODO: as parameter?
        this.searchParams.pageSize = 20;

        this.searchParams.page = page;

        this.searchParams.fromDate = $('#fromDate').val();
        this.searchParams.toDate = $('#toDate').val();

        this.busy = this.lookupService.getPortfolioVars(this.searchParams)
            .subscribe(
                searchResult  => {
                    this.searchResults = searchResult;
                    //console.log(searchResult);
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error searching portfolio VaRs";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                    //alert(this.errorMessage);
                }
            );

    }

    clearSearchForm(){
        this.searchParams = new PortfolioVarSearchParams();
    }

    edit(item){
        this.errorMessageSavePortfolioVar = null;
        this.errorMessageSavePortfolioVar = null;
        this.successMessageSavePortfolioVar = null;
        if(item){
            this.selectedPortfolioVar = item;
        }else{
        }
    }

    save(){
        this.selectedPortfolioVar.date = $('#valueDate').val();

        // TODO: check required fields
        if(!this.selectedPortfolioVar.date){
            this.successMessageSavePortfolioVar = null;
            this.errorMessageSavePortfolioVar = "Date required.";
            return false;
        }


        if(!this.selectedPortfolioVar.portfolioVar){
            this.successMessageSavePortfolioVar = null;
            this.errorMessageSavePortfolioVar = "Type required.";
            return false;
        }

        if(!this.selectedPortfolioVar.value){
            this.successMessageSavePortfolioVar = null;
            this.errorMessageSavePortfolioVar = "Value required.";
            return false;
        }

        this.busy = this.lookupService.savePortfolioVar(this.selectedPortfolioVar)
            .subscribe(

                (saveResponse: SaveResponse) => {
                    if(saveResponse.status === 'SUCCESS' ){
                        //console.log(saveResponse);

                        // TODO: Search params
                        this.search(0);

                        this.successMessageSavePortfolioVar = saveResponse.message.nameEn;
                        this.errorMessageSavePortfolioVar = null;

                    }else{
                        if(saveResponse.message != null){
                            var message = saveResponse.message.nameEn != null ? saveResponse.message.nameEn :
                                saveResponse.message.nameRu != null ? saveResponse.message.nameRu : saveResponse.message.nameKz;
                            if(message != null && message != ''){
                                this.postAction(null, message);
                            }else{
                                this.postAction(null, "Error saving portfolio VaR");
                            }
                        }
                    }
                },
                (error: ErrorResponse) => {
                    this.successMessageSavePortfolioVar = null;
                    this.errorMessageSavePortfolioVar = error && error.message ? error.message : "Error saving portfolio VaR.";
                    //this.processErrorMessage(error);
                }
            );
    }

    closePortfolioVarUploadModal(){
        this.uploadedValues = "";
        this.uploadPortfolioVarCode = null;
        this.portfolioVarUploadModalSuccessMessage = null;
        this.portfolioVarUploadModalErrorMessage = null;
    }

    closePortfolioVarEditModal(){
        this.selectedPortfolioVar = new PortfolioVarValue();
        this.successMessageSavePortfolioVar = null;
        this.errorMessageSavePortfolioVar = null;
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