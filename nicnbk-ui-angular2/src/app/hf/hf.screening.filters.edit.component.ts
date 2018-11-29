import { Component } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HedgeFundService} from "./hf.fund.service";
import {CommonFormViewComponent} from "../common/common.component";
import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";
import {HedgeFundScreeningSearchParams} from "./model/hf.screening-search-params";
import {HedgeFundScreeningSearchResults} from "./model/hf-screening-search-results";
import {HedgeFundScreening} from "./model/hf.screening";
import {HedgeFundScreeningService} from "./hf.fund.screening.service";
import {HedgeFundScreeningFilteredResult} from "./model/hf.screening.filtered.result";
import {HedgeFundScreeningFilteredResultStatistics} from "./model/hf.screening.filtered.result.statistics";

declare var $:any
@Component({
    selector: 'hf-fund-search',
    templateUrl: 'view/hf.screening.filters.edit.component.html',
    styleUrls: [
        //'../../../public/css/...',
        '../../../node_modules/angular2-busy/build/style/busy.css'
    ],
    providers: []
})
export class HFScreeningFilteredResultsEditComponent extends CommonFormViewComponent{

    screeningId;
    id;
    filteredResult = new HedgeFundScreeningFilteredResult;
    filteredFundList;
    showManagerAUMInput = false;
    isUnqualifiedFundList = false;
    fundListLookbackAUM;
    fundListLookbackReturn;

    modalSuccessMessage;
    modalErrorMessage;

    private moduleAccessChecker: ModuleAccessCheckerService;

    public sub: any;
    busyGet : Subscription;
    busyStats : Subscription;
    busyModal: Subscription;

    constructor(
        private screeningService: HedgeFundScreeningService,
        private router: Router,
        private route: ActivatedRoute
    ){
        super(router);

        this.moduleAccessChecker = new ModuleAccessCheckerService;

        if(!this.moduleAccessChecker.checkAccessHedgeFunds()){
            this.router.navigate(['accessDenied']);
        }

        this.sub = this.route
            .params
            .subscribe(params => {
                this.screeningId = +params['screeningId'];
                this.id = +params['id'];
                if(this.screeningId > 0) {
                    if (this.id > 0) {
                        this.busyGet = this.screeningService.getFilteredResult(this.id)
                            .subscribe(
                                result => {
                                    //console.log(result);
                                    this.filteredResult = result;
                                    this.onNumberChangeFundAUM();
                                    this.onNumberChangeManagerAUM();
                                },
                                error => {
                                    //console.log(error);
                                    this.postAction(null, "Failed to load screening filtered results");
                                }
                            );
                    } else {
                        this.filteredResult = new HedgeFundScreeningFilteredResult();
                        this.filteredResult.screeningId = this.screeningId;
                    }
                }
            });
    }

    ngOnInit():any {
        $('#startDateTPickeer').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
    }

    save() {
        this.filteredResult.startDate= $('#startDate').val();
        this.filteredResult.fundAUM = Number(this.filteredResult.fundAUM.toString().replace(/,/g, ''));
        this.filteredResult.managerAUM = Number(this.filteredResult.managerAUM.toString().replace(/,/g, ''));
        if(this.filteredResult.trackRecord != null && this.filteredResult.trackRecord == 0){
            this.postAction(null, "Track record must be greater than 0");
        }

        if(this.filteredResult.startDate == null){
            this.postAction(null, "Date required");
            return;
        }

        //console.log(this.filteredResult);
        this.busyGet = this.screeningService.saveFilteredResult(this.filteredResult)
            .subscribe(
                response => {
                    this.filteredResult.id = response.entityId;

                    this.onNumberChangeFundAUM();
                    this.onNumberChangeManagerAUM();
                    this.postAction("Successfully saved filters. Need to reapply filters to see the changes", null);
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error saving filters";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, this.errorMessage);
                }
            );
    }


    applyFilters(){

        // TODO: check params

        this.filteredResult.startDate= $('#startDate').val();
        this.filteredResult.fundAUM = Number(this.filteredResult.fundAUM.toString().replace(/,/g, ''));
        this.filteredResult.managerAUM = Number(this.filteredResult.managerAUM.toString().replace(/,/g, ''));

        // TODO: check required parameters
        if(this.filteredResult.startDate == null){
            this.postAction(null, "Date required");
            return;
        }

        this.busyStats = this.screeningService.getFilteredResultStatistics(this.filteredResult)
            .subscribe(
                result => {
                    console.log(result);
                    this.filteredResult.filteredResultStatistics = result;
                },
                error => {
                    this.postAction(null, "Failed to load screening filtered results statistics");
                }
            );
    }

