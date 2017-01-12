import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {HedgeFund} from "./model/hf.fund";
import {LookupService} from "../common/lookup.service";
import {CommonFormViewComponent} from "../common/common.component";
import {SaveResponse} from "../common/save-response.";
import {HedgeFundService} from "./hf.fund.service";
import {HFManager} from "./model/hf.manager";
import {HFManagerService} from "./hf.manager.service";
import {AlbourneService} from "./hf.albourne.service";
import {GoogleChartComponent} from "../google-chart/google-chart.component";

declare var google:any;
declare var $:any

@Component({
    selector: 'hf-fund-profile',
    templateUrl: 'view/hf.fund-profile.component.html',
    styleUrls: [
        //'../../../public/css/...',
    ],
    providers: []
})
export class HFFundProfileComponent extends GoogleChartComponent implements OnInit{

    private fund = new HedgeFund();

    public sub: any;
    public fundIdParam: number;
    public managerIdParam: number;

    strategyLookup = [];
    substrategyLookup = [];
    geographyLookup = [];
    fundStatusLookup = [];
    currencyLookup = [];

    subscriptionFrequencyLookup = [];
    redemptionFrequencyLookup = [];
    redemptionNotificationPeriodLookup = [];
    sidePocketLookup = [];

    albourneRatingLookup: any;

    chart;

    uploadedReturns;
    returnUploadErrorMessage;
    returnUploadSuccessMessage;

    constructor(
        private lookupService: LookupService,
        private route: ActivatedRoute,
        private fundService: HedgeFundService,
        private managerService: HFManagerService,
        private albourneService: AlbourneService
    ) {

        super();

        // loadLookups
        this.loadLookups();

        // TODO: wait/sync on lookup loading
        // TODO: sync on subscribe results
        this.waitSleep(500);

        // parse params and load data
        this.sub = this.route
            .params
            .subscribe(params => {
                this.fundIdParam = +params['id'];
                this.managerIdParam = +params['managerId'];


                this.fund.manager = new HFManager();

                if(this.fundIdParam > 0) {
                    this.fundService.get(this.fundIdParam)
                        .subscribe(
                            (data: HedgeFund) => {
                                if(data && data.id > 0) {
                                    this.fund = data;
                                    if(this.fund.strategy != null){
                                        this.loadSubstrategies();
                                    }
                                    if(this.fund.strategyBreakdownList == null){
                                        this.fund.strategyBreakdownList = [];
                                    }else{
                                        //this.drawGraph();
                                    }
                                    if(this.fund.investorBaseList == null){
                                        this.fund.investorBaseList = [];
                                    }
                                    if(this.fund.managers == null){
                                        this.fund.managers = [];
                                    }
                                    if(this.fund.returns == null){
                                        this.fund.returns = [];
                                    }
                                }else{
                                    // TODO: handle error
                                    this.errorMessage = "Error loading fund profile";
                                }
                            },
                            error => this.errorMessage = "Error loading manager profile"
                        );
                }else if(this.managerIdParam > 0){
                    this.managerService.get(this.managerIdParam)
                        .subscribe(
                            (data: HFManager) => {
                                if(data && data.id > 0) {
                                    this.fund.manager = data;
                                }else{
                                    // TODO: handle error
                                    this.errorMessage = "Error loading fund manager info";
                                }
                            },
                            error => this.errorMessage = "Error loading manager profile"
                        );
                }else{
                    // TODO: handle error
                    error => this.errorMessage = "Invalid parameter values";
                    return;
                }
            });

    }

    drawGraph(){
        this.drawSubstrategiesChart();
    }


