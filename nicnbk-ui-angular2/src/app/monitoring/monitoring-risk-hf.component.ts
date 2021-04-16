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
    templateUrl: 'view/monitoring-risk-hf.component2.html',
    styleUrls: [],
    providers: [MonitoringRiskHedgeFundService],
})
export class MonitoringRiskHedgeFundComponent extends GoogleChartComponent {

    reportMonth;
    reportYear;
    reportYearList = [];

    modalErrorMessage;
    modalSuccessMessage;

    private moduleAccessChecker = new ModuleAccessCheckerService;

    private tableDate;

    returnsFileClassA: File[];
    returnsFileClassB: File[];
    returnsFileCons: File[];
    allocationsFileCons: File[];

    private subStrategyList = [];

    private topPortfolioList = [];

    private myFiles: File[];

    private myFilesTopPortfolio: File[];

    busy: Subscription;

    availableDates: any[];
    availablePrevDates: any[];

    selectedDate;
    selectedPrevDate;
    selectedDateMonitoringInfo = {};

    selectedDateMonitoringInfoStressTests;

    constructor(
        private monitoringRiskHFService: MonitoringRiskHedgeFundService
    ){
        super();

        this.myFiles = [];
        this.myFilesTopPortfolio = [];

        this.returnsFileClassA = [];
        this.returnsFileClassB = [];
        this.returnsFileCons = [];
        this.allocationsFileCons = [];

        var currentYear = new Date().getFullYear();
        for(var y = 2016; y <= currentYear; y++){
            this.reportYearList.push(y);
        }

         Observable.forkJoin(
                    // Load dates
                    this.busy = this.monitoringRiskHFService.getAvailableDates()
                    )
                    .subscribe(
                        ([data]) => {
                            this.availableDates = data;
                            if(this.availableDates != null && this.availableDates.length > 0){
                                this.availablePrevDates = [];
                                for(var i = 1; i < this.availableDates.length; i++){
                                    this.availablePrevDates.push(this.availableDates[i]);
                                }
                                if(this.availablePrevDates != null && this.availablePrevDates.length > 0){
                                    this.selectedPrevDate = this.availablePrevDates[0];
                                }
                                //console.log(this.availablePrevDates);
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
            this.drawMarketSensitivitiesSinceInceptionLEGATRUH();
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
            // clear graph
            $('#marketSensitivitiesSinceInceptionMSCI').hide();
            return;
       }

       $('#marketSensitivitiesSinceInceptionMSCI').show();
        var dataArr = [];
        dataArr.push(['', 'Portfolio', 'MSCI ACWI IMI', { role: 'style' }]);
        for(var i = 0; i < this.selectedDateMonitoringInfo.marketSensitivitesMSCI.length; i++){
            dataArr.push([this.selectedDateMonitoringInfo.marketSensitivitesMSCI[i].name,
                        Number((this.selectedDateMonitoringInfo.marketSensitivitesMSCI[i].portfolioValue * 100).toFixed(2)),
                        Number((this.selectedDateMonitoringInfo.marketSensitivitesMSCI[i].benchmarkValue * 100).toFixed(2)),
                        'grey']);
        }
        //console.log(dataArr);

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

        //console.log(document.getElementById('marketSensitivitiesSinceInceptionMSCI'));
        var chart = new google.charts.Bar(document.getElementById('marketSensitivitiesSinceInceptionMSCI'));
        chart.draw(data, options);
    }

    drawMarketSensitivitiesSinceInceptionLEGATRUH(){
        //console.log("drawMarketSensitivitiesSinceInceptionLEGATRUH");

        if(typeof google.charts === 'undefined'){
           console.log("google undefined");
           return;
       }
       if(this.selectedDateMonitoringInfo.marketSensitivitesBarclays == null || this.selectedDateMonitoringInfo.marketSensitivitesBarclays.length == 0){
            $('#marketSensitivitiesSinceInceptionLEGATRUH').hide();
            return;
       }
       $('#marketSensitivitiesSinceInceptionLEGATRUH').show();
        var dataArr = [];
        dataArr.push(['', 'Portfolio', 'LEGATRUH', { role: 'style' }]);
        for(var i = 0; i < this.selectedDateMonitoringInfo.marketSensitivitesBarclays.length; i++){
            dataArr.push([this.selectedDateMonitoringInfo.marketSensitivitesBarclays[i].name,
                        Number((this.selectedDateMonitoringInfo.marketSensitivitesBarclays[i].portfolioValue * 100).toFixed(2)),
                        Number((this.selectedDateMonitoringInfo.marketSensitivitesBarclays[i].benchmarkValue * 100).toFixed(2)),
                        'grey']);
        }
        //console.log(dataArr);
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

        var chart = new google.charts.Bar(document.getElementById('marketSensitivitiesSinceInceptionLEGATRUH'));
        chart.draw(data, options);
    }

    drawAllocationBySubStrategy() {
        if(typeof google.charts === 'undefined'){
            console.log("google undefined");
            return;
        }
        if(this.selectedDateMonitoringInfo.subStrategyAllocations == null || this.selectedDateMonitoringInfo.subStrategyAllocations.length == 0){
            $('#allocationByStrategy').hide();
            return;
        }
        $('#allocationByStrategy').show();
        var dataArr = [];
        var prevDate = this.selectedDateMonitoringInfo.subStrategyAllocations[0].previousDate != null ?
                            this.selectedDateMonitoringInfo.subStrategyAllocations[0].previousDate : "";
        dataArr.push(['Strategy', prevDate, this.selectedDateMonitoringInfo.subStrategyAllocations[0].currentDate]);
        for (var i = 0; i < this.selectedDateMonitoringInfo.subStrategyAllocations.length; i++) {
            var prevValue = this.selectedDateMonitoringInfo.subStrategyAllocations[i].previousValue != null ?
                            this.selectedDateMonitoringInfo.subStrategyAllocations[i].previousValue : 0.0;
            dataArr.push([this.selectedDateMonitoringInfo.subStrategyAllocations[i].subStrategyName,
                Number((prevValue * 100).toFixed(2)),
                Number((this.selectedDateMonitoringInfo.subStrategyAllocations[i].currentValue * 100).toFixed(2))
                ]);
        }
        //console.log(dataArr);
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
        var chart = new google.charts.Bar(document.getElementById('allocationByStrategy'));
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

    selectPrevDate(prevDate){
        this.selectedPrevDate = prevDate;
        this.selectDate(this.selectedDate, this.selectedPrevDate);
    }

    selectDate(value, prevDate){
        this.selectedDate = value;
        this.availablePrevDates = [];
        this.selectedPrevDate = null;
        var startPrevious = false;
        for(var i = 0; i < this.availableDates.length; i++){
            if(this.availableDates[i] === this.selectedDate){
                startPrevious = true;
                continue;
            }
            if(startPrevious){
                this.availablePrevDates.push(this.availableDates[i]);
            }
        }
        if(prevDate != null){
             this.selectedPrevDate = prevDate;
        }else if(this.availablePrevDates != null && this.availablePrevDates.length > 0){
            this.selectedPrevDate = this.availablePrevDates[0];
        }
        this.selectedDateMonitoringInfoStressTests = null;
        this.busy = this.monitoringRiskHFService.getMonthlyHFRiskReport(value, this.selectedPrevDate)
            .subscribe(
                monitoringData => {
                    //console.log(monitoringData);
                    this.selectedDateMonitoringInfo = monitoringData;
                    if(monitoringData.status != null && monitoringData.status === 'SUCCESS'){
                        this.selectedDateMonitoringInfo = monitoringData;
                        //console.log(this.selectedDateMonitoringInfo);
                        if(this.selectedDateMonitoringInfo != null && this.selectedDateMonitoringInfo.stressTests != null){
                            var stressTestsNames = [];
                            //var stressTestsDates = [];
                            var stressTestsValues = [];
                            for(var i = 0; i < this.selectedDateMonitoringInfo.stressTests.length; i++){
                                var stressTestsNameItem = [];
                                //var stressTestsDatesItem = [];
                                var stressTestsValueItem = [];
                                if(stressTestsNames.length > 0){
                                    stressTestsNameItem = stressTestsNames[stressTestsNames.length - 1];
                                    //stressTestsDatesItem = stressTestsDates[stressTestsDates.length -1];
                                    stressTestsValueItem = stressTestsValues[stressTestsValues.length - 1];
                                    if(stressTestsNameItem.length == 5){
                                        stressTestsNames.push([]);
                                       //stressTestsDates.push([]);
                                        stressTestsValues.push([]);
                                        stressTestsNameItem = stressTestsNames[stressTestsNames.length - 1];
                                        //stressTestsDatesItem = stressTestsDates[stressTestsDates.length - 1];
                                        stressTestsValueItem = stressTestsValues[stressTestsValues.length - 1]
                                    }
                                }else{
                                    stressTestsNames.push(stressTestsNameItem);
                                    //stressTestsDates.push(stressTestsDatesItem);
                                    stressTestsValues.push(stressTestsValueItem);
                                }
                                stressTestsNameItem.push(this.selectedDateMonitoringInfo.stressTests[i].name);
                                //stressTestsDatesItem.push(this.selectedDateMonitoringInfo.stressTests[i].date);
                                stressTestsValueItem.push(this.selectedDateMonitoringInfo.stressTests[i].value);
                            }
                            this.selectedDateMonitoringInfoStressTests = [];
                            for(var i = 0; i < stressTestsNames.length; i++){
                                this.selectedDateMonitoringInfoStressTests.push(stressTestsNames[i]);
                                //this.selectedDateMonitoringInfoStressTests.push(stressTestsDates[i]);
                                this.selectedDateMonitoringInfoStressTests.push(stressTestsValues[i]);
                            }
                            //console.log(this.selectedDateMonitoringInfoStressTests);
                        }
                    }else{
                        if(monitoringData.message != null && monitoringData.message.nameEn != null && monitoringData.message.nameEn.trim() != ''){
                            this.errorMessage = monitoringData.message.nameEn;
                        }
                    }
                    //console.log("draw after select date");
                    //console.log(this.selectedDateMonitoringInfo);
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
        //console.log(this.myFiles);
    }

    onFileChangeReturnsClassA(event){
        var target = event.target || event.srcElement;
        var files = target.files;
        this.returnsFileClassA.length = 0;
        for (var i = 0; i < files.length; i++) {
            this.returnsFileClassA.push(files[i]);
        }
    }

    removeUnsavedReturnsFileClassA(){
        this.returnsFileClassA = [];
    }

    onFileChangeReturnsClassB(files: any){
        var target = event.target || event.srcElement;
        var files = target.files;
        this.returnsFileClassB.length = 0;
        for (var i = 0; i < files.length; i++) {
            this.returnsFileClassB.push(files[i]);
        }
    }

    removeUnsavedReturnsFileClassB(){
        this.returnsFileClassB = [];
    }

    onFileChangeReturnsCons(files: any){
        var target = event.target || event.srcElement;
        var files = target.files;
        this.returnsFileCons.length = 0;
        for (var i = 0; i < files.length; i++) {
            this.returnsFileCons.push(files[i]);
        }
    }
    removeUnsavedReturnsFileCons(){
        this.returnsFileCons = [];
    }

    onFileChangeAllocationsCons(files: any){
        var target = event.target || event.srcElement;
        var files = target.files;
        this.allocationsFileCons.length = 0;
        for (var i = 0; i < files.length; i++) {
            this.allocationsFileCons.push(files[i]);
        }
    }

    removeUnsavedAllocationsFileCons(){
        this.allocationsFileCons = [];
    }

    fileChangeTopPortfolio(files: any){
        this.myFilesTopPortfolio = files;
        //console.log(this.myFilesTopPortfolio);
    }

    private getDataByDate(tableDate){
        for(var i = 0; i < this.subStrategyList.length; i++){
            if(this.subStrategyList[i].date === tableDate){
                return this.subStrategyList[i];
            }
        }
        return null;
    }

    private onExportTopPortfolio() {
        this.busy = this.monitoringRiskHFService.exportTopPortfolio(this.selectedDate).
            subscribe(
                response => {
                    //console.log("ok");
                },
                error => {
                    //console.log(this.selectedDate)
                    this.postAction(null, "Error exporting data");
                }
        );
    }

    private onDeleteTopPortfolio() {
        if (confirm('Are you sure?')) {
            this.busy = this.monitoringRiskHFService.deleteTopPortfolio(this.selectedDate).
            subscribe(
                response => {
                    //console.log("ok");

                    this.postAction(response.messageEn, null);
                },
                error => {
                    //console.log(this.selectedDate)
                    this.postAction(null, "Error deleting data");
                }
            );
        } else {
            this.postAction(null, null);
            //console.log('Not deleted');
        }
    }

    private onSubmitTopPortfolio() {
        this.busy = this.monitoringRiskHFService.postFilesTopPortfolio(this.myFilesTopPortfolio)
            .subscribe(
                (response) => {
                    this.topPortfolioList = response.MonitoringRiskHedgeFundFundAllocationDtoList;

                    //console.log(response);
                    //console.log(response.message.nameEn);

                    this.postAction(response.message.nameEn, null);

                    if(this.topPortfolioList.length > 0) {
                        // this.tableDate = this.getAllDates()[0];
                        // this.tableDate = this.getAllDates()[0];
                        // this.drawActualAllocationChart(this.tableDate);
                        // this.drawNewlyCreatedAllocationChart(this.subStrategyList);

                        this.postAction(response.message.nameEn, null);
                        //console.log("topPortfolioList > 0");

                        this.myFilesTopPortfolio = [];
                        $("#fileuploadTopPortfolio").val(null);
                    }
                },
                error => {
                    // this.processErrorMessage(error);
                    this.postAction(null, JSON.parse(error).message.nameEn);
                    //console.log(error);
                    //console.log(JSON.parse(error).message.nameEn);
                }
            )
    }

    private onExportSubStrategy() {
        this.busy = this.monitoringRiskHFService.exportSubStrategy(this.selectedDate).
        subscribe(
            response => {
                //console.log("ok");
            },
            error => {
                //console.log(this.selectedDate)
                this.postAction(null, "Error exporting data");
            }
        );
    }

    deleteReturnsFileClassA(){
        if (confirm('Are you sure want to delete?')) {
            this.busy = this.monitoringRiskHFService.deleteReturnsClassAFile(this.selectedDateMonitoringInfo.reportId).
            subscribe(
                response => {
                    //console.log("ok");
                    this.postAction(response.messageEn, null);
                    this.selectDate(this.selectedDate);
                },
                error => {
                    //console.log(this.selectedDate)
                    this.postAction(null, "Error deleting data");
                }
            );
        } else {
            this.postAction(null, null);
            //console.log('Not deleted');
        }
    }
    deleteReturnsFileClassB(){
        if (confirm('Are you sure want to delete?')) {
            this.busy = this.monitoringRiskHFService.deleteReturnsClassBFile(this.selectedDateMonitoringInfo.reportId).
            subscribe(
                response => {
                    //console.log("ok");
                    this.postAction(response.messageEn, null);
                    this.selectDate(this.selectedDate);
                },
                error => {
                    //console.log(this.selectedDate)
                    this.postAction(null, "Error deleting data");
                }
            );
        } else {
            this.postAction(null, null);
            //console.log('Not deleted');
        }
    }
    deleteReturnsFileCons(){
        if (confirm('Are you sure want to delete?')) {
            this.busy = this.monitoringRiskHFService.deleteReturnsConsFile(this.selectedDateMonitoringInfo.reportId).
            subscribe(
                response => {
                    //console.log("ok");
                    this.postAction(response.messageEn, null);
                    this.selectDate(this.selectedDate);
                },
                error => {
                    //console.log(this.selectedDate)
                    this.postAction(null, "Error deleting data");
                }
            );
        } else {
            this.postAction(null, null);
            //console.log('Not deleted');
        }
    }

    deleteAllocationsFileCons(){
        if (confirm('Are you sure want to delete?')) {
            this.busy = this.monitoringRiskHFService.deleteAllocationsConsFile(this.selectedDateMonitoringInfo.reportId).
            subscribe(
                response => {
                    //console.log("ok");
                    this.postAction(response.messageEn, null);
                    this.selectDate(this.selectedDate);
                },
                error => {
                    //console.log(this.selectedDate)
                    this.postAction(null, "Error deleting data");
                }
            );
        } else {
            this.postAction(null, null);
            //console.log('Not deleted');
        }
    }

    private onDeleteSubStrategy() {
        if (confirm('Are you sure?')) {
            this.busy = this.monitoringRiskHFService.deleteSubStrategy(this.selectedDate).
            subscribe(
                response => {
                    //console.log("ok");

                    this.postAction(response.messageEn, null);
                },
                error => {
                    //console.log(this.selectedDate)
                    this.postAction(null, "Error deleting data");
                }
            );
        } else {
            this.postAction(null, null);
            //console.log('Not deleted');
        }
    }

    private onSubmitSubStrategy() {
        this.busy = this.monitoringRiskHFService.postFiles(this.myFiles)
            .subscribe(
                (response) => {
                    this.subStrategyList = response.monitoringRiskHedgeFundAllocationSubStrategyDtoList;

                    //console.log(response);
                    //console.log(response.message.nameEn);

                    this.postAction(response.message.nameEn, null);

                    if(this.subStrategyList.length > 0) {

                        this.postAction(response.message.nameEn, null);

                        this.myFiles = [];
                        $("#fileuploadSubStrategy").val(null);
                    }
                },
                error => {
                    // this.processErrorMessage(error);
                    this.postAction(null, JSON.parse(error).message.nameEn);
                    //console.log(error);
                    //console.log(JSON.parse(error).message.nameEn);
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

    public uploadReturnsClassA(){
        var data = JSON.stringify({"report":{"id": this.selectedDateMonitoringInfo.reportId}, "fileType": 'MONHFRISK1'});
        this.busy = this.monitoringRiskHFService.uploadReturns(this.returnsFileClassA, data)
            .subscribe(
                (response) => {
                    this.postAction("Successfully saved Class A Returns", null);
                    this.returnsFileClassA = [];
                     $("#fileUploadReturnsClassA").val(null);
                     this.selectDate(this.selectedDate);
                },
                error => {
                    var errorResponse = JSON.parse(error);
                    if(errorResponse != null && errorResponse.message != null && errorResponse.message.nameEn != null){
                        this.errorMessage = errorResponse.message.nameEn;
                    }else if(errorResponse != null && errorResponse.errorMessageEn != null){
                        this.errorMessage = errorResponse.errorMessageEnn;
                    }else {
                        this.errorMessage = "Failed to parse returns class A file";
                    }
                     this.returnsFileClassA = [];
                     $("#fileUploadReturnsClassA").val(null);
                    this.postAction(null, this.errorMessage);
                }
            )
    }
    public uploadReturnsClassB(){
        var data = JSON.stringify({"report":{"id": this.selectedDateMonitoringInfo.reportId}, "fileType": 'MONHFRISK2'});
        this.busy = this.monitoringRiskHFService.uploadReturns(this.returnsFileClassB, data)
            .subscribe(
                (response) => {
                    this.postAction("Successfully saved Class B Returns", null);
                    this.returnsFileClassB = [];
                     $("#fileUploadReturnsClassB").val(null);
                     this.selectDate(this.selectedDate);
                },
                error => {
                    var errorResponse = JSON.parse(error);
                    if(errorResponse != null && errorResponse.message != null && errorResponse.message.nameEn != null){
                        this.errorMessage = errorResponse.message.nameEn;
                    }else if(errorResponse != null && errorResponse.errorMessageEn != null){
                        this.errorMessage = errorResponse.errorMessageEnn;
                    }else {
                        this.errorMessage = "Failed to parse returns class B file";
                    }
                     this.returnsFileClassB = [];
                     $("#fileUploadReturnsClassB").val(null);
                    this.postAction(null, this.errorMessage);
                }
            )
    }
    public uploadReturnsCons(){
        var data = JSON.stringify({"report":{"id": this.selectedDateMonitoringInfo.reportId}, "fileType": 'MONHFRISK3'});
        this.busy = this.monitoringRiskHFService.uploadReturns(this.returnsFileCons, data)
            .subscribe(
                (response) => {
                    this.postAction("Successfully saved Cons Returns", null);
                    this.returnsFileCons = [];
                     $("#fileUploadReturnsCons").val(null);
                     this.selectDate(this.selectedDate);
                },
                error => {
                    var errorResponse = JSON.parse(error);
                    if(errorResponse != null && errorResponse.message != null && errorResponse.message.nameEn != null){
                        this.errorMessage = errorResponse.message.nameEn;
                    }else if(errorResponse != null && errorResponse.errorMessageEn != null){
                        this.errorMessage = errorResponse.errorMessageEnn;
                    }else {
                        this.errorMessage = "Failed to parse returns cons file";
                    }
                     this.returnsFileCons = [];
                     $("#fileUploadReturnsCons").val(null);
                    this.postAction(null, this.errorMessage);
                }
            )
    }


    public uploadAllocationsCons(){
        var data = JSON.stringify({"report":{"id": this.selectedDateMonitoringInfo.reportId}, "fileType": 'MONHFRISK4'});
        this.busy = this.monitoringRiskHFService.uploadAllocations(this.allocationsFileCons, data)
            .subscribe(
                (response) => {
                    this.postAction("Successfully saved Cons Allocations", null);
                    this.allocationsFileCons = [];
                     $("#fileUploadAllocationsCons").val(null);
                     this.selectDate(this.selectedDate);
                },
                error => {
                    //console.log(error);
                    var errorResponse = JSON.parse(error);
                    if(errorResponse != null && errorResponse.message != null && errorResponse.message.nameEn != null){
                        this.errorMessage = errorResponse.message.nameEn;
                    }else if(errorResponse != null && errorResponse.errorMessageEn != null){
                        this.errorMessage = errorResponse.errorMessageEnn;
                    }else {
                        this.errorMessage = "Failed to parse allocations file cons";
                    }
                     this.allocationsFileCons = [];
                     $("#fileUploadAllocationsCons").val(null);
                    this.postAction(null, this.errorMessage);
                }
            )
    }

    public createNewReport(){
        var report = {"reportDate": null};
        var reportDate = new Date(Number(this.reportYear), Number(this.reportMonth), 0); //'01-' + this.reportMonth + '-' + this.reportYear;
        report.reportDate = reportDate.getDate() + "-" + (reportDate.getMonth() + 1 < 10 ? "0" + (reportDate.getMonth() + 1) : reportDate.getMonth() + 1) + "-" + reportDate.getFullYear();
        //console.log(report);
        if(this.availableDates && this.availableDates.length > 0){
            for(var i = 0; i < this.availableDates.length; i++){
                if(report.reportDate == this.availableDates[i]){
                    this.modalErrorMessage = "Report date already exists";
                    this.report = null;
                    return false;
                }
            }
        }

        this.busyCreate = this.monitoringRiskHFService.saveReport(report)
            .subscribe(
                (response: SaveResponse)  => {
                    this.modalSuccessMessage = response.message != null && response.message.nameEn != null ?
                        response.message.nameEn : "Successfully saved report";
                    this.modalErrorMessage = null;
                     this.busy = this.monitoringRiskHFService.getAvailableDates()
                            .subscribe(
                                response  => {
                                    this.availableDates = response;
                                    if(this.availableDates != null && this.availableDates.length > 0){
                                        this.selectDate(this.availableDates[0]);
                                    }
                                },
                                (error: ErrorResponse) => {
                                    this.processErrorResponse(error);
                                }
                            )
                },
                (error) => {
                    this.processErrorResponse(error);
                }
            )
    }

    closeModal(){
        $('#newReportModal').modal('toggle');

        this.reportMonth = null;
        this.reportYear = null;
        this.modalErrorMessage = null;
        this.modalSuccessMessage = null;

    }

    openCreateReportModal(){
        this.reportMonth = null;
        this.reportYear = null;
        this.modalErrorMessage = null;
        this.modalSuccessMessage = null;
    }
}