    checkManagerAUM(fund){
        if(fund.managerAUM != null) {
            //fund.managerAUM = Number(fund.managerAUM.toString().replace(/,/g, ''));
            var fundAUM = fund.fundAUM != null ? Number(fund.fundAUM.toString().replace(/,/g, '')) : 0;
            return Number(fund.managerAUM.toString().replace(/,/g, '')) >= Number(this.filteredResult.managerAUM.toString().replace(/,/g, '')) &&
                Number(fund.managerAUM.toString().replace(/,/g, '')) >= fundAUM;
        }else if(fund.fundAUM != null){
            return Number(fund.fundAUM.toString().replace(/,/g, '')) >= Number(this.filteredResult.managerAUM.toString().replace(/,/g, ''))
        }else{
            return false;
        }
    }

    showFunds(lookbackReturn, lookbackAUM, type, value){

        this.fundListLookbackAUM = lookbackAUM;
        this.fundListLookbackReturn = lookbackReturn;

        this.modalErrorMessage = null;
        this.modalSuccessMessage = null;
        this.showManagerAUMInput = false;
        this.isUnqualifiedFundList = false;

        var params = new HedgeFundScreeningFilteredResult();
        params.screeningId = this.filteredResult.screeningId;

        params.fundAUM = Number(this.filteredResult.fundAUM.toString().replace(/,/g, ''));
        params.managerAUM = Number(this.filteredResult.managerAUM.toString().replace(/,/g, ''));
        params.startDate = $('#startDate').val();
        params.trackRecord = this.filteredResult.trackRecord;
        params.lookbackReturns = lookbackReturn;
        params.lookbackAUM = lookbackAUM;

        this.filteredFundList = [];

        if(params.startDate == null){
            this.postAction(null, "Date required");
            return;
        }

        if(type == 1) {
            this.busyModal = this.screeningService.getFilteredResultQualifiedFundList(params)
                .subscribe(
                    result => {
                        this.filteredFundList = result;
                        if(value != this.filteredFundList.length){
                            alert("Expected " + value + ", received " + this.filteredFundList.length);
                        }
                    },
                    error => {
                        this.modalPostAction(null,  "Failed to load fund list");
                    }
                );
        }else if(type == 2) {
            this.isUnqualifiedFundList = true;
            this.busyModal = this.screeningService.getFilteredResultUnqualifiedFundList(params)
                .subscribe(
                    result => {
                        this.filteredFundList = result;
                        if(value != this.filteredFundList.length){
                            alert("Expected " + value + ", received " + this.filteredFundList.length);
                        }
                    },
                    error => {
                        this.modalPostAction(null,  "Failed to load fund list");
                    }
                );
        }else if(type == 3) {
            this.showManagerAUMInput = true;
            this.busyModal = this.screeningService.getFilteredResultUndecidedFundList(params)
                .subscribe(
                    result => {
                        this.filteredFundList = result;
                        if(value != this.filteredFundList.length){
                            alert("Expected " + value + ", received " + this.filteredFundList.length);
                        }
                    },
                    error => {
                        this.modalPostAction(null,  "Failed to load fund list");
                    }
                );
        }
    }

    getCellColor(i,j){
        var value = this.filteredResult.filteredResultStatistics.qualified[i][j];
        if(j==0 || i==0){
            return "white";
        }
        var min = null;
        var max = null;
        for(var i = 1; i < this.filteredResult.filteredResultStatistics.qualified.length; i++){
            for(var j = 1; j < this.filteredResult.filteredResultStatistics.qualified[i].length; j++){
                var temp = this.filteredResult.filteredResultStatistics.qualified[i][j];
                if(min == null || temp < min){
                    min = temp;
                }
                if(max == null || temp > max){
                    max = temp;
                }
            }
        }
        var step = (max - min) / 5.0;
        //console.log("value=" + value + ", min="  +min + ", max=" + max + ", step=" + step)

        if(value >= min && value <= min + 1*step){
            return '#E86753';
        }else if(value > min + 1*step && value <= min + 2*step){
            return '#FCB4A5';
        }else if(value > min + 2*step && value <= min + 3*step){
            return '#B9D7B7';
        }else if(value > min + 3*step && value <= min + 4*step){
            return '#74AF72';
        }else if(value > min + 4*step ){
            return '#428F4A';
        }
    }

    editFundManagerAUM(){
        this.showManagerAUMInput = !this.showManagerAUMInput;
        for(var i = 0; i < this.filteredFundList.length; i++) {
            this.onNumberFund(this.filteredFundList[i]);
        }
    }

