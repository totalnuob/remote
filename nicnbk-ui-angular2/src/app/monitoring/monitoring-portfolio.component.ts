import {Component, AfterViewInit, ViewChild} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {CommonFormViewComponent} from "../common/common.component";
import {CommonTableau} from "./common-tableau.component";
import {GoogleChartComponent} from "../google-chart/google-chart.component";
import {TableChartDto, TableColumnDto} from "../google-chart/table-chart.dto";
import {MonitoringPortfolioService} from "./monitoring-portfolio.service";
import {Subscription} from "../../../node_modules/rxjs";
import {ErrorResponse} from "../common/error-response";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {FileDownloadService} from "../common/file.download.service";
import {DATA_APP_URL} from "../common/common.service.constants";

declare var google:any;
declare var $: any;

@Component({
    selector: 'monitoring-portfolio',
    // templateUrl: 'view/monitoring-portfolio.component.html',  //if used, check redrawLineChart(), getAssetTypes() methods
    templateUrl: 'view/monitoring-portfolio.component2.html',
    styleUrls: [],
    providers: [MonitoringPortfolioService],
})
export class MonitoringPortfolioComponent extends GoogleChartComponent {
    //ngAfterViewInit():void {
    //    this.tableau_func()
    //}

    private tableDate;
    // private performanceType;

    private nicPortfolioList = [];

    private moduleAccessChecker = new ModuleAccessCheckerService;

    private myFiles: File[];

    private FILE_DOWNLOAD_URL = DATA_APP_URL + "monitoring/portfolio/download/";

    busy: Subscription;

    constructor(
        private monitoringPortfolioService: MonitoringPortfolioService,
        private downloadService: FileDownloadService,
    ){
        super();

        this.myFiles = [];

        //this.getLineChartLAternativePerformance();
        //this.getLineChartLPublicPerformance();

        // TODO: wait/sync on data loading
        // TODO: sync on subscribe results
        //this.waitSleep(700);

        //$("#tableDate").val($("#tableDate option:first").val());
        //$("#performanceType").val($("#performanceType option:first").val());

    }

    drawGraph(){
        this.getDataAndDraw();
    }

    redraw(){
        // this.redrawTables();
        // this.redrawActualAllocationPieChart();
        this.redrawTablesAndActualAllocationPieChart();
    }

    // public redrawTables(){
    //     //alert($('#tableDate').val());
    //     this.drawTables($("#tableDate").val());
    // }

    // public redrawActualAllocationPieChart(){
    //     //alert($('#tableDate').val());
    //     this.drawActualAllocationChart($("#tableDate").val());
    // }

    public redrawTablesAndActualAllocationPieChart(){
        this.drawTablesAndActualAllocationChart($("#tableDate").val());
    }

    // public redrawLineChart(){
    //     this.drawAlternativePerformanceChart($('#performanceType').val());
    // }

    // drawTables(tableDate){
    //     var NAVdata = this.getNAVData(tableDate);
    //     this.drawPortfolioValueTable(NAVdata);
    //
    //     var portfolioPerformanceData = this.getPortfolioPerformanceData(tableDate);
    //     this.drawPortfolioPerformanceTable(portfolioPerformanceData);
    //
    //     //var benchmarkPerformanceData = this.getBenchmarkPerformanceData(tableDate);
    //     //this.drawBenchmarksPerformanceTable(benchmarkPerformanceData);
    // }

    drawTablesAndActualAllocationChart(tableDate){
        this.drawPortfolioValueTableAndActualAllocationChart(tableDate);

        var portfolioPerformanceData = this.getPortfolioPerformanceData(tableDate);
        this.drawPortfolioPerformanceTable(portfolioPerformanceData);
    }

    // NAV ------------------------------

    // getNAVData(tableDate){
    //     var data = new google.visualization.DataTable();
    //     var formatter = new google.visualization.NumberFormat({
    //         prefix:'$ ',
    //         groupingSymbol: ' ',
    //         fractionDigits: 0
    //     });
    //     data.addColumn("string", "");
    //     data.addColumn("number", "NAV");
    //
    //     // var NAVarray = this.getNAVByDate(tableDate);
    //     // if(tableDate.endsWith("19")) {
    //     //     data.addRows([
    //     //         ["Private Equity", NAVarray[1]],
    //     //         ["Hedge Funds", NAVarray[2]],
    //     //         ["Real Estate", NAVarray[3]],
    //     //         ["Fixed income securities", NAVarray[4]],
    //     //         ["Public Equity", NAVarray[5]],
    //     //         ["Transition portfolio", NAVarray[6]],
    //     //         ["NICK MF Other", NAVarray[7]],
    //     //         ["Total", NAVarray[8]],
    //     //         ["Transfer", NAVarray[9]]
    //     //     ]);
    //     // }else {
    //     //     data.addRows([
    //     //         ["NIC Total", NAVarray[1]],
    //     //         ["Liquid Portfolio", NAVarray[2]],
    //     //         ["NICK Master Fund", NAVarray[3]],
    //     //         ["Hedge Funds Portfolio", NAVarray[5]],
    //     //         ["Private Equity Portfolio", NAVarray[4]],
    //     //         ["NICK Master Fund Cash", NAVarray[6]]
    //     //     ]);
    //     // }
    //
    //     var currentData = this.getDataByDate(tableDate);
    //     if(currentData != null) {
    //         if(currentData.nicTotalAumNav != null) {
    //             data.addRows([["NIC total AUM", currentData.nicTotalAumNav]]);
    //         }
    //         if(currentData.transitionPortfolioNav != null) {
    //             data.addRows([["Transition portfolio", currentData.transitionPortfolioNav]]);
    //         }
    //         if(currentData.alternativePortfolioNav != null) {
    //             data.addRows([["Alternative portfolio", currentData.alternativePortfolioNav]]);
    //         }
    //         if(currentData.fixedPortfolioNav != null) {
    //             data.addRows([["Fixed portfolio", currentData.fixedPortfolioNav]]);
    //         }
    //         if(currentData.equityPortfolioNav != null) {
    //             data.addRows([["Equity portfolio", currentData.equityPortfolioNav]]);
    //         }
    //         if(currentData.hedgeFundsNav != null) {
    //             data.addRows([["Hedge funds", currentData.hedgeFundsNav]]);
    //         }
    //         if(currentData.privateEquityNav != null) {
    //             data.addRows([["Private equity", currentData.privateEquityNav]]);
    //         }
    //         if(currentData.realEstateNav != null) {
    //             data.addRows([["Real estate", currentData.realEstateNav]]);
    //         }
    //         if(currentData.nickMfOtherNav != null) {
    //             data.addRows([["NICK Mf other", currentData.nickMfOtherNav]]);
    //         }
    //         if(currentData.transferNav != null) {
    //             data.addRows([["Transfer portfolio", currentData.transferNav]]);
    //         }
    //     }
    //
    //     formatter.format(data,1);
    //     return data;
    // }

