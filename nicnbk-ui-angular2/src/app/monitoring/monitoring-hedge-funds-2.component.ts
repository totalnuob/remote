import {Component, AfterViewInit, ViewChild} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {CommonFormViewComponent} from "../common/common.component";
import {CommonTableau} from "./common-tableau.component";
import {GoogleChartComponent} from "../google-chart/google-chart.component";

declare var google:any;
declare var $: any;

@Component({
    selector: 'monitoring-hedge-funds-2',
    templateUrl: 'view/monitoring-hedge-funds-2.component.html',
    styleUrls: [],
    providers: [],
})
export class MonitoringHedgeFunds2Component extends GoogleChartComponent {

    constructor(
    ) {
        super();

    }

    drawGraph(){
        // OVERALL ---------------------------------
        this.drawContributionToReturn();
        this.drawAllocationByStrategy();
        this.drawTable();

        this.drawPerformanceMonthly();

        // PORTFOLIO A -----------------------------
        this.drawPortfolioAComparisonReturns();
        this.drawAllocationChart();

        // PORTFOLIO B -----------------------------
        this.drawPortfolioBComparisonReturns();
    }

    drawContributionToReturn(){
        var data = google.visualization.arrayToDataTable([
            ['Element', 'Contribution to Return'],
            ['Equities', 0.33],
            ['Credit', 0.24],            // English color name
            ['Macro', 0.16],
            ['Event Driven', 0.12],            // RGB value
            ['Multi-Strategy', 0.06], // CSS-style declaration
            ['Relative Value', 0.03],
            ['Commodities', -0.05],
        ]);

        var options = {
            title: "Contribution To Return, YTD %",
            titleTextStyle: {
                color: 'black',    // any HTML string color ('red', '#cc00cc')
                //fontName: <string>, // i.e. 'Times New Roman'
                fontSize: 14, // 12, 18 whatever you want (don't specify px)
                bold: true,    // true or false
                italic: false   // true of false
            },
            //width: 600,
            //height: 400,
            animation: {
                duration: 500,
                easing: 'out',
                startup: true,
            },
            chartArea: {left:100},
            bar: {groupWidth: "80%"},
            colors: ['#307240'],
            legend: { position: "none" },
        };
        var chart = new google.visualization.BarChart(document.getElementById("contributionToReturn"));
        chart.draw(data, options);

    }

    drawAllocationByStrategy(){
        var data = google.visualization.arrayToDataTable([
            ['Element', 'Allocation by Strategy'],
            ['Equities', 31.17],            // RGB value
            ['Credit', 28.93],            // English color name
            ['Relative Value', 11.84], // CSS-style declaration
            ['Macro', 10.51],
            ['Event Driven', 7.62],
            ['Multi-Strategy', 6.73],
            ['Commodities', 0],
            ['Cash', 3.20]
        ]);

        var options = {
            title: "Allocation by Strategy, MTD %",
            titleTextStyle: {
                color: 'black',    // any HTML string color ('red', '#cc00cc')
                //fontName: <string>, // i.e. 'Times New Roman'
                fontSize: 14, // 12, 18 whatever you want (don't specify px)
                bold: true,    // true or false
                italic: false   // true of false
            },
            animation: {
                duration: 500,
                easing: 'out',
                startup: true,
            },
            //width: 600,
            //height: 400,
            bar: {groupWidth: "80%"},
            colors: ['#307240'],
            chartArea: {left:100},
            legend: { position: "none" },
        };
        var chartOverall = new google.visualization.BarChart(document.getElementById("allocationByStrategy"));
        chartOverall.draw(data, options);

        var chartPortfolioA = new google.visualization.BarChart(document.getElementById("allocationByStrategyPortfolioA"));
        chartPortfolioA.draw(data, options);


    }

    drawTable(){
        var data = new google.visualization.DataTable();

        data.addColumn("string", "Singularity");
        data.addColumn("number", "Jan");
        data.addColumn("number", "Feb");
        data.addColumn("number", "Mar");
        data.addColumn("number", "Apr");
        data.addColumn("number", "May");
        data.addColumn("number", "Jun");
        data.addColumn("number", "Jul");
        data.addColumn("number", "Aug");
        data.addColumn("number", "Sep");
        data.addColumn("number", "Oct");
        data.addColumn("number", "Nov");
        data.addColumn("number", "Dec");
        data.addColumn("number", "YTD");

        var rowData = this.getReturnsRowData();
        data.addRows(rowData);
        //this.setReturnsRowData(data);

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {
                tableCell: '',
            }
        };

