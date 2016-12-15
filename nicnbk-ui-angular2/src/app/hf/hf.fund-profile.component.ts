import { Component } from '@angular/core';
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

@Component({
    selector: 'hf-fund-profile',
    templateUrl: 'view/hf.fund-profile.component.html',
    styleUrls: [
        //'../../../public/css/...',
    ],
    providers: []
})
export class HFFundProfileComponent extends GoogleChartComponent {

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

        // parse params and load data
        this.sub = this.route
            .params
            .subscribe(params => {
                this.fundIdParam = +params['id'];
                this.managerIdParam = +params['managerId'];


                this.fund.manager = new HFManager();

                //this.fund.calculatedValues = [];
                //var item = {'name': 'Return', 'item.year1': '1.01',
                //    'item.year2': '1.01',
                //    'item.year3': '1.01',
                //    'item.year4': '1.01',
                //    'item.year5': '1.01',
                //    'item.year6': '1.01',
                //    'item.year7': '1.01',
                //    'item.year8': '1.01',
                //    'item.year9': '1.01',
                //    'item.year10': '1.01'};
                //this.fund.calculatedValues.push(item);
                //this.fund.calculatedValues.push(item);
                //this.fund.calculatedValues.push(item);
                //this.fund.calculatedValues.push(item);
                //this.fund.calculatedValues.push(item);

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
                                    }
                                    if(this.fund.investorBaseList == null){
                                        this.fund.investorBaseList = [];
                                    }
                                    if(this.fund.managers == null){
                                        this.fund.managers = [];
                                    }
                                }else{
                                    // TODO: handle error
                                    this.errorMessage = "Error loading fund profile.";
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
                                    this.errorMessage = "Error loading fund manager info.";
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

    save(){
        console.log(this.fund);

        this.fundService.save(this.fund)
            .subscribe(
                (response: SaveResponse)  => {
                    this.fund.id = response.entityId;
                    this.fund.creationDate = response.creationDate;

                    this.postAction("Successfully saved.", null);
                },
                error =>  {
                    this.postAction(null, "Error saving manager profile.");
                }
            );
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
        this.lookupService.getGeographies()
            .subscribe(
                data => {
                    //data.forEach(element => {
                    //    this.strategyLookup.push({ id: element.code, value: element.nameEn});
                    //});
                    this.geographyLookup = data;
                },
                error =>  this.errorMessage = <any>error
            );

        // currency
        this.lookupService.getCurrencyList()
            .subscribe(
                data => {
                    this.currencyLookup = data;
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
        console.log(this.albourneRatingLookup);

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
        this.fund.returns.push({year: "", jan: "", feb: "", mar: "", apr: "", may: "", jun: "", jul: "", aug: "", sep: "", oct: "", nov: "", dec: ""});
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
                },
                error =>  this.errorMessage = <any>error
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
            }
        };

        var chart = new google.visualization.PieChart(document.getElementById('strategyBreakdownChart'));
        chart.draw(data, options);
    }

    getStrategyBreakdownRowData(){
        var list = [['Type', '%']];
        for(var i = 0; i < this.fund.strategyBreakdownList.length; i++){
            list.push([this.fund.strategyBreakdownList[i].code, Number(this.fund.strategyBreakdownList[i].value)]);
        }
        return list;
    }

    calculateReturnsYTD(returnRecord){
        var ytd = (
        this.getReturnValue(Number(returnRecord.jan)) + this.getReturnValue(Number(returnRecord.feb)) +
        this.getReturnValue(Number(returnRecord.mar)) + this.getReturnValue(Number(returnRecord.apr)) +
        this.getReturnValue(Number(returnRecord.may)) + this.getReturnValue(Number(returnRecord.jun)) +
        this.getReturnValue(Number(returnRecord.jul)) + this.getReturnValue(Number(returnRecord.aug)) +
        this.getReturnValue(Number(returnRecord.sep)) + this.getReturnValue(Number(returnRecord.oct)) +
        this.getReturnValue(Number(returnRecord.nov)) + this.getReturnValue(Number(returnRecord.dec))
        );
        if(ytd == 0){
            return "";
        }
        return ytd;
    }

    private getReturnValue(value){
        if(value == null || value === 'undefined' || value === ''){
            return 0;
        }else{
            return Number(value);
        }
    }
}