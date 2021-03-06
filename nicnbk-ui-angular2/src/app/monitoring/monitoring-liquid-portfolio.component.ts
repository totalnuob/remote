import {Component, AfterViewInit, ViewChild} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
//simport {CommonFormViewComponent} from "../common/common.component";
//import {CommonTableau} from "./common-tableau.component";
import {GoogleChartComponent} from "../google-chart/google-chart.component";
import {MonitoringLiquidPortfolioService} from "./monitoring-liquid-portfolio.service";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {DATA_APP_URL} from "../common/common.service.constants";
import {Subscription} from "../../../node_modules/rxjs";
import {FileDownloadService} from "../common/file.download.service";
import {ErrorResponse} from "../common/error-response";

declare var google:any;
declare var $: any;

@Component({
    selector: 'monitoring-liquid-portfolio',
    templateUrl: 'view/monitoring-liquid-portfolio.component.html',
    //templateUrl: 'view/monitoring-liquid-portfolio.component2.html',
    styleUrls: [],
    providers: [MonitoringLiquidPortfolioService],
})
export class MonitoringLiquidPortfolioComponent extends GoogleChartComponent {
    private tableDate;

    private liquidPortfolioList = [];

    private moduleAccessChecker = new ModuleAccessCheckerService;

    private myFiles: File[];

    private FILE_DOWNLOAD_URL = DATA_APP_URL + "monitoring/liq/download/";

    busy: Subscription;

    constructor(
        private monitoringLiquidPortfolioService: MonitoringLiquidPortfolioService,
        private downloadService: FileDownloadService,
    ) {

        super();

        this.myFiles = [];
    }

    // drawGraph(){
    //     var tableDate = this.getAllDates()[0];
    //
    //     //$("#tableDate").val($("#tableDate option:last").val());
    //     this.tableDate = tableDate;
    //     this.drawTables(tableDate);
    //
    //     //this.drawReturnsLineCharts(null);
    //     this.drawPieCharts(tableDate);
    //
    //     //this.drawPositionsTable(tableDate);
    // }

    drawGraph(){
        this.getDataAndDraw();
    }

    // drawTables(tableDate){
    //     if(tableDate.endsWith("19")) {
    //         //console.log("drawTables: " + tableDate);
    //         var data = this.getFixedIncomeData(tableDate);
    //         this.drawFixedIncomeTable(data);
    //
    //         var data = this.getEquityData(tableDate);
    //         this.drawEquityTable(data);
    //
    //         var data = this.getTransitionData(tableDate);
    //         this.drawTransitionTable(data);
    //
    //     }else{
    //         var navData = this.getNAVData(tableDate);
    //         this.drawValueTable(navData);
    //
    //         var performanceData = this.getPerformanceData(tableDate);
    //         this.drawPerformanceTable(performanceData);
    //     }
    //
    // }

    // public isNewVersion(){
    //     if(this.tableDate == null){
    //         return true;
    //     }
    //     return this.tableDate.endsWith("19");
    // }

    // public redraw(){
    //     var date = $('#tableDate').val()
    //     this.drawTables(date);
    //     //this.drawReturnsLineCharts(null);
    //     this.drawPieCharts(date);
    //     //this.drawPositionsTable(date);
    // }

    public redraw(){
        this.drawTablesAndCharts($('#tableDate').val());
    }

    public drawTablesAndCharts(tableDate){
        this.drawFixedIncomeTableAndChart(tableDate);
        this.drawEquityTableAndChart(tableDate);
        this.drawTransitionTableAndChart(tableDate);
    }

    // NAV TABLE ---------------------------------------

    // private getNAVData(tableDate){
    //     var data = new google.visualization.DataTable();
    //     var formatter = new google.visualization.NumberFormat({
    //         prefix:'$ ',
    //         groupingSymbol: ' ',
    //         fractionDigits: 0
    //     });
    //     data.addColumn("string", "");
    //     data.addColumn("number", "");
    //     var NAVarray = this.getNAVByDate(tableDate);
    //     data.addRows([
    //         ["Government Bonds", NAVarray[1]],
    //         ["Corporate Bonds", NAVarray[2]],
    //         ["Agencies", NAVarray[3]],
    //         ["Supranationals", NAVarray[4]],
    //         ["Margin at Broker", NAVarray[5]],
    //         ["Cash", NAVarray[6]],
    //         ["Total", NAVarray[7]]
    //     ]);
    //     formatter.format(data, 1);
    //     return data;
    // }

    // private getFixedIncomeData(tableDate){
    //     var data = new google.visualization.DataTable();
    //     var formatter = new google.visualization.NumberFormat({
    //         prefix:'$ ',
    //         groupingSymbol: ' ',
    //         fractionDigits: 0
    //     });
    //     var formatter_prec = new google.visualization.NumberFormat({
    //         pattern:'#.##%',
    //         negativeColor: 'red'
    //     });
    //     data.addColumn("string", "");
    //     data.addColumn("number", "NAV");
    //     data.addColumn("number", "MTD");
    //     data.addColumn("number", "QTD");
    //     data.addColumn("number", "YTD");
    //     var dataByDate = this.getFixedIncomeByDate(tableDate);
    //     data.addRows([
    //         ["Government Bonds", dataByDate[1][0], dataByDate[2][0], dataByDate[3][0],dataByDate[4][0]],
    //         ["Corporate Bonds", dataByDate[1][1], dataByDate[2][1],dataByDate[3][1],dataByDate[4][1]],
    //         ["Agencies", dataByDate[1][2], dataByDate[2][2],dataByDate[3][2],dataByDate[4][2]],
    //         ["???????????????? ?? ???????????????? ???? ???????????????????? ?????????? ", dataByDate[1][3], dataByDate[2][3],dataByDate[3][3],dataByDate[4][3]],
    //         ["????????????", dataByDate[1][4], dataByDate[2][4],dataByDate[3][4],dataByDate[4][4]],
    //         ["??????????????", dataByDate[1][5], dataByDate[2][5],dataByDate[3][5],dataByDate[4][5]],
    //         ["Cash",dataByDate[1][6], dataByDate[2][6],dataByDate[3][6],dataByDate[4][6]],
    //         ["Total",dataByDate[1][7], dataByDate[2][7],dataByDate[3][7],dataByDate[4][7]]
    //     ]);
    //     formatter.format(data, 1);
    //     formatter_prec.format(data, 2);
    //     formatter_prec.format(data, 3);
    //     formatter_prec.format(data, 4);
    //     return data;
    // }

    // private getEquityData(tableDate){
    //     var data = new google.visualization.DataTable();
    //     var formatter = new google.visualization.NumberFormat({
    //         prefix:'$ ',
    //         groupingSymbol: ' ',
    //         fractionDigits: 0
    //     });
    //     var formatter_perc = new google.visualization.NumberFormat({
    //         pattern:'#.##%',
    //         negativeColor: 'red'
    //     });
    //     data.addColumn("string", "");
    //     data.addColumn("number", "NAV");
    //     data.addColumn("number", "MTD");
    //     data.addColumn("number", "QTD");
    //     data.addColumn("number", "YTD");
    //     var dataByDate = this.getEquityByDate(tableDate);
    //     data.addRows([
    //         ["ETF", dataByDate[1][0], dataByDate[2][0], dataByDate[3][0],dataByDate[4][0]],
    //         ["Cash", dataByDate[1][1], dataByDate[2][1],dataByDate[3][1],dataByDate[4][1]],
    //         ["Total", dataByDate[1][2], dataByDate[2][2],dataByDate[3][2],dataByDate[4][2]]
    //     ]);
    //     formatter.format(data, 1);
    //     formatter_perc.format(data, 2);
    //     formatter_perc.format(data, 3);
    //     formatter_perc.format(data, 4);
    //     return data;
    // }

    // private getTransitionData(tableDate){
    //     var data = new google.visualization.DataTable();
    //     var formatter = new google.visualization.NumberFormat({
    //         prefix:'$ ',
    //         groupingSymbol: ' ',
    //         fractionDigits: 0
    //     });
    //     var formatter_perc = new google.visualization.NumberFormat({
    //         pattern:'#.##%',
    //         negativeColor: 'red'
    //     });
    //     data.addColumn("string", "");
    //     data.addColumn("number", "NAV");
    //     data.addColumn("number", "MTD");
    //     data.addColumn("number", "QTD");
    //     data.addColumn("number", "YTD");
    //     var dataByDate = this.getTransitionByDate(tableDate);
    //     data.addRows([
    //         ["Government bonds", dataByDate[1][0], dataByDate[2][0], dataByDate[3][0], dataByDate[4][0]],
    //         ["Cash", dataByDate[1][1], dataByDate[2][1], dataByDate[3][1], dataByDate[4][1]],
    //         ["Total", dataByDate[1][2], dataByDate[2][2], dataByDate[3][2], dataByDate[4][2]]
    //     ]);
    //     formatter.format(data, 1);
    //     formatter_perc.format(data, 2);
    //     formatter_perc.format(data, 3);
    //     formatter_perc.format(data, 4);
    //     return data;
    // }

    // private getNAVByDate(tableDate){
    //     for(var i = 0; i < this.nav.length; i++){
    //         if(this.nav[i][0] === tableDate){
    //             return this.nav[i];
    //         }
    //     }
    //     return null;
    // }

    // private getFixedIncomeByDate(tableDate){
    //     for(var i = 0; i < this.fixed_income.length; i++){
    //         if(this.fixed_income[i][0] === tableDate){
    //             return this.fixed_income[i];
    //         }
    //     }
    //     return null;
    // }

    // private getEquityByDate(tableDate){
    //     for(var i = 0; i < this.equity.length; i++){
    //         if(this.equity[i][0] === tableDate){
    //             return this.equity[i];
    //         }
    //     }
    //     return null;
    // }

    // private getTransitionByDate(tableDate){
    //     for(var i = 0; i < this.transition.length; i++){
    //         if(this.transition[i][0] === tableDate){
    //             return this.transition[i];
    //         }
    //     }
    //     return null;
    // }