        //var colorFormatter = this.getReturnsFormatter(rowData);
        //for(var i = 1; i <= 12; i++){
        //    colorFormatter.format(data, i);
        //}

        // set cell format
        this.formatCells(data, rowData, 1, 12);

        var chart = this.createTableChart(document.getElementById('returns'));
        chart.draw(data, options);
    }

    private formatCells(data, rowData, colFrom, colTo) {

        // row 0
        var minMax = this.getMinMax(rowData[0]);
        this.setColor(minMax[0], minMax[1], 0, data, colFrom, colTo);

        // row 1
        minMax = this.getMinMax(rowData[1]);
        this.setColor(minMax[0], minMax[1], 1, data, colFrom, colTo);

    }

    private setColor(min, max, rowIndex, data, colFrom, colTo){
        var diff = Number((max - min) / 6);
        for (var i = colFrom; i <= colTo; i++) {
            var value = data.getValue(rowIndex, i) != null ?
                Number(data.getValue(rowIndex, i)) : null;
            if(value == null){
                continue;
            }
            var bgColor = '#CC312B';
            var textColor = 'black';
            if (value >= Number(min + diff) && value < Number(min + (2*diff))) {
                bgColor = '#ec816a';
            } else if (value >= Number(min + (2*diff)) && value < Number(min + (3*diff))) {
                bgColor = '#f4cd87';
            } else if (value >= Number(min + (3*diff)) && value < Number(min + (4*diff))) {
                bgColor = 'orange';
            } else if (value >= Number(min + (4*diff)) && value < Number(min + (5*diff))) {
                bgColor = '#b1e05b';
            } else if (value >= Number(min + (5*diff)) && value < Number(max + diff)) {
                bgColor = 'green';
            }
            data.setProperty(rowIndex, i, 'style', 'background-color:' + bgColor + '; color: ' + textColor);
        }
        //console.log(min, max);
        //console.log(diff);
        //console.log("-----------------");
        //console.log(Number(min - 1) + " to " + Number(min + diff));
        //console.log(Number(min + diff) + " to " + Number(min + (2*diff)));
        //console.log(Number(min + (2*diff)) + " to " + Number(min + (3*diff)));
        //console.log(Number(min + (3*diff)) + " to " + Number(min + (4*diff)));
        //console.log(Number(min + (4*diff)) + " to " + Number(min + (5*diff)));
        //console.log(Number(min + (5*diff)) + " to " + Number(max + 1));
    }

    private getReturnsRowData(){
        return [
            ["2015", null,null,null,null,null,null,null,-1.59,-2.17,0.12,0.19,-0.56,-3.96],
            ["2016", -2.42,-1.56,0.14,0.76,0.83,-0.31,0.42,0.88,0.17,0.04,1.24,0.83,0.97]
        ];
    }

    drawPerformanceMonthly(){
        var data = new google.visualization.DataTable();
        var formatter = new google.visualization.NumberFormat({
            pattern: '#.##%'
        });
        data.addColumn("string", "Date");
        data.addColumn("number", "Singularity");
        data.addColumn("number", "HFRIFOF");
        data.addRows(this.getPerformanceMonthlyRowData());

        formatter.format(data,1);
        formatter.format(data,2);

        var options = {
            title: 'Monthly historical performance vs. benchmark',
            showRowNumber: false,
            width: '100%',
            height: '100%',
            animation: {
                duration: 500,
                easing: 'out',
                startup: true,
            },
            'allowHtml': true,
            cssClassNames: {},
            colors:['green', '#a4dfb2'],
            chartArea: {width: '65%'},
            legend: {position: 'right'},
            vAxis: {
                format: 'percent',
            },
        };

        var chart = this.createLineChart(document.getElementById('performanceMonthly'));
        chart.draw(data, options);
    }

    private getPerformanceMonthlyRowData(){
        var performance = [];
        for(var i = 0; i < this.performance.length; i++){
            if(this.performance[i][1] === "MTD"){
                var item = [];
                item.push(this.performance[i][0]);
                item.push(this.performance[i][2]);
                item.push(this.performance[i][3]);
                performance.push(item);
            }
        }
        return performance;
    }

    getPortfolioATop5Funds(){
        return [
            ["Ren Inst Div Alpha LP", "Equities", 0.17],
            ["Basswood Enhanced LS Ltd", "Equities ", 0.14],
            ["Element Capital Ltd", "Macro", 0.11],
            ["Trian Partners Ltd", "Equities", 0.10],
            ["CVI Intl Credit Ltd", "Credit", 0.09]
        ];
    }

    getPortfolioATop5NegativeFunds(){
        return [
            ["Discovery Gbl Opp Ltd", "Equities", -0.09],
            ["Nipun Capital Ltd", "Equities", -0.05],
            ["GCM COM Ltd", "Commodities ", -0.05],
            ["Blue Mtn LS Equity Ltd", "Equities ", -0.03],
            ["Argentiere Enhanced Ltd", "Relative Value", -0.02],
        ];
    }

    getPortfolioATop10AllocationFunds() {
        return [
            ["CVI Intl Credit Ltd", "Credit", 8.34],
            ["Chenavari Struct Cred Ltd", "Credit", 7.29],
            ["Canyon Opp Cred GRF Ltd", "Credit", 5.88],
            ["Whitebox Asymm Opp Ltd", "Relative Value", 5.22],
            ["York Euro Opp Unit Trust", "Event Driven", 5.11],
            ["Myriad Opportunities Ltd", "Multi-Strategy", 4.98],
            ["Graticule Asia Macro Ltd", "Macro", 4.46],
            ["Trian Partners Ltd", "Equities", 4.45],
            ["Anchorage Cap Ltd", "Credit", 4.29],
            ["Atlas Enhanced Fund Ltd", "Equities", 3.72]
        ];
    }

    drawPortfolioAComparisonReturns(){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "2016");
        data.addColumn("number", "Jan");
        data.addColumn("number", "Feb");
        data.addColumn("number", "Mar");
        data.addColumn("number", "Apr");
        data.addColumn("number", "May");
        data.addColumn("number", "Jun");
        data.addColumn("number", "Jul");
        data.addColumn("number", "Aug");
        data.addColumn("number", "Sep");
        data.addColumn("number", "Oct");
        data.addColumn("number", "Nov");
        data.addColumn("number", "Dec");
        data.addColumn("number", "YTD");

        var rowData = this.getPortfolioAReturnsRowData();
        data.addRows(rowData);
        //this.setPortfolioAReturnsRowData(data);

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {
                tableCell: '',
            }
        };

        //var colorFormatter = this.getReturnsFormatter(rowData);
        //for(var i = 1; i <= 12; i++){
        //    colorFormatter.format(data, i);
        //}

        // set cell format
        this.formatCells(data, rowData, 1, 13);

        var chart = this.createTableChart(document.getElementById('comparisonReturns'));
        chart.draw(data, options);
    }

    drawPortfolioBComparisonReturns(){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "2017");
        data.addColumn("number", "Jan");
        data.addColumn("number", "Feb");
        data.addColumn("number", "Mar");
        data.addColumn("number", "Apr");
        data.addColumn("number", "May");
        data.addColumn("number", "Jun");
        data.addColumn("number", "Jul");
        data.addColumn("number", "Aug");
        data.addColumn("number", "Sep");
        data.addColumn("number", "Oct");
        data.addColumn("number", "Nov");
        data.addColumn("number", "Dec");
        data.addColumn("number", "YTD");

        var rowData = this.getPortfolioBReturnsRowData();
        data.addRows(rowData);
        //this.setPortfolioAReturnsRowData(data);

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {
                tableCell: '',
            }
        };

        //var colorFormatter = this.getReturnsFormatter(rowData);
        //for(var i = 1; i <= 12; i++){
        //    colorFormatter.format(data, i);
        //}

        // set cell format
        this.formatCells(data, rowData, 1, 13);

        var chart = this.createTableChart(document.getElementById('classBComparisonReturns'));
        chart.draw(data, options);
    }

    private getPortfolioAReturnsRowData(){
        return [
            ["Singularity", -2.42, -1.56, 0.14, 0.76, 0.83, -0.31, 0.42, 0.88, 0.17, 0.04, 1.24, 0.83, 0.97],
            ["HFRIFOF", -2.66, -1.20, 0.73, 0.52, 0.50, -0.47, 1.50, 0.44, 0.33, -0.29, 0.27, 1.08, 0.71]
        ];

    }

    private getPortfolioBReturnsRowData(){
        return [
            ["Singul class B", null, null, null, null, null, null, null, null, null, null, null, null, null],
            ["HFRIFOF", null, null, null, null, null, null, null, null, null, null, null, null, null]
        ];

    }

    private getReturnsFormatter(data){
        var minMax = this.getMinMax(data[0]);
        var min = Number(minMax[0]);
        var max = Number(minMax[1]);
        var diff = Number((max - min) / 6);
        var colorFormatter = new google.visualization.ColorFormat();
        colorFormatter.addRange(Number(min - 1),Number(min + diff), 'black', '#CC312B');
        colorFormatter.addRange( Number(min + diff), Number(min + (2*diff)), 'black', '#ec816a');
        colorFormatter.addRange(Number(min + (2*diff)), Number(min + (3*diff)), 'black', '#f4cd87');
        colorFormatter.addRange(Number(min + (3*diff)), Number(min + (4*diff)), 'black', 'orange');
        colorFormatter.addRange(Number(min + (4*diff)), Number(min + (5*diff)), 'black', '#b1e05b');
        colorFormatter.addRange(Number(min + (5*diff)), Number(max + 1), 'black', 'green');

        //console.log(minMax);
        //console.log(diff);
        //console.log("-----------------");
        //console.log(Number(min - 1) + "-" + Number(min + diff));
        //console.log(Number(min + diff) + "-" + Number(min + (2*diff)));
        //console.log(Number(min + (2*diff)) + "-" + Number(min + (3*diff)));
        //console.log(Number(min + (3*diff)) + "-" + Number(min + (4*diff)));
        //console.log(Number(min + (4*diff)) + "-" + Number(min + (5*diff)));
        //console.log(Number(min + (5*diff)) + "-" + Number(max + 1));


        return colorFormatter;
    }

    private getMinMax(returns){
        var min = returns[1];
        var max = returns[1];
        for(var i = 2; i <= 12; i++){
            if(returns[i] < min){
                min = returns[i];
            }
            if(returns[i] > max){
                max = returns[i];
            }
        }
        return [min, max];
    }

    drawAllocationChart(){
        var data = google.visualization.arrayToDataTable([
            ['Allocation', 'Percent'],
            ["MS",55],
            ["Uninvested",45]
        ]);

        var options = {
            title: 'Expected Capital allocation by the end of Dec 2016, %',
            colors: ['#307240', 'darkgrey'],
            pieSliceText: 'none'
            //legend: {position: 'labeled'}
        };

        var chart = new google.visualization.PieChart(document.getElementById('allocation'));
        chart.draw(data, options);
    }

    private performance = [
        ["Aug-15","MTD",-0.0159,-0.0200],
        ["Sep-15","MTD",-0.0217,-0.0183],
        ["Oct-15","MTD",0.0012,0.0085],
        ["Nov-15","MTD",0.0019,0.0030],
        ["Dec-15","MTD",-0.0056,-0.0042],
        ["Jan-16","MTD",-0.0242,-0.0266],
        ["Feb-16","MTD",-0.0156,-0.0120],
        ["Mar-16","MTD",0.0014,0.0073],
        ["Apr-16","MTD",0.0076,0.0052],
        ["May-16","MTD",0.0083,0.0050],
        ["Jun-16","MTD",-0.0031,-0.0047],
        ["Jul-16","MTD",0.0042,0.0150],
        ["Aug-16","MTD",0.0088,0.0044],
        ["Sep-16","MTD",0.0017,0.0033],
        ["Oct-16","MTD",0.0004,-0.0029],
        ["Nov-16","MTD",0.0124,0.0027],
        ["Dec-16","MTD",0.0083,0.0108]
    ];

}