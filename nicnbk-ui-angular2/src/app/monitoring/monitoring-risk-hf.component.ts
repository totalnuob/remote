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
    busy: Subscription;

    selectedDate;
    selectedDateMonitoringInfo;
    selectedDateMonitoringInfoStressTests;

    constructor(
        private monitoringRiskHFService: MonitoringRiskHedgeFundService
    ){
        super();

        var dates = this.getAllDates();
        if(dates != null && dates.length > 0){
            this.selectDate(dates[0]);
        }
    }

    drawGraph(){
        //console.log("MonitoringRiskHedgeFundComponent: drawGraph");
        if(this.selectedDateMonitoringInfo != null){
            this.drawMarketSensitivitiesSinceInceptionMSCI();
            this.drawMarketSensitivitiesSinceInceptionBarclaysGblAgg();
        }
    }

    getAllDates(){
        return ["31-12-2019", "30-11-2019", "31-10-2019", '30-09-2019'];
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
                            var stressTestsValues = [];
                            for(var i = 0; i < this.selectedDateMonitoringInfo.stressTests.length; i++){
                                var stressTestsNameItem = [];
                                var stressTestsValueItem = [];
                                if(stressTestsNames.length > 0){
                                    stressTestsNameItem = stressTestsNames[stressTestsNames.length - 1];
                                    stressTestsValueItem = stressTestsValues[stressTestsValues.length - 1];
                                    if(stressTestsNameItem.length == 5){
                                        stressTestsNames.push([]);
                                        stressTestsValues.push([]);
                                        stressTestsNameItem = stressTestsNames[stressTestsNames.length - 1];
                                        stressTestsValueItem = stressTestsValues[stressTestsValues.length - 1]
                                    }
                                }else{
                                    stressTestsNames.push(stressTestsNameItem);
                                    stressTestsValues.push(stressTestsValueItem);
                                }
                                stressTestsNameItem.push(this.selectedDateMonitoringInfo.stressTests[i].name);
                                stressTestsValueItem.push(this.selectedDateMonitoringInfo.stressTests[i].value);
                            }
                            console.log(stressTestsNames);
                            console.log(stressTestsValues);
                            this.selectedDateMonitoringInfoStressTests = [];
                            for(var i = 0; i < stressTestsNames.length; i++){
                                this.selectedDateMonitoringInfoStressTests.push(stressTestsNames[i]);
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
}