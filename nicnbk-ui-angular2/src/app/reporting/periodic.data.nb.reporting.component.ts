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
import {PeriodicDataSearchResults} from "./model/periodic-data-search-results";
import {PeriodicDataSearchParams} from "./model/periodic-data-search-params";

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/forkJoin';
import {PeriodicData} from "./model/periodic.data";

declare var $:any

@Component({
    selector: 'periodic-data-nb-reporting',
    templateUrl: 'view/periodic.data.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class PeriodicDataNBReportingComponent extends CommonNBReportingComponent implements OnInit{

    searchParams: PeriodicDataSearchParams;
    searchResults: PeriodicDataSearchResults;

    periodicDataTypes: BaseDictionary[];

    selectedEditRecord = new PeriodicData();

    errorMessageSaveRecord;
    successMessageSaveRecord;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService,
        private lookupService: LookupService
    ){
        super(router, route, periodicReportService);

        Observable.forkJoin(
            // Load lookups
            this.lookupService.getPeriodicDataType()
            )
            .subscribe(
                ([data]) => {

                    this.periodicDataTypes = data;

                    this.searchParams = new PeriodicDataSearchParams();
                    this.search(0);
                });
    }

    busy: Subscription;
    busyModal: Subscription;

    ngOnInit():void {
        $('#valueDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
    }

    search(page){

        if(this.searchParams == null){
            this.searchParams = new PeriodicDataSearchParams;
        }
        //console.log(this.searchParams);
        // TODO: as parameter?
        this.searchParams.pageSize = 20;

        this.searchParams.page = page;

        this.searchParams.dateFrom = $('#dateFrom').val();
        this.searchParams.dateTo = $('#dateTo').val();

        this.busy = this.periodicReportService.searchPeriodicData(this.searchParams)
            .subscribe(
                searchResult  => {
                    this.searchResults = searchResult;
                    if(this.searchResults != null && this.searchResults.periodicData != null){
                        for(var i = 0; i < this.searchResults.periodicData.length; i++){
                            this.onNumberChange(this.searchResults.periodicData[i]);
                        }
                    }
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error searching Periodic Data";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                    //alert(this.errorMessage);
                }
            );

    }


    edit(record){
        this.successMessageSaveRecord = null;
        this.errorMessageSaveRecord = null;
        $('#valueDate').val(null);
        if(record){
            //this.selectedEditRecord = record;
            this.selectedEditRecord = new PeriodicData;
            this.selectedEditRecord.id = record.id;
            this.selectedEditRecord.date = record.date;
            this.selectedEditRecord.type = record.type;
            this.selectedEditRecord.value = record.value;
            this.selectedEditRecord.correction = record.correction;
            this.selectedEditRecord.revaluated = record.revaluated;
            this.selectedEditRecord.editable = record.editable;
            this.selectedEditRecord.total = record.total;

            $('#valueDate').val(this.selectedEditRecord.date);
        }else{
            this.selectedEditRecord = new PeriodicData;
        }
    }

    delete(item){
        if(confirm("Are you sure want to delete record?")){
            this.busy = this.periodicReportService.deletePeriodicDataRecord(item.id)
                .subscribe(
                    deleteResult  => {
                        if(deleteResult){
                            this.errorMessage = null;
                            this.successMessage = "Successfully deleted record";

                            // TODO: request params

                            this.search(0);
                            return;
                        }else{
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

    save(){
        this.selectedEditRecord.value = Number(this.selectedEditRecord.value.toString().replace(/,/g, ''));
        this.selectedEditRecord.correction = Number(this.selectedEditRecord.correction.toString().replace(/,/g, ''));

        this.selectedEditRecord.date = $('#valueDate').val();

        // Check required
        if(this.selectedEditRecord == null){
            this.errorMessageSaveRecord = "Error saving lookup";
            this.successMessageSaveRecord = null;
            return;
        }else if(this.selectedEditRecord.type == null || this.selectedEditRecord.type.code == null){
            this.errorMessageSaveRecord = "Type required.";
            this.successMessageSaveRecord = null;
            return;
        }else if(!this.selectedEditRecord.date){
            this.errorMessageSaveRecord = "Date required.";
            this.successMessageSaveRecord = null;
            return;
        }else if(this.selectedEditRecord.value == null){
            this.errorMessageSaveRecord = "Value required.";
            this.successMessageSaveRecord = null;
            return;
        }

        var date = moment($('#valueDate').val(), "DD-MM-YYYY");

        if((this.selectedEditRecord.type.code === 'NET_PROFIT' || this.selectedEditRecord.type.code === 'RSRV_REVAL')
            && (date.toDate().getDate() != 31 || date.toDate().getMonth() != 11)){
            this.errorMessageSaveRecord = "Date must be '31.12.YYYY' for this data type.";
            this.successMessageSaveRecord = null;
            return;
        }

        // Check duplicate values
        if(this.searchResults.periodicData && this.searchResults.periodicData.length > 0) {
            for (var i = 0; i < this.searchResults.periodicData.length; i++) {
                if(this.searchResults.periodicData[i].id == this.selectedEditRecord.id){
                    continue;
                }
                if(this.searchResults.periodicData[i].type.code === this.selectedEditRecord.type.code &&
                    this.searchResults.periodicData[i].date == this.selectedEditRecord.date){
                    this.errorMessageSaveRecord = "Duplicate type and date.";
                    this.successMessageSaveRecord = null;
                    console.log(this.selectedEditRecord);
                    console.log(this.searchResults.periodicData[i]);
                    return;
                }
            }

        }

        this.busyModal = this.periodicReportService.savePeriodicData(this.selectedEditRecord)
            .subscribe(
                (saveResponse: SaveResponse) => {
                    if(saveResponse.status === 'SUCCESS' ){
                        console.log(saveResponse);
                        this.errorMessageSaveRecord = null;
                        this.successMessageSaveRecord = saveResponse.message.nameEn;

                        this.search(0);

                    }else{
                        if(saveResponse.message != null){
                            var message = saveResponse.message.nameEn != null ? saveResponse.message.nameEn :
                                saveResponse.message.nameRu != null ? saveResponse.message.nameRu : saveResponse.message.nameKz;
                            if(message != null && message != ''){
                                this.postAction(null, message);
                            }else{
                                this.postAction(null, "Error saving periodic data");
                            }
                            this.successMessageSaveRecord = null;
                        }
                    }
                },
                (error: ErrorResponse) => {
                    this.errorMessageSaveRecord = error && error.message ? error.message : "Error saving periodic data.";
                    this.successMessageSaveRecord = null;
                }
            );
    }

    showRevaluationCheckbox(){
        if(this.selectedEditRecord != null && this.selectedEditRecord.type != null){
            if(this.selectedEditRecord.id != null && (this.selectedEditRecord.type.code === 'NET_PROFIT' || this.selectedEditRecord.type.code === 'RSRV_REVAL')){
                // updating existing record
                return true;
            }
        }
        return false;
    }

     onNumberChange(record){
        if(record != null){
            if(record.value != null && record.value != 'undefined' && record.value.toString().length > 0) {
                if(record.value.toString()[record.value.toString().length - 1] != '.' || record.value.toString().split('.').length > 2){
                    record.value = record.value.toString().replace(/,/g , '');
                    if(record.value != '-'){
                        record.value = parseFloat(record.value).toLocaleString('en', {maximumFractionDigits: 2});
                    }
                }
            }
            if(record.correction != null && record.correction != 'undefined' && record.correction.toString().length > 0) {
                if(record.correction.toString()[record.correction.toString().length - 1] != '.' || record.correction.toString().split('.').length > 2){
                    record.correction = record.correction.toString().replace(/,/g , '');
                    if(record.correction != '-'){
                        if(record.correction.toString().split('.').length == 2 && parseFloat(record.correction.toString().split('.')[1]) == 0.0
                            && record.correction.toString().split('.')[1].length < 2){
                            // skip
                        }else{
                            record.correction = parseFloat(record.correction).toLocaleString('en', {maximumFractionDigits: 2});
                        }

                    }
                }
            }
            var value = record.value != null ? Number(record.value.toString().replace(/,/g, '')) : 0;
            var correction = record.correction != null ? Number(record.correction.toString().replace(/,/g, '')) : 0;
            var total = value + correction;
            if(total.toString()[total.toString().length - 1] != '.' || total.toString().split('.').length > 2){
                record.total = total.toString().replace(/,/g , '');
                if( record.total != '-'){
                     record.total = parseFloat( record.total).toLocaleString('en', {maximumFractionDigits: 2});
                }
            }
        }
    }
}