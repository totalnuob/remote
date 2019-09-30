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
import {MonitoringHFDataApprovedFundInfo} from "./model/monitoring-hf-data-approved-fund-info";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";

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

    private moduleAccessChecker: ModuleAccessCheckerService;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private monitoringHFService: MonitoringHedgeFundService
    ) {
        super();
        this.moduleAccessChecker = new ModuleAccessCheckerService;

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

                        this.regroupApprovedFunds();
                        this.drawGraph();
                    }

                },
                (error) => {
                    this.errorMessage = "Error loading monitoring data";
                    this.successMessage = null;
                }
            );
    }

    canEdit(){
        return this.moduleAccessChecker.checkAccessHedgeFundsEditor();
    }

    regroupApprovedFunds(){
        if(this.selectedData && this.selectedData.monitoringData && this.selectedData.monitoringData.approvedFunds){
            // SORT ARRAY
            this.selectedData.monitoringData.approvedFunds.sort((a, b) => (a.strategy > b.strategy) ? 1 : -1);

            var approvedFunds = [];
            var currentStrategy = null;
            for(var j = 0; j < this.selectedData.monitoringData.approvedFunds.length; j++){
            var fund = this.selectedData.monitoringData.approvedFunds[j];
            if(currentStrategy == null){
                currentStrategy = fund.strategy;
                // header row
                var headerRow = new MonitoringHFDataApprovedFundInfo();
                headerRow.headerRow = true;
                headerRow.fundName = fund.strategy;
                approvedFunds.push(headerRow);

            }else if(currentStrategy != fund.strategy){
                // header row
                var headerRow = new MonitoringHFDataApprovedFundInfo();
                headerRow.headerRow = true;
                headerRow.fundName = fund.strategy;

                approvedFunds.push(headerRow);
                currentStrategy = fund.strategy;
            }

            approvedFunds.push(fund);
            }
            this.selectedData.monitoringData.approvedFunds = approvedFunds;
        }
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
            var empty = true;
            //return;
        }


        var dataArray = [['', '']];
        for(var i = 0; !empty && i < this.selectedData.monitoringData.overall.contributionToReturn.length; i++ ){
            var element = this.selectedData.monitoringData.overall.contributionToReturn[i];
            dataArray.push([element.name, element.value]);
        }

        //console.log(dataArray);
        if(google && google.visualization) {
            var data = google.visualization.arrayToDataTable(dataArray);

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
                chartArea: {left: 100},
                bar: {groupWidth: "80%"},
                colors: ['#307240'],
                legend: {position: "none"},
            };
            var chart = new google.visualization.BarChart(document.getElementById("contributionToReturn"));
            chart.draw(data, options);
        }

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

        var empty = false;
        if(this.selectedData == null || this.selectedData.monitoringData == null || this.selectedData.monitoringData.overall == null ||
            this.selectedData.monitoringData.overall.allocationByStrategy == null){
            empty = true;
            //return;
        }
        var dataArray = [['', '']];
        for(var i = 0; !empty && i < this.selectedData.monitoringData.overall.allocationByStrategy.length; i++ ){
            var element = this.selectedData.monitoringData.overall.allocationByStrategy[i];
            dataArray.push([element.name, element.value]);
        }

        if(!google.visualization){
            return;
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

        var empty = false;
        if(this.selectedData == null || this.selectedData.monitoringData == null || this.selectedData.monitoringData.classA == null ||
            this.selectedData.monitoringData.classA.allocationByStrategy == null){
            empty = true;
            //return;
        }
        var dataArray2 = [['', '']];
        for(var i = 0; !empty && i < this.selectedData.monitoringData.classA.allocationByStrategy.length; i++ ){
            var element = this.selectedData.monitoringData.classA.allocationByStrategy[i];
            dataArray2.push([element.name, element.value]);
        }
        if(!google.visualization){
            return;
        }
        var data2 = google.visualization.arrayToDataTable(dataArray2);
        var chartPortfolioA = new google.visualization.BarChart(document.getElementById("allocationByStrategyPortfolioA"));
        chartPortfolioA.draw(data2, options);

    }

    drawReturnsTableOverall(){
        if(!google.visualization){
            return;
        }
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

        var empty = false;
        var rowData = this.getReturnsRowData();
        if(rowData == null){
            empty = true;
            //return;
        }else{
            for(var i = 0; i < rowData.length; i++){
                for(var j = 1; j < rowData[i].length; j++){
                    if(rowData[i][j]) {
                        rowData[i][j] = Number((parseFloat(rowData[i][j])*100).toFixed(2));
                    }else{
                    }

                    if(j == rowData[i].length - 1) { // YTD
                        var year = Number(rowData[i][0]);

                        if (this.monitoringDataAll && this.monitoringDataAll.returnsYTDConsolidated) {
                            for (var k = 0; k < this.monitoringDataAll.returnsYTDConsolidated.length; k++) {
                                if (this.monitoringDataAll.returnsYTDConsolidated[k] != null && this.monitoringDataAll.returnsYTDConsolidated[k].date) {
                                    var yearYTD = Number(this.monitoringDataAll.returnsYTDConsolidated[k].date.split("-")[2]);
                                    var monthYTD = Number(this.monitoringDataAll.returnsYTDConsolidated[k].date.split("-")[1]);
                                    var dayYTD = Number(this.monitoringDataAll.returnsYTDConsolidated[k].date.split("-")[0]);
                                    if (yearYTD == year && monthYTD == 12 && dayYTD == 31) {
                                        rowData[i][j] = Number(parseFloat(this.monitoringDataAll.returnsYTDConsolidated[k].value*100).toFixed(2));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if(this.selectedData == null){
            empty = true;
            //return;
        }else{
            // TODO: limit by selected date
            var month = Number(this.selectedData.date.split("-")[1]);
            var year = Number(this.selectedData.date.split("-")[2]);
            for(var i = 0; i < rowData.length; i++){
                for(var j = 1; j < rowData[i].length; j++){
                    if(Number(rowData[i][0]) > year || (Number(rowData[i][0]) == year && j > month)){
                        rowData[i][j] = null;
                    }
                }
            }
        }
        if(!empty){
            data.addRows(rowData);

            // set cell format
            this.formatCells(data, rowData, 0, 13);
        }
        //this.setReturnsRowData(data);
        //console.log(rowData);

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {
                headerCell: 'tableCellCenter',
                tableCell: 'tableCellCenter',
            }
        };

        //var colorFormatter = this.getReturnsFormatter(rowData);
        //for(var i = 1; i <= 12; i++){
        //    colorFormatter.format(data, i);
        //}

        var chart = this.createTableChart(document.getElementById('returns'));
        chart.draw(data, options);
    }

    private formatCells(data, rowData, colFrom, colTo) {
        for(var i = 0; i < rowData.length; i++){
            var minMax = this.getMinMax(rowData[i]);
            this.setColor(-1,1, i, data, colFrom, colTo);
        }
        //
        //// row 0
        //var minMax = this.getMinMax(rowData[0]);
        //this.setColor(-1,1, 0, data, colFrom, colTo);
        //
        //// row 1
        //minMax = this.getMinMax(rowData[1]);
        //this.setColor(-1,1, 1, data, colFrom, colTo);
        //
        //// row 2
        //minMax = this.getMinMax(rowData[2]);
        //this.setColor(-1,1, 2, data, colFrom, colTo);
        //// row 3
        //minMax = this.getMinMax(rowData[3]);
        //this.setColor(-1,1, 3, data, colFrom, colTo);
        //// row 4
        //minMax = this.getMinMax(rowData[4]);
        //this.setColor(-1,1, 4, data, colFrom, colTo);

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
            //if (value == null) {
            //    continue;
            //}
            var bgColor = '#f5f2f2';
            var textColor = '#5a5656';
            var fontWeight = '';



            //if (value >= Number(min + diff) && value < Number(min + (2*diff))) {
            //    bgColor = '#ec816a';
            //} else if (value >= Number(min + (2*diff)) && value < Number(min + (3*diff))) {
            //    bgColor = '#f4cd87';
            //} else if (value >= Number(min + (3*diff)) && value < Number(min + (4*diff))) {
            //    bgColor = 'orange';
            //} else if (value >= Number(min + (4*diff)) && value < Number(min + (5*diff))) {
            //    bgColor = '#b1e05b';
            //} else if (value >= Number(min + (5*diff))) {
            //    bgColor = 'green';
            //}

            if (i >= 1 && i <= 12 && value != null){
                if (value >= 1) {
                    //bgColor = "#18c451"; //green
                    textColor = "#09a709";
                    fontWeight = "bold";
                } else if (value <= -1) {
                    //bgColor = "#f54568"; //red
                    textColor = "#de0404";
                    fontWeight = "bold";
                }
            }

            data.setProperty(rowIndex, i, 'style', 'text-align: center; background-color:' + bgColor + '; color: ' + textColor + "; font-weight: " + fontWeight);
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
        for(var i = 0; returns != null && i <returns.length; i++){
            var element = returns[i];
            //console.log(element);
            var year = Number(element.date.split('-')[2]);
            var month = Number(element.date.split('-')[1]);
            var day = Number(element.date.split('-')[0]);
            if(currentYear == 0){
                currentYear = year;
                currentYearReturns.push(currentYear + '');
            }

            if(currentYear == year && currentMonth > month){
                return [];
            }
            if(currentYear > year){
                return [];
            }

            if(currentYear != year){
                // different year

                // YTD
                currentYearReturns.push(0);

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
            //YTD
            currentYearReturns.push(0);
            returnsArray.push(currentYearReturns);
        }
        return returnsArray;
    }

    private getReturnsRowData(){
        if(this.monitoringDataAll == null || this.monitoringDataAll.returnsConsolidated == null){
            return null;
        }
        return this.getReturnsByYear(this.monitoringDataAll.returnsConsolidated);
    }

    drawPerformanceMonthlyOverall(){
        if(!google.visualization){
            return;
        }
        var data = new google.visualization.DataTable();
        var formatter = new google.visualization.NumberFormat({
            pattern: '#.##%'
        });
        data.addColumn("string", "Date");
        data.addColumn("number", "Singularity");
        data.addColumn("number", "HFRIFOF");
        data.addColumn({type: 'number', role: 'interval'});
        data.addColumn({type: 'number', role: 'interval'});
        var dataRows = this.getPerformanceMonthlyRowData()
        //console.log(dataRows);

        var empty = false;
        if(dataRows == null){
            empty = true;
            //return;
        }else{
            data.addRows(dataRows);

            formatter.format(data,1);
            formatter.format(data,2);
        }

        var options = {
            title: 'Monthly historical performance vs. benchmark',
            showRowNumber: false,
            width: '100%',
            height: '450',
            animation: {
                duration: 500,
                easing: 'out',
                startup: true,
            },
            'allowHtml': true,
            cssClassNames: {},
            colors:['green', '#a4dfb2'],
            chartArea: {
                top: 55,
                height: '60%',
                width: '80%'
            },
            legend: {position: 'top'},
            vAxis: {
                format: 'percent',
            },
            hAxis: {
                slantedText: false,
                textStyle: {
                    color: 'black',
                    fontSize: 9
                },
                showTextEvery: 1,
                minTextSpacing: 1
            },
            annotations: {
                style: 'interval'
            }
        };

        var chart = this.createLineChart(document.getElementById('performanceMonthly'));
        chart.draw(data, options);
    }

    private getPerformanceMonthlyRowData(){
        if(this.monitoringDataAll == null || this.monitoringDataAll.cumulativeReturnsConsolidated == null || this.monitoringDataAll.cumulativeReturnsHFRI == null){
            return null;
        }

        var returns = this.getReturnsByYear(this.monitoringDataAll.cumulativeReturnsConsolidated);
        var returnsHFRI = this.getReturnsByYear(this.monitoringDataAll.cumulativeReturnsHFRI);
        //console.log(returns);
        //console.log(returnsHFRI);

        if(returns == null || returnsHFRI == null || returns.length != returnsHFRI.length || returns.flat(2).length != returnsHFRI.flat(2).length){
            console.log("Returns and HFRI size different: " + (returns == null ? 0 : returns.flat(2).length) + " - " +
                (returnsHFRI == null ? 0 : returnsHFRI.flat(2).length));
            return null;
        }
        //check dates match
        //for(var i = 0; i < returns.length; i++){
        //    //console.log(returns[i]);
        //    for(var j = 0; j < returns[i].length; j++) {
        //        // must be in sorted order
        //        if ((returns[i][j] != null && returnsHFRI[i][j] == null) || (returns[i][j] == null && returnsHFRI[i][j] != null)) {
        //            console.log("Returns and HFRI differ: " + returns[i][j] + " - " + returnsHFRI[i][j]);
        //            return null;
        //        }
        //    }
        //}

        // limit by selected data date
        if(this.selectedData.date != null){
            var selectedDate = new Date(Number(this.selectedData.date.split("-")[2]),
                Number(this.selectedData.date.split("-")[1]), Number(this.selectedData.date.split("-")[0]));

            for(var i = 0; i < returns.length; i++){
                var returnsDate = new Date(Number(returns[i][0].split("-")[2]),
                    Number(returns[i][0].split("-")[1]), Number(returns[i][0].split("-")[0]));
                for(var j =0; j < returns[i].length; j++) {
                    if (returnsDate > selectedDate) {
                        returns[i][j] = null;
                    }
                }
            }

            for(var i = 0; i < returnsHFRI.length; i++){
                var returnsDate = new Date(Number(returnsHFRI[i][0].split("-")[2]),
                    Number(returnsHFRI[i][0].split("-")[1]), Number(returnsHFRI[i][0].split("-")[0]));
                for(var j =0; j < returnsHFRI[i].length; j++) {
                    if (returnsDate > selectedDate) {
                        returnsHFRI[i][j] = null;
                    }
                }
            }
        }

        //console.log("AFTER LIMIT BY DATE");
        //console.log(returns);
        //console.log(returnsHFRI);

        var performance = [];
        for(var i = 0; i < returns.length; i++){
            for(var j = 1; j <= 12; j++){
                //if((returns[i][j] == null && returnsHFRI[i][j] != null) || (returnsHFRI[i][j] == null && returns[i][j] != null)){
                //    return null;
                //}
                if(returns[i][j] == null && returnsHFRI[i][j] == null){
                    continue;
                }
                var year = returns[i][0];
                var month = j < 10 ? '0' + j : j + '';
                if(returns[i][j] != null && returnsHFRI[i][j] != null) {
                    var date = j == 1 || performance.length == 0 ? (month + '-' + year) : null;
                    performance.push([date, returns[i][j], returnsHFRI[i][j], j == 1 ? -0.1 : null, j == 1 ? 0.1 : null]);
                }
            }
        }
        performance[performance.length - 1][0] = (month + '-' + year);
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
        //console.log(yearNum, portfolioName);
        if(this.monitoringDataAll == null){
            return null;
        }
        var selectedReturns = null;
        if(portfolioName === 'PortfolioA'){
            if(this.monitoringDataAll == null || this.monitoringDataAll.returnsClassA == null){
                return;
            }
            selectedReturns = this.monitoringDataAll.returnsClassA;
        }else if(portfolioName === 'PortfolioB'){
            if(this.monitoringDataAll == null || this.monitoringDataAll.returnsClassB == null){
                return;
            }
            selectedReturns = this.monitoringDataAll.returnsClassB;
        }else{
            return;
        }

        var returns = this.getReturnsByYear(selectedReturns);
        var returnsHFRI = this.getReturnsByYear(this.monitoringDataAll.returnsHFRI);

        if(yearNum > returns.length){
            return;
        }

        var returnsHFRIMathchingYear = null;
        for (var i = 0; returnsHFRI != null && i < returnsHFRI.length; i++){
            if(returns[yearNum - 1][0] == returnsHFRI[i][0]){
                returnsHFRIMathchingYear = returnsHFRI[i];
            }
        }

        if(returnsHFRIMathchingYear == null){
            // TODO: create empty array of the same size
            returnsHFRIMathchingYear = [returns[yearNum - 1][0], null, null, null, null, null, null, null, null, null, null, null, null, null];
        }

        var rowData = [returns[yearNum - 1], returnsHFRIMathchingYear];
        var headerYear = returns[yearNum - 1][0];
        //console.log(year);
        //console.log(rowData);

        if(rowData == null){
            return;
        }else{
            for(var i = 0; i < rowData.length; i++){
                for(var j = 1; rowData[i] != null && j < rowData[i].length; j++){
                    if(rowData[i][j]) {
                        rowData[i][j] = Number((parseFloat(rowData[i][j])*100).toFixed(2));
                    }

                    if(i == 0 && j == rowData[i].length - 1) { // YTD
                        var year = Number(rowData[i][0]);

                        if(portfolioName === "PortfolioA"){
                            var returnsYTD = this.monitoringDataAll.returnsYTDClassA;
                        }else if(portfolioName === "PortfolioB"){
                            var returnsYTD = this.monitoringDataAll.returnsYTDClassB;
                       }
                        if (returnsYTD) {
                            for (var k = 0; k < returnsYTD.length; k++) {
                                if (returnsYTD[k].date) {
                                    var yearYTD = Number(returnsYTD[k].date.split("-")[2]);
                                    var monthYTD = Number(returnsYTD[k].date.split("-")[1]);
                                    var dayYTD = Number(returnsYTD[k].date.split("-")[0]);
                                    if (yearYTD == year && monthYTD == 12 && dayYTD == 31) {
                                        rowData[i][j] = Number(parseFloat(returnsYTD[k].value*100).toFixed(2));
                                        break;
                                    }
                                }
                            }
                        }
                    }else if(i == 1 && j == rowData[i].length - 1) { // YTD
                        var year = Number(rowData[i][0]);

                        var returnsYTD = this.monitoringDataAll.returnsYTDHFRI;
                        if (returnsYTD) {
                            for (var k = 0; k < returnsYTD.length; k++) {
                                if (returnsYTD[k].date) {
                                    var yearYTD = Number(returnsYTD[k].date.split("-")[2]);
                                    var monthYTD = Number(returnsYTD[k].date.split("-")[1]);
                                    var dayYTD = Number(returnsYTD[k].date.split("-")[0]);
                                    if (yearYTD == year && monthYTD == 12 && dayYTD == 31) {
                                        rowData[i][j] = Number(parseFloat(returnsYTD[k].value*100).toFixed(2));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        for(var i = 1; rowData.length > 0 && i < rowData[0].length; i++){
            if(rowData[0][i] == null){
                if(rowData.length > 1 && rowData[1] != null && i < rowData[1].length) {
                    rowData[1][i] = null;
                }
            }
        }


        var month = Number(this.selectedData.date.split("-")[1]);
        var year = Number(this.selectedData.date.split("-")[2]);
        for(var i = 0; i < rowData.length; i++){
            for(var j = 1; rowData[i] != null && j < rowData[i].length; j++){
                if(Number(rowData[i][0]) > year || (Number(rowData[i][0]) == year && j > month)){
                    rowData[i][j] = null;
                }
            }
        }

        rowData[0][0] = "Singularity";
        rowData[1][0] = "HFRI";

        if(!google.visualization){
            return;
        }
        var data = new google.visualization.DataTable();
        data.addColumn("string", headerYear);
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
                headerCell: 'tableCellCenter',
                tableCell: 'tableCellCenter',
            }
        };

        //var colorFormatter = this.getReturnsFormatter(rowData);
        //for(var i = 1; i <= 12; i++){
        //    colorFormatter.format(data, i);
        //}

        // set cell format
        this.formatCells2(data, rowData, 0, 13);

        var chart = this.createTableChart(document.getElementById('comparisonReturns' + portfolioName + yearNum));
        chart.draw(data, options);
    }

    //drawPortfolioAComparisonReturns(yearNum){
    //    if(this.selectedData == null || this.selectedData.monitoringData == null || this.selectedData.monitoringData.classA == null ||
    //        this.selectedData.monitoringData.classA.returns == null || this.monitoringDataAll.returnsHFRI == null){
    //        return null;
    //    }
    //
    //    var returns = this.getReturnsByYear(this.selectedData.monitoringData.classA.returns);
    //    var returnsHFRI = this.getReturnsByYear(this.monitoringDataAll.returnsHFRI);
    //
    //    if(yearNum > returns.length || yearNum > returnsHFRI.length){
    //        return;
    //    }
    //
    //    var rowData = [returns[yearNum - 1], returnsHFRI[yearNum - 1]];
    //    var year = returns[yearNum - 1][0];
    //
    //    rowData[0][0] = "Singularity";
    //    rowData[1][0] = "HFRI";
    //
    //    if(!google.visualization){
    //        return;
    //    }
    //
    //    var data = new google.visualization.DataTable();
    //    data.addColumn("string", year);
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
    //    data.addRows(rowData);
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
    //    var chart = this.createTableChart(document.getElementById('comparisonReturns' + yearNum));
    //    chart.draw(data, options);
    //}

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

        var empty = false;
        if(this.selectedData == null || this.selectedData.monitoringData == null || this.selectedData.monitoringData.classB == null ||
            this.selectedData.monitoringData.classB.allocationByStrategy == null){
            empty = true;
            //return;
        }
        var dataArray2 = [['', '']];
        for(var i = 0; !empty && i < this.selectedData.monitoringData.classB.allocationByStrategy.length; i++ ){
            var element = this.selectedData.monitoringData.classB.allocationByStrategy[i];
            dataArray2.push([element.name, element.value]);
        }
        if(!google.visualization){
            return;
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
                this.busy = this.monitoringHFService.get({'monitoringId': this.selectedData.id}).subscribe(
                    monitoringData => {
                        console.log(monitoringData);
                        this.selectedData = monitoringData;

                        this.regroupApprovedFunds();
                        this.drawGraph();
                    });
                return;
            }
        }
    }
    addNewData(){
    }

    saveOverall(){
        //console.log(this.selectedData.monitoringData.overall);
    }

    saveClassA(){
        //console.log(this.selectedData.monitoringData.classA);
    }

    saveClassB(){
        //console.log(this.selectedData.monitoringData.classB);
    }

    saveApprovedFunds(){
        //console.log(this.selectedData.monitoringData.approvedFunds);
    }

    navigateToEditComponent(isNew){
        console.log(isNew);
        var params = JSON.stringify({});
        if(!isNew && this.selectedData && this.selectedData.id) {
            var params = JSON.stringify({"monitoringDate": this.selectedData.date, "monitoringId": this.selectedData.id});
        }
        //console.log(params);
        this.router.navigate(['/monitoring/hf/edit/', {params}]);
    }

}