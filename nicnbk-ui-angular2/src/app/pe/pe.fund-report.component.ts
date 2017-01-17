import {Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {PeFirmService} from "./pe.firm.service";
import {CommonComponent} from "../common/common.component";
import {PeFirm} from "./model/pe.firm";
import {PeFund} from "./model/pe.fund";
import {PeFundService} from "./pe.fund.service";
import {PESearchParams} from "./model/pe.search-params";

import '../../../public/js/viz_v1.js';
import {GoogleChartComponent} from "../google-chart/google-chart.component";

import {Subscription} from 'rxjs';

declare var google:any;
declare var $: any;

@Component({
    selector: 'pe-fund-report',
    templateUrl: 'view/pe.fund-report.component.html',
    styleUrls: [],
    providers: [PeFirmService, PeFundService]
})
export class PeFundReportComponent extends GoogleChartComponent {

    private sub: any;
    private firm = new PeFirm();
    //private fund = new PeFund();
    public firmIdParam: number;
    private fundsList: PeFund[];
    private openFund = new PeFund();
    private searchParams = new PESearchParams();

    public firmStrategyList: Array<any> = [];
    public firmIndustryList: Array<any> = [];
    public firmGeographyList: Array<any> = [];

    public fundStrategyList: Array<any> = [];
    public fundIndustryList: Array<any> = [];
    public fundGeographyList: Array<any> = [];

    busy: Subscription;

    constructor(
        private firmService: PeFirmService,
        private fundService: PeFundService,
        private route: ActivatedRoute
    ){
        super();

        this.sub = this.route
            .params
            .subscribe(params => {
                this.firmIdParam = +params['id'];
                if(this.firmIdParam > 0){
                    this.searchParams['id'] = this.firmIdParam;
                    this.busy = this.firmService.get(this.firmIdParam)
                        .subscribe(
                            (data: PeFirm) => {
                                if(data && data.id > 0){
                                    this.firm = data;
                                    this.preselectFirmStrategyGeographyIndustry();
                                } else {
                                    // TODO: handle error
                                    //this.errorMessage = "Error loading firm";
                                }
                            }
                            //error => this.errorMessage = "Error loading firm profile"
                        );
                    this.fundService.search(this.searchParams)
                        .subscribe(
                            searchResult => {
                                this.fundsList = searchResult;
                                for(var i = 0; i <= this.fundsList.length; i++){
                                    if(this.fundsList[i].status == 'Open'){
                                        this.openFund = this.fundsList[i];
                                        break;
                                    }
                                }
                                this.preselectFundStrategyGeographyIndustry();
                            }
                            //error => this.errorMessage = "Failed to load GP's funds"
                        );
                } else {
                    // TODO: handle error
                    //error => this.errorMessage = "Invalid parameter values";
                    return;
                }
            })
    }

    preselectFirmStrategyGeographyIndustry(){
        if(this.firm.strategy) {
            this.firm.strategy.forEach(element => {
                this.firmStrategyList.push(element.nameEn.toString());
            });
        }
        if(this.firm.industryFocus) {
            this.firm.industryFocus.forEach(element => {
                this.firmIndustryList.push(element.nameEn.toString());
            });
        }
        if(this.firm.geographyFocus) {
            this.firm.geographyFocus.forEach(element => {
                this.firmGeographyList.push(element.nameEn.toString());
            });
        }
    }
    preselectFundStrategyGeographyIndustry(){
        if(this.openFund.strategy) {
            this.openFund.strategy.forEach(element => {
                this.fundStrategyList.push(element.nameEn.toString());
            });
        }
        if(this.openFund.industry) {
            this.openFund.industry.forEach(element => {
                this.fundIndustryList.push(element.nameEn.toString());
            });
        }
        if(this.openFund.geography) {
            this.openFund.geography.forEach(element => {
                this.fundGeographyList.push(element.nameEn.toString());
            });
        }
    }

    drawGraph(){

        //var irrData = google.visualization.arrayToDataTable([
        //    ['Fund', 'Platinum', 'CA mid cap buyout'],
        //    ['Fund I', 61.9, 13],
        //    ['Fund II', 13.9, 6.83],
        //    ['Fund III', 40.1, 7.54],
        //]);
        //
        //var tvpiData = google.visualization.arrayToDataTable([
        //    ['Fund', 'Platinum', 'CA mid cap buyout'],
        //    ['Fund I', 2.56, 1.69],
        //    ['Fund II', 1.62, 1.3],
        //    ['Fund III', 1.58, 1.11],
        //]);

        //console.log(this.openFund);
        var irrData = new google.visualization.DataTable();
        irrData.addColumn('string', 'Fund');
        irrData.addColumn('number', this.openFund.firm.firmName);
        irrData.addColumn('number', 'CA');

        for(var i = 0; i < this.fundsList.length; i++) {
            if(this.fundsList[i].status == 'Closed'){
                if(this.fundsList[i].netIrr != NaN) {
                    irrData.addRow([this.fundsList[i].fundName, this.fundsList[i].netIrr, 30]);
                } else {
                    irrData.addRow([this.fundsList[i].fundName, 30, 30]);
                }
            }
        }

        var tvpiData = new google.visualization.DataTable();
        tvpiData.addColumn('string', 'Fund');
        tvpiData.addColumn('number', this.openFund.firm.firmName);
        tvpiData.addColumn('number', 'CA');

        for(var i = 0; i < this.fundsList.length; i++) {
            if(this.fundsList[i].status == 'Closed'){
                tvpiData.addRow([this.fundsList[i].fundName, this.fundsList[i].netTvpi, 1]);
            }
        }

        this.drawBarChart('bar-chart-1', irrData, 'NET IRR');
        this.drawBarChart('bar-chart-2', tvpiData, 'NET TVPI');
    }

    drawBarChart(id, data, title){
        var options = {
            showRowNumber: false,
            width: 400,
            height: 200,
            legend: {position: 'bottom', maxLines: 3},
            'allowHtml': true,
            cssClassNames: {},
            title: title,
        };
        var chart = new google.visualization.ColumnChart(document.getElementById(id));

        chart.draw(data, options);
    }
}