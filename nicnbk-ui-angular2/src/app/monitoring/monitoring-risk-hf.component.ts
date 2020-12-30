import {Component} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {CommonFormViewComponent} from "../common/common.component";
import {CommonTableau} from "./common-tableau.component";
import {GoogleChartComponent} from "../google-chart/google-chart.component";
import {TableChartDto, TableColumnDto} from "../google-chart/table-chart.dto";
import {MonitoringRiskHedgeFundService} from "./monitoring-risk-hf.service";
import {Subscription} from "../../../node_modules/rxjs";
import {ErrorResponse} from "../common/error-response";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {FileDownloadService} from "../common/file.download.service";
import {DATA_APP_URL} from "../common/common.service.constants";
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/forkJoin';

declare var google:any;
declare var $: any;

@Component({
    selector: 'monitoring-risk-hf',
    templateUrl: 'view/monitoring-risk-hf.component.html',
    styleUrls: [],
    providers: [MonitoringRiskHedgeFundService],
})
export class MonitoringRiskHedgeFundComponent extends GoogleChartComponent {

    private moduleAccessChecker = new ModuleAccessCheckerService;

    private tableDate;

    private subStrategyList = [];

    private myFiles: File[];

    busy: Subscription;

    availableDates: any[];

    selectedDate;
    selectedDateMonitoringInfo;
    selectedDateMonitoringInfoStressTests;

    constructor(
        private monitoringRiskHFService: MonitoringRiskHedgeFundService
    ){
        super();

        this.myFiles = [];

         Observable.forkJoin(
                    // Load dates
                    this.monitoringRiskHFService.getAvailableDates()
                    )
                    .subscribe(
                        ([data]) => {
                            this.availableDates = data;
                            if(this.availableDates != null && this.availableDates.length > 0){
                                this.selectDate(this.availableDates[0]);
                            }
                        },
                        (error) => {
                            this.errorMessage = "Error loading dates list.";
                            this.successMessage = null;
                        });
    }

    drawGraph(){
        //console.log("MonitoringRiskHedgeFundComponent: drawGraph");
        if(this.selectedDateMonitoringInfo != null){
            this.drawMarketSensitivitiesSinceInceptionMSCI();
            this.drawMarketSensitivitiesSinceInceptionBarclaysGblAgg();
            this.drawAllocationBySubStrategy();
            //this.drawAllocationBySubStrategy2();
        }
    }

    drawMarketSensitivitiesSinceInceptionMSCI(){
        //console.log("drawMarketSensitivitiesSinceInceptionMSCI");
        if(typeof google.charts === 'undefined'){
           console.log("google undefined");
           return;
       }
       if(this.selectedDateMonitoringInfo.marketSensitivitesMSCI == null || this.selectedDateMonitoringInfo.marketSensitivitesMSCI.length == 0){
            return;
       }

        var dataArr = [];
        dataArr.push(['', 'Portfolio', 'MSCI ACWI IMI', { role: 'style' }]);
        for(var i = 0; i < this.selectedDateMonitoringInfo.marketSensitivitesMSCI.length; i++){
            dataArr.push([this.selectedDateMonitoringInfo.marketSensitivitesMSCI[i].name,
                        Number((this.selectedDateMonitoringInfo.marketSensitivitesMSCI[i].portfolioValue * 100).toFixed(2)),
                        Number((this.selectedDateMonitoringInfo.marketSensitivitesMSCI[i].benchmarkValue * 100).toFixed(2)),
                        'grey']);
        }
        console.log(dataArr);

        var data = google.visualization.arrayToDataTable(dataArr);
        var formatNumber = new google.visualization.NumberFormat({
            pattern: '0.00'
          });
          formatNumber.format(data, 1);
          formatNumber.format(data, 2);

        var options = {
          chart: {
            title: '',
            subtitle: '',
            legend: {
                position: 'bottom', alignment: 'start'
            },
          },
          series: {
            0: {color: '#3664ab'},
            1: {color: 'grey'}
          }
        };

        var chart = new google.charts.Bar(document.getElementById('marketSensitivitiesSinceInceptionMSCI'));
        chart.draw(data, options);
    }

