import {Component, AfterViewInit, ViewChild} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {CommonFormViewComponent} from "../common/common.component";
import {CommonTableau} from "./common-tableau.component";
import {GoogleChartComponent} from "../google-chart/google-chart.component";
import {MonitoringHFData} from "./model/monitoring-hf-data";
import {Subscription} from 'rxjs';
import {MonitoringHedgeFundService} from "./monitoring-hf.service";
import {MonitoringHFDataHolder} from "./model/monitoring-hf-data-holder";
import {MonitoringHFListDataHolder} from "./model/monitoring-hf-list-data-holder";

declare var google:any;
declare var $: any;

@Component({
    selector: 'monitoring-hedge-funds-2',
    templateUrl: 'view/monitoring-hedge-funds-2.component.html',
    styleUrls: [],
    providers: [],
})
export class MonitoringHedgeFunds2Component extends GoogleChartComponent {
    public sub: any;
    busy: Subscription;

    activeTab = "OVERALL";

    monitoringDates = [];

    selectedDate;
    selectedData: MonitoringHFDataHolder;

    monitoringDataAll: MonitoringHFListDataHolder;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private monitoringHFService: MonitoringHedgeFundService
    ) {
        super();

        this.busy = this.monitoringHFService.getAll()
            .subscribe(
                monitoringData => {
                    console.log(monitoringData);

                    this.monitoringDataAll = monitoringData;

                    if(this.monitoringDataAll.data && this.monitoringDataAll.data.length > 0){
                        for(var i = 0; i < this.monitoringDataAll.data.length; i++) {
                            this.monitoringDates.push(this.monitoringDataAll.data[i].date);
                        }

                        this.selectedDate = this.monitoringDataAll.data[0].date;
                        this.selectedData = this.monitoringDataAll.data[0];


                        this.drawGraph();
                    }

                    //console.log(this.monitoringDataAll.data);
                    //console.log(this.monitoringDates);
                    //console.log(this.selectedDate);
                    //console.log(this.selectedData);
                },
                (error) => {
                    this.errorMessage = "Error loading monitoring data";
                    this.successMessage = null;
                }
            );
    }

    drawGraph(){
        // OVERALL ---------------------------------
        this.drawContributionToReturnOverall();
        this.drawAllocationByStrategyOverall();
        this.drawReturnsTableOverall();
        this.drawPerformanceMonthlyOverall();

        // PORTFOLIO A -----------------------------
        this.drawAllocationByStrategyClassA();
        this.drawReturnsComparisonTable(1, "PortfolioA");
        this.drawReturnsComparisonTable(2, "PortfolioA");
        this.drawReturnsComparisonTable(3, "PortfolioA");
        this.drawReturnsComparisonTable(4, "PortfolioA");
        this.drawReturnsComparisonTable(5, "PortfolioA");
        //this.drawPortfolioAComparisonReturns(1);
        //this.drawPortfolioAComparisonReturns(2);
        //this.drawPortfolioAComparisonReturns(3);
        //this.drawPortfolioAComparisonReturns(4);
        //this.drawPortfolioAComparisonReturns(5);
        //
        //// PORTFOLIO B -----------------------------
        this.drawAllocationByStrategyClassB();
        this.drawReturnsComparisonTable(1, "PortfolioB");
        this.drawReturnsComparisonTable(2, "PortfolioB");
        this.drawReturnsComparisonTable(3, "PortfolioB");
        this.drawReturnsComparisonTable(4, "PortfolioB");
        this.drawReturnsComparisonTable(5, "PortfolioB");
    }

    drawContributionToReturnOverall(){
        if(this.selectedData == null || this.selectedData.monitoringData == null || this.selectedData.monitoringData.overall == null ||
            this.selectedData.monitoringData.overall.contributionToReturn == null){
            return;
        }
        var dataArray = [['', '']];
        for(var i = 0; i < this.selectedData.monitoringData.overall.contributionToReturn.length; i++ ){
            var element = this.selectedData.monitoringData.overall.contributionToReturn[i];
            dataArray.push([element.name, element.value]);
        }

        //console.log(dataArray);
        var data = google.visualization.arrayToDataTable(dataArray);
        //var data = google.visualization.arrayToDataTable([
        //    ['Element', 'Contribution to Return'],
        //    ['Quantitative', 0.00],
        //    ['Equities', 2.86],
        //    ['Credit', 0.62],
        //    ['Multi-Strategy', 0.10],
        //    ['Macro', 0.55],
        //    ['Relative Value', 0.48],
        //]);

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
            hAxis: {
                format: '#.##',
            },
            chartArea: {left:100},
            bar: {groupWidth: "80%"},
            colors: ['#307240'],
            legend: { position: "none" },
        };
        var chart = new google.visualization.BarChart(document.getElementById("contributionToReturn"));
        chart.draw(data, options);

    }

    drawAllocationByStrategyOverall(){
        //var data = google.visualization.arrayToDataTable([
        //    ['Element', 'Allocation by Strategy'],
        //    ['Credit', 11.61],            // English color name
        //    ['Equities', 35.40],            // RGB value
        //    ['Macro', 17.343],
        //    ['Relative Value', 22.66], // CSS-style declaration
        //    ['Multi-Strategy', 6.32],
        //    ['Quantitative', 4.81],
        //    ['Cash', 1.73]
        //]);

        if(this.selectedData == null || this.selectedData.monitoringData == null || this.selectedData.monitoringData.overall == null ||
            this.selectedData.monitoringData.overall.allocationByStrategy == null){
            return;
        }
        var dataArray = [['', '']];
        for(var i = 0; i < this.selectedData.monitoringData.overall.allocationByStrategy.length; i++ ){
            var element = this.selectedData.monitoringData.overall.allocationByStrategy[i];
            dataArray.push([element.name, element.value]);
        }

        var data = google.visualization.arrayToDataTable(dataArray);
        //var data = google.visualization.arrayToDataTable([
        //    ['Element', 'Allocation by Strategy'],
        //    ['Credit', 25.23],            // English color name
        //    ['Equities', 24.67],            // RGB value
        //    ['Macro', 8.35],
        //    ['Relative Value', 13.43], // CSS-style declaration
        //    ['Multi-Strategy', 13.75],
        //    ['Quantitative', 10.47],
        //    ['Cash', 3.76]
        //]);

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
        var chartOverall = new google.visualization.BarChart(document.getElementById("allocationByStrategyOverall"));
        chartOverall.draw(data, options);

    }

    drawAllocationByStrategyClassA(){
        //var data = google.visualization.arrayToDataTable([
        //    ['Element', 'Allocation by Strategy'],
        //    ['Credit', 11.61],            // English color name
        //    ['Equities', 35.40],            // RGB value
        //    ['Macro', 17.343],
        //    ['Relative Value', 22.66], // CSS-style declaration
        //    ['Multi-Strategy', 6.32],
        //    ['Quantitative', 4.81],
        //    ['Cash', 1.73]
        //]);

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

        if(this.selectedData == null || this.selectedData.monitoringData == null || this.selectedData.monitoringData.classA == null ||
            this.selectedData.monitoringData.classA.allocationByStrategy == null){
            return;
        }
        var dataArray2 = [['', '']];
        for(var i = 0; i < this.selectedData.monitoringData.classA.allocationByStrategy.length; i++ ){
            var element = this.selectedData.monitoringData.classA.allocationByStrategy[i];
            dataArray2.push([element.name, element.value]);
        }
        var data2 = google.visualization.arrayToDataTable(dataArray2);
        var chartPortfolioA = new google.visualization.BarChart(document.getElementById("allocationByStrategyPortfolioA"));
        chartPortfolioA.draw(data2, options);

    }

    drawReturnsTableOverall(){
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
        if(rowData == null){
            return;
        }
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
        this.setColor(-1,1, 0, data, colFrom, colTo);

        // row 1
        minMax = this.getMinMax(rowData[1]);
        this.setColor(-1,1, 1, data, colFrom, colTo);

        // row 2
        minMax = this.getMinMax(rowData[2]);
        this.setColor(-1,1, 2, data, colFrom, colTo);
        // row 3
        minMax = this.getMinMax(rowData[3]);
        this.setColor(-1,1, 3, data, colFrom, colTo);
        // row 4
        minMax = this.getMinMax(rowData[4]);
        this.setColor(-1,1, 4, data, colFrom, colTo);

    }

    private formatCells2(data, rowData, colFrom, colTo) {

        // row 0
        var minMax = this.getMinMax(rowData[0]);
        this.setColor(-1,1, 0, data, colFrom, colTo);

        // row 1
        minMax = this.getMinMax(rowData[1]);
        this.setColor(-1,1, 1, data, colFrom, colTo);

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
            } else if (value >= Number(min + (5*diff))) {
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

    private getReturnsByYear(returns){
        var currentYear = 0;
        var currentMonth = 1;
        var returnsArray = [];
        var currentYearReturns = [];
        for(var i = 0; i <returns.length; i++){
            var element = returns[i];
            //console.log(element.date);
            var year = Number(element.date.split('-')[2]);
            var month = Number(element.date.split('-')[1]);
            var day = Number(element.date.split('-')[0]);
            if(currentYear == 0){
                currentYear = year;
                currentYearReturns.push(currentYear + '');
            }

            if(currentYear != year){
                // different year

                // TODO: YTD
                currentYearReturns.push(-10);

                returnsArray.push(currentYearReturns);

                currentYearReturns = [];
                currentYearReturns.push(year + '');
                currentMonth = 1;
                currentYear = year;
            }

            while(currentMonth != month && currentMonth <= 12){
                currentYearReturns.push(null);
                currentMonth++;
            }

            currentYearReturns.push(element.value);
            currentMonth++;
        }
        if(currentYearReturns != null && currentYearReturns.length > 0){
            while(currentMonth != month && currentMonth <= 12){
                currentYearReturns.push(null);
                currentMonth++;
            }
            // TODO: YTD
            currentYearReturns.push(-10);
            returnsArray.push(currentYearReturns);
        }
        //console.log(returnsArray);
        return returnsArray;
    }

    private getReturnsRowData(){
        if(this.selectedData == null ||this.selectedData.monitoringData == null || this.selectedData.monitoringData.overall == null ||
            this.selectedData.monitoringData.overall.returns == null){
            return null;
        }
        //console.log(this.selectedData.monitoringData.overall.returns);
        return this.getReturnsByYear(this.selectedData.monitoringData.overall.returns);
        //return [
        //    ["2015", null,null,null,null,null,null,null,-1.59,-2.17,0.12,0.19,-0.56,-3.96],
        //    ["2016", -2.42,-1.56,0.14,0.76,0.83,-0.31,0.42,0.88,0.17,0.04,1.27,0.88,1.02],
        //    ["2017", 0.95,0.57,0.02,0.51,0.67,-0.07,0.70,0.22,0.54,1.21,0.33,0.54,6.39],
        //    ["2018",2.25,-0.89,-0.16,0.16,1.32,0.03,0.15,0.53,0.53,-2.69,-0.95,-1.43,-1.22],
        //    ["2019", 2.05,0.97,0.53,0.88,null,null,null,null,null,null,null,null,4.50]
        //];
    }

    drawPerformanceMonthlyOverall(){
        var data = new google.visualization.DataTable();
        var formatter = new google.visualization.NumberFormat({
            pattern: '#.##%'
        });
        data.addColumn("string", "Date");
        data.addColumn("number", "Singularity");
        data.addColumn("number", "HFRIFOF");
        var dataRows = this.getPerformanceMonthlyRowData()
        if(dataRows == null){
            return;
        }
        data.addRows(dataRows);

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
            chartArea: {width: '80%'},
            legend: {position: 'bottom'},
            vAxis: {
                format: 'percent',
            },
        };

        var chart = this.createLineChart(document.getElementById('performanceMonthly'));
        chart.draw(data, options);
    }

    private getPerformanceMonthlyRowData(){
        //var performance = [];
        //for(var i = 0; i < this.performance.length; i++){
        //    if(this.performance[i][1] === "MTD"){
        //        var item = [];
        //        item.push(this.performance[i][0]);
        //        item.push(this.performance[i][2]);
        //        item.push(this.performance[i][3]);
        //        performance.push(item);
        //    }
        //}
        //return performance;

        if(this.selectedData == null || this.selectedData.monitoringData == null || this.selectedData.monitoringData.overall == null ||
            this.selectedData.monitoringData.overall.returns == null || this.selectedData.monitoringData.returnsHFRI == null){
            return null;
        }

        var returns = this.getReturnsByYear(this.selectedData.monitoringData.overall.returns);
        var returnsHFRI = this.getReturnsByYear(this.selectedData.monitoringData.returnsHFRI);
        //console.log(returns);
        //console.log(returnsHFRI);
        if(returns == null || returnsHFRI == null || returns.length != returnsHFRI.length || returns.flat(2).length != returnsHFRI.flat(2).length){
            console.log("Returns and HFRI size different: " + returns.flat(2).length + " - " + returnsHFRI.flat(2).length);
            return null;
        }

        var performance = [];
        for(var i = 0; i < returns.length; i++){
            for(var j = 1; j < 12; j++){
                if((returns[i][j] == null && returnsHFRI[i][j] != null) || (returnsHFRI[i][j] == null && returns[i][j] != null)){
                    return null;
                }
                var year = returns[i][0];
                var month = j < 10 ? '0' + j : j + '';
                if(returns[i][j] != null && returnsHFRI[i][j] != null) {
                    performance.push([(month + '-' + year), returns[i][j], returnsHFRI[i][j]]);
                }
            }
        }
        //console.log(performance);
        return performance;

    }

    //getPortfolioATop5Funds(){
    //    //return [
    //    //    ["Ren Inst Div Alpha LP", "Equities ", 0.28],
    //    //    ["Myriad Opportunities Ltd", "Multi-Strategy", 0.16],
    //    //    ["Hitchwood Ltd", "Equities", 0.11],
    //    //    ["Graticule Asia Macro Ltd", "Macro", 0.10],
    //    //    ["Trian Partners Ltd", "Equities", 0.08],
    //    //];
    //    return [
    //            ["Canyon Opp Cred GRF Ltd",	"Credit",	0.59],
    //            ["Element Capital Ltd","Macro",	0.52],
    //            ["Lagunita Ltd","Equities",0.46],
    //            ["CVI Intl Credit Ltd","Credit",0.38],
    //            ["Atlas Enhanced Fund Ltd","Equities",0.33]
    //    ];
    //}

    //getPortfolioATop5NegativeFunds(){
    //    //return [
    //    //    ["Discovery Gbl Opp Ltd", "Macro ", -0.13],
    //    //    ["Ionic Vol Arb Fund II Ltd", "Relative Value", -0.10],
    //    //    ["Lagunita Ltd", "Equities", -0.08],
    //    //    ["Argentiere Enhanced Ltd", "Relative value", -0.04],
    //    //    ["MTP Energy Corp and Ltd", "Equities", -0.04],
    //    //];
    //    return [
    //        ["Ionic Vol Arb Fund II Ltd", "Relative Value", -0.52],
    //        ["Argentiere Enhanced Ltd", "Relative value", -0.51],
    //        ["Ren Inst Div Alpha LP","Quantitative",-0.06],
    //        ["Discovery Spec Opp II Ltd (Redeemed)","Macro",-0.01],
    //        ["Myriad Opportunities Ltd","Multi-Strategy",0.01]
    //    ];
    //}

    //getPortfolioATop10AllocationFunds() {
    //    //return [
    //    //    ["CVI Intl Credit Ltd", "Credit", 9.06],
    //    //    ["Whitebox Asymm Opp Ltd", "Relative Value", 7.05],
    //    //    ["Myriad Opportunities Ltd", "Multi-Strategy", 6.99],
    //    //    ["Canyon Opp Cred GRF Ltd", "Credit", 6.23],
    //    //    ["Element Capital Ltd", "Macro", 5.58],
    //    //    ["Chenavari Struct Cred Ltd", "Credit", 5.53],
    //    //    ["Atlas Enhanced Fund Ltd", "Equities", 5.47],
    //    //    ["York Euro Opp Unit Trust", "Event Driven", 5.47],
    //    //    ["Graticule Asia Macro Ltd", "Macro", 4.90],
    //    //    ["Ren Inst Div Alpha LP", "Quantitative", 4.86]
    //    //];
    //    return [
    //        ["CVI Intl Credit Ltd", "Credit", 8.45],
    //        ["Element Capital Ltd", "Macro", 8.35],
    //        ["Whitebox Asymm Opp Ltd", "Relative Value", 7.58],
    //        ["Canyon Opp Cred GRF Ltd", "Credit", 7.14],
    //        ["Ren Inst Div Alpha LP", "Quantitative", 6.94],
    //        ["Myriad Opportunities Ltd", "Multi-Strategy", 6.48],
    //        ["Magnetar Constell Ltd", "Credit", 6.35],
    //        ["York Euro Opp Unit Trust", "Event Driven", 5.39],
    //        ["Atlas Enhanced Fund Ltd", "Equities", 4.18],
    //        ["Wexford Catalyst Off", "Multi-Strategy", 4.08],
    //    ];
    //}

    drawReturnsComparisonTable(yearNum, portfolioName){
        if(this.selectedData == null || this.selectedData.monitoringData == null || this.selectedData.monitoringData.returnsHFRI == null){
            return null;
        }
        var selectedReturns = null;
        if(portfolioName === 'PortfolioA'){
            if(this.selectedData.monitoringData.classA == null || this.selectedData.monitoringData.classA.returns == null){
                return;
            }
            selectedReturns = this.selectedData.monitoringData.classA.returns;
        }else if(portfolioName === 'PortfolioB'){
            if(this.selectedData.monitoringData.classB == null || this.selectedData.monitoringData.classB.returns == null){
                return;
            }
            selectedReturns = this.selectedData.monitoringData.classB.returns;
        }else{
            return;
        }

        var returns = this.getReturnsByYear(selectedReturns);
        var returnsHFRI = this.getReturnsByYear(this.selectedData.monitoringData.returnsHFRI);

        if(yearNum > returns.length || yearNum > returnsHFRI.length){
            return;
        }

        var rowData = [returns[yearNum - 1], returnsHFRI[yearNum - 1]];
        var year = returns[yearNum - 1][0];

        rowData[0][0] = "Singularity";
        rowData[1][0] = "HFRI";

        var data = new google.visualization.DataTable();
        data.addColumn("string", year);
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

        data.addRows(rowData);

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
        this.formatCells2(data, rowData, 1, 12);

        var chart = this.createTableChart(document.getElementById('comparisonReturns' + portfolioName + yearNum));
        chart.draw(data, options);
    }

    drawPortfolioAComparisonReturns(yearNum){
        if(this.selectedData == null || this.selectedData.monitoringData == null || this.selectedData.monitoringData.classA == null ||
            this.selectedData.monitoringData.classA.returns == null || this.monitoringDataAll.returnsHFRI == null){
            return null;
        }

        var returns = this.getReturnsByYear(this.selectedData.monitoringData.classA.returns);
        var returnsHFRI = this.getReturnsByYear(this.monitoringDataAll.returnsHFRI);

        if(yearNum > returns.length || yearNum > returnsHFRI.length){
            return;
        }

        var rowData = [returns[yearNum - 1], returnsHFRI[yearNum - 1]];
        var year = returns[yearNum - 1][0];

        rowData[0][0] = "Singularity";
        rowData[1][0] = "HFRI";

        var data = new google.visualization.DataTable();
        data.addColumn("string", year);
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

        data.addRows(rowData);

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
        this.formatCells2(data, rowData, 1, 12);

        var chart = this.createTableChart(document.getElementById('comparisonReturns' + yearNum));
        chart.draw(data, options);
    }

    //drawPortfolioAComparisonReturns2(){
    //    var data = new google.visualization.DataTable();
    //    data.addColumn("string", "2016");
    //    data.addColumn("number", "Jan");
    //    data.addColumn("number", "Feb");
    //    data.addColumn("number", "Mar");
    //    data.addColumn("number", "Apr");
    //    data.addColumn("number", "May");
    //    data.addColumn("number", "Jun");
    //    data.addColumn("number", "Jul");
    //    data.addColumn("number", "Aug");
    //    data.addColumn("number", "Sep");
    //    data.addColumn("number", "Oct");
    //    data.addColumn("number", "Nov");
    //    data.addColumn("number", "Dec");
    //    data.addColumn("number", "YTD");
    //
    //    var rowData = this.getPortfolioAReturnsRowData2016();
    //    data.addRows(rowData);
    //    //this.setPortfolioAReturnsRowData(data);
    //
    //    var options = {
    //        showRowNumber: false,
    //        width: '100%',
    //        height: '100%',
    //        'allowHtml': true,
    //        cssClassNames: {
    //            tableCell: '',
    //        }
    //    };
    //
    //    //var colorFormatter = this.getReturnsFormatter(rowData);
    //    //for(var i = 1; i <= 12; i++){
    //    //    colorFormatter.format(data, i);
    //    //}
    //
    //    // set cell format
    //    this.formatCells2(data, rowData, 1, 12);
    //
    //    var chart = this.createTableChart(document.getElementById('comparisonReturns2'));
    //    chart.draw(data, options);
    //}
    //
    //drawPortfolioAComparisonReturns3(){
    //    var data = new google.visualization.DataTable();
    //    data.addColumn("string", "2017");
    //    data.addColumn("number", "Jan");
    //    data.addColumn("number", "Feb");
    //    data.addColumn("number", "Mar");
    //    data.addColumn("number", "Apr");
    //    data.addColumn("number", "May");
    //    data.addColumn("number", "Jun");
    //    data.addColumn("number", "Jul");
    //    data.addColumn("number", "Aug");
    //    data.addColumn("number", "Sep");
    //    data.addColumn("number", "Oct");
    //    data.addColumn("number", "Nov");
    //    data.addColumn("number", "Dec");
    //    data.addColumn("number", "YTD");
    //
    //    var rowData = this.getPortfolioAReturnsRowData2017();
    //    data.addRows(rowData);
    //    //this.setPortfolioAReturnsRowData(data);
    //
    //    var options = {
    //        showRowNumber: false,
    //        width: '100%',
    //        height: '100%',
    //        'allowHtml': true,
    //        cssClassNames: {
    //            tableCell: '',
    //        }
    //    };
    //
    //    //var colorFormatter = this.getReturnsFormatter(rowData);
    //    //for(var i = 1; i <= 12; i++){
    //    //    colorFormatter.format(data, i);
    //    //}
    //
    //    // set cell format
    //    this.formatCells2(data, rowData, 1, 12);
    //
    //    var chart = this.createTableChart(document.getElementById('comparisonReturns3'));
    //    chart.draw(data, options);
    //}
    //
    //drawPortfolioAComparisonReturns4(){
    //    var data = new google.visualization.DataTable();
    //    data.addColumn("string", "2018");
    //    data.addColumn("number", "Jan");
    //    data.addColumn("number", "Feb");
    //    data.addColumn("number", "Mar");
    //    data.addColumn("number", "Apr");
    //    data.addColumn("number", "May");
    //    data.addColumn("number", "Jun");
    //    data.addColumn("number", "Jul");
    //    data.addColumn("number", "Aug");
    //    data.addColumn("number", "Sep");
    //    data.addColumn("number", "Oct");
    //    data.addColumn("number", "Nov");
    //    data.addColumn("number", "Dec");
    //    data.addColumn("number", "YTD");
    //
    //    var rowData = this.getPortfolioAReturnsRowData2018();
    //    data.addRows(rowData);
    //    //this.setPortfolioAReturnsRowData(data);
    //
    //    var options = {
    //        showRowNumber: false,
    //        width: '100%',
    //        height: '100%',
    //        'allowHtml': true,
    //        cssClassNames: {
    //            tableCell: '',
    //        }
    //    };
    //
    //    //var colorFormatter = this.getReturnsFormatter(rowData);
    //    //for(var i = 1; i <= 12; i++){
    //    //    colorFormatter.format(data, i);
    //    //}
    //
    //    // set cell format
    //    this.formatCells2(data, rowData, 1, 12);
    //
    //    var chart = this.createTableChart(document.getElementById('comparisonReturns4'));
    //    chart.draw(data, options);
    //}
    //
    //drawPortfolioAComparisonReturns5(){
    //    var data = new google.visualization.DataTable();
    //    data.addColumn("string", "2019");
    //    data.addColumn("number", "Jan");
    //    data.addColumn("number", "Feb");
    //    data.addColumn("number", "Mar");
    //    data.addColumn("number", "Apr");
    //    data.addColumn("number", "May");
    //    data.addColumn("number", "Jun");
    //    data.addColumn("number", "Jul");
    //    data.addColumn("number", "Aug");
    //    data.addColumn("number", "Sep");
    //    data.addColumn("number", "Oct");
    //    data.addColumn("number", "Nov");
    //    data.addColumn("number", "Dec");
    //    data.addColumn("number", "YTD");
    //
    //    var rowData = this.getPortfolioAReturnsRowData2019();
    //    data.addRows(rowData);
    //    //this.setPortfolioAReturnsRowData(data);
    //
    //    var options = {
    //        showRowNumber: false,
    //        width: '100%',
    //        height: '100%',
    //        'allowHtml': true,
    //        cssClassNames: {
    //            tableCell: '',
    //        }
    //    };
    //
    //    //var colorFormatter = this.getReturnsFormatter(rowData);
    //    //for(var i = 1; i <= 12; i++){
    //    //    colorFormatter.format(data, i);
    //    //}
    //
    //    // set cell format
    //    this.formatCells2(data, rowData, 1, 12);
    //
    //    var chart = this.createTableChart(document.getElementById('comparisonReturns5'));
    //    chart.draw(data, options);
    //}

    drawPortfolioBComparisonReturns2017(){
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

        var rowData = this.getPortfolioBReturnsRowData2017();
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
        this.formatCells2(data, rowData, 1, 12);

        var chart = this.createTableChart(document.getElementById('classBComparisonReturns2017'));
        chart.draw(data, options);
    }

    drawPortfolioBComparisonReturns2018(){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "2018");
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

        var rowData = this.getPortfolioBReturnsRowData2018();
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
        this.formatCells2(data, rowData, 1, 12);

        var chart = this.createTableChart(document.getElementById('classBComparisonReturns2018'));
        chart.draw(data, options);
    }

    drawPortfolioBComparisonReturns2019(){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "2019");
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

        var rowData = this.getPortfolioBReturnsRowData2019();
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
        this.formatCells2(data, rowData, 1, 12);

        var chart = this.createTableChart(document.getElementById('classBComparisonReturns2019'));
        chart.draw(data, options);
    }

    private getPortfolioAReturnsRowData2015(){
        return [
            ["Singularity",null,null,null,null,null,null,null,-1.59,-2.17,0.12,0.19,-0.56,-3.96],
            ["HFRIFOF",null,null,null,null,null,null,null,-2.00,-1.83,0.85,0.30,-0.42,-3.09]
        ];
    }


    private getPortfolioAReturnsRowData2016(){
        return [
            ["Singularity", -2.42, -1.56, 0.14, 0.76, 0.83, -0.31, 0.42, 0.88, 0.17, 0.04, 1.27, 0.88, 1.02],
            ["HFRIFOF", -2.66, -1.20, 0.73, 0.52, 0.50, -0.47, 1.50, 0.44, 0.33, -0.28, 0.25, 0.92, 0.54]
        ];
    }

    private getPortfolioAReturnsRowData2017(){
        return [
            ["Singularity A", 0.89, 0.63, -0.05, 0.54, 0.55, -0.12, 0.63, 0.14, 0.66,1.19, 0.54, 0.58, 6.36],
            ["HFRIFOF", 1.01, 0.90, 0.45, 0.51, 0.32, -0.02,1.02 ,0.83 ,0.44 ,1.15 ,-0.02 ,0.92 ,7.76]
        ];
    }

    private getPortfolioAReturnsRowData2018(){
        return [
            ["Singularity A",2.29,-1.22,-0.22,-0.10,1.50,-0.13,0.31,0.57,0.30,-2.40,-0.85,-2.38,-2.42],
            ["HFRIFOF",2.33,-1.53,-0.49,0.20,0.72,-0.46,0.21,0.23,-0.20,-2.92,-0.44,-1.66,-4.03]
        ];
    }

    private getPortfolioAReturnsRowData2019(){
        return [
            ["Singularity A", 1.46,0.52,-0.10,0.64, null, null, null, null, null, null, null, null, 2.53],
            ["HFRIFOF", 2.57, 1.07, 0.92,0.99, null, null, null, null, null, null, null, null, 5.66]
        ];
    }

    private getPortfolioBReturnsRowData2017(){
        return [
            ["Singularity B", 1.81,0.26 ,0.38,0.38,1.45,0.18,1.04,0.58,-0.05,1.27,-0.70,0.39,7.18],
            ["HFRIFOF", 1.01,0.90,0.45,0.51,0.32,-0.02,1.02,0.83,0.44,1.15,-0.02,0.92,7.76]
        ];
    }

    private getPortfolioBReturnsRowData2018(){
        return [
            ["Singularity B",2.15,0.13,0.01,0.72,0.94,0.39,-0.20,0.44,0.90,-3.05,-1.06,-0.33,0.95],
            ["HFRIFOF",2.33,-1.53,-0.49,0.20,0.72,-0.46,0.21,0.23,-0.20,-2.92,-0.44,-1.66,-4.03]
        ];
    }

    private getPortfolioBReturnsRowData2019(){
        return [
            ["Singularity B",2.73,1.39,1.08,1.08,,,,,,,,,6.42],
            ["HFRIFOF",2.57,1.07,0.92,0.99,,,,,,,,,5.66]
        ];
    }

    getPortfolioBFundAllocations(){
        return [
            ["MW Eureka Fund","Equities",21.6],
            ["HBK Multi-Strategy Fund","Relative Value",17.2],
            ["Alphadyne International Fund","Macro",16.79],
            ["Citadel", "Relative Value",13.31],
            ["BlackRock European Hedge Fund","Equities",11.48],
            ["Autonomy Global Macro","Macro",82],
            ["Southpoint","Equities",5.85],
            ["Palestra","Equities",5.58]
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

    drawAllocationByStrategyClassB(){
        //var data = google.visualization.arrayToDataTable([
        //    ['Allocation', 'Percent'],
        //    ["Equities",44.51],
        //    ["Macro",24.99],
        //    ["Relative Value",30.51]
        //]);
        //
        //var options = {
        //    title: 'Capital allocation, %',
        //    colors: ['#307240', 'darkgrey', '#a5e2b4'],
        //    pieSliceText: 'none'
        //    //legend: {position: 'labeled'}
        //};
        //
        //var chart = new google.visualization.PieChart(document.getElementById('allocation'));
        //chart.draw(data, options);

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

        if(this.selectedData == null || this.selectedData.monitoringData == null || this.selectedData.monitoringData.classB == null ||
            this.selectedData.monitoringData.classB.allocationByStrategy == null){
            return;
        }
        var dataArray2 = [['', '']];
        for(var i = 0; i < this.selectedData.monitoringData.classB.allocationByStrategy.length; i++ ){
            var element = this.selectedData.monitoringData.classB.allocationByStrategy[i];
            dataArray2.push([element.name, element.value]);
        }
        var data2 = google.visualization.arrayToDataTable(dataArray2);
        var chartPortfolioA = new google.visualization.BarChart(document.getElementById("allocationByStrategyPortfolioB"));
        chartPortfolioA.draw(data2, options);

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
        ["Oct-16","MTD",0.0004,-0.0028],
        ["Nov-16","MTD",0.0127,0.0026],
        ["Dec-16","MTD",0.0088,0.0089],
        ["Jan-17","MTD",0.0095,0.0101],
        ["Feb-17","MTD",0.0057,0.0090],
        ["Mar-17","MTD",0.0002,0.0045],
        ["Apr-17","MTD",0.0051,0.0051],
        ["May-17","MTD",0.0070,0.0032],
        ["Jun-17","MTD",-0.0007,-0.0002],
        ["Jul-17","MTD",0.0070,0.0102],
        ["Aug-17","MTD",0.0022,0.0083],
        ["Sep-17","MTD",0.0054,0.0044],
        ["Oct-17","MTD",0.0121,0.0115],
        ["Nov-17","MTD",0.0033,-0.0002],
        ["Dec-17","MTD",0.0054,0.0092],


        ["Jan-18","MTD",0.0225,0.0233],
        ["Feb-18","MTD",-0.0089,-0.0153],
        ["Mar-18","MTD",-0.0016,-0.0049],
        ["Apr-18","MTD",0.0016,0.0020],
        ["May-18","MTD",0.0132,0.0072],
        ["Jun-18","MTD",0.0003,-0.0046],
        ["Jul-18","MTD",0.0015,0.0021],
        ["Aug-18","MTD",0.0053,0.0023],
        ["Sep-18","MTD",0.0053,-0.0020],
        ["Oct-18","MTD",-0.0269,-0.0292],
        ["Nov-18","MTD",-0.0095,-0.0044],
        ["Dec-18","MTD",-0.0143,-0.0166],

        ["Jan-19","MTD",0.0205,0.0257],
        ["Feb-19","MTD",0.0097,0.0107],
        ["Mar-19","MTD",0.0053,0.0092],
        ["Apr-19","MTD",0.0088,0.0099]

    ];


    selectDate(value){
        //console.log(value);
        for(var i = 0; i < this.monitoringDataAll.data.length; i++){
            if(this.monitoringDataAll.data[i].date === value){
                this.selectedData = this.monitoringDataAll.data[i];
                this.selectedDate = this.monitoringDataAll.data[i].date;
                this.drawGraph();

                return;
            }
        }
    }

    addNewData(){
    }

    saveOverall(){
        console.log(this.selectedData.monitoringData.overall);
    }

    saveClassA(){
        console.log(this.selectedData.monitoringData.classA);
    }

    saveClassB(){
        console.log(this.selectedData.monitoringData.classB);
    }

    saveApprovedFunds(){
        console.log(this.selectedData.monitoringData.approvedFunds);
    }

    navigateToEditComponent(date){
        if(date) {
            var params = JSON.stringify({"monitoringDate": date});
        }else{
            params = JSON.stringify({});
        }
        console.log(params);
        this.router.navigate(['/monitoring/hf/edit/', {params}]);
    }

}