    ngOnInit():any {

        super.ngOnInit();

        // TODO: exclude jQuery
        // datetimepicker
        $('#inceptionDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        $('input[type=text], textarea').autogrow();
    }

    save(){

        this.fund.inceptionDate = $('#inceptionDateValue').val();
        //console.log(this.fund);

        if(!this.validate()){
            //this.postAction(null, "Error saving fund profile.");
            return;
        }

        //console.log(this.fund.returns);
        this.fundService.save(this.fund)
            .subscribe(
                (response: SaveResponse)  => {
                    this.fund.id = response.entityId;
                    this.fund.creationDate = response.creationDate;

                    this.postAction("Successfully saved", null);
                },
                error =>  {
                    this.postAction(null, "Error saving fund profile");
                }
            );
    }

    validate(){

        // Check required fields
        if(this.fund.manager == null || this.fund.manager.id == null){
            this.postAction(null, "Fund manager required");
            return false;
        }

        if(this.fund.name == null || this.fund.name.trim() === ''){
            this.postAction(null, "Fund name required");
            return false;
        }

        // check returns
        if(!this.checkReturns()){
            return false;
        }
        return true;
    }

    checkReturns(){
        // check duplicate years
        var set = Object.create(null);
        for(var i = 0; i < this.fund.returns.length; i++){
            if (this.fund.returns[i].year in set) {
                this.postAction(null, "Invalid returns: duplicate years");
                return false;
            }
            set[this.fund.returns[i].year] = true;
        }
        return true;
    }

    loadLookups(){

        //strategy
        this.lookupService.getHFStrategies()
            .subscribe(
                data => {
                    //data.forEach(element => {
                    //    this.strategyLookup.push({ id: element.code, value: element.nameEn});
                    //});
                    this.strategyLookup = data;
                },
                error =>  this.errorMessage = <any>error
            );

        // geography
        //this.lookupService.getGeographies()
        //    .subscribe(
        //        data => {
        //            //data.forEach(element => {
        //            //    this.strategyLookup.push({ id: element.code, value: element.nameEn});
        //            //});
        //            this.geographyLookup = data;
        //        },
        //        error =>  this.errorMessage = <any>error
        //    );

        // currency
        this.lookupService.getCurrencyList()
            .subscribe(
                data => {
                    this.currencyLookup = data;

                    // TODO: wait/sync on lookup loading
                    // TODO: sync on subscribe results
                    if(this.fund.id == null){
                        this.fund.aumCurrency = 'USD';
                    }
                },
                error =>  this.errorMessage = <any>error
            );

        // TODO: load from DB

        // status
        this.lookupService.getHedgeFundStatuses()
            .subscribe(
                data => {
                    this.fundStatusLookup = data;
                },
                error =>  this.errorMessage = <any>error
            );
        //this.lookupService.getManagerStatuses().then(data => this.fundStatusLookup = data);

        // legal structure
        //this.lookupService.getLegalStructures().then(data => this.legalStructureLookup = data);

        // domicile country
        //this.lookupService.getDomicileCountries().then(data => this.domicileCountryLookup = data);

        // subscription frequency
        this.lookupService.getSubscriptionFrequencies()
            .subscribe(
                data => {
                    this.subscriptionFrequencyLookup = data;
                },
                error =>  this.errorMessage = <any>error
            );
        //this.lookupService.getSubscriptionFrequencyTypes().then(data => this.subscriptionFrequencyLookup = data);

        // redemption frequency
        this.lookupService.getRedemptionFrequencies()
            .subscribe(
                data => {
                    this.redemptionFrequencyLookup = data;
                },
                error =>  this.errorMessage = <any>error
            );
        //this.lookupService.getRedemptionFrequencyTypes().then(data => this.redemptionFrequencyLookup = data);


        // redemption notification period
        this.lookupService.getRedemptionNoticePeriods()
            .subscribe(
                data => {
                    this.redemptionNotificationPeriodLookup = data;
                },
                error =>  this.errorMessage = <any>error
            );
        //this.lookupService.getRedemptionNotificationPeriodTypes().then(data => this.redemptionNotificationPeriodLookup = data);

        this.lookupService.getSidePocketLookup()
            .subscribe(
                data => {
                    this.sidePocketLookup = data;
                },
                error =>  this.errorMessage = <any>error
            );

        this.albourneRatingLookup = this.albourneService.getIDDAnalysisAssessmentLookup();
        //console.log(this.albourneRatingLookup);

    }

    addStrategyBreakdown(){
        this.fund.strategyBreakdownList.push({name: "", value: "", code: ""});
        //console.log(this.strategyBreakdownList);
    }

    removeStrategyBreakdown(item){

        // TODO: pass index instead of item ?
        //console.log(item);
        for(var i = this.fund.strategyBreakdownList.length; i--;) {
            if(this.fund.strategyBreakdownList[i] === item) {
                this.fund.strategyBreakdownList.splice(i, 1);
            }
        }
    }

    addFundManager(){
        this.fund.managers.push({name: "", description: "", since: ""});
    }

    removeFundManager(item){
        //console.log(item);
        for(var i = this.fund.managers.length; i--;) {
            if(this.fund.managers[i] === item) {
                this.fund.managers.splice(i, 1);
            }
        }
    }

    addReturn(){
        this.fund.returns.push({year: "", january: "", february: "", march: "", april: "", may: "", june: "", july: "", august: "", september: "", october: "", november: "", december: ""});
    }

    removeReturn(item){
        //console.log(item);
        for(var i = this.fund.returns.length; i--;) {
            if(this.fund.returns[i] === item) {
                this.fund.returns.splice(i, 1);
            }
        }
    }

    strategyChanged(){
        //alert(this.fund.strategy);

        // TODO: check if substrategies entered, if so as confirmation
        this.loadSubstrategies();
    }

    loadSubstrategies(){

        //substrategy
        this.lookupService.getHFSubStrategies(this.fund.strategy)
            .subscribe(
                data => {
                    //data.forEach(element => {
                    //    this.strategyLookup.push({ id: element.code, value: element.nameEn});
                    //});
                    this.substrategyLookup = data;
                    this.drawGraph();
                },
                error =>  {this.errorMessage = <any>error}
            );
    }

    addInvestorBase(){
        this.fund.investorBaseList.push({category: "", fund: ""});
    }

    removeInvestorBase(item){
        for(var i = this.fund.investorBaseList.length; i--;) {
            if(this.fund.investorBaseList[i] === item) {
                this.fund.investorBaseList.splice(i, 1);
            }
        }
    }


    getAlbourneRatingText(type) {
        if (type === 'IDD_AA') {
            //return this.albourneRatingLookup.IDD_AA this.fund.ALBIDDAnalystAssessment
            return this.getAlbourneRatingTextFromList(this.albourneRatingLookup.IDD_AA, this.fund.albourneIddAnalysisAssessment);
        } else if (type === 'CONVICTION') {
            return this.getAlbourneRatingTextFromList(this.albourneRatingLookup.CONVICTION, this.fund.albourneConviction);
        } else if (type === 'EXPECTED_ALPHA') {
            return this.getAlbourneRatingTextFromList(this.albourneRatingLookup.EXPECTED_ALPHA, this.fund.albourneExpectedAlpha);
        } else if (type === 'EXPECTED_BETA') {
            return this.getAlbourneRatingTextFromList(this.albourneRatingLookup.EXPECTED_BETA, this.fund.albourneExpectedBeta);
        } else if (type === 'EXPECTED_RISK') {
            return this.getAlbourneRatingTextFromList(this.albourneRatingLookup.EXPECTED_RISK, this.fund.alalbournebExpectedRisk);
        } else if (type === 'STRATEGY_INVESTMENT') {
            return this.getAlbourneRatingTextFromList(this.albourneRatingLookup.STRATEGY_INVESTMENT, this.fund.albourneStrategyInvestmentProcess);
        } else if (type === 'MNG_TEAM') {
            return this.getAlbourneRatingTextFromList(this.albourneRatingLookup.MNG_TEAM, this.fund.albourneManagementTeam);
        } else if (type === 'RISK_PROCESS') {
            return this.getAlbourneRatingTextFromList(this.albourneRatingLookup.RISK_PROCESS, this.fund.albourneRiskProcess);
        }
    }

    private getAlbourneRatingTextFromList(list, value){
    for(var i = 0;i < list.length; i++){
        if(list[i].value === value){
            return list[i].text;
        }
    }
    }

    getAlbourneRatingColor(type){
        if (type === 'IDD_AA') {
            return this.getAlbourneRatingColorFromList(this.albourneRatingLookup.IDD_AA, this.fund.albourneIddAnalysisAssessment);
        } else if (type === 'CONVICTION') {
            return this.getAlbourneRatingColorFromList(this.albourneRatingLookup.CONVICTION, this.fund.albourneConviction);
        } else if (type === 'EXPECTED_ALPHA') {
            return this.getAlbourneRatingColorFromList(this.albourneRatingLookup.EXPECTED_ALPHA, this.fund.albourneExpectedAlpha);
        } else if (type === 'EXPECTED_BETA') {
            return this.getAlbourneRatingColorFromList(this.albourneRatingLookup.EXPECTED_BETA, this.fund.albourneExpectedBeta);
        } else if (type === 'EXPECTED_RISK') {
            return this.getAlbourneRatingColorFromList(this.albourneRatingLookup.EXPECTED_RISK, this.fund.alalbournebExpectedRisk);
        } else if (type === 'STRATEGY_INVESTMENT') {
            return this.getAlbourneRatingColorFromList(this.albourneRatingLookup.STRATEGY_INVESTMENT, this.fund.albourneStrategyInvestmentProcess);
        } else if (type === 'MNG_TEAM') {
            return this.getAlbourneRatingColorFromList(this.albourneRatingLookup.MNG_TEAM, this.fund.albourneManagementTeam);
        } else if (type === 'RISK_PROCESS') {
            return this.getAlbourneRatingColorFromList(this.albourneRatingLookup.RISK_PROCESS, this.fund.albourneRiskProcess);
        }
    }

    private getAlbourneRatingColorFromList(list, value){
        for(var i = 0;i < list.length; i++){
            if(list[i].value === value){
                return list[i].color;
            }
        }
    }

    drawSubstrategiesChart(){
        var data = google.visualization.arrayToDataTable(this.getStrategyBreakdownRowData());

        var options = {
            title: 'Strategy breakdown',
            legend: {position: 'left'},
            chartArea: {
                height: '80%',
                top: '10%'
            },
            sliceVisibilityThreshold: 0
        };

        this.chart = new google.visualization.PieChart(document.getElementById('strategyBreakdownChart'));
        this.chart.draw(data, options);
    }

    getStrategyBreakdownRowData(){

        //console.log(this.substrategyLookup);

        var list = [['Type', '%']];
        for(var i = 0; i < this.fund.strategyBreakdownList.length; i++){
            list.push([this.getSubstrategyName(this.fund.strategyBreakdownList[i].code), Number(this.fund.strategyBreakdownList[i].value)]);
        }
        return list;
    }

    getSubstrategyName(code){
        for(var i = 0; i < this.substrategyLookup.length; i++){
            if(this.substrategyLookup[i].code === code){
                return this.substrategyLookup[i].nameEn;
            }
        }
    }

    calculateReturnsYTD(returnRecord){
        var ytd = this.getReturnValue(returnRecord.january) + this.getReturnValue(returnRecord.february) +
        this.getReturnValue(returnRecord.march) + this.getReturnValue(returnRecord.april) +
        this.getReturnValue(returnRecord.may) + this.getReturnValue(returnRecord.june) +
        this.getReturnValue(returnRecord.july) + this.getReturnValue(returnRecord.august) +
        this.getReturnValue(returnRecord.september) + this.getReturnValue(returnRecord.october) +
        this.getReturnValue(returnRecord.november) + this.getReturnValue(returnRecord.december);

        if(ytd == 0){
            return "";
        }
        return ytd.toFixed(4);
    }

    private getReturnValue(value){
        if(value == null || value === 'undefined' || value === ''){
            return 0;
        }else{
            return parseFloat(value);
        }
    }

    parseReturns(){
        var returns = [];
        var rows = this.uploadedReturns.split("\n");

        for(var i = 0; i < rows.length; i++){
            if(rows[i].trim() === ""){
                continue;
            }
            var row = rows[i].split("\t");
            if(row.length != 2){
                this.returnUploadSuccessMessage = null;
                this.returnUploadErrorMessage = "Invalid returns format";
                return;
            }
            if(row[0] == null || row[0] === 'undefined' || row[0].split(".").length != 3){
                this.returnUploadSuccessMessage = null;
                this.returnUploadErrorMessage = "Invalid return format - date";
                return;
            }
            var month = row[0].split(".")[1];
            var year = row[0].split(".")[2];
            var returnRow = this.findReturnByYear(returns, year);
            if(returnRow == null){
                var newRow = {year: year, january: "", february: "", march: "", april: "", may: "", june: "", july: "",
                        august: "", september: "", october: "", november: "", december: ""};
                //set return
                this.setReturnValue(newRow, month, row[1]);
                returns.push(newRow);
            }else{
                //set return
                this.setReturnValue(returnRow, month, row[1]);
            }
        }

        Array.prototype.push.apply(this.fund.returns,returns);

        this.returnUploadErrorMessage = null;
        this.returnUploadSuccessMessage = "Returns added";

    }

    closeReturnsModal(){
        this.uploadedReturns = "";
        this.returnUploadSuccessMessage = null;
        this.returnUploadErrorMessage = null;
    }

    private findReturnByYear(returns, year){
        for(var i = 0; i < returns.length; i++){
            if(returns[i].year === year){
                return returns[i];
            }
        }
        return null;
    }

    private setReturnValue(returnObject, month, value) {
        value = parseFloat(value.replace(",","."));
        if (month == '01') {
            returnObject.january = value;
        } else if (month == '02') {
            returnObject.february = value;
        } else if (month == '03') {
            returnObject.march = value;
        } else if (month == '04') {
            returnObject.april = value;
        } else if (month == '05') {
            returnObject.may = value;
        } else if (month == '06') {
            returnObject.june = value;
        } else if (month == '07') {
            returnObject.july = value;
        } else if (month == '08') {
            returnObject.august = value;
        } else if (month == '09') {
            returnObject.september = value;
        } else if (month == '10') {
            returnObject.october = value;
        } else if (month == '11') {
            returnObject.november = value;
        } else if (month == '12') {
            returnObject.december = value;
        }
    }
}