    drawMarketSensitivitiesSinceInceptionBarclaysGblAgg(){
        //console.log("drawMarketSensitivitiesSinceInceptionBarclaysGblAgg");

        if(typeof google.charts === 'undefined'){
           console.log("google undefined");
           return;
       }
       if(this.selectedDateMonitoringInfo.marketSensitivitesBarclays == null || this.selectedDateMonitoringInfo.marketSensitivitesBarclays.length == 0){
            return;
       }
        var dataArr = [];
        dataArr.push(['', 'Portfolio', 'Barclays Gbl Agg', { role: 'style' }]);
        for(var i = 0; i < this.selectedDateMonitoringInfo.marketSensitivitesBarclays.length; i++){
            dataArr.push([this.selectedDateMonitoringInfo.marketSensitivitesBarclays[i].name,
                        Number((this.selectedDateMonitoringInfo.marketSensitivitesBarclays[i].portfolioValue * 100).toFixed(2)),
                        Number((this.selectedDateMonitoringInfo.marketSensitivitesBarclays[i].benchmarkValue * 100).toFixed(2)),
                        'grey']);
        }
        console.log(dataArr);
        var data = google.visualization.arrayToDataTable(dataArr);

        var formatNumber = new google.visualization.NumberFormat({
            pattern: '0.00'
          });
          formatNumber.format(data, 1);
          formatNumber.format(data, 2);
        var options = {
          chart: {
            title: '',
            subtitle: '',
            legend: {
                position: 'bottom'
            }
          },
          width: '85%',
          series: {
              0: {color: '#3664ab'},
              1: {color: 'grey'}
            }
        };

        var chart = new google.charts.Bar(document.getElementById('marketSensitivitiesSinceInceptionBarclaysGblAgg'));
        chart.draw(data, options);
    }

    drawAllocationBySubStrategy() {
        if(typeof google.charts === 'undefined'){
            console.log("google undefined");
            return;
        }
        if(this.selectedDateMonitoringInfo.subStrategyAllocations == null || this.selectedDateMonitoringInfo.subStrategyAllocations.length == 0){
            return;
        }
        var dataArr = [];
        dataArr.push(['Sub-Strategy', this.selectedDateMonitoringInfo.subStrategyAllocations[0].firstDate,
            this.selectedDateMonitoringInfo.subStrategyAllocations[0].lastDate]);
        for (var i = 0; i < this.selectedDateMonitoringInfo.subStrategyAllocations.length; i++) {
            dataArr.push([this.selectedDateMonitoringInfo.subStrategyAllocations[i].subStrategyName,
                Number((this.selectedDateMonitoringInfo.subStrategyAllocations[i].currentValue * 100).toFixed(2)),
                Number((this.selectedDateMonitoringInfo.subStrategyAllocations[i].previousValue * 100).toFixed(2))]);
        }
        console.log(dataArr);
        var data = google.visualization.arrayToDataTable(dataArr);
        var formatNumber = new google.visualization.NumberFormat({
            pattern: '0.00'
        });
        formatNumber.format(data, 1);
        formatNumber.format(data, 2);
        var options = {
            chart: {
                title: '',
                subtitle: '',
            },
            bars: 'horizontal'
        };
        var chart = new google.charts.Bar(document.getElementById('allocation123'));
        chart.draw(data, google.charts.Bar.convertOptions(options));
    }

    drawAllocationBySubStrategy2(){
        var data = google.visualization.arrayToDataTable([
            ['Sub-strategy', '30.09.2020', '31.10.2020'],
            ['Other Investments', 0.1, 0.1],
            ['Specialist Equity', 1.2, 1.2],
            ['Non-Directional Quantiative', 1.3, 1.3],
            ['Event Driven', 1.6, 1.6],
            ['Structured Credit', 1.8, 1.8],
            ['Fundamental Credit', 4.7, 4.7],
            ['Diversified Multi-Strategy', 3.6, 3.6],
            ['Directional Equity', 12.4, 12.4],
            ['Diversified Macro', 4.4, 4.4],
            ['Option Volatility Arbitrage', 6.7, 6.7],
            ['Low Net Equity', 8.5, 8.5],
            ['Fundamental Market Neutral Equity', 14.1, 14.1],
            ['Diversified Relative Value', 35.7, 37.4]
        ]);

        var options = {
            chart: {
                title: '',
                subtitle: '',
            },
            bars: 'horizontal'
        };

        var chart = new google.charts.Bar(document.getElementById('allocationBySubStrategy'));

        chart.draw(data, google.charts.Bar.convertOptions(options));
    }

