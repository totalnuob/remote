import {Component, AfterViewInit, ViewChild} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {CommonComponent} from "../common/common.component";
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
    }

    drawContributionToReturn(){
        var data = google.visualization.arrayToDataTable([
            ['Element', 'Contribution to Return'],
            ['Event Driven', 0.54],            // RGB value
            ['Credit', 0.21],            // English color name
            ['Macro', 0.19],
            ['Multi-Strategy', 0.10], // CSS-style declaration
            ['Commodities', -0.05],
            ['Relative Value', -0.48],
            ['Equities', -1.02]
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
            ['Element', 'Contribution to Return'],
            ['Equities', 30.80],            // RGB value
            ['Credit', 29.28],            // English color name
            ['Macro', 13.23],
            ['Relative Value', 110.2], // CSS-style declaration
            ['Event Driven', 7.75],
            ['Multi-Strategy', 4.27],
            ['Commodities', 2.30],
            ['Cash', 1.35]
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
            ["2016", -2.42,-1.56,0.14,0.76,0.83,-0.32,0.40,0.84,0.12,null,null,null,-1.19]
        ];
    }

    drawPerformanceMonthly(){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "Date");
        data.addColumn("number", "Singularity");
        data.addColumn("number", "HFRIFOF");
        data.addRows(this.getPerformanceMonthlyRowData());

        var options = {
            title: 'Monthly historical performance vs. benchmark',
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {},
            colors:['green', '#a4dfb2'],
            chartArea: {width: '65%'},
            legend: { position: 'right' },
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
            ["CVI Intl Credit Ltd", "Credit", 0.60],
            ["Canyon Opp Cred GRF Ltd", "Credit", 0.44],
            ["MTP Energy Corp and Ltd", "Event Driven", 0.40],
            ["Whitebox Asymm Opp Ltd", "Relative Value", 0.21],
            ["Element Capital Ltd", "Macro", 0.21]
        ];
    }

    getPortfolioATop5NegativeFunds(){
        return [
            ["Prosiris Gbl Opp Fund Ltd", "Credit", -1.16],
            ["GS Gamma Investments Ltd", "Relative Value", -0.35],
            ["Ionic Vol Arb Fund II Ltd", "Relative Value", -0.28],
            ["Passport Global LS Ltd", "Equities", -0.23],
            ["Hitchwood Ltd", "Equities", -0.20]
        ];
    }

    getPortfolioATop10AllocationFunds() {
        return [
            ["CVI Intl Credit Ltd", "Credit", 8.23],
            ["Chenavari Struct Cred Ltd", "Credit", 7.28],
            ["Canyon Opp Cred GRF Ltd", "Credit", 5.87],
            ["York Euro Opp Unit Trust", "Event Driven", 5.25],
            ["Whitebox Asymm Opp Ltd", "Relative Value", 5.01],
            ["Graticule Asia Macro Ltd", "Macro", 4.40],
            ["Anchorage Cap Ltd", "Credit", 4.31],
            ["Trian Partners Ltd", "Equities", 4.30],
            ["Myriad Opportunities Ltd", "Multi-Strategy", 4.26],
            ["Atlas Enhanced Fund Ltd", "Equities", 3.83]
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

    private getPortfolioAReturnsRowData(){
        return [
            ["Singularity", -2.42, -1.56, 0.14, 0.76, 0.83, -0.32, 0.40, 0.84, 0.12, null, null, null, -1.19],
            ["HFRIFOF", -2.66, -1.20, 0.73, 0.52, 0.50, -0.46, 1.54, 0.49, 0.56,null, null, null, -0.11]
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
        ["Aug-15","QTD",-0.0159,-0.0161],
        ["Aug-15","YTD",-0.0159,0.0251],
        ["Aug-15","CUMULATIVE",-0.0159,-0.0200],
        ["Sep-15","MTD",-0.0217,-0.0183],
        ["Sep-15","QTD",-0.0372,-0.0341],
        ["Sep-15","YTD",-0.0372,0.0125],
        ["Sep-15","CUMULATIVE",-0.0372,-0.0378],
        ["Oct-15","MTD",0.0012,0.0085],
        ["Oct-15","QTD",0.0012,0.0085],
        ["Oct-15","YTD",-0.0361,0.0150],
        ["Oct-15","CUMULATIVE",-0.0361,-0.0296],
        ["Nov-15","MTD",0.0019,0.0030],
        ["Nov-15","QTD",0.0031,0.0116],
        ["Nov-15","YTD",-0.0342,0.0180],
        ["Nov-15","CUMULATIVE",-0.0342,-0.0267],
        ["Dec-15","MTD",-0.0056,-0.0042],
        ["Dec-15","QTD",-0.0025,0.0073],
        ["Dec-15","YTD",-0.0396,0.0137],
        ["Dec-15","CUMULATIVE",-0.0396,-0.0308],
        ["Jan-16","MTD",-0.0242,-0.0253],
        ["Jan-16","QTD",-0.0242,-0.0253],
        ["Jan-16","YTD",-0.0242,-0.0253],
        ["Jan-16","CUMULATIVE",-0.0629,-0.0550],
        ["Feb-16","MTD",-0.0156,-0.0116],
        ["Feb-16","QTD",-0.0395,-0.0366],
        ["Feb-16","YTD",-0.0395,-0.0366],
        ["Feb-16","CUMULATIVE",-0.0775,-0.0659],
        ["Mar-16","MTD",0.0012,0.0060],
        ["Mar-16","QTD",-0.0383,-0.0308],
        ["Mar-16","YTD",-0.0383,-0.0308],
        ["Mar-16","CUMULATIVE",-0.0764,-0.0604],
        ["Apr-16","MTD",0.0074,0.0047],
        ["Apr-16","QTD",0.0074,0.0047],
        ["Apr-16","YTD",-0.0312,-0.0266],
        ["Apr-16","CUMULATIVE",-0.0696,-0.0566],
        ["May-16","MTD",0.0084,0.0061],
        ["May-16","QTD",0.0160,0.0109],
        ["May-16","YTD",-0.0229,-0.0207],
        ["May-16","CUMULATIVE",-0.0616,-0.0509],
        ["Jun-16","MTD",-0.0031,-0.0043],
        ["Jun-16","QTD",0.0129,0.0061],
        ["Jun-16","YTD",-0.0259,-0.0256],
        ["Jun-16","CUMULATIVE",-0.0645,-0.0556],
        ["Jul-16","MTD",0.0040,0.0146],
        ["Jul-16","QTD",0.0040,0.0146],
        ["Jul-16","YTD",-0.0220,-0.0121],
        ["Jul-16","CUMULATIVE",-0.0608,-0.0425],
        ["Aug-16","MTD",0.0084,0.0044],
        ["Aug-16","QTD",0.0124,0.0196],
        ["Aug-16","YTD",-0.0138,-0.0066],
        ["Aug-16","CUMULATIVE",-0.0529,-0.0366],
        ["Sep-16","MTD",0.0012,0.0056],
        ["Sep-16","QTD",0.0136,0.0253],
        ["Sep-16","YTD",-0.0126,-0.0011],
        ["Sep-16","CUMULATIVE",-0.0517,-0.0319]
    ];

}