    // private getNAVByDate(tableDate){
    //
    //     for(var i = 0; i < this.nav.length; i++){
    //         if(this.nav[i][0] === tableDate){
    //             return this.nav[i];
    //         }
    //     }
    //     return null;
    // }

    // drawPortfolioValueTable(data){
    //     var options = {
    //         showRowNumber: false,
    //         width: '100%',
    //         height: '100%',
    //         'allowHtml': true,
    //         cssClassNames: {}
    //     };
    //
    //     //var chart = this.createTableChart(document.getElementById('portfolio_value'));
    //     var chart = this.createTableChart(document.getElementById('portfolio_nav_table'));
    //     chart.draw(data, options);
    // }

    drawPortfolioValueTableAndActualAllocationChart(tableDate){
        var tableData = this.getDataByDate(tableDate);

        var dataNAV = new google.visualization.DataTable();

        var formatter = new google.visualization.NumberFormat({
            prefix:'$ ',
            groupingSymbol: ' ',
            fractionDigits: 0
        });

        dataNAV.addColumn("string", "");
        dataNAV.addColumn("number", "NAV");

        if(tableData != null) {
            if(tableData.nicTotalAumNav != null) {
                dataNAV.addRows([["NIC total AUM", tableData.nicTotalAumNav]]);
                dataNAV.setProperty(0, 0, 'style', 'font-weight: bold;')
                dataNAV.setProperty(0, 1, 'style', 'font-weight: bold;')
            }
            if(tableData.transitionPortfolioNav != null) {
                dataNAV.addRows([["&nbsp;&nbsp;&nbsp;&nbsp;" + "Transition portfolio", tableData.transitionPortfolioNav]]);
            }
            if(tableData.alternativePortfolioNav != null) {
                dataNAV.addRows([["&nbsp;&nbsp;&nbsp;&nbsp;" + "Alternative portfolio", tableData.alternativePortfolioNav]]);
            }
            if(tableData.fixedPortfolioNav != null) {
                dataNAV.addRows([["&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + "Fixed portfolio", tableData.fixedPortfolioNav]]);
            }
            if(tableData.equityPortfolioNav != null) {
                dataNAV.addRows([["&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + "Equity portfolio", tableData.equityPortfolioNav]]);
            }
            if(tableData.hedgeFundsNav != null) {
                dataNAV.addRows([["&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + "Hedge funds", tableData.hedgeFundsNav]]);
            }
            if(tableData.privateEquityNav != null) {
                dataNAV.addRows([["&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + "Private equity", tableData.privateEquityNav]]);
            }
            if(tableData.realEstateNav != null) {
                dataNAV.addRows([["&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + "Real estate", tableData.realEstateNav]]);
            }
            if(tableData.nickMfOtherNav != null) {
                dataNAV.addRows([["&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + "NICK Mf other", tableData.nickMfOtherNav]]);
            }
            if(tableData.transferNav != null) {
                dataNAV.addRows([["&nbsp;&nbsp;&nbsp;&nbsp;" + "Transfer portfolio", tableData.transferNav]]);
            }

            var dataActualAllocation = google.visualization.arrayToDataTable(
                [
                    ["Allocation", "%"],
                    ["Transition portfolio", 0],
                    ["Fixed portfolio", tableData.fixedPortfolioNav != null ? tableData.fixedPortfolioNav : 0],
                    ["Equity portfolio", tableData.equityPortfolioNav != null ? tableData.equityPortfolioNav : 0],
                    ["Hedge funds", tableData.hedgeFundsNav != null ? tableData.hedgeFundsNav : 0],
                    ["Private equity", tableData.privateEquityNav != null ? tableData.privateEquityNav : 0],
                    ["Real estate", tableData.realEstateNav != null ? tableData.realEstateNav : 0],
                    ["Infrastructure", 0],
                    ["NICK MF other", tableData.nickMfOtherNav != null ? tableData.nickMfOtherNav : 0],
                    ["Transfer portfolio", 0],
                ]
            );
        }

        formatter.format(dataNAV,1);

        var optionsNAV = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {}
        };

        var optionsActualAllocation = {
            animation: {
                duration: 800,
                easing: 'out',
                startup: true
            },
            height: 300,
            pieHole: 0.4,
            title: 'Actual Allocation'
        };

        var chartNAV = this.createTableChart(document.getElementById('portfolio_nav_table'));
        chartNAV.draw(dataNAV, optionsNAV);

        var chartActualAllocation = new google.visualization.PieChart(document.getElementById('actual_allocation_chart'));
        chartActualAllocation.draw(dataActualAllocation, optionsActualAllocation);

        google.visualization.events.addListener(chartNAV, 'select',
            function() {
                chartActualAllocation.draw(dataActualAllocation, optionsActualAllocation);

                var x = dataNAV.Tf[chartNAV.getSelection()[0].row].c[0].v.split(';');

                for (var i = 0; i < dataActualAllocation.Tf.length; i++) {
                    if (dataActualAllocation.Tf[i].c[0].v == x[x.length == 1 ? 0 : x.length - 1]) {
                        console.log(i);
                        chartActualAllocation.setSelection([{row: i}]);
                    }
                }

                chartNAV.setSelection(null);
            });

        // google.visualization.events.addListener(chartActualAllocation, 'select',
        //     function() {
        //         chartNAV.setSelection(chartActualAllocation.getSelection());
        //     });

        // function selectHandler() {
        //     console.log(chartNAV.getSelection()[0].row);
        // }
    }

    // PORTFOLIO PERFORMANCE -------------

    getPortfolioPerformanceData(tableDate){
        var data = new google.visualization.DataTable();
        var formatter = new google.visualization.NumberFormat({
            pattern:'#.##%',
            negativeColor: 'red'
        });
        data.addColumn("string", "");
        data.addColumn("number", "MTD");
        data.addColumn("number", "QTD");
        data.addColumn("number", "YTD");

        // var performanceArray = this.getPerformanceByDate(tableDate);
        // if(tableDate.endsWith("19")){
        //     data.addRows([
        //         ["Private Equity", performanceArray[0][2], performanceArray[1][2], performanceArray[2][2],],
        //         ["Hedge Funds", performanceArray[0][3], performanceArray[1][3], performanceArray[2][3],],
        //         ["Real Estate", performanceArray[0][4], performanceArray[1][4], performanceArray[2][4]],
        //         ["Fixed income securities", performanceArray[0][5], performanceArray[1][5], performanceArray[2][5]],
        //         ["Public Equity", performanceArray[0][6], performanceArray[1][6], performanceArray[2][6]],
        //         ["Alternative portfolio", performanceArray[0][7], performanceArray[1][7], performanceArray[2][7]],
        //         ["Transition portfolio", performanceArray[0][8], performanceArray[1][8], performanceArray[2][8]]
        //     ]);
        // }else {
        //     data.addRows([
        //         ["NIC Portfolio Total", performanceArray[0][2], performanceArray[1][2], performanceArray[2][2],],
        //         ["Liquid Portfolio", performanceArray[0][3], performanceArray[1][3], performanceArray[2][3],],
        //         ["NICK Master Fund", performanceArray[0][6], performanceArray[1][6], performanceArray[2][6]],
        //         ["Hedge Funds Portfolio", performanceArray[0][5], performanceArray[1][5], performanceArray[2][5]],
        //         ["Private Equity Portfolio", performanceArray[0][4], performanceArray[1][4], performanceArray[2][4]]
        //     ]);
        // }

        var tableData = this.getDataByDate(tableDate);
        if(tableData != null) {
            if(tableData.transitionPortfolioMtd != null || tableData.transitionPortfolioQtd != null || tableData.transitionPortfolioYtd != null) {
                data.addRows([["Transition portfolio", tableData.transitionPortfolioMtd, tableData.transitionPortfolioQtd, tableData.transitionPortfolioYtd]]);
            }
            if(tableData.alternativePortfolioMtd != null || tableData.alternativePortfolioQtd != null || tableData.alternativePortfolioYtd != null) {
                data.addRows([["Alternative portfolio", tableData.alternativePortfolioMtd, tableData.alternativePortfolioQtd, tableData.alternativePortfolioYtd]]);
            }
            if(tableData.fixedPortfolioMtd != null || tableData.fixedPortfolioQtd != null || tableData.fixedPortfolioYtd != null) {
                data.addRows([["Fixed portfolio", tableData.fixedPortfolioMtd, tableData.fixedPortfolioQtd, tableData.fixedPortfolioYtd]]);
            }
            if(tableData.equityPortfolioMtd != null || tableData.equityPortfolioQtd != null || tableData.equityPortfolioYtd != null) {
                data.addRows([["Equity portfolio", tableData.equityPortfolioMtd, tableData.equityPortfolioQtd, tableData.equityPortfolioYtd]]);
            }
            if(tableData.hedgeFundsMtd != null || tableData.hedgeFundsQtd != null || tableData.hedgeFundsYtd != null) {
                data.addRows([["Hedge funds", tableData.hedgeFundsMtd, tableData.hedgeFundsQtd, tableData.hedgeFundsYtd]]);
            }
            if(tableData.privateEquityMtd != null || tableData.privateEquityQtd != null || tableData.privateEquityYtd != null) {
                data.addRows([["Private equity", tableData.privateEquityMtd, tableData.privateEquityQtd, tableData.privateEquityYtd]]);
            }
            if(tableData.realEstateMtd != null || tableData.realEstateQtd != null || tableData.realEstateYtd != null) {
                data.addRows([["Real estate", tableData.realEstateMtd, tableData.realEstateQtd, tableData.realEstateYtd]]);
            }
            if(tableData.nickMfOtherMtd != null || tableData.nickMfOtherQtd != null || tableData.nickMfOtherYtd != null) {
                data.addRows([["NICK Mf other", tableData.nickMfOtherMtd, tableData.nickMfOtherQtd, tableData.nickMfOtherYtd]]);
            }
            if(tableData.transferMtd != null || tableData.transferQtd != null || tableData.transferYtd != null) {
                data.addRows([["Transfer", tableData.transferMtd, tableData.transferQtd, tableData.transferYtd]]);
            }
        }

        formatter.format(data, 1);
        formatter.format(data, 2);
        formatter.format(data, 3);
        return data;
    }

    // private getPerformanceByDate(tableDate){
    //     var performanceArray = [];
    //     for(var i = 0; i < this.performance.length; i++){
    //         if(this.performance[i][0] === tableDate){
    //             performanceArray.push(this.performance[i]);
    //         }
    //     }
    //     return performanceArray;
    // }

    drawPortfolioPerformanceTable(data){
        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {}
        };

        //var chart = this.createTableChart(document.getElementById('portfolio_performance'));
        var chart = this.createTableChart(document.getElementById('portfolio_performance_table'));
        chart.draw(data, options);
    }

    // BENCHMARK PERFORMANCE ------------------

    // getBenchmarkPerformanceData(tableDate){
    //
    //     var data = new google.visualization.DataTable();
    //     var formatter = new google.visualization.NumberFormat({
    //         pattern:'#.##%',
    //         negativeColor: 'red'
    //     });
    //     data.addColumn("string", "");
    //     data.addColumn("number", "MTD");
    //     data.addColumn("number", "QTD");
    //     data.addColumn("number", "YTD");
    //
    //     var performanceArray = this.getPerformanceByDate(tableDate)
    //     data.addRows([
    //         ["Composite Benchmark", performanceArray[0][11], performanceArray[1][11], performanceArray[2][11]],
    //         ["Cpi + 5%", performanceArray[0][9], performanceArray[1][9], performanceArray[2][9]],
    //         ["US 6m T-bills1", performanceArray[0][8], performanceArray[1][8], performanceArray[2][8]],
    //         ["HFRI FoF index", performanceArray[0][10], performanceArray[1][10], performanceArray[2][10]]
    //     ]);
    //     formatter.format(data,1);
    //     formatter.format(data,2);
    //     formatter.format(data,3);
    //     return data;
    // }

    // drawBenchmarksPerformanceTable(data){
    //     var options = {
    //         showRowNumber: false,
    //         width: '100%',
    //         height: 170,
    //         'allowHtml': true,
    //         cssClassNames: {}
    //     };
    //
    //     var chart = this.createTableChart(document.getElementById('benchmarks_performance'));
    //     chart.draw(data, options);
    // }

    // ALTERNATIVE PERFORMANCE -----------------

    // drawAlternativePerformanceChart(type){
    //     var data = new google.visualization.DataTable();
    //     var formatter = new google.visualization.NumberFormat({
    //         pattern:'#.##%'
    //     });
    //     data.addColumn("string", "Date");
    //     data.addColumn("number", this.getLabelsByType(type, 0));
    //     data.addColumn("number", this.getLabelsByType(type, 1));
    //
    //     var compareValue = this.getPerformanceWithBenchmarks(type);
    //     data.addRows(compareValue);
    //
    //     formatter.format(data, 1);
    //     formatter.format(data, 2);
    //
    //     var options = {
    //         chart: {
    //             title: "Alternative portfolio performance",
    //             subtitle: "monthly",
    //             legend: { position: 'top' },
    //             width: 600,
    //             height: 500,
    //         },
    //         animation: {
    //             duration: 500,
    //             easing: 'out',
    //             startup: true,
    //         },
    //         vAxis: {
    //             format: '#.##%',
    //         },
    //         showRowNumber: false,
    //         width: '100%',
    //         height: '100%'
    //     };
    //
    //     var chart = this.createLineChart(document.getElementById('alternative_performance'));
    //     chart.draw(data, options);
    //
    //     //this.data_alt_perf = new google.visualization.DataTable();
    //     //for(var i = 0; i < this.tableChart_alt_perf.columns.length; i++){
    //     //    this.data_alt_perf.addColumn(this.tableChart_alt_perf.columns[i].type, this.tableChart_alt_perf.columns[i].name);
    //     //}
    //     //this.data_alt_perf.addRows(this.tableChart_alt_perf.rows);
    //     //
    //     //this.options_alt_perf = {
    //     //    chart: {
    //     //        title: "Alternative portfolio performance",
    //     //        subtitle: "monthly",
    //     //        legend: { position: 'right' },
    //     //    },
    //     //    showRowNumber: false,
    //     //    width: '100%',
    //     //    height: '100%'
    //     //};
    //     //var chart = this.createLineChart(document.getElementById('alternative_performance'));
    //     //chart.draw(this.data_alt_perf, this.options_alt_perf);
    //
    // }

    // private getLabelsByType(type, index){
    //     if(type === "LIQUID"){
    //         if(index == 0){
    //             return "NIC Liquid";
    //         }else{
    //             return "US 6mT-bills";
    //         }
    //     }else if(type === "PE"){
    //         if(index == 0){
    //             return "NIC Private Equity";
    //         }else{
    //             return "CPI + 5%";
    //         }
    //
    //     }else if(type === "HF"){
    //         if(index == 0){
    //             return "NIC Hedge Funds";
    //         }else{
    //             return "HFRI FoF Index";
    //         }
    //     }else{
    //         if(index == 0){
    //             return "NIC Portfolio";
    //         }else{
    //             return "Composite Benchmark";
    //         }
    //     }
    // }

    // private getPerformanceWithBenchmarks(type){
    //     var values = [];
    //
    //     var index1;
    //     var index2;
    //     if(type === "TOTAL"){
    //         index1 = 2;
    //         index2 = 11;
    //     }else if(type === "LIQUID"){
    //         index1 = 3;
    //         index2 = 8;
    //     }else if(type === "PE"){
    //         index1 = 4;
    //         index2 = 9;
    //     }else if(type === "HF"){
    //         index1 = 5;
    //         index2 = 10;
    //     }
    //     for(var i = 0; i < this.performance.length; i++ ){
    //         if(this.performance[i][1] === "MTD"){
    //             var item = [this.performance[i][0], this.performance[i][index1], this.performance[i][index2]];
    //             values.push(item);
    //         }
    //     }
    //     return values;
    // }

    // ALLOCATIONS -----------------------------

    drawTargetAllocationChart(){
        var data = google.visualization.arrayToDataTable(
        [
            ["Allocation", "%"],
            ["Transition portfolio", 0],
            ["Fixed portfolio", 10],
            ["Equity portfolio", 10],
            ["Hedge funds", 10],
            ["Private equity", 40],
            ["Real estate", 20],
            ["Infrastructure", 10],
            ["NICK MF other", 0],
            ["Transfer portfolio", 0],
        ]);

        var options = {
            title: 'Target Allocation',
            pieHole: 0.4,
            animation: {
                duration: 500,
                easing: 'out',
                startup: true,
            },
            height: 300
        };

        var chart = new google.visualization.PieChart(document.getElementById('target_allocation_chart'));
        chart.draw(data, options);
    }

    // drawActualAllocationChart(tableDate){
    //
    //     // if(tableDate.endsWith("19")) {
    //     //     var tableData = this.getActualAllocationData(tableDate);
    //     //     console.log(tableData);
    //     //     var data = google.visualization.arrayToDataTable(
    //     //         [
    //     //             ["Allocation", "%"],
    //     //             ["Private Equity", tableData[1][0]],
    //     //             ["Hedge Funds", tableData[1][1]],
    //     //             ["Real Estate", tableData[1][2]],
    //     //             ["Fixed income securities", tableData[1][3]],
    //     //             ["Public Equity", tableData[1][4]],
    //     //             ["Transition portfolio", tableData[1][5]],
    //     //             ["NICK MF Other", tableData[1][6]],
    //     //             ["Transfer", null]
    //     //         ]
    //     //     );
    //     // }else{
    //     //     var data = google.visualization.arrayToDataTable(
    //     //         [
    //     //             ["Allocation", "%"],
    //     //             ["Private Equity", 12],
    //     //             ["Hedge Funds", 22],
    //     //             ["Liquid portfolio", 66],
    //     //             ["Real Estate", 0],
    //     //             ["Infrastructure ", 0],
    //     //             ["Public Equity", 0]
    //     //         ]
    //     //     );
    //     // }
    //
    //     var tableData = this.getDataByDate(tableDate);
    //     if(tableData != null) {
    //         var data = google.visualization.arrayToDataTable(
    //             [
    //                 ["Allocation", "%"],
    //                 ["Transition portfolio", 0],
    //                 ["Fixed portfolio", tableData.fixedPortfolioNav != null ? tableData.fixedPortfolioNav : 0],
    //                 ["Equity portfolio", tableData.equityPortfolioNav != null ? tableData.equityPortfolioNav : 0],
    //                 ["Hedge funds", tableData.hedgeFundsNav != null ? tableData.hedgeFundsNav : 0],
    //                 ["Private equity", tableData.privateEquityNav != null ? tableData.privateEquityNav : 0],
    //                 ["Real estate", tableData.realEstateNav != null ? tableData.realEstateNav : 0],
    //                 ["Infrastructure", 0],
    //                 ["NICK MF other", tableData.nickMfOtherNav != null ? tableData.nickMfOtherNav : 0],
    //                 ["Transfer portfolio", 0],
    //             ]
    //         );
    //     }
    //
    //     var options = {
    //         animation: {
    //             duration: 500,
    //             easing: 'out',
    //             startup: true
    //         },
    //         height: 300,
    //         pieHole: 0.4,
    //         title: 'Actual Allocation'
    //     };
    //
    //     var chart = new google.visualization.PieChart(document.getElementById('actual_allocation_chart'));
    //     chart.draw(data, options);
    //     // if (rowNumber >= 0 && rowNumber <=8) {
    //     //     chart.setSelection([{row: rowNumber}]);
    //     // }
    // }

    // PUBLIC PERFORMANCE ----------------------

    // drawPublicPerformanceChart(){
    //     var data = new google.visualization.DataTable();
    //     var formatter = new google.visualization.NumberFormat({
    //         pattern:'#.##%'
    //     });
    //     data.addColumn("string", "Date");
    //     data.addColumn("number", "S&P500 Index");
    //     data.addColumn("number", "MSCI World Index");
    //     data.addColumn("number", "HFRX Global Hedge Fund Index");
    //     data.addColumn("number", "Barclays Capital Bond Index");
    //
    //     var benchmarks = this.getPublicPerformance();
    //     data.addRows(benchmarks);
    //     formatter.format(data,1);
    //     formatter.format(data,2);
    //     formatter.format(data,3);
    //     formatter.format(data,4);
    //     var options = {
    //         chart: {
    //             title: "Public markets performance",
    //             legend: { position: 'top' },
    //             width: 600,
    //             height: 500
    //         },
    //         animation: {
    //             duration: 500,
    //             easing: 'out',
    //             startup: true,
    //         },
    //         vAxis: {
    //             format: '#.##%',
    //         },
    //         showRowNumber: false,
    //         width: '100%',
    //         height: '100%'
    //     };
    //
    //     var chart = this.createLineChart(document.getElementById('public_performance'));
    //     chart.draw(data, options);
    //
    //
    //     //this.data_public_perf = new google.visualization.DataTable();
    //     //for(var i = 0; i < this.tableChart_public_perf.columns.length; i++){
    //     //    this.data_public_perf.addColumn(this.tableChart_public_perf.columns[i].type, this.tableChart_public_perf.columns[i].name);
    //     //}
    //     //this.data_public_perf.addRows(this.tableChart_public_perf.rows);
    //     //
    //     //this.options_public_perf = {
    //     //    chart: {
    //     //        title: "Alternative portfolio performance",
    //     //        subtitle: "since inception",
    //     //        legend: { position: 'right' },
    //     //    },
    //     //    showRowNumber: false,
    //     //    width: '100%',
    //     //    height: '100%'
    //     //};
    //     //
    //     //var chart = this.createLineChart(document.getElementById('public_performance'));
    //     //chart.draw(this.data_public_perf, this.options_public_perf);
    //
    // }

    // private getPublicPerformance(){
    //     return this.publicMarketsPerformance;
    // }

    postAction(successMessage, errorMessage) {
        this.successMessage = successMessage;
        this.errorMessage = errorMessage;

        // TODO: non jQuery
        $('html, body').animate({scrollTop: 0}, 'fast');
    }

    public getDataAndDraw(){
        this.busy = this.monitoringPortfolioService.get()
            .subscribe(
                (response) => {
                    this.nicPortfolioList = response.nicPortfolioDtoList;
                    // console.log(this.nicPortfolioList);

                    console.log(response.message.nameEn);

                    if(this.nicPortfolioList.length > 0) {
                        this.tableDate = this.getAllDates()[0];
                        // this.performanceType = "TOTAL";

                        this.drawTablesAndActualAllocationChart(this.tableDate);
                        this.drawTargetAllocationChart();
                        // this.drawActualAllocationChart(this.tableDate);

                        //this.drawAlternativePerformanceChart("TOTAL");
                        //this.drawPublicPerformanceChart();
                    }
                },
                (error: ErrorResponse) => {
                    this.processErrorMessage(error);
                    this.postAction(null, error.message);
                    console.log(error);
                    console.log(error.message);
                }
            )
    }

    public getAllDates(){
        var dates = [];
        // for(var i = this.nav.length - 1; i >= 0; i--){
        //     dates.push(this.nav[i][0]);
        // }
        for(var i = this.nicPortfolioList.length - 1; i >= 0; i--){
            dates.push(this.nicPortfolioList[i].date);
        }
        return dates;
    }

    private getDataByDate(tableDate){

        for(var i = 0; i < this.nicPortfolioList.length; i++){
            if(this.nicPortfolioList[i].date === tableDate){
                return this.nicPortfolioList[i];
            }
        }
        return null;
    }

    fileChange(files: any){
        this.myFiles = files;
        console.log(this.myFiles);
    }

    private onSubmitNicPortfolio() {
        this.busy = this.monitoringPortfolioService.postFiles(this.myFiles)
            .subscribe(
                (response) => {
                    this.nicPortfolioList = response.nicPortfolioDtoList;
                    // console.log(this.nicPortfolioList);

                    console.log(response.message.nameEn);

                    if(this.nicPortfolioList.length > 0) {
                        this.tableDate = this.getAllDates()[0];

                        this.drawTablesAndActualAllocationChart(this.tableDate);
                        this.drawTargetAllocationChart();
                        // this.drawActualAllocationChart(this.tableDate);

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

    private fileDownload() {
        this.busy = this.downloadService.makeFileRequest(this.FILE_DOWNLOAD_URL, '')
            .subscribe(
                (response) => {
                    console.log("File downloaded!");
                },
                (error) => {
                    this.postAction(null, "Error loading file!");
                    console.log(error);
                }
            )
    }

    canEdit(){
        return this.moduleAccessChecker.checkAccessReportingEditor();
    }

    // public getAssetTypes(){
    //     var assetTypes = ["TOTAL", "LIQUID", "PE", "HF"];
    //     return assetTypes;
    //
    // }

    // private nav = [
    //     ["Dec-14",799882987,799882987,0,0,0,0],
    //     ["Jan-15",801943108,801943108,0,0,0,0],
    //     ["Feb-15",801406672,801406672,0,0,0,0],
    //     ["Mar-15",802150976,802150976,0,0,0,0],
    //     ["Apr-15",802783935,802783935,0,0,0,0],
    //     ["May-15",803051737,803051737,0,0,0,0],
    //     ["Jun-15",803394104,803394104,0,0,0,0],
    //     ["Jul-15",803667686,728467686,75200000,0,75000000,200000],
    //     ["Aug-15",802500296,653509334,148990962,0,73806138,184824],
    //     ["Sep-15",799649211,653880251,145768960,0,145584323,184637],
    //     ["Oct-15",800087405,650177665,149909740,3965000,145761377,183363],
    //     ["Nov-15",799268030,649883201,149384829,3163463,146039154,182212],
    //     ["Dec-15",798611908,644606893,154005015,8601146,145224471,179398],
    //     ["Jan-16",795560566,642166580,153393987,11509729,141704890,179368],
    //     ["Feb-16",793922449,642659327,151263123,11593892,139490031,179200],
    //     ["Mar-16",795539712,642564052,152975660,13135835,139660777,179048],
    //     ["Apr-16",796760017,638773831,157986185,17090141,140717168,178876],
    //     ["May-16",798052761,638972351,159080410,17001537,141900131,178742],
    //     ["Jun-16",800253478,637271323,162982154,21365316,141459937,156901],
    //     ["Jul-16",801305788,630241088,171064700,28882486,142025472,156742],
    //     ["Aug-16",803435223,623049078,180386145,36981406,143248121,156618],
    //     ["Sep-16",804756535,614994068,189762468,46115007,143491005,156456],
    //     ["Oct-16",805554825,604231637,201323187,57581011,143595932,146244],
    //     ["Nov-16",808536057,594589358,213946698,68373409,145427045,146244],
    //     ["Dec-16",810806237,581364178,229442058,72629726,146631106,146226],
    //     ["Jan-17",812090177,559897597,252192580,74316457,177729308,146142],
    //     ["Feb-17",814963819,555892063,259071756,80136212,178740865,194682],
    //     ["Mar-17",815184627,550358874,264825752,83365166,178775081,2848148],
    //     ["Apr-17",817411061,538901091,278509970,98665606,179691709,137571],
    //     ["May-17",819535274,537027941,282722341,101665606,180889915,137571],
    //     ["Jan-19",347105381,321908990,3344282,387159417,43641662,279687644,9407088,1392254464],
    //     ["Feb-19",363931637,334981328,3351953,387940018,44775217,263618055,102836,1398701044],
    //     ["Mar-19",395429584,336837508,4920563,393543346,45310456,231620254,97586,1407759298,0]
    // ];

    // private performance = [
    //     ["Dec-14","MTD",0,0,0,0,0,0,0,0,0,0],
    //     ["Dec-14","QTD",0,0,0,0,0,0,0,0,0,0],
    //     ["Dec-14","YTD",0,0,0,0,0,0,0,0,0,0],
    //     ["Jan-15","MTD",0.0026,0.0026,0,0,0,0,0.0003,-0.0011,0.0013,0.0003],
    //     ["Jan-15","QTD",0.0026,0.0026,0,0,0,0,0.0003,-0.0011,0.0013,0.0003],
    //     ["Jan-15","YTD",0.0026,0.0026,0,0,0,0,0.0003,-0.0011,0.0013,0.0003],
    //     ["Feb-15","MTD",-0.0007,-0.0007,0,0,0,0,0.0001,0.0082,0.0169,0.0001],
    //     ["Feb-15","QTD",0.0019,0.0019,0,0,0,0,0.0004,0.0071,0.0183,0.0004],
    //     ["Feb-15","YTD",0.0019,0.0019,0,0,0,0,0.0004,0.0071,0.0183,0.0004],
    //     ["Mar-15","MTD",0.0009,0.0009,0,0,0,0,0.0000,0.0093,0.0066,0.0000],
    //     ["Mar-15","QTD",0.0009,0.0028,0,0,0,0,0.0005,0.0165,0.0250,0.0005],
    //     ["Mar-15","YTD",0.0028,0.0028,0,0,0,0,0.0005,0.0165,0.0250,0.0005],
    //     ["Apr-15","MTD",0.0008,0.0008,0,0,0,0,0.0004,0.0061,0.0025,0.0004],
    //     ["Apr-15","QTD",0.0008,0.0008,0,0,0,0,0.0004,0.0061,0.0025,0.0004],
    //     ["Apr-15","YTD",0.0036,0.0036,0,0,0,0,0.0009,0.0227,0.0275,0.0009],
    //     ["May-15","MTD",0.0003,0.0003,0,0,0,0,0.0001,0.0080,0.0080,0.0001],
    //     ["May-15","QTD",0.0011,0.0011,0,0,0,0,0.0005,0.0141,0.0105,0.0005],
    //     ["May-15","YTD",0.0040,0.0033,0,0,0,0,0.0010,0.0309,0.0357,0.0010],
    //     ["Jun-15","MTD",0.0004,0.0004,0,0,0,0,0.0001,0.0060,0.0060,0.0001],
    //     ["Jun-15","QTD",0.0015,0.0015,0,0,0,0,0.0006,0.0202,0.0166,0.0006],
    //     ["Jun-15","YTD",0.0044,0.0044,0,0,0,0,0.0011,0.0371,0.0419,0.0011],
    //     ["Jul-15","MTD",0.0003,0.0004,0,0,0,0,-0.0001,0.0039,0.0039,0.0003],
    //     ["Jul-15","QTD",0.0003,0.0004,0,0,0,0,-0.0001,0.0039,0.0039,0.0003],
    //     ["Jul-15","YTD",0.0047,0.0048,0,0,0,0,0.0011,0.0411,0.0460,0.0014],
    //     ["Aug-15","MTD",-0.0015,0.0001,0,-0.0159,-0.0161,-0.0759,0.0001,0.0041,-0.0200,-0.0036],
    //     ["Aug-15","QTD",-0.0011,0.0004,0,-0.0159,-0.0161,-0.0759,0.0000,0.0080,-0.0161,-0.0033],
    //     ["Aug-15","YTD",0.0033,0.0048,0,-0.0159,-0.0161,-0.0759,0.0011,0.0454,0.0251,-0.0022],
    //     ["Sep-15","MTD",-0.0036,0.0006,0,-0.0217,-0.0216,-0.0010,0.0011,0.0026,-0.0183,-0.0025],
    //     ["Sep-15","QTD",-0.0047,0.0010,0,-0.0372,-0.0374,-0.0768,0.0011,0.0106,-0.0341,-0.0058],
    //     ["Sep-15","YTD",-0.0003,0.0054,0,-0.0372,-0.0374,-0.0768,0.0022,0.0481,0.0064,-0.0047],
    //     ["Oct-15","MTD",0.0005,0.0004,0,0.0012,0.0012,-0.0069,-0.0003,0.0041,0.0085,0.0013],
    //     ["Oct-15","QTD",0.0005,0.0004,0,0.0012,0.0012,-0.0069,-0.0003,0.0041,0.0085,0.0013],
    //     ["Oct-15","YTD",0.0003,0.0058,0,-0.0361,-0.0362,-0.0832,0.0018,0.0523,0.0150,-0.0034],
    //     ["Nov-15","MTD",-0.0010,-0.0005,-0.2022,0.0019,-0.0035,-0.0063,-0.0001,0.0023,0.0030,0.0005],
    //     ["Nov-15","QTD",-0.0005,0.0000,-0.2022,0.0031,-0.0023,-0.0131,-0.0004,0.0064,0.0116,0.0018],
    //     ["Nov-15","YTD",-0.0008,0.0054,-0.2022,-0.0342,-0.0396,-0.0889,0.0018,0.0548,0.0180,-0.0029],
    //     ["Dec-15","MTD",0.0029,0.0000,0.0494,-0.0056,-0.0044,-0.0154,0.0004,0.0020,-0.0042,-0.0004],
    //     ["Dec-15","QTD",0.0024,0.0000,-0.1628,-0.0025,-0.0067,-0.0284,0.0000,0.0085,0.0073,0.0014],
    //     ["Dec-15","YTD",0.0021,0.0054,-0.1628,-0.0396,-0.0438,-0.1030,0.0022,0.0569,0.0137,-0.0033],
    //     ["Jan-16","MTD",-0.0038,0.0008,-0.0053,-0.0242,-0.0228,-0.0002,0.0009,0.0026,-0.0266,-0.0040],
    //     ["Jan-16","QTD",-0.0038,0.0008,-0.0053,-0.0242,-0.0228,-0.0002,0.0009,0.0026,-0.0266,-0.0040],
    //     ["Jan-16","YTD",-0.0038,0.0008,-0.0053,-0.0242,-0.0228,-0.0002,0.0009,0.0026,-0.0266,-0.0040],
    //     ["Feb-16","MTD",-0.0021,0.0008,0.0073,-0.0156,-0.0139,-0.0009,0.0004,0.0056,-0.0120,-0.0017],
    //     ["Feb-16","QTD",-0.0059,0.0016,0.0020,-0.0395,-0.0364,-0.0011,0.0012,0.0082,-0.0383,-0.0057],
    //     ["Feb-16","YTD",-0.0059,0.0016,0.0020,-0.0395,-0.0364,-0.0011,0.0012,0.0082,-0.0383,-0.0057],
    //     ["Mar-16","MTD",0.0020,0.0014,0.0467,0.0012,0.0047,-0.0008,0.0009,0.0084,0.0074,0.0022],
    //     ["Mar-16","QTD",-0.0038,0.0030,0.0488,-0.0383,-0.0318,-0.0019,0.0022,0.0167,-0.0311,-0.0035],
    //     ["Mar-16","YTD",-0.0038,0.0030,0.0488,-0.0383,-0.0318,-0.0019,0.0022,0.0167,-0.0311,-0.0035],
    //     ["Apr-16","MTD",0.0015,0.0003,-0.0014,0.0076,0.0068,-0.0010,0.0007,0.0068,0.0047,0.0015],
    //     ["Apr-16","QTD",0.0015,0.0003,-0.0014,0.0076,0.0068,-0.0010,0.0007,0.0068,0.0047,0.0015],
    //     ["Apr-16","YTD",-0.0023,0.0033,0.0473,-0.0310,-0.0253,-0.0029,0.0028,0.0235,-0.0266,-0.0021],
    //     ["May-16","MTD",0.0016,0.0003,-0.0052,0.0084,0.0069,-0.0008,0.0001,0.0076,0.0056,0.0012],
    //     ["May-16","QTD",0.0032,0.0006,-0.0066,0.0160,0.0138,-0.0017,0.0008,0.0144,0.0105,0.0028],
    //     ["May-16","YTD",-0.0007,0.0036,0.0419,-0.0229,-0.0185,-0.0037,0.0029,0.0313,-0.0210,-0.0008],
    //     ["Jun-16","MTD",0.0028,0.0037,0.0188,-0.0031,-0.0009,-0.1222,0.0012,0.0062,-0.0055,0],
    //     ["Jun-16","QTD",0.0059,0.0043,0.0121,0.0129,0.0129,-0.1237,0.0020,0.0207,0.0050,0],
    //     ["Jun-16","YTD",0.0021,0.0073,0.0615,-0.0259,-0.0194,-0.1254,0.0041,0.0377,-0.0263,0],
    //     ["Jul-16","MTD",0.0013,0.0011,-0.0091,0.0040,0.0023,-0.0010,0.0004,0,0.0146,0],
    //     ["Jul-16","QTD",0.0013,0.0011,-0.0091,0.0040,0.0023,-0.0010,0.0004,0,0.0146,0],
    //     ["Jul-16","YTD",0.0034,0.0084,0.0519,-0.0220,-0.0172,-0.1263,0.0045,0,-0.0121,0],
    //     ["Aug-16","MTD",0.0027,0.0012,0.0062,0.0084,0.0082,-0.0008,0.0001,0,0.0044,0],
    //     ["Aug-16","QTD",0.0040,0.0022,-0.0029,0.0124,0.0105,-0.0018,0.0004,0,0.0196,0],
    //     ["Aug-16","YTD",0.0060,0.0095,0.0584,-0.0138,-0.0091,-0.1270,0.0046,0.0380,-0.0066,0],
    //     ["Sep-16","MTD",0.0016,0.0007,0.0176,0.0012,0.0049,-0.0010,0.0009,0,0.0056,0],
    //     ["Sep-16","QTD",0.0056,0.0029,0.0146,0.0136,0.0155,-0.0028,0.0013,0,0.0253,0],
    //     ["Sep-16","YTD",0.0116,0.0102,0.0770,-0.0126,-0.0042,-0.1279,0.0054,0.0380,-0.0011,0],
    //     ["Oct-16","MTD",0.000948059,0.001458104,-0.003643586,0.000310251,-0.000704331,-0.065264996,0.000603965,0,-0.001779918,0],
    //     ["Oct-16","QTD",0.000948059,0.001458104,-0.003643586,0.000310251,-0.000704331,-0.065264996,0.000603965,0,-0.001779918,0],
    //     ["Oct-16","YTD",0.008693605,0.011654748,0.073078718,-0.011213943,0.004535928,-0.185257219,0.006027404,0.037959089,-0.003803904,0],
    //     ["Nov-16","MTD",0.0036904,0.0017386,0.0017300,0.0126927,0.0095482,0.0000000,0.0002091,0.0033439,0.0026662,0],
    //     ["Nov-16","QTD",0.0046524,0.0031992,-0.0019199,0.0130661,0.0088817,-0.0652650,0.0008132,0.0092703,-0.0002114,0],
    //     ["Nov-16","YTD",0.0124266,0.0134136,0.0749351,0.0013949,-0.0388503,-0.1852572,0.0062378,0.0619921,-0.0036626,0],
    //     ["Dec-16","MTD",0.0028078,0.0030438,-0.0108768,0.0082795,0.0021518,-0.0001221,0.0004904,0,0.0085264,0],
    //     ["Dec-16","QTD",0.0074732,0.0062528,-0.0127758,0.0214537,0.0110525,-0.0653791,0.0013040,0,0.0083132,0],
    //     ["Dec-16","YTD",0.0152693,0.0164982,0.0632433,0.0096859,0,-0.1853567,0.0067312,0,0.0048325,0],
    //     ["Jan-17","MTD",0.001498076,-0.000106250,-0.003002641,0.009536391,0.005561920,-0.000579648,0.000741160,0.006816763,0.009987461,0],
    //     ["Jan-17","QTD",0.001498076,-0.000106250,-0.003002641,0.009536391,0.005561920,-0.000579648,0.000741160,0.006816763,0.009987461,0],
    //     ["Jan-17","YTD",0.001498076,-0.000106250,-0.003002641,0.009536391,0.005561920,-0.000579648,0.000741160,0.006816763,0.009987461,0],
    //     ["Feb-17","MTD",0.003539275,0.003562199,-0.002425371,0.005690938,0.003295909,0.000000000,0.000569903,0.007174871,0.009287931,0],
    //     ["Feb-17","QTD",0.005041953,0.003455570,-0.005420730,0.015277968,0.008873687,0.000000000,0.001311486,0.013856466,0.019639001,0],
    //     ["Feb-17","YTD",0.005041953,0.003455570,-0.005420730,0.015277968,0.008873687,0.000000000,0.001311486,0.013856466,0.019639001,0],
    //     ["Mar-17","MTD",0.000270942,0.000839752,-0.002653482,0.000191428,-0.011191764,0.000000000,-0.000045934,0.015874489,0.000654574,0],
    //     ["Mar-17","QTD",0.005314261,0.004298224,-0.008059828,0.014548498,-0.002417390,0.000000000,0.001265492,0.029950920,0.020306431,0],
    //     ["Mar-17","YTD",0.005314261,0.004298224,-0.008059828,0.014548498,-0.002417390,0.000000000,0.001265492,0.029950920,0.020306431,0],
    //     ["Apr-17","MTD",0.002531181,0.001813274,0.001608921,0.005127269,0.004022214,0.000000000,0.000754661,0.000000000,0.005645153,0],
    //     ["Apr-17","QTD",0.002531181,0.001813274,0.001608921,0.005211644,0.004022214,0.000000000,0.000754661,0.000000000,0.005645153,0],
    //     ["Apr-17","YTD",0.007441777,0.006119291,-0.011297804,0.020764617,0.010608921,0.000000000,0.002021107,0.019555166,0.029678425,0],
    //     ["May-17","MTD",0.00261721,0.002096921,0,0.006668121,0.004282629,0,0,0.000638683,0.002973823],
    //     ["May-17","QTD",0.005136467,0.003935094,0,0.011829579,0.008479505,0,0.006979989,0.001393825,0.008355635],
    //     ["May-17","YTD",0.010059823,0.008283152,0,0.027484865,0.01559899,0,0.02667165,0.002661081,0.032544563],
    //     ["Jan-19","MTD",-0.0001,0.0203,null,0.0120,0.0818,0.0128,0.0030],
    //     ["Jan-19","QTD",-0.0001,0.0203,null,0.0120,0.0818,0.0128,0.0030],
    //     ["Jan-19","YTD",-0.0001,0.0203,null,0.0120,0.0818,0.0128,0.0030],
    //
    //     ["Feb-19","MTD",0.0037,0.0094,null,0.0020,0.0260,0.0056,0.0003],
    //     ["Feb-19","QTD",0.0036,0.0301,null,0.0140,0.1099,0.0185,0.0033],
    //     ["Feb-19","YTD",0.0036,0.0301,null,0.0140,0.1099,0.0185,0.0033],
    //
    //     ["Mar-19","MTD",-0.0013,0.0053,null,0.0144,0.0120,0.0064,0.0064],
    //     ["Mar-19","QTD",0.0024,0.0358,null,0.0287,0.1232,0.0250,0.0098],
    //     ["Mar-19","YTD",0.0024,0.0358,null,0.0287,0.1232,0.0250,0.0098],
    // ];

    // private publicMarketsPerformance = [
    //         ["Aug-15",-0.0626,-0.0681,-0.0221,0.0010],
    //         ["Sep-15",-0.0264,-0.0386,-0.0207,0.0079],
    //         ["Oct-15",0.0830,0.0783,0.0146,0.0004],
    //         ["Nov-15",0.0005,-0.0067,-0.0072,-0.0088],
    //         ["Dec-15",-0.0175,-0.0187,-0.0133,0.0016],
    //         ["Jan-16",-0.0507,-0.0605,-0.0276,0.0116],
    //         ["Feb-16",-0.0041,-0.0096,-0.0032,0.0169],
    //         ["Mar-16",0.0660,0.0652,0.0124,0.0163],
    //         ["Apr-16",0.0027,0.0138,0.0041,0.0100],
    //         ["May-16",0.0153,0.0023,0.0046,-0.0052],
    //         ["Jun-16",0.0009,-0.0128,0.0020,0.0256],
    //         ["Jul-16",0.0356,0.0415,0.0145,0.0061],
    //         ["Aug-16",-0.0012,-0.0013,0.0016,-0.0033],
    //         ["Sep-16",-0.0012,0.0036,0.0055,0.0026],
    //         ["Oct-16",-0.0194,-0.0201,-0.0057,-0.0173],
    //         ["Nov-16",0.03417445,0.01251981,0.00872417,-0.03221788],
    //         ["Dec-16",0.01820075,0.02285511,0.00856492,-0.0041],
    //         ["Jan-17",0.0178843,0.0235150,0.0050189,0.0077559],
    //         ["Feb-17",0.03719826,0.02583129,0.01122778,0.00641355],
    //         ["Mar-17",-0.00038923,0.00815250,0.00027799,-0.00005098],
    //         ["Apr-17",0.00909122,0.01326543,0.00425038,0.009635974],
    //         ["May-17",0.01160000,0.01781417,0.002441764,0.010629703],
    // ];

    // private getActualAllocationData(tableDate){
    //     for(var i = 0; i < this.actual_allocation.length; i++){
    //         if(this.actual_allocation[i][0] === tableDate){
    //             return this.actual_allocation[i];
    //         }
    //     }
    //     return null;
    // }

    // private actual_allocation = [
    //     ['Jan-19',[24.93,23.12,0.24,27.81,3.13,20.09,0.68]],
    //     ['Feb-19',[26.02,23.95,0.24,27.74,3.20,18.85,0.00]],
    //     ['Mar-19',[28.09, 23.93,0.35,27.96,3.22,16.45,0.01]]
    // ];

    //private setStaticColumns(tableChart){
    //
    //    tableChart.columns = [];
    //
    //    var types = ["number", "number","number"];
    //    var names = ["Day", "Name 1", "Name 2"];
    //    for(var i = 0; i < names.length; i++){
    //        var column = new TableColumnDto();
    //        column.type = types[i];
    //        column.name = names[i];
    //        tableChart.columns.push(column);
    //    }
    //}
    //
    //private setStaticRows(tableChart){
    //    tableChart.rows =[
    //        [1,  37.8, 80.8],
    //        [2,  30.9, 69.5],
    //        [3,  25.4,   57],
    //        [4,  11.7, 18.8],
    //        [5,  11.9, 17.6],
    //        [6,   8.8, 13.6],
    //        [7,   7.6, 12.3],
    //        [8,  12.3, 29.2],
    //        [9,  16.9, 42.9],
    //        [10, 12.8, 30.9],
    //        [11,  5.3,  7.9],
    //        [12,  6.6,  8.4],
    //        [13,  4.8,  6.3],
    //        [14,  4.2,  6.2  ]
    //    ];
    //}
    //getLineChartLAternativePerformance(){
    //    this.tableChart_alt_perf= new TableChartDto();
    //    this.setStaticColumns(this.tableChart_alt_perf);
    //    this.setStaticRows(this.tableChart_alt_perf);
    //}
    //getLineChartLPublicPerformance(){
    //    this.tableChart_public_perf= new TableChartDto();
    //    this.setStaticColumns(this.tableChart_public_perf);
    //    this.setStaticRows(this.tableChart_public_perf);
    //}
}