    selectDate(value){
        this.selectedDate = value;
        this.selectedDateMonitoringInfoStressTests = null;
        this.busy = this.monitoringRiskHFService.getMonthlyHFRiskReport(value)
            .subscribe(
                monitoringData => {
                    console.log(monitoringData);
                    this.selectedDateMonitoringInfo = monitoringData;
                    if(monitoringData.status != null && monitoringData.status === 'SUCCESS'){
                        this.selectedDateMonitoringInfo = monitoringData;
                        if(this.selectedDateMonitoringInfo != null && this.selectedDateMonitoringInfo.stressTests != null){
                            var stressTestsNames = [];
                            var stressTestsDates = [];
                            var stressTestsValues = [];
                            for(var i = 0; i < this.selectedDateMonitoringInfo.stressTests.length; i++){
                                var stressTestsNameItem = [];
                                var stressTestsDatesItem = [];
                                var stressTestsValueItem = [];
                                if(stressTestsNames.length > 0){
                                    stressTestsNameItem = stressTestsNames[stressTestsNames.length - 1];
                                    stressTestsDatesItem = stressTestsDates[stressTestsDates.length -1];
                                    stressTestsValueItem = stressTestsValues[stressTestsValues.length - 1];
                                    if(stressTestsNameItem.length == 5){
                                        stressTestsNames.push([]);
                                        stressTestsDates.push([]);
                                        stressTestsValues.push([]);
                                        stressTestsNameItem = stressTestsNames[stressTestsNames.length - 1];
                                        stressTestsDatesItem = stressTestsDates[stressTestsDates.length - 1];
                                        stressTestsValueItem = stressTestsValues[stressTestsValues.length - 1]
                                    }
                                }else{
                                    stressTestsNames.push(stressTestsNameItem);
                                    stressTestsDates.push(stressTestsDatesItem);
                                    stressTestsValues.push(stressTestsValueItem);
                                }
                                stressTestsNameItem.push(this.selectedDateMonitoringInfo.stressTests[i].name);
                                stressTestsDatesItem.push(this.selectedDateMonitoringInfo.stressTests[i].date);
                                stressTestsValueItem.push(this.selectedDateMonitoringInfo.stressTests[i].value);
                            }
                            console.log(stressTestsNames);
                            console.log(stressTestsDates);
                            console.log(stressTestsValues);
                            this.selectedDateMonitoringInfoStressTests = [];
                            for(var i = 0; i < stressTestsNames.length; i++){
                                this.selectedDateMonitoringInfoStressTests.push(stressTestsNames[i]);
                                this.selectedDateMonitoringInfoStressTests.push(stressTestsDates[i]);
                                this.selectedDateMonitoringInfoStressTests.push(stressTestsValues[i]);
                            }
                        }
                    }else{
                        if(monitoringData.message != null && monitoringData.message.nameEn != null && monitoringData.message.nameEn.trim() != ''{
                            this.errorMessage = monitoringData.message.nameEn;
                        }
                    }
                    this.drawGraph();
                },
                (error: ErrorResponse) => {
                     this.processErrorMessage(error);
                     this.postAction(null, error.message);
                     console.log(error);
                     console.log(error.message);
                }
        );
    }

    canEdit(){
        return this.moduleAccessChecker.checkAccessMonitoringEditor();
    }

    fileChange(files: any){
        this.myFiles = files;
        console.log(this.myFiles);
    }

    private getDataByDate(tableDate){
        for(var i = 0; i < this.subStrategyList.length; i++){
            if(this.subStrategyList[i].date === tableDate){
                return this.subStrategyList[i];
            }
        }
        return null;
    }

    private onSubmitSubStrategy() {
        this.busy = this.monitoringRiskHFService.postFiles(this.myFiles)
            .subscribe(
                (response) => {
                    this.subStrategyList = response.monitoringRiskHedgeFundAllocationSubStrategyDtoList;

                    console.log(response);
                    console.log(response.message.nameEn);

                    this.postAction(response.message.nameEn, null);

                    if(this.subStrategyList.length > 0) {
                        // this.tableDate = this.getAllDates()[0];
                        // this.tableDate = this.getAllDates()[0];
                        // this.drawActualAllocationChart(this.tableDate);
                        // this.drawNewlyCreatedAllocationChart(this.subStrategyList);

                        this.postAction(response.message.nameEn, null);

                        this.myFiles = [];
                        $("#fileupload").val(null);
                    }
                },
                error => {
                    // this.processErrorMessage(error);
                    this.postAction(null, JSON.parse(error).message.nameEn);
                    console.log(error);
                    console.log(JSON.parse(error).message.nameEn);
                }
            )
    }

    public getAllDates(){
        var dates = [];
        // for(var i = this.nav.length - 1; i >= 0; i--){
        //     dates.push(this.nav[i][0]);
        // }
        for(var i = this.subStrategyList.length - 1; i >= 0; i--){
            dates.push(this.subStrategyList[i].date);
        }
        return dates;
    }
}