    saveUndecidedList(){

        for(var i = 0; i < this.filteredFundList.length; i++) {
            if(this.filteredFundList[i].managerAUM != null) {
                if(this.filteredFundList[i].managerAUM != null && this.filteredFundList[i].managerAUM.trim() != '') {
                    var managerAUM = Number(this.filteredFundList[i].managerAUM.toString().replace(/,/g, ''));
                    if (this.filteredFundList[i].fundAUM != null) {
                        var fundAUM = Number(this.filteredFundList[i].fundAUM.toString().replace(/,/g, ''));
                        if (fundAUM > managerAUM) {
                            this.modalErrorMessage = "Manager AUM cannot be less than Fund AUM: " + this.filteredFundList[i].fundName;
                            this.modalSuccessMessage = null;
                            $('#modalMessagesDiv')[0].scrollIntoView({
                                block: "start",
                                behavior: "smooth"
                            });

                            $('#' + this.filteredFundList[i].fundId).css({'background-color': '#dcdcdc'});
                            return;
                        } else {
                            $('#' + this.filteredFundList[i].fundId).css({'background-color': 'white'});
                        }
                    }
                }
            }
        }

        for(var i = 0; i < this.filteredFundList.length; i++) {
            if (this.filteredFundList[i].managerAUM != null) {
                this.filteredFundList[i].managerAUM = Number(this.filteredFundList[i].managerAUM.toString().replace(/,/g, ''));
                if(this.filteredFundList[i].managerAUM == 0){
                    this.filteredFundList[i].managerAUM = null;
                }
            }
        }
        this.busyStats = this.screeningService.updateManagerAUM(this.filteredFundList)
            .subscribe(
                result => {
                    //console.log(result);

                    for(var i = 0; i < this.filteredFundList.length; i++) {
                        if(this.filteredFundList[i].managerAUM != null) {
                            this.onNumberFund(this.filteredFundList[i]);
                        }
                    }
                    this.showManagerAUMInput = false;

                    this.modalPostAction("Successfully updated Manager AUM. Need to reapply filters to see the changes.", null);
                },
                error => {
                    this.modalPostAction(null,  "Failed to update Manager AUM");
                }
            );
    }

    closeModal(){
        this.filteredFundList = null;
    }

    public onNumberChangeFundAUM(){
        if(this.filteredResult.fundAUM != null && this.filteredResult.fundAUM != 'undefined' && this.filteredResult.fundAUM.toString().length > 0) {
            if(this.filteredResult.fundAUM.toString()[this.filteredResult.fundAUM.toString().length - 1] != '.' || this.filteredResult.fundAUM.toString().split('.').length > 2){
                this.filteredResult.fundAUM = this.filteredResult.fundAUM.toString().replace(/,/g , '');
                if(this.filteredResult.fundAUM != '-'){
                    this.filteredResult.fundAUM = parseFloat(this.filteredResult.fundAUM).toLocaleString('en', {maximumFractionDigits: 2});
                }
            }
        }
    }

    public onNumberChangeManagerAUM(){
        if(this.filteredResult.managerAUM != null && this.filteredResult.managerAUM != 'undefined' && this.filteredResult.managerAUM.toString().length > 0) {
            if(this.filteredResult.managerAUM.toString()[this.filteredResult.managerAUM.toString().length - 1] != '.' || this.filteredResult.managerAUM.toString().split('.').length > 2){
                this.filteredResult.managerAUM = this.filteredResult.managerAUM.toString().replace(/,/g , '');
                if(this.filteredResult.managerAUM != '-'){
                    this.filteredResult.managerAUM = parseFloat(this.filteredResult.managerAUM).toLocaleString('en', {maximumFractionDigits: 2});
                }
            }
        }
    }

    public onNumberFund(fund){
        //console.log(fund);
        if(fund.managerAUM != null && fund.managerAUM != 'undefined' && fund.managerAUM.toString().length > 0) {
            if(fund.managerAUM.toString()[fund.managerAUM.toString().length - 1] != '.' || fund.managerAUM.toString().split('.').length > 2){
                fund.managerAUM = fund.managerAUM.toString().replace(/,/g , '');
                if(fund.managerAUM != '-'){
                    fund.managerAUM = parseFloat(fund.managerAUM).toLocaleString('en', {maximumFractionDigits: 2});
                }
            }
        }
    }

    modalPostAction(successMessage, errorMessage){
        this.modalSuccessMessage = successMessage;
        this.modalErrorMessage = errorMessage;

        $('#modalMessagesDiv')[0].scrollIntoView({
            block: "start",
            behavior: "smooth"
        });
    }

}