    // drawValueTable(data){
    //     var options = {
    //         showRowNumber: false,
    //         width: '100%',
    //         height: '100%',
    //         'allowHtml': true,
    //         cssClassNames: {}
    //     };
    //
    //     var chart = this.createTableChart(document.getElementById('net_value'));
    //     chart.draw(data, options);
    // }

    // drawFixedIncomeTable(data){
    //     var options = {
    //         showRowNumber: false,
    //         width: '100%',
    //         height: '100%',
    //         'allowHtml': true,
    //         cssClassNames: {}
    //     };
    //
    //     var chart = this.createTableChart(document.getElementById('fixed_income_portfolio'));
    //     chart.draw(data, options);
    // }

    // drawEquityTable(data){
    //     var options = {
    //         showRowNumber: false,
    //         width: '100%',
    //         height: '100%',
    //         'allowHtml': true,
    //         cssClassNames: {}
    //     };
    //
    //     var chart = this.createTableChart(document.getElementById('equity_portfolio'));
    //     chart.draw(data, options);
    // }

    // drawTransitionTable(data){
    //     var options = {
    //         showRowNumber: false,
    //         width: '100%',
    //         height: '100%',
    //         'allowHtml': true,
    //         cssClassNames: {}
    //     };
    //
    //     var chart = this.createTableChart(document.getElementById('transition_portfolio'));
    //     chart.draw(data, options);
    // }

    drawFixedIncomeTableAndChart(tableDate){
        var tableData = this.getDataByDate(tableDate);

        var dataTable = new google.visualization.DataTable();

        var formatter = new google.visualization.NumberFormat({
            prefix:'$ ',
            groupingSymbol: ' ',
            fractionDigits: 0
        });

        var formatter_perc = new google.visualization.NumberFormat({
            pattern:'#.##%',
            negativeColor: 'red'
        });

        dataTable.addColumn("string", "");
        dataTable.addColumn("number", "NAV");
        dataTable.addColumn("number", "MTD");
        dataTable.addColumn("number", "QTD");
        dataTable.addColumn("number", "YTD");

        if(tableData != null) {
            if(tableData.governmentsFixed != null || tableData.governmentsFixedMtd != null || tableData.governmentsFixedQtd != null || tableData.governmentsFixedYtd != null) {
                dataTable.addRows([["Government bonds", tableData.governmentsFixed, tableData.governmentsFixedMtd, tableData.governmentsFixedQtd, tableData.governmentsFixedYtd]]);
            }
            if(tableData.corporates != null || tableData.corporatesMtd != null || tableData.corporatesQtd != null || tableData.corporatesYtd != null) {
                dataTable.addRows([["Corporate bonds", tableData.corporates, tableData.corporatesMtd, tableData.corporatesQtd, tableData.corporatesYtd]]);
            }
            if(tableData.agencies != null || tableData.agenciesMtd != null || tableData.agenciesQtd != null || tableData.agenciesYtd != null) {
                dataTable.addRows([["Agencies", tableData.agencies, tableData.agenciesMtd, tableData.agenciesQtd, tableData.agenciesYtd]]);
            }
            if(tableData.supranationals != null || tableData.supranationalsMtd != null || tableData.supranationalsQtd != null || tableData.supranationalsYtd != null) {
                dataTable.addRows([["Supranationals", tableData.supranationals, tableData.supranationalsMtd, tableData.supranationalsQtd, tableData.supranationalsYtd]]);
            }
            if(tableData.currency != null) {
                dataTable.addRows([["Currency", tableData.currency, null, null, null]]);
            }
            if(tableData.options != null) {
                dataTable.addRows([["Options", tableData.options, null, null, null]]);
            }
            if(tableData.cashFixed != null) {
                dataTable.addRows([["Cash", tableData.cashFixed, null, null, null]]);
            }
            if(tableData.cashBrokerAndFutures != null || tableData.cashBrokerAndFuturesMtd != null || tableData.cashBrokerAndFuturesQtd != null || tableData.cashBrokerAndFuturesYtd != null) {
                dataTable.addRows([["Cash margin at broker + futures", tableData.cashBrokerAndFutures, tableData.cashBrokerAndFuturesMtd, tableData.cashBrokerAndFuturesQtd, tableData.cashBrokerAndFuturesYtd]]);
            }
            if(tableData.totalFixed != null || tableData.totalFixedMtd != null || tableData.totalFixedQtd != null || tableData.totalFixedYtd != null) {
                dataTable.addRows([["Total", tableData.totalFixed, tableData.totalFixedMtd, tableData.totalFixedQtd, tableData.totalFixedYtd]]);
            }

            var dataChart = google.visualization.arrayToDataTable([
                ["Type", "%"],
                ["Government bonds", tableData.governmentsFixed != null ? tableData.governmentsFixed : 0],
                ["Corporate bonds", tableData.corporates != null ? tableData.corporates : 0],
                ["Agencies", tableData.agencies != null ? tableData.agencies : 0],
                ["Supranationals", tableData.supranationals != null ? tableData.supranationals : 0],
                ["Currency", tableData.currency != null ? tableData.currency : 0],
                ["Options", tableData.options != null ? tableData.options : 0],
                ["Cash", tableData.cashFixed != null ? tableData.cashFixed : 0],
                ["Cash margin at broker + futures", tableData.cashBrokerAndFutures != null ? tableData.cashBrokerAndFutures : 0],
            ]);
        }

        formatter.format(dataTable, 1);
        formatter_perc.format(dataTable, 2);
        formatter_perc.format(dataTable, 3);
        formatter_perc.format(dataTable, 4);

        var optionsTable = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {}
        };

        var optionsChart = {
            title: 'Fixed income portfolio structure',
            pieHole: 0.4,
        };

        var chartTable = this.createTableChart(document.getElementById('fixed_income_portfolio'));
        chartTable.draw(dataTable, optionsTable);

        var chartPieChart = new google.visualization.PieChart(document.getElementById('portfolio_structure_fixed_income_chart'));
        chartPieChart.draw(dataChart, optionsChart);

        google.visualization.events.addListener(chartTable, 'select',
            function() {
                chartPieChart.draw(dataChart, optionsChart);

                for (var i = 0; i < dataChart.Tf.length; i++) {
                    if (dataChart.Tf[i].c[0].v == dataTable.Tf[chartTable.getSelection()[0].row].c[0].v) {
                        console.log(i);
                        chartPieChart.setSelection([{row: i}]);
                    }
                }

                chartTable.setSelection(null);
            });
    }

    drawEquityTableAndChart(tableDate){
        var tableData = this.getDataByDate(tableDate);

        var dataTable = new google.visualization.DataTable();

        var formatter = new google.visualization.NumberFormat({
            prefix:'$ ',
            groupingSymbol: ' ',
            fractionDigits: 0
        });

        var formatter_perc = new google.visualization.NumberFormat({
            pattern:'#.##%',
            negativeColor: 'red'
        });

        dataTable.addColumn("string", "");
        dataTable.addColumn("number", "NAV");
        dataTable.addColumn("number", "MTD");
        dataTable.addColumn("number", "QTD");
        dataTable.addColumn("number", "YTD");

        if(tableData != null) {
            if(tableData.etf != null || tableData.etfMtd != null || tableData.etfQtd != null || tableData.etfYtd != null) {
                dataTable.addRows([["ETF", tableData.etf, tableData.etfMtd, tableData.etfQtd, tableData.etfYtd]]);
            }
            if(tableData.cashEquity != null) {
                dataTable.addRows([["Cash", tableData.cashEquity, null, null, null]]);
            }
            if(tableData.totalEquity != null || tableData.totalEquityMtd != null || tableData.totalEquityQtd != null || tableData.totalEquityYtd != null) {
                dataTable.addRows([["Total", tableData.totalEquity, tableData.totalEquityMtd, tableData.totalEquityQtd, tableData.totalEquityYtd]]);
            }

            var dataChart = google.visualization.arrayToDataTable([
                ["Type", "%"],
                ["ETF", tableData.etf != null ? tableData.etf : 0],
                ["Cash", tableData.cashEquity != null ? tableData.cashEquity : 0],
            ]);
        }

        formatter.format(dataTable, 1);
        formatter_perc.format(dataTable, 2);
        formatter_perc.format(dataTable, 3);
        formatter_perc.format(dataTable, 4);

        var optionsTable = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {}
        };

        var optionsChart = {
            title: 'Equity portfolio structure',
            pieHole: 0.4,
        };

        var chartTable = this.createTableChart(document.getElementById('equity_portfolio'));
        chartTable.draw(dataTable, optionsTable);

        var chartPieChart = new google.visualization.PieChart(document.getElementById('portfolio_structure_equity_chart'));
        chartPieChart.draw(dataChart, optionsChart);

        google.visualization.events.addListener(chartTable, 'select',
            function() {
                chartPieChart.draw(dataChart, optionsChart);

                for (var i = 0; i < dataChart.Tf.length; i++) {
                    if (dataChart.Tf[i].c[0].v == dataTable.Tf[chartTable.getSelection()[0].row].c[0].v) {
                        console.log(i);
                        chartPieChart.setSelection([{row: i}]);
                    }
                }

                chartTable.setSelection(null);
            });
    }

    drawTransitionTableAndChart(tableDate){
        var tableData = this.getDataByDate(tableDate);

        var dataTable = new google.visualization.DataTable();

        var formatter = new google.visualization.NumberFormat({
            prefix:'$ ',
            groupingSymbol: ' ',
            fractionDigits: 0
        });

        var formatter_perc = new google.visualization.NumberFormat({
            pattern:'#.##%',
            negativeColor: 'red'
        });

        dataTable.addColumn("string", "");
        dataTable.addColumn("number", "NAV");
        dataTable.addColumn("number", "MTD");
        dataTable.addColumn("number", "QTD");
        dataTable.addColumn("number", "YTD");

        if(tableData != null) {
            if(tableData.governmentsTransition != null || tableData.governmentsTransitionMtd != null || tableData.governmentsTransitionQtd != null || tableData.governmentsTransitionYtd != null) {
                dataTable.addRows([["Government bonds", tableData.governmentsTransition, tableData.governmentsTransitionMtd, tableData.governmentsTransitionQtd, tableData.governmentsTransitionYtd]]);
            }
            if(tableData.cashTransition != null) {
                dataTable.addRows([["Cash", tableData.cashTransition, null, null, null]]);
            }
            if(tableData.totalTransition != null || tableData.totalTransitionMtd != null || tableData.totalTransitionQtd != null || tableData.totalTransitionYtd != null) {
                dataTable.addRows([["Total", tableData.totalTransition, tableData.totalTransitionMtd, tableData.totalTransitionQtd, tableData.totalTransitionYtd]]);
            }

            var dataChart = google.visualization.arrayToDataTable([
                ["Type", "%"],
                ["Government bonds", tableData.governmentsTransition != null ? tableData.governmentsTransition : 0],
                ["Cash", tableData.cashTransition != null ? tableData.cashTransition : 0],
            ]);
        }

        formatter.format(dataTable, 1);
        formatter_perc.format(dataTable, 2);
        formatter_perc.format(dataTable, 3);
        formatter_perc.format(dataTable, 4);

        var optionsTable = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {}
        };

        var optionsChart = {
            title: 'Transition portfolio structure',
            pieHole: 0.4
        };

        var chartTable = this.createTableChart(document.getElementById('transition_portfolio'));
        chartTable.draw(dataTable, optionsTable);

        var chartPieChart = new google.visualization.PieChart(document.getElementById('portfolio_structure_transition_chart'));
        chartPieChart.draw(dataChart, optionsChart);

        google.visualization.events.addListener(chartTable, 'select',
            function() {
                chartPieChart.draw(dataChart, optionsChart);

                for (var i = 0; i < dataChart.Tf.length; i++) {
                    if (dataChart.Tf[i].c[0].v == dataTable.Tf[chartTable.getSelection()[0].row].c[0].v) {
                        console.log(i);
                        chartPieChart.setSelection([{row: i}]);
                    }
                }

                chartTable.setSelection(null);
            });
    }

    // PERFORMANCE TABLE -------------------------------

    // private getPerformanceData(tableDate) {
    //     var data = new google.visualization.DataTable();
    //     data.addColumn("string", "");
    //     data.addColumn("number", "");
    //
    //     var performanceArray = this.getPerformanceByDate(tableDate);
    //     data.addRows([
    //         ["Government Bonds", performanceArray[1]],
    //         ["Corporate Bonds", performanceArray[2]],
    //         ["Agencies", performanceArray[3]],
    //         ["Supranationals", performanceArray[4]],
    //         ["Cash", performanceArray[5]],
    //         ["Total", performanceArray[6]]
    //     ]);
    //     return data;
    // }

    // private getPerformanceByDate(tableDate){
    //     for(var i = 0; i < this.performance.length; i++){
    //         if(this.performance[i][0] === tableDate){
    //             return this.performance[i];
    //         }
    //     }
    //     return null;
    // }

    // drawPerformanceTable(data){
    //     var options = {
    //         showRowNumber: false,
    //         width: '100%',
    //         height: '100%',
    //         'allowHtml': true,
    //         cssClassNames: {}
    //     };
    //
    //     var chart = this.createTableChart(document.getElementById('performance'));
    //     chart.draw(data, options);
    // }

    // RETURNS -----------------------------------------

    // drawReturnsLineCharts(date){
    //     this.drawReturnsYTD(date);
    //     this.drawReturnsSinceInception(date);
    // }

    // drawReturnsYTD(date){
    //     var data = new google.visualization.DataTable();
    //     var formatter = new google.visualization.NumberFormat({
    //         pattern:'#.##%'
    //     });
    //     data.addColumn("string", "Date");
    //     data.addColumn("number", "Portfolio");
    //     data.addColumn("number", "ML 6m T Bills");
    //     data.addRows(this.getRowDataReturnsYTD(date));
    //     formatter.format(data, 1);
    //     formatter.format(data, 2);
    //
    //     var options = {
    //         showRowNumber: false,
    //         width: '100%',
    //         height: '100%',
    //         'allowHtml': false,
    //         vAxis: {
    //             format: '#.##%',
    //         },
    //         cssClassNames: {}
    //     };
    //
    //     var chart = this.createLineChart(document.getElementById('cum_returns_ytd'));
    //     chart.draw(data, options);
    // }

    // drawReturnsSinceInception(date){
    //     var data = new google.visualization.DataTable();
    //     var formatter = new google.visualization.NumberFormat({
    //         pattern:'#.##%'
    //     });
    //     data.addColumn("string", "Date");
    //     data.addColumn("number", "ML 6m T Bills");
    //     data.addColumn("number", "Portfolio");
    //     data.addRows(this.getRowDataReturnsSinceInception(date));
    //     formatter.format(data, 1);
    //     formatter.format(data, 2);
    //
    //     var options = {
    //         showRowNumber: false,
    //         width: '100%',
    //         height: '100%',
    //         'allowHtml': true,
    //         vAxis: {
    //             format: '#.##%',
    //         },
    //         cssClassNames: {}
    //     };
    //
    //     var chart = this.createLineChart(document.getElementById('cum_returns_inception'));
    //     chart.draw(data, options);
    //
    // }

    //  PORTFOLIO STRUCTURE ----------------------------

    // drawPieCharts(tableDate){
    //     if(tableDate.endsWith("19")){
    //         this.drawFixedIncomeChart(tableDate);
    //         this.drawEquityChart(tableDate);
    //         this.drawTransitionChart(tableDate);
    //     }else {
    //         this.drawPortfolioStructureChart();
    //         this.drawBondsByCountryChart();
    //         this.drawBondsBySectorChart();
    //     }
    // }

    // drawPortfolioStructureChart(){
    //     var data = google.visualization.arrayToDataTable([
    //         ['Type', '%'],
    //         ["Government Bonds",0.1485],
    //         ["Corporate Bonds",0.5487],
    //         ["Agencies",0.1037],
    //         ["Supernationals",0.1211],
    //         ["Margin at Broker",0.0068],
    //         ["Swaps", 0.0473],
    //         ["Cash", 0.0239]
    //     ]);
    //
    //     var options = {
    //         title: 'Portfolio Structure',
    //     };
    //
    //     var chart = new google.visualization.PieChart(document.getElementById('portfolio_structure'));
    //     chart.draw(data, options);
    // }

    // drawFixedIncomeChart(tableDate){
    //     var tableData = this.getFixedIncomeChartData(tableDate)
    //     var data = google.visualization.arrayToDataTable([
    //         ['Type', '%'],
    //         ["Government Bonds",tableData[1][0]],
    //         ["Corporate Bonds",tableData[1][1]],
    //         ["Agencies",tableData[1][2]],
    //         ["???????????????? ?? ???????????????? ???? ???????????????????? ??????????",tableData[1][3]],
    //         ["???????????? ",tableData[1][4]],
    //         ["Cash", tableData[1][5]]
    //         //["Total", 100]
    //     ]);
    //
    //     var options = {
    //         title: 'Fixed Income Portfolio Structure',
    //         pieHole: 0.4,
    //     };
    //
    //     var chart = new google.visualization.PieChart(document.getElementById('portfolio_structure_fixed_income_chart'));
    //     chart.draw(data, options);
    // }

    // getFixedIncomeChartData(tableDate){
    //     for(var i = 0; i < this.fixed_income_chart.length; i++){
    //         if(this.fixed_income_chart[i][0] === tableDate){
    //             return this.fixed_income_chart[i];
    //         }
    //     }
    //     return null;
    // }

    // getEquityChartData(tableDate){
    //     for(var i = 0; i < this.equity_chart.length; i++){
    //         if(this.equity_chart[i][0] === tableDate){
    //             return this.equity_chart[i];
    //         }
    //     }
    //     return null;
    // }

    // getTransitionChartData(tableDate){
    //     for(var i = 0; i < this.transition_chart.length; i++){
    //         if(this.transition_chart[i][0] === tableDate){
    //             return this.transition_chart[i];
    //         }
    //     }
    //     return null;
    // }

    // private fixed_income_chart = [
    //     ['Jan-19',[30.48,49.02,3.85,2.69,13.51,0.01,0.44]],
    //     ['Feb-19',[28.63,48.39,8.99,10.46,3.30,0.22]],
    //     ['Mar-19',[37,48,4,3,8,0]]
    // ];

    // private equity_chart = [
    //     ['Jan-19',[98.97, 1.03]],
    //     ['Feb-19',[98.99,1.01]],
    //     ['Mar-19',[99.64,0.36]]
    // ];

    // private transition_chart = [
    //     ['Jan-19',[99.6, 0.40]],
    //     ['Feb-19',[99.09,0.91]],
    //     ['Mar-19',[99.81,0.19]]
    // ];

    // drawEquityChart(tableDate){
    //     var tableData = this.getEquityChartData(tableDate)
    //     var data = google.visualization.arrayToDataTable([
    //         ['Type', '%'],
    //         ["ETF",tableData[1][0]],
    //         ["Cash", tableData[1][1]]
    //         //["Total", 100]
    //     ]);
    //
    //     var options = {
    //         title: 'Equity Portfolio Structure',
    //         pieHole: 0.4,
    //     };
    //
    //     var chart = new google.visualization.PieChart(document.getElementById('portfolio_structure_equity_chart'));
    //     chart.draw(data, options);
    // }

    // drawTransitionChart(tableDate){
    //     var tableData = this.getTransitionChartData(tableDate)
    //     var data = google.visualization.arrayToDataTable([
    //         ['Type', '%'],
    //         ["Government bonds",tableData[1][0]],
    //         ["Cash", tableData[1][1]]
    //         //["Total", 1]
    //     ]);
    //
    //     var options = {
    //         title: 'Transition Portfolio Structure',
    //         pieHole: 0.4
    //     };
    //
    //     var chart = new google.visualization.PieChart(document.getElementById('portfolio_structure_transition_chart'));
    //     chart.draw(data, options);
    // }

    // drawBondsByCountryChart(){
    //     var data = google.visualization.arrayToDataTable([
    //         ['Country', '%'],
    //         ["United States", 0.5231],
    //         ["Canada", 	0.0369],
    //         ["South Korea", 0.0185],
    //         ["Japan", 0.0776],
    //         ["China", 0.0486],
    //         ["Britain", 0.2952],
    //         ["Others", 0]
    //     ]);
    //
    //     var options = {
    //         title: 'Corporate Bonds By Country'
    //     };
    //
    //     var chart = new google.visualization.PieChart(document.getElementById('bonds_by_country'));
    //     chart.draw(data, options);
    // }

    // drawBondsBySectorChart(){
    //     var data = google.visualization.arrayToDataTable([
    //         ['Sector', '%'],
    //         //['Energy', 0],
    //         ['Consumers', 0.11],
    //         ['Financial', 0.85],
    //         ['Communications', 0.04],
    //         //['Other', 0]
    //     ]);
    //
    //     var options = {
    //         title: 'Corporate Bonds By Sector'
    //     };
    //
    //     var chart = new google.visualization.PieChart(document.getElementById('bonds_by_sector'));
    //     chart.draw(data, options);
    // }

    // POSITIONS TABLE ----------------------------------

    // drawPositionsTable(date){
    //     var data = new google.visualization.DataTable();
    //     data.addColumn("string", "ISIN");
    //     data.addColumn("string", "Issuer");
    //     data.addColumn("string", "Type");
    //     data.addColumn("string", "Maturity date");
    //     data.addColumn("number", "Face value");
    //     data.addColumn("number", "Buy price");
    //     data.addColumn("number", "Transaction Amount");
    //     data.addColumn("number", "Price change");
    //     data.addColumn("number", "Abs. price P&L");
    //     data.addColumn("number", "Abs. Total Return");
    //     data.addColumn("number", "Total Return");
    //
    //     data.addRows(this.getPositions(date));
    //
    //     var options = {
    //         showRowNumber: false,
    //         width: '100%',
    //         height: '100%',
    //         'allowHtml': true,
    //         cssClassNames: {
    //             headerRow: 'googleChartTable',
    //             tableCell: 'googleChartTable',
    //         }
    //     };
    //
    //     var chart = this.createTableChart(document.getElementById('position_table'));
    //     chart.draw(data, options);
    // }

    // DATA SOURCES --------------------------------------

    // public getAllDates(){
    //     var dates = [];
    //     for(var i = this.fixed_income.length - 1; i >= 0; i--){
    //         if(!dates.includes(this.fixed_income[i][0])){
    //             dates.push(this.fixed_income[i][0]);
    //         }
    //     }
    //     for(var i = this.nav.length - 1; i >= 0; i--){
    //         if(!dates.includes(this.nav[i][0])) {
    //             dates.push(this.nav[i][0]);
    //         }
    //     }
    //     return dates;
    // }

    // private nav = [
    //     ["Sep-16",105939320,145406608,180764953,105447406,1251970,61795069,614968977],
    //     ["Oct-16",91022227,237796146,119768433,105493262,1246973,26006836,607250213],
    //     ["Nov-16",56068270,253124756,111749442,100499500,1999277,26075274,594189386],
    //     ["Dec-16",69811340,316801341,86769238,64912802,6103132,105030857,591152852],
    //     ["Jan-17",69876340,268378406,76711605,65074072,6065035,48660019,560171989],
    //     ["Feb-17",103448315,268515293,71713700,65123983,5694154,99157299,556305393],
    //     ["Mar-17",104495055,292570184,55550922,64943737,5654862,1908436,550424168],
    //     ["Apr-17",94397032,293174382,55622068,65019968,5790134,540846,538617288],
    //     ["May-17",79710320,294581051,55696260,64997438,4352978,12823324,536854358],
    // ];

    // private fixed_income = [
    //     ["Jan-19",[118022844,189787416,14924250,10399225,52309397,20312,1695973,387159417],
    //         [0.0148,0.0175,0.0052,-0.0443,null,null,null,0.0120],
    //         [0.0148,0.0175,0.0052,-0.0443,null,null,null,0.0120],
    //         [0.0148,0.0175,0.0052,-0.0443,null,null,null,0.0120]
    //     ],
    //     ["Feb-19",[111074185,187723937,34891620,12810512,40595153,844611,387940018],
    //         [-0.0069,-0.0003,0.0027,.2319,null,null,0.0020],
    //         [0.0078,0.0172,0.0078,0.1773,null,null,0.0140],
    //         [0.0078,0.0172,0.0078,0.1773,null,null,0.0140]
    //     ],
    //     ["Mar-19",[144732326,187901404,15053250,11669144,32959251,null,1227971,393543346],
    //         [0.0271,0.0139,0.0228,-0.0891,null,null,null,0.0144],
    //         [0.0351,0.0313,0.0308,0.0724,null,null,null,0.0287],
    //         [0.0351,0.0313,0.0308,0.0724,null,null,null,0.0287]
    //     ],
    // ];

    // private equity = [
    //     ["Jan-19",[43192296,449366,43641662],
    //         [0.0827,null,0.0818],
    //         [0.0827,null,0.0818],
    //         [0.0827,null,0.0818]
    //     ],
    //     ["Feb-19",[44324991,450226,44775217],
    //         [0.0262,null,0.026],
    //         [0.1111,null,0.1099],
    //         [0.11,null,0.1099]
    //     ],
    //     ["Mar-19",[45147697,162759,45310456],
    //         [0.0090,null,0.0120],
    //         [0.1211,null,0.1232],
    //         [0.1211,null,0.1232]
    //     ],
    // ];

    // private transition = [
    //     ["Jan-19",[278574157,1113487,279687644],
    //         [0.0030,null,0.0030],
    //         [0.0030,null,0.0030],
    //         [0.0030,null,0.0030]
    //     ],
    //     ["Feb-19",[261231301,2386754,263618055],
    //         [0.0004,null,0.0003],
    //         [0.0034,null,0.0033],
    //         [0.0034,null,0.0033]
    //     ],
    //     ["Mar-19",[231174987,445267,231620254],
    //         [0.0065,null,0.0064],
    //         [0.0099,null,0.0098],
    //         [0.0099,null,0.0098]
    //     ],
    // ];

    // private performance = [
    //     ["Sep-16",0.82,0.94,1.11,0.79,0.75,1.03],
    //     ["Oct-16",0.42,2.10,1.19,0.89,0.74,1.39],
    //     ["Nov-16",0.86,0.75,1.2,0.83,0.77,1.16],
    //     ["Dec-16",0.68,2.44,-0.03,-1.6,0,-0.46],
    //     ["Jan-17",0.68,2.44,-0.03,-1.6,0,1.21],
    //     ["Feb-17",0.76,9.98,0.09,0.33,0,-1.51],
    //     ["Mar-17",0.73,4.08,-0.06,0.03,0,1.79],
    //     ["Apr-17",0.45,0.87,2.95,1.21,0,1.65],
    //     ["May-17",0.08,2.76,-0.04,0.9,0,-0.5],
    // ];

    // private getRowDataReturnsYTD(date){
    //     var returns = [
    //         ["04.03.2016",0.001313476,0.001445176],
    //         ["07.03.2016",0.001337466,0.001457065],
    //         ["08.03.2016",0.001410065,0.001468954],
    //         ["09.03.2016",0.001404179,0.001434608],
    //         ["10.03.2016",0.001378614,0.00140026],
    //         ["11.03.2016",0.001365935,0.001391013],
    //         ["14.03.2016",0.001451675,0.001381765],
    //         ["15.03.2016",0.00142579,0.001372517],
    //         ["16.03.2016",0.001585374,0.001496704],
    //         ["17.03.2016",0.001859792,0.001575963],
    //         ["18.03.2016",0.001988246,0.001743715],
    //         ["21.03.2016",0.00200378,0.00175428],
    //         ["22.03.2016",0.001979982,0.001766166],
    //         ["23.03.2016",0.001985612,0.001797861],
    //         ["24.03.2016",0.002059552,0.00184276],
    //         ["25.03.2016",0.002081504,0.00184276],
    //         ["28.03.2016",0.002182316,0.001853325],
    //         ["29.03.2016",0.00234054,0.001988017],
    //         ["30.03.2016",0.002514319,0.002100245],
    //         ["31.03.2016",0.002991509,0.002150412],
    //         ["04.04.2016",0.002754185,0.002401239],
    //         ["05.04.2016",0.002821637,0.002533218],
    //         ["06.04.2016",0.002818493,0.002615034],
    //         ["07.04.2016",0.002891971,0.002538503],
    //         ["08.04.2016",0.002968227,0.002563575],
    //         ["11.04.2016",0.003045785,0.002572812],
    //         ["12.04.2016",0.002996665,0.00258073],
    //         ["13.04.2016",0.00298783,0.002589967],
    //         ["14.04.2016",0.003030496,0.002597884],
    //         ["15.04.2016",0.003016996,0.002624274],
    //         ["18.04.2016",0.003076393,0.002633511],
    //         ["19.04.2016",0.003044128,0.002641428],
    //         ["20.04.2016",0.003063329,0.002650664],
    //         ["21.04.2016",0.002244127,0.002703442],
    //         ["22.04.2016",0.002999753,0.002728511],
    //         ["25.04.2016",0.003113167,0.002692888],
    //         ["26.04.2016",0.003097443,0.002702124],
    //         ["27.04.2016",0.00312801,0.002743025],
    //         ["28.04.2016",0.003305664,0.002782605],
    //         ["29.04.2016",0.00327622,0.002807671],
    //         ["02.05.2016",0.003466931,0.00282746],
    //         ["03.05.2016",0.003585354,0.002874952],
    //         ["04.05.2016",0.003593862,0.002981804],
    //         ["05.05.2016",0.003640015,0.002992357],
    //         ["06.05.2016",0.003686512,0.003021375],
    //         ["09.05.2016",0.003810531,0.003149315],
    //         ["10.05.2016",0.003858567,0.00311107],
    //         ["11.05.2016",0.003907727,0.00312162],
    //         ["12.05.2016",0.003863685,0.003129533],
    //         ["13.05.2016",0.00383544,0.003124258],
    //         ["16.05.2016",0.003867003,0.0031902],
    //         ["17.05.2016",0.003751241,0.003120306],
    //         ["18.05.2016",0.003504102,0.003006886],
    //         ["19.05.2016",0.00348025,0.002972592],
    //         ["20.05.2016",0.003457023,0.002939617],
    //         ["23.05.2016",0.003662327,0.002885534],
    //         ["24.05.2016",0.003571393,0.002874981],
    //         ["25.05.2016",0.003570987,0.002909279],
    //         ["26.05.2016",0.003843459,0.002940938],
    //         ["27.05.2016",0.003841076,0.002944895],
    //         ["30.05.2016",0.003866972,0.00298051],
    //         ["31.05.2016",0.003559487,0.002905326],
    //         ["01.06.2016",0.00392497,0.002967325],
    //         ["02.06.2016",0.003990701,0.003004258],
    //         ["03.06.2016",0.004613008,0.003232443],
    //         ["06.06.2016",0.004753997,0.003242993],
    //         ["07.06.2016",0.004923881,0.003254861],
    //         ["08.06.2016",0.004955708,0.00326541],
    //         ["09.06.2016",0.005041667,0.003277278],
    //         ["10.06.2016",0.005149111,0.003310244],
    //         ["13.06.2016",0.005403625,0.003459244],
    //         ["14.06.2016",0.00537341,0.003468473],
    //         ["15.06.2016",0.005353695,0.00356867],
    //         ["16.06.2016",0.005409639,0.003758498],
    //         ["17.06.2016",0.0054228,0.003740046],
    //         ["20.06.2016",0.005639798,0.003683371],
    //         ["21.06.2016",0.005810147,0.003692597],
    //         ["22.06.2016",0.005805354,0.003724231],
    //         ["23.06.2016",0.005873018,0.003733458],
    //         ["24.06.2016",0.006042963,0.003761136],
    //         ["27.06.2016",0.006404775,0.003978605],
    //         ["28.06.2016",0.006864128,0.004089293],
    //         ["29.06.2016",0.007121543,0.004097198],
    //         ["30.06.2016",0.007228747,0.004103786],
    //         ["01.07.2016",0.007689584,0.00416571],
    //         ["04.07.2016",0.007844265,0.004190742],
    //         ["05.07.2016",0.007988972,0.004199964],
    //         ["06.07.2016",0.007974455,0.004184155],
    //         ["07.07.2016",0.007987922,0.004120918],
    //         ["08.07.2016",0.008008299,0.00410379],
    //         ["11.07.2016",0.008266482,0.004041866],
    //         ["12.07.2016",0.008280844,0.004076124],
    //         ["13.07.2016",0.008198488,0.004086665],
    //         ["14.07.2016",0.008260219,0.004049773],
    //         ["15.07.2016",0.008445447,0.00401288],
    //         ["18.07.2016",0.008172517,0.004024739],
    //         ["19.07.2016",0.008431259,0.00403528],
    //         ["20.07.2016",0.008292138,0.004045821],
    //         ["21.07.2016",0.008114605,0.004057679],
    //         ["22.07.2016",0.008283998,0.00409062],
    //         ["25.07.2016",0.008338327,0.00410116],
    //         ["26.07.2016",0.008397397,0.004144639],
    //         ["27.07.2016",0.008265367,0.004252673],
    //         ["28.07.2016",0.008345726,0.004326444],
    //         ["29.07.2016",0.008088429,0.004438411],
    //         ["01.08.2016",0.00864646,0.004398898],
    //         ["02.08.2016",0.008563237,0.004359383],
    //         ["03.08.2016",0.00863257,0.004346211],
    //         ["04.08.2016",0.008600359,0.004380459],
    //         ["05.08.2016",0.008963705,0.004340943],
    //         ["08.08.2016",0.008810452,0.004281668],
    //         ["09.08.2016",0.008891798,0.004292206],
    //         ["10.08.2016",0.008799993,0.00439759],
    //         ["11.08.2016",0.009113658,0.004408127],
    //         ["12.08.2016",0.009021881,0.004463447],
    //         ["15.08.2016",0.009288599,0.004473984],
    //         ["16.08.2016",0.009258088,0.00443974],
    //         ["17.08.2016",0.009057501,0.004429203],
    //         ["18.08.2016",0.009029254,0.004484522],
    //         ["19.08.2016",0.009183942,0.004516131],
    //         ["22.08.2016",0.009230445,0.004505595],
    //         ["23.08.2016",0.009328816,0.004559593],
    //         ["24.08.2016",0.009397718,0.004571445],
    //         ["25.08.2016",0.009411376,0.004581981],
    //         ["26.08.2016",0.009533862,0.004571445],
    //         ["29.08.2016",0.009520256,0.004541156],
    //         ["30.08.2016",0.0096352,0.004593835],
    //         ["31.08.2016",0.009297151,0.004543793],
    //         ["01.09.2016",0.009507775,0.004604373],
    //         ["02.09.2016",0.009557391,0.004654414],
    //         ["05.09.2016",0.009624318,0.004691285],
    //         ["06.09.2016",0.009658427,0.004666267],
    //         ["07.09.2016",0.009690987,0.00470182],
    //         ["08.09.2016",0.009749326,0.0046452],
    //         ["09.09.2016",0.009713309,0.004546438],
    //         ["12.09.2016",0.009762425,0.004513514],
    //         ["13.09.2016",0.009786874,0.004528001],
    //         ["14.09.2016",0.009753406,0.004608337],
    //         ["15.09.2016",0.009836408,0.004800601],
    //         ["16.09.2016",0.00981676,0.004837466],
    //         ["19.09.2016",0.0097708,0.004892763],
    //         ["20.09.2016",0.009830738,0.004904611],
    //         ["21.09.2016",0.009755396,0.005066539],
    //         ["22.09.2016",0.010144197,0.005354803],
    //         ["23.09.2016",0.010172401,0.005382437],
    //         ["26.09.2016",0.010234513,0.005391648],
    //         ["27.09.2016",0.010174384,0.005400859],
    //         ["28.09.2016",0.010282192,0.00541007],
    //         ["29.09.2016",0.010138182,0.005417965],
    //         ["30.09.2016",0.010212477,0.005406122]
    //     ];
    //     if(date == null){
    //         return returns;
    //     }
    //     var monthReturns = [];
    //     var month = date.substring(3,5);
    //     for(var i = 0; i < returns.length; i++){
    //         if(typeof returns[i][0] === 'string' && (returns[i][0] + "").substring(3,5) === month){
    //             monthReturns.push(returns[i]);
    //         }
    //     }
    //
    //     return monthReturns;
    // }

    // private getRowDataReturnsSinceInception(date){
    //     var returns = [
    //         ["04.03.2016",0.01441552,0.008092661],
    //         ["07.03.2016",0.01443951,0.008104550],
    //         ["08.03.2016",0.014512109,0.008116439],
    //         ["09.03.2016",0.014506222,0.008082093],
    //         ["10.03.2016",0.014480658,0.008047745],
    //         ["11.03.2016",0.014467979,0.008038498],
    //         ["14.03.2016",0.014553719,0.008029250],
    //         ["15.03.2016",0.014527834,0.008020002],
    //         ["16.03.2016",0.014687418,0.008144190],
    //         ["17.03.2016",0.014961836,0.008223448],
    //         ["18.03.2016",0.01509029,0.008391200],
    //         ["21.03.2016",0.015105823,0.008401765],
    //         ["22.03.2016",0.015082026,0.008413651],
    //         ["23.03.2016",0.015087656,0.008445346],
    //         ["24.03.2016",0.015161595,0.008490246],
    //         ["25.03.2016",0.015183548,0.008490246],
    //         ["28.03.2016",0.01528436,0.008500810],
    //         ["29.03.2016",0.015442584,0.008635502],
    //         ["30.03.2016",0.015616363,0.008747730],
    //         ["31.03.2016",0.016093553,0.008797897],
    //         ["04.04.2016",0.015856228,0.009048724],
    //         ["05.04.2016",0.01592368,0.009180703],
    //         ["06.04.2016",0.015920537,0.009262519],
    //         ["07.04.2016",0.015994015,0.009185988],
    //         ["08.04.2016",0.016070271,0.009211060],
    //         ["11.04.2016",0.016147829,0.009220297],
    //         ["12.04.2016",0.016098709,0.009228215],
    //         ["13.04.2016",0.016089873,0.009237452],
    //         ["14.04.2016",0.01613254,0.009245369],
    //         ["15.04.2016",0.01611904,0.009271759],
    //         ["18.04.2016",0.016178437,0.009280996],
    //         ["19.04.2016",0.016146172,0.009288913],
    //         ["20.04.2016",0.016165373,0.009298149],
    //         ["21.04.2016",0.015346171,0.009350927],
    //         ["22.04.2016",0.016101796,0.009375996],
    //         ["25.04.2016",0.01621521,0.009340373],
    //         ["26.04.2016",0.016199486,0.009349609],
    //         ["27.04.2016",0.016230054,0.009390510],
    //         ["28.04.2016",0.016407708,0.009430090],
    //         ["29.04.2016",0.016378263,0.009455157],
    //         ["02.05.2016",0.016568974,0.000019789],
    //         ["03.05.2016",0.016687398,0.000047492],
    //         ["04.05.2016",0.016695906,0.000106852],
    //         ["05.05.2016",0.016742058,0.000010552],
    //         ["06.05.2016",0.016788556,0.000029018],
    //         ["09.05.2016",0.016912574,0.000127940],
    //         ["10.05.2016",0.016960611,-0.000038245],
    //         ["11.05.2016",0.017009771,0.000010551],
    //         ["12.05.2016",0.016965729,0.000007913],
    //         ["13.05.2016",0.016937484,-0.000005275],
    //         ["16.05.2016",0.016969047,0.000065942],
    //         ["17.05.2016",0.016853285,-0.000069894],
    //         ["18.05.2016",0.016606146,-0.000113420],
    //         ["19.05.2016",0.016582293,-0.000034294],
    //         ["20.05.2016",0.016559067,-0.000032976],
    //         ["23.05.2016",0.016764371,-0.000054082],
    //         ["24.05.2016",0.016673437,-0.000010553],
    //         ["25.05.2016",0.01667303,0.000034298],
    //         ["26.05.2016",0.016945503,0.000031659],
    //         ["27.05.2016",0.01694312,0.000003957],
    //         ["30.05.2016",0.016969015,0.000035615],
    //         ["31.05.2016",0.016661531,-0.000075184],
    //         ["01.06.2016",0.017027014,0.000061999],
    //         ["02.06.2016",0.017092745,0.000036933],
    //         ["03.06.2016",0.017715052,0.000228186],
    //         ["06.06.2016",0.01785604,0.000010550],
    //         ["07.06.2016",0.018025925,0.000011868],
    //         ["08.06.2016",0.018057751,0.000010549],
    //         ["09.06.2016",0.018143711,0.000011868],
    //         ["10.06.2016",0.018251155,0.000032966],
    //         ["13.06.2016",0.018505669,0.000149000],
    //         ["14.06.2016",0.018475453,0.000009229],
    //         ["15.06.2016",0.018455738,0.000100197],
    //         ["16.06.2016",0.018511683,0.000189828],
    //         ["17.06.2016",0.018524844,-0.000018452],
    //         ["20.06.2016",0.018741842,-0.000056675],
    //         ["21.06.2016",0.01891219,0.000009227],
    //         ["22.06.2016",0.018907398,0.000031634],
    //         ["23.06.2016",0.018975061,0.000009226],
    //         ["24.06.2016",0.019145007,0.000027679],
    //         ["27.06.2016",0.019506819,0.000217469],
    //         ["28.06.2016",0.019966172,0.000110687],
    //         ["29.06.2016",0.020223587,0.000007905],
    //         ["30.06.2016",0.02033079,0.000006588],
    //         ["01.07.2016",0.020791627,0.000061924],
    //         ["04.07.2016",0.020946309,0.000025032],
    //         ["05.07.2016",0.021091015,0.000009222],
    //         ["06.07.2016",0.021076498,-0.000015809],
    //         ["07.07.2016",0.021089966,-0.000063237],
    //         ["08.07.2016",0.021110343,-0.000017128],
    //         ["11.07.2016",0.021368526,-0.000061924],
    //         ["12.07.2016",0.021382888,0.000034258],
    //         ["13.07.2016",0.021300532,0.000010541],
    //         ["14.07.2016",0.021362263,-0.000036892],
    //         ["15.07.2016",0.021547491,-0.000036893],
    //         ["18.07.2016",0.02127456,0.000011859],
    //         ["19.07.2016",0.021533302,0.000010541],
    //         ["20.07.2016",0.021394182,0.000010541],
    //         ["21.07.2016",0.021216649,0.000011859],
    //         ["22.07.2016",0.021386041,0.000032940],
    //         ["25.07.2016",0.021440371,0.000010540],
    //         ["26.07.2016",0.021499441,0.000043479],
    //         ["27.07.2016",0.021367411,0.000108034],
    //         ["28.07.2016",0.02144777,0.000073771],
    //         ["29.07.2016",0.021190473,0.000111966],
    //         ["01.08.2016",0.021748504,-0.000039513],
    //         ["02.08.2016",0.021665281,-0.000039515],
    //         ["03.08.2016",0.021734614,-0.000013172],
    //         ["04.08.2016",0.021702403,0.000034248],
    //         ["05.08.2016",0.022065749,-0.000039515],
    //         ["08.08.2016",0.021912496,-0.000059275],
    //         ["09.08.2016",0.021993842,0.000010538],
    //         ["10.08.2016",0.021902037,0.000105384],
    //         ["11.08.2016",0.022215702,0.000010537],
    //         ["12.08.2016",0.022123925,0.000055320],
    //         ["15.08.2016",0.022390643,0.000010537],
    //         ["16.08.2016",0.022360132,-0.000034243],
    //         ["17.08.2016",0.022159545,-0.000010537],
    //         ["18.08.2016",0.022131298,0.000055319],
    //         ["19.08.2016",0.022285986,0.000031609],
    //         ["22.08.2016",0.022332489,-0.000010536],
    //         ["23.08.2016",0.022430859,0.000053998],
    //         ["24.08.2016",0.022499761,0.000011852],
    //         ["25.08.2016",0.022513419,0.000010535],
    //         ["26.08.2016",0.022635905,-0.000010535],
    //         ["29.08.2016",0.0226223,-0.000030289],
    //         ["30.08.2016",0.022737244,0.000052679],
    //         ["31.08.2016",0.022399195,-0.000050042],
    //         ["01.09.2016",0.022609818,0.000060580],
    //         ["02.09.2016",0.022659435,0.000050042],
    //         ["05.09.2016",0.022726362,0.000036871],
    //         ["06.09.2016",0.022760471,-0.000025019],
    //         ["07.09.2016",0.022793031,0.000035554],
    //         ["08.09.2016",0.02285137,-0.000056620],
    //         ["09.09.2016",0.022815353,-0.000098762],
    //         ["12.09.2016",0.022864469,-0.000032924],
    //         ["13.09.2016",0.022888918,0.000014487],
    //         ["14.09.2016",0.02285545,0.000080336],
    //         ["15.09.2016",0.022938452,0.000192264],
    //         ["16.09.2016",0.022918803,0.000036865],
    //         ["19.09.2016",0.022872844,0.000055296],
    //         ["20.09.2016",0.022932782,0.000011849],
    //         ["21.09.2016",0.022857439,0.000161928],
    //         ["22.09.2016",0.023246241,0.000288264],
    //         ["23.09.2016",0.023274445,0.000027634],
    //         ["26.09.2016",0.023336557,0.000009211],
    //         ["27.09.2016",0.023276427,0.000009211],
    //         ["28.09.2016",0.023384236,0.000009211],
    //         ["29.09.2016",0.023240226,0.000007895],
    //         ["30.09.2016",0.023314521,-0.000011842]
    //     ];
    //     if(date == null){
    //         return returns;
    //     }
    //     var monthReturns = [];
    //     var month = date.substring(3,5);
    //     for(var i = 0; i < returns.length; i++){
    //         if(typeof returns[i][0] === 'string' && (returns[i][0] + "").substring(3,5) === month){
    //             monthReturns.push(returns[i]);
    //         }
    //     }
    //     return monthReturns;
    // }

    // private getPositions(date){
    //     var positions= [
    //         ["Sep-16", ["US50064FAF18","REPUBLIC OF KOREA","GOVTS","07.12.2016",6000000,103.647,6254695,-0.028288325,-175920,38475.83333,0.006151512]],
    //         ["Sep-16", ["US912796KP37","TREASURY BILL","GOVTS","23.03.2017",100000000,99.75177222,99751772.22,0.00051255,51127.78,51127.78,0.00051255]],
    //         ["Sep-16", ["US50050HAE62","KOOKMIN BANK","CORPS","14.07.2017",5000000,99.88,4995805.56,0.002713256,13550,191397.22,0.038311583]],
    //         ["Sep-16", ["USY1391CDU28","BANK OF CHINA HONG KONG","CORPS","08.11.2016",5000000,104.81,5279041.67,-0.04360271,-228500,181916.66,0.034460167]],
    //         ["Sep-16", ["USJ46186AT93","MIZUHO BANK LTD","CORPS","25.09.2017",21000000,100,21000000,-0.00072,-15120,290699.775,0.013842846]],
    //         ["Sep-16", [" US05574LTV08 ","BNP PARIBAS","CORPS","12.12.2016",8040000,100.234,8066879.06,-0.001386755,-11175.6,52415.1,0.006497569]],
    //         ["Sep-16", ["US78010UNX18","ROYAL BANK OF CANADA","CORPS","23.01.2017",10000000,100.211,10022766.67,-0.002185389,-21900,-1233.34,-0.000123054]],
    //         ["Sep-16", ["US50065XAB01","KOREA NATIONAL OIL CORP","CORPS","27.10.2016",5000000,102.05,5159722.22,-0.018520333,-94500,33277.78,0.006449529]],
    //         ["Sep-16", ["US06366QW868","BANK OF MONTREAL","CORPS","11.01.2017",17925000,101.105,18248795.21,-0.007457594,-135154.5,61522.58,0.003371323]],
    //         ["Sep-16", ["US064159AM82","BANK OF NOVA SCOTIA","CORPS","12.01.2017",20000000,101.145,20370666.67,-0.007256908,-146800,77033.33,0.003781581]],
    //         ["Sep-16", ["XS1437622548","BANK OF CHINA/LUXEMBOURG","CORPS","12.07.2019",10000000,99.93,9993000,0.003382368,33800,74425,0.007447713]],
    //         ["Sep-16", ["US46623EKG34","JPMORGAN CHASE & CO","CORPS","15.08.2021",15000000,100.5,15088387.5,-0.00281592,-42450,-6112.5,-0.000405113]],
    //         ["Sep-16", ["US06738EAN58","BARCLAYS PLC","CORPS","12.01.2026",11200000,103.35,11667755.56,0.001093372,12656,26267.11,0.002251256]],
    //         ["Sep-16", ["US06738EAN58","BARCLAYS PLC","CORPS","12.01.2026",5000000,103.55,5219427.08,-0.000840174,-4350,1118.75,0.000214343]],
    //         ["Sep-16", ["US06738EAN58","BARCLAYS PLC","CORPS","12.01.2026",5000000,104.65,5281718.75,-0.01134257,-59350,-59350,-0.011236872]],
    //         ["Sep-16", ["XS1395052639","STANDARD CHARTERED PLC","CORPS","12.04.2026",2000000,104.43,2127300,-0.013511443,-28220,0,0]],
    //         ["Sep-16", ["US06120TAA60","BANK OF CHINA","CORPS","13.11.2024",3000000,108.75,3321666.67,-0.005333333,-17400,0,0]],
    //         ["Sep-16", ["XS0755979753","DEVELOPMENT BK OF JAPAN","AGENCIES","13.03.2017",16000000,100.201,16112160,-0.001067854,-17120,154213.3333,0.009571239]],
    //         ["Sep-16", ["XS0735390303","TOKYO METROPOLITAN GOVT","AGENCIES","27.01.2017",8200000,100.655,8403616.25,-0.004450847,-36736,70889,0.008435535]],
    //         ["Sep-16", ["XS0735390303","TOKYO METROPOLITAN GOVT","AGENCIES","27.01.2017",11670000,100.555,11801020.06,-0.003460793,-40611.6,40835.2775,0.003460318]],
    //         ["Sep-16", ["FR0011616416","CAISSE DES DEPOTS ET CON","AGENCIES","07.11.2016",8000000,99.99,8012422.22,0.000110011,880,50463.33556,0.006298137]],
    //         ["Sep-16", ["XS0975574624","AGENCE FRANCAISE DEVELOP","AGENCIES","03.10.2016",20000000,100.2,20120625,-0.00250499,-50200,92300,0.004587333]],
    //         ["Sep-16", ["XS0285449368","DEVELOPMENT BK OF JAPAN","AGENCIES","01.02.2017",5000000,103.7,5206354.17,-0.023153327,-120050,28717.35778,0.005515829]],
    //         ["Sep-16", ["XS0789060653","TOKYO METROPOLITAN GOVT","AGENCIES","08.06.2017",5000000,100.72,5099923.61,-0.003504766,-17650,-10601.38778,-0.002078735]],
    //         ["Sep-16", ["XS0789060653","TOKYO METROPOLITAN GOVT","AGENCIES","08.06.2017",20000000,100.45,20359305.56,-0.000826282,-16600,-2016.671111,-0.000099054]],
    //         ["Sep-16", ["XS1383319461","DEXIA CREDIT LOCAL","AGENCIES","23.03.2018",20000000,100,20000000,0.00183,36600,42290.72778,0.002114536]],
    //         ["Sep-16", ["US500769FZ20","KFW","AGENCIES","15.12.2016",25000000,99.97,25047621.53,0.000290087,7250,75826.38667,0.003027289]],
    //         ["Sep-16", ["US25215DAA72","DEXIA CREDIT LOCAL SA NY","AGENCIES","18.10.2016",40800000,100.16,40870946.67,-0.001727236,-70584,153249.33,0.003749591]],
    //         ["Sep-16", ["US45950KBQ22","INTL FINANCE CORP","SUPRANATIONAL","23.11.2016",50000000,101.28,50688437.5,-0.011996445,-607500,948750,0.018717286]],
    //         ["Sep-16", ["US298785GE09","EUROPEAN INVESTMENT BANK","SUPRANATIONAL","15.12.2016",50000000,101.065,50546562.5,-0.009864938,-498500,776500,0.015362073]],
    //         ["Sep-16", ["US4581X0CB23","INTER-AMERICAN DEVEL BK","SUPRANATIONAL","15.11.2016",5000000,100.075,5006545.14,-0.00011991,-600,34886.11,0.006968101]],
    //         ["Oct-16", ["US912796KP37","TREASURY BILL","GOVTS","23.03.2017",85000000,99.75177222,84789006.39,0.001044871,88593.613,88593.613,0.001044871]],
    //         ["Oct-16", ["US50050HAE62","KOOKMIN BANK","CORPS","14.07.2017",5000000,99.88,4995805.56,0.00164197,8200,192818.05,0.038595988]],
    //         ["Oct-16", ["USY1391CDU28","BANK OF CHINA HONG KONG","CORPS","08.11.2016",5000000,104.81,5279041.67,-0.045587253,-238900,187141.66,0.03544993]],
    //         ["Oct-16", ["USJ46186AT93","MIZUHO BANK LTD","CORPS","25.09.2017",21000000,100,21000000,-0.00044,-9240,320209.635,0.015248078]],
    //         ["Oct-16", ["US05574LTV08","BNP PARIBAS","CORPS","12.12.2016",8040000,100.234,8066879.06,-0.001626195,-13105.2,60423.53,0.007490323]],
    //         ["Oct-16", ["US78010UNX18","ROYAL BANK OF CANADA","CORPS","23.01.2017",10000000,100.211,10022766.67,-0.001646526,-16500,14500,0.001446706]],
    //         ["Oct-16", ["US06366QW868","BANK OF MONTREAL","CORPS","11.01.2017",17925000,101.105,18248795.21,-0.007892785,-143041.5,92224.12,0.00505371]],
    //         ["Oct-16", ["US064159AM82","BANK OF NOVA SCOTIA","CORPS","12.01.2017",20000000,101.145,20370666.67,-0.00811706,-164200,103550,0.00508329]],
    //         ["Oct-16", ["XS1437622548","BANK OF CHINA/LUXEMBOURG","CORPS","12.07.2019",10000000,99.93,9993000,0.002501751,25000,81250,0.008130691]],
    //         ["Oct-16", ["US46623EKG34","JPMORGAN CHASE & CO","CORPS","15.08.2021",15000000,100.5,15088387.5,-0.004199005,-63300,2681.25,0.000177703]],
    //         ["Oct-16", ["US06738EAN58","BARCLAYS PLC","CORPS","12.01.2026",11200000,103.35,11667755.56,-0.00427673,-49504,6301.55,0.000540082]],
    //         ["Oct-16", ["US06738EAN58","BARCLAYS PLC","CORPS","12.01.2026",5000000,103.55,5219427.08,-0.006199903,-32100,-7794.44,-0.001493352]],
    //         ["Oct-16", ["US06738EAN58","BARCLAYS PLC","CORPS","12.01.2026",5000000,104.65,5281718.75,-0.016645963,-87100,-70086.11,-0.013269565]],
    //         ["Oct-16", ["XS1395052639","STANDARD CHARTERED PLC","CORPS","12.04.2026",2000000,104.43,2127300,-0.015761754,-32920,-67570,-0.031763268]],
    //         ["Oct-16", ["US06120TAA60","BANK OF CHINA","CORPS","13.11.2024",3000000,108.75,3321666.67,-0.014694253,-47940,-37523.34,-0.01129654]],
    //         ["Oct-16", ["US780097BD21","ROYAL BK SCOTLND GRP PLC","CORPS","12.09.2023",15986000,98.75,15829192.88,-0.008546835,-134921.84,-93624.67,-0.005914684]],
    //         ["Oct-16", ["US89114QBN79","TORONTO-DOMINION BANK","CORPS","06.09.2018",10000000,100,10014902.78,-0.00012,-1200,6050,0.0006041]],
    //         ["Oct-16", ["US172967JE29","CITIGROUP INC","CORPS","24.11.2017",30000000,100.4,30340458.33,-0.00059761,-18000,3583.34,0.000118104]],
    //         ["Oct-16", ["XS1395052639","STANDARD CHARTERED PLC","CORPS","12.04.2026",5000000,103.59,5182312.5,-0.007780674,-40300,-32987.5,-0.006365402]],
    //         ["Oct-16", ["XS1395052639","STANDARD CHARTERED PLC","CORPS","12.04.2026",5000000,103.57,5181312.5,-0.00758907,-39300,-31987.5,-0.006173629]],
    //         ["Oct-16", ["US06738EAN58","BARCLAYS PLC","CORPS","12.01.2026",6000000,103.35,6271000,-0.00427673,-26520,-17040.83,-0.002717402]],
    //         ["Oct-16", ["US46625HJG65","JPMORGAN CHASE & CO","CORPS","25.01.2018",25000000,100.49,25230000,-0.001224002,-30750,-18250,-0.000723345]],
    //         ["Oct-16", ["XS0755979753","DEVELOPMENT BK OF JAPAN","AGENCIES","13.03.2017",16000000,100.201,16112160,-0.000778435,-12480,178853.3333,0.011100519]],
    //         ["Oct-16", ["XS0735390303","TOKYO METROPOLITAN GOVT","AGENCIES","27.01.2017",8200000,100.655,8403616.25,-0.005096617,-42066,78371.5,0.009325926]],
    //         ["Oct-16", ["XS0735390303","TOKYO METROPOLITAN GOVT","AGENCIES","27.01.2017",11670000,100.555,11801020.06,-0.004107205,-48197.1,51484.1525,0.004362687]],
    //         ["Oct-16", ["FR0011616416","CAISSE DES DEPOTS ET CON","AGENCIES","07.11.2016",8000000,99.99,8012422.22,-4.0004E-05,-320,55096.66889,0.006876406]],
    //         ["Oct-16", ["XS0285449368","DEVELOPMENT BK OF JAPAN","AGENCIES","01.02.2017",5000000,103.7,5206354.17,-0.026229508,-136000,34833.33,0.006690542]],
    //         ["Oct-16", ["XS0789060653","TOKYO METROPOLITAN GOVT","AGENCIES","08.06.2017",5000000,100.72,5099923.61,-0.004328832,-21800,-7459.721111,-0.001462712]],
    //         ["Oct-16", ["XS0789060653","TOKYO METROPOLITAN GOVT","AGENCIES","08.06.2017",20000000,100.45,20359305.56,-0.001652563,-33200,10549.99556,0.00051819]],
    //         ["Oct-16", ["XS1383319461","DEXIA CREDIT LOCAL","AGENCIES","23.03.2018",20000000,100,20000000,0.0021,42000,72892.52222,0.003644626]],
    //         ["Oct-16", ["US500769FZ20","KFW","AGENCIES","15.12.2016",25000000,99.97,25047621.53,0.000240072,6000,88031.24778,0.003514555]],
    //         ["Oct-16", ["US45950KBQ22","INTL FINANCE CORP","SUPRANATIONAL","23.11.2016",50000000,101.28,50688437.5,-0.012440758,-630000,974687.5,0.019228991]],
    //         ["Oct-16", ["US298785GE09","EUROPEAN INVESTMENT BANK","SUPRANATIONAL","15.12.2016",50000000,101.065,50546562.5,-0.010181566,-514500,807375,0.015972896]],
    //         ["Oct-16", ["US4581X0CB23","INTER-AMERICAN DEVEL BK","SUPRANATIONAL","15.11.2016",5000000,100.075,5006545.14,-0.00071946,-3600,35531.94333,0.007097098]],
    //         ["Nov-16", ["US50064FAF18","REPUBLIC OF KOREA","GOVTS","07.12.2016",6000000,103.647,6254695,-0.032967669,-205020,50375.83333,0.008054083]],
    //         ["Nov-16", ["US912796KP37","TREASURY BILL","GOVTS","23.03.2017",50000000,99.75177222,49875886.11,0.000823322,41063.89,41063.89,0.000823322]],
    //         ["Nov-16", ["US50050HAE62","KOOKMIN BANK","CORPS","14.07.2017",5000000,99.88,4995805.56,0.000170204,850,189530.55,0.037937936]],
    //         ["Nov-16", ["USJ46186AT93","MIZUHO BANK LTD","CORPS","25.09.2017",21000000,100,21000000,-0.0005,-10500,332670.195,0.015841438]],
    //         ["Nov-16", ["US05574LTV08 ","BNP PARIBAS","CORPS","12.12.2016",8040000,100.234,8066879.06,-0.001945448,-15678,63621.2,0.007886718]],
    //         ["Nov-16", ["US78010UNX18","ROYAL BANK OF CANADA","CORPS","23.01.2017",10000000,100.211,10022766.67,-0.00160661,-16100,20566.66,0.002051994]],
    //         ["Nov-16", ["US06366QW868","BANK OF MONTREAL","CORPS","11.01.2017",17925000,101.105,18248795.21,-0.008703823,-157740,98687.08,0.005407868]],
    //         ["Nov-16", ["US064159AM82","BANK OF NOVA SCOTIA","CORPS","12.01.2017",20000000,101.145,20370666.67,-0.008977211,-181600,110233.33,0.005411376]],
    //         ["Nov-16", ["XS1437622548","BANK OF CHINA/LUXEMBOURG","CORPS","12.07.2019",10000000,99.93,9993000,-0.002341639,-23400,42225,0.004225458]],
    //         ["Nov-16", ["US46623EKG34","JPMORGAN CHASE & CO","CORPS","15.08.2021",15000000,100.5,15088387.5,-0.021791045,-328500,-246262.5,-0.016321327]],
    //         ["Nov-16", ["US06738EAN58","BARCLAYS PLC","CORPS","12.01.2026",11200000,103.35,11667755.56,-0.03194001,-369712,-290767.56,-0.024920608]],
    //         ["Nov-16", ["US06738EAN58","BARCLAYS PLC","CORPS","12.01.2026",5000000,103.55,5219427.08,-0.033809754,-175050,-140414.58,-0.026902297]],
    //         ["Nov-16", ["US06738EAN58","BARCLAYS PLC","CORPS","12.01.2026",5000000,104.65,5281718.75,-0.0439656,-230050,-202706.25,-0.038378842]],
    //         ["Nov-16", ["XS1395052639","STANDARD CHARTERED PLC","CORPS","12.04.2026",2000000,104.43,2127300,-0.051747582,-108080,-138680,-0.065190617]],
    //         ["Nov-16", ["US06120TAA60","BANK OF CHINA","CORPS","13.11.2024",3000000,108.75,3321666.67,-0.047926437,-156360,-213443.34,-0.064257905]],
    //         ["Nov-16", ["US89114QBN79","TORONTO-DOMINION BANK","CORPS","06.09.2018",10000000,100,10014902.78,-0.00344,-34400,-20302.78,-0.002027257]],
    //         ["Nov-16", ["US172967JE29","CITIGROUP INC","CORPS","24.11.2017",30000000,100.4,30340458.33,-0.00060757,-18300,29491.67,0.000972025]],
    //         ["Nov-16", ["XS1395052639","STANDARD CHARTERED PLC","CORPS","12.04.2026",5000000,103.59,5182312.5,-0.044058307,-228200,-210762.5,-0.040669585]],
    //         ["Nov-16", ["XS1395052639","STANDARD CHARTERED PLC","CORPS","12.04.2026",5000000,103.57,5181312.5,-0.043873709,-227200,-209762.5,-0.040484433]],
    //         ["Nov-16", ["US06738EAN58","BARCLAYS PLC","CORPS","12.01.2026",6000000,103.35,6271000,-0.03194001,-198060,-176185,-0.0280952]],
    //         ["Nov-16", ["US46625HJG65","JPMORGAN CHASE & CO","CORPS","25.01.2018",25000000,100.49,25230000,-0.002796298,-70250,-36500,-0.00144669]],
    //         ["Nov-16", ["US06738EAN58","BARCLAYS PLC","CORPS","12.01.2026",10000000,103.63,10502756.94,-0.034555631,-358100,-344731.94,-0.032822995]],
    //         ["Nov-16", ["US06738EAN58","BARCLAYS PLC","CORPS","12.01.2026",5000000,103.63,5251378.47,-0.034555631,-179050,-172365.97,-0.032822995]],
    //         ["Nov-16", ["XS1395052639","STANDARD CHARTERED PLC","CORPS","12.04.2026",10000000,103.6499992,10393125,-0.044611667,-462399.92,-450025,-0.043300259]],
    //         ["Nov-16", ["XS1395052639","STANDARD CHARTERED PLC","CORPS","12.04.2026",12400000,103.8,12907470,-0.045992293,-591976,-578026,-0.044782285]],
    //         ["Nov-16", ["XS0755979753","DEVELOPMENT BK OF JAPAN","AGENCIES","13.03.2017",16000000,100.201,16112160,-0.001217553,-19520,183813.3333,0.011408361]],
    //         ["Nov-16", ["XS0735390303","TOKYO METROPOLITAN GOVT","AGENCIES","27.01.2017",8200000,100.655,8403616.25,-0.005553624,-45838,82287,0.009791856]],
    //         ["Nov-16", ["XS0735390303","TOKYO METROPOLITAN GOVT","AGENCIES","27.01.2017",11670000,100.555,11801020.06,-0.004564666,-53565.3,57056.5775,0.004834885]],
    //         ["Nov-16", ["XS0285449368","DEVELOPMENT BK OF JAPAN","AGENCIES","01.02.2017",5000000,103.7,5206354.17,-0.028486017,-147700,35234.02444,0.006767504]],
    //         ["Nov-16", ["XS0789060653","TOKYO METROPOLITAN GOVT","AGENCIES","08.06.2017",5000000,100.72,5099923.61,-0.005371326,-27050,-8334.721111,-0.001634284]],
    //         ["Nov-16", ["XS0789060653","TOKYO METROPOLITAN GOVT","AGENCIES","08.06.2017",20000000,100.45,20359305.56,-0.00269786,-54200,7049.995556,0.000346279]],
    //         ["Nov-16", ["XS1383319461","DEXIA CREDIT LOCAL","AGENCIES","23.03.2018",20000000,100,20000000,0.00197,39400,84925.82222,0.004246291]],
    //         ["Nov-16", ["US500769FZ20","KFW","AGENCIES","15.12.2016",25000000,99.97,25047621.53,0.000270081,6750,96159.72,0.003839076]],
    //         ["Nov-16", ["US45950KBQ22","INTL FINANCE CORP","SUPRANATIONAL","23.11.2016",50000000,101.28,50688437.5,-0.012954186,-656000,975250,0.019240088]],
    //         ["Nov-16", ["US298785GE09","EUROPEAN INVESTMENT BANK","SUPRANATIONAL","15.12.2016",50000000,101.065,50546562.5,-0.010478405,-529500,820500,0.016232558]],
    //     ];
    //     //if(date == null){
    //     //    return positions;
    //     //}
    //
    //     var monthPositions = [];
    //     for(var i = 0; i < positions.length; i++){
    //         if(positions[i][0] === date){
    //             monthPositions.push(positions[i][1]);
    //         }
    //     }
    //     //var month = date.substring(3,5);
    //     //for(var i = 0; i < positions.length; i++){
    //     //    if(typeof positions[i][0] === 'string' && (positions[i][0] + "").substring(3,5) === month){
    //     //        monthPositions.push(positions[i][1]);
    //     //    }
    //     //}
    //     return monthPositions;
    // }

    ///////////////////////////////////////////////////////////

    postAction(successMessage, errorMessage) {
        this.successMessage = successMessage;
        this.errorMessage = errorMessage;

        // TODO: non jQuery
        $('html, body').animate({scrollTop: 0}, 'fast');
    }

    public getDataAndDraw(){
        this.busy = this.monitoringLiquidPortfolioService.get()
            .subscribe(
                (response) => {
                    this.liquidPortfolioList = response.liquidPortfolioDtoList;
                    // console.log(this.liquidPortfolioList);

                    console.log(response.message.nameEn);

                    if(this.liquidPortfolioList.length > 0) {
                        this.tableDate = this.getAllDates()[0];

                        this.drawTablesAndCharts(this.tableDate);
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
        for(var i = this.liquidPortfolioList.length - 1; i >= 0; i--){
            dates.push(this.liquidPortfolioList[i].date);
        }
        return dates;
    }

    private getDataByDate(tableDate){
        for(var i = 0; i < this.liquidPortfolioList.length; i++){
            if(this.liquidPortfolioList[i].date === tableDate){
                return this.liquidPortfolioList[i];
            }
        }
        return null;
    }

    fileChange(files: any){
        this.myFiles = files;
        console.log(this.myFiles);
    }

    private onSubmitLiquidPortfolio() {
        this.busy = this.monitoringLiquidPortfolioService.postFiles(this.myFiles)
            .subscribe(
                (response) => {
                    this.liquidPortfolioList = response.liquidPortfolioDtoList;
                    // console.log(this.liquidPortfolioList);

                    console.log(response.message.nameEn);

                    if(this.liquidPortfolioList.length > 0) {
                        this.tableDate = this.getAllDates()[0];

                        this.drawTablesAndCharts(this.tableDate);

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
        return this.moduleAccessChecker.checkAccessMonitoringEditor();
    }
}