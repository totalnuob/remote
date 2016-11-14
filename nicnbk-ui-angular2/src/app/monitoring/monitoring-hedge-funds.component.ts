import {Component, AfterViewInit, ViewChild} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {CommonFormViewComponent} from "../common/common.component";
import {CommonTableau} from "./common-tableau.component";
import {GoogleChartComponent} from "../google-chart/google-chart.component";

declare var google:any;
declare var $: any;


@Component({
    selector: 'monitoring-hedge-funds',
    templateUrl: 'view/monitoring-hedge-funds.component.html',
    styleUrls: [],
    providers: [],
})
export class MonitoringHedgeFundsComponent extends GoogleChartComponent {
    //ngAfterViewInit():void {
    //    this.tableau_func()
    //}

    private tableDate;

    constructor(
    ) {
        super();

        //$("#tableDate").val($("#tableDate option:last").val());
        //this.tableDate = $("#tableDate option:last").val();
    }

    drawGraph(){
        var tableDate = this.getAllDates()[0];
        this.tableDate = tableDate;

        this.drawPortfolioTable(tableDate);
        this.drawPerformanceTable(tableDate);
        this.drawNAV();
        this.drawPerformanceCharts();
        this.drawHoldings(tableDate);
        this.drawAllocations();
    }

    redraw(){
        this.tableDate = $("#tableDate").val();
        this.drawPortfolioTable(this.tableDate);

        this.drawPerformanceTable(this.tableDate);

        this.drawHoldings(this.tableDate);
    }

    // PORTFOLIO ---------------------------------------------------------
    drawPortfolioTable(tableDate){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "");
        data.addColumn("number", "");
        data.addRows([
            ["Hedge Funds Portfolio", this.getPortfolioByDate(tableDate)]
        ]);

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {
                tableCell: '',
            }
        };

        var chart = this.createTableChart(document.getElementById('value_nav'));
        chart.draw(data, options);
    }

    private getPortfolioByDate(date){
        for(var i = 0; i < this.portfolio.length; i++){
            if(this.portfolio[i][0] === date){
                return this.portfolio[i][1];
            }
        }
        return "";
    }

    // PERFORMANCE --------------------------------------------------------
    drawPerformanceTable(tableDate){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "");
        data.addColumn("number", "MTD");
        data.addColumn("number", "QTD");
        data.addColumn("number", "YTD");
        data.addColumn("number", "CUMULATIVE");
        data.addRows(this.getPerformanceRowData(tableDate));

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {
                tableCell: '',
            }
        };

        var chart = this.createTableChart(document.getElementById('value_performance'));
        chart.draw(data, options);
    }

    private getPerformanceRowData(date){
        console.log(date);
        var performance = [];
        var portfolio = ["Hedge Funds Portfolio", 0, 0, 0, 0];
        var HFRIFOF = ["HFRIFOF Index", 0, 0, 0, 0];
        for(var i = 0; i < this.performance.length; i++){
            if(this.performance[i][0] === date){
                if(this.performance[i][1] === "MTD"){
                    portfolio[1] = this.performance[i][2];
                    HFRIFOF[1] = this.performance[i][3];
                }else if(this.performance[i][1] === "QTD"){
                    portfolio[2] = this.performance[i][2];
                    HFRIFOF[2] = this.performance[i][3];
                }else if(this.performance[i][1] === "YTD"){
                    portfolio[3] = this.performance[i][2];
                    HFRIFOF[3] = this.performance[i][3];
                }else if(this.performance[i][1] === "CUMULATIVE"){
                    portfolio[4] = this.performance[i][2];
                    HFRIFOF[4] = this.performance[i][3];
                }
            }
        }
        performance.push(portfolio);
        performance.push(HFRIFOF);
        return performance;
    }

    // NAV LINE CHART ------------------------------------------------------
    drawNAV(){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "Date");
        data.addColumn("number", "NAV");
        data.addRows(this.getNAVRowData());

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {}
        };

        var chart = this.createLineChart(document.getElementById('nav'));
        chart.draw(data, options);
    }

    private getNAVRowData(){
        return this.portfolio;
    }

    // PERFORMANCE LINE CHARTS ---------------------------------------------
    drawPerformanceCharts(){
        this.drawPerformanceMonthly();
        this.drawPerformanceCumulative();
    }

    drawPerformanceMonthly(){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "Date");
        data.addColumn("number", "Singularity");
        data.addColumn("number", "HFRIFOF");
        data.addRows(this.getPerformanceMonthlyRowData());

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {}
        };

        var chart = this.createLineChart(document.getElementById('performance_monthly'));
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

    drawPerformanceCumulative(){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "Date");
        data.addColumn("number", "Singularity");
        data.addColumn("number", "HFRIFOF");
        data.addRows(this.getPerformanceCumulativeRowData());

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {}
        };

        var chart = this.createLineChart(document.getElementById('performance_cumulative'));
        chart.draw(data, options);

    }

    private getPerformanceCumulativeRowData(){
        var performance = [];
        for(var i = 0; i < this.performance.length; i++){
            if(this.performance[i][1] === "CUMULATIVE"){
                var item = [];
                item.push(this.performance[i][0]);
                item.push(this.performance[i][2]);
                item.push(this.performance[i][3]);
                performance.push(item);
            }
        }
        return performance;
    }

    // HOLDINGS ---------------------------------------------------------------------

    drawHoldings(date){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "Fund");
        data.addColumn("string", "Strategy");
        data.addColumn("string", "Substrategy");
        data.addColumn("number", "Month Opening Allocation Percent");
        data.addColumn("number", "RoR Percent");
        data.addColumn("number", "CtoR Percent");
        data.addRows(this.getHoldingsRowData(date));

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {
                tableCell: 'googleChartTable',
            }
        };

        var chart = this.createTableChart(document.getElementById('holdings'));
        chart.draw(data, options);
    }

    private getHoldingsRowData(selectedDate){
        var holdingsArray = [];
        for(var i = 0; i < this.holdings.length; i++){
            var date = this.holdings[i][0];
            if(typeof date === 'string' && (date.substring(3,5) === this.getMonthNumber(selectedDate))){
                var item = [this.holdings[i][1], this.holdings[i][2], this.holdings[i][3], this.holdings[i][4], this.holdings[i][5], this.holdings[i][6]];
                holdingsArray.push(item);
            }
        }
        console.log(holdingsArray);
        return holdingsArray;
    }

    // ALLOCATIONS
    drawAllocations(){
        this.drawTargetAllocationsTable();
        this.drawActualAllocationsChart();
    }

    drawTargetAllocationsTable(){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "Strategy");
        data.addColumn("number", "MIN");
        data.addColumn("number", "MAX");
        data.addRows(this.getTargetAllocationRowTata());

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {
                tableCell: 'googleChartTable',
            }
        };

        var chart = this.createTableChart(document.getElementById('target_allocations'));
        chart.draw(data, options);

    }

    private getTargetAllocationRowTata(){
        return [
            ["Credit",0.20,0.40],
            ["Relative Value",0.10,0.25],
            ["Multi-Strategy",0.05,0.20],
            ["Event Driven",0.05,0.20],
            ["Equities",0.20,0.40],
            ["Macro",0.00,0.15],
            ["Commodities",0.00,0.10],
            ["Portfolio Hedges",0.00,0.02],
            ["Direct Opportunities",0.00,0.10]
        ];
    }

    drawActualAllocationsChart(){
        var data = google.visualization.arrayToDataTable([
            ['Task', 'Hours per Day'],
            ["Credit",0.29],
            ["Relative Value",0.11],
            ["Multi-Strategy",0.04],
            ["Event Driven",0.08],
            ["Equities",0.31],
            ["Macro",0.13],
            ["Commodities",0.02],
            ["Portfolio Hedges",0],
            ["Direct Opportunities",0]
        ]);

        var options = {
            title: 'Actual Allocation by Strategy'
        };

        var chart = new google.visualization.PieChart(document.getElementById('actual_allocations'));
        chart.draw(data, options);
    }




    private getMonthNumber(fullName){
        var monthName = fullName.split("-")[0];
        if(monthName.toUpperCase() === "JAN"){
            return "01";
        }else if(monthName.toUpperCase() === "FEB"){
            return "02";
        }else if(monthName.toUpperCase() === "MAR"){
            return "03";
        }else if(monthName.toUpperCase() === "APR"){
            return "04";
        }else if(monthName.toUpperCase() === "MAY"){
            return "05";
        }else if(monthName.toUpperCase() === "JUN"){
            return "06";
        }else if(monthName.toUpperCase() === "JUL"){
            return "07";
        }else if(monthName.toUpperCase() === "AUG"){
            return "08";
        }else if(monthName.toUpperCase() === "SEP"){
            return "09";
        }else if(monthName.toUpperCase() === "OCT"){
            return "10";
        }else if(monthName.toUpperCase() === "NOV"){
            return "11";
        }else if(monthName.toUpperCase() === "DEC"){
            return "12";
        }
        return "";
    }

    public getAllDates(){
        var dates = [];
        for(var i = this.portfolio.length - 1; i >= 0 ; i--){
            dates.push(this.portfolio[i][0]);
        }
        return dates;
    }

    private portfolio = [
        ["Jul-15",75000000],
        ["Aug-15",73806138],
        ["Sep-15",145584323],
        ["Oct-15",145761377],
        ["Nov-15",146039154],
        ["Dec-15",145224471],
        ["Jan-16",141704890],
        ["Feb-16",139490031],
        ["Mar-16",139660777],
        ["Apr-16",140717168],
        ["May-16",141900131],
        ["Jun-16",141459937],
        ["Jul-16",142025472],
        ["Aug-16",143248121],
        ["Sep-16",143491005]
    ];

    private performance = [
        ["Dec-14","MTD",0,0],
        ["Dec-14","QTD",0,0],
        ["Dec-14","YTD",0,0],
        ["Dec-14","CUMULATIVE",0,0],
        ["Jan-15","MTD",0,0.0013],
        ["Jan-15","QTD",0,0.0013],
        ["Jan-15","YTD",0,0.0013],
        ["Jan-15","CUMULATIVE",0,0],
        ["Feb-15","MTD",0,0.0169],
        ["Feb-15","QTD",0,0.0183],
        ["Feb-15","YTD",0,0.0183],
        ["Feb-15","CUMULATIVE",0,0],
        ["Mar-15","MTD",0,0.0066],
        ["Mar-15","QTD",0,0.0250],
        ["Mar-15","YTD",0,0.0250],
        ["Mar-15","CUMULATIVE",0,0],
        ["Apr-15","MTD",0,0.0025],
        ["Apr-15","QTD",0,0.0025],
        ["Apr-15","YTD",0,0.0275],
        ["Apr-15","CUMULATIVE",0,0],
        ["May-15","MTD",0,0.0080],
        ["May-15","QTD",0,0.0105],
        ["May-15","YTD",0,0.0533],
        ["May-15","CUMULATIVE",0,0],
        ["Jun-15","MTD",0,0.0060],
        ["Jun-15","QTD",0,0.0166],
        ["Jun-15","YTD",0,0.0419],
        ["Jun-15","CUMULATIVE",0,0],
        ["Jul-15","MTD",0,0.0039],
        ["Jul-15","QTD",0,0.0039],
        ["Jul-15","YTD",0,0.0460],
        ["Jul-15","CUMULATIVE",0,0],
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

    private holdings = [
        ["30.09.2016","Canyon Opp Cred GRF Ltd","Credit","Long-Biased Credit",0.0583560881498609,0.0071000002152275,0.0004143282384239],
        ["30.09.2016","CVI Intl Credit Ltd","Credit","Long-Biased Credit",0.0817850289402005,0.0069999996800597,0.0005724951764151],
        ["30.09.2016","Anchorage Cap Ltd","Credit","Long/Short Credit",0.0432248337941347,-0.0018000000529483,-0.0000778047031181],
        ["30.09.2016","Chenavari Struct Cred Ltd","Credit","Structured Credit",0.0723710698020410,0.0067000003711994,0.0004848861945378],
        ["30.09.2016","Prosiris Gbl Opp Fund Ltd","Credit","Structured Credit",0.0437695923143344,0.0033999987189121,0.0001488165577960],
        ["30.09.2016","GS Gamma Investments Ltd","Relative Value","Fixed Income Arbitrage",0.0263483823028455,0.0039999989724824,0.0001053935021380],
        ["30.09.2016","Argentiere Enhanced Ltd","Relative Value","Option Volatility Arbitrage",0.0266360841457681,-0.0023999988515511,-0.0000639265713597],
        ["30.09.2016","Argentiere Ltd","Relative Value","Option Volatility Arbitrage",0.0068360942626054,-0.0013999991609743,-0.0000095705262320],
        ["30.09.2016","Ionic Vol Arb Fund II Ltd","Relative Value","Option Volatility Arbitrage",0.0257688102861139,-0.0019000007042997,-0.0000489607576926],
        ["30.09.2016","Whitebox Asymm Opp Ltd","Relative Value","Diversified Relative Value",0.0492407796834440,0.0182350965752332,0.0008979103729674],
        ["30.09.2016","Myriad Opportunities Ltd","Multi-Strategy","Multi-Strategy",0.0421928265740045,0.0113000004465156,0.0004767789591260],
        ["30.09.2016","MTP Energy Corp and Ltd","Event Driven","Diversified Event Driven",0.0243610803482978,0.0239999975252650,0.0005846658680719],
        ["30.09.2016","York Euro Opp Unit Trust","Event Driven","Diversified Event Driven",0.0523423869825814,0.0034912814372807,0.0001827420040553],
        ["30.09.2016","Basswood Enhanced LS Ltd","Equities","Long-Biased Hedged Equities",0.0218833317499170,0.0046000000829032,0.0001006633278638],
        ["30.09.2016","Discovery Gbl Opp Ltd","Equities","Long-Biased Hedged Equities",0.0322969586902887,0.0046999992438327,0.0001517956814225],
        ["30.09.2016","Hitchwood Ltd","Equities","Long-Biased Hedged Equities",0.0432572473848695,-0.0028000002245390,-0.0001211203023906],
        ["30.09.2016","Incline Global ELS Ltd","Equities","Long-Biased Hedged Equities",0.0247123973483923,-0.0028000005161463,-0.0000691947253307],
        ["30.09.2016","Lagunita Ltd","Equities","Long-Biased Hedged Equities",0.0104941941099231,0.0500999990358815,0.0005257591147895],
        ["30.09.2016","Blue Mtn LS Equity Ltd","Equities","Less-Correlated Hedged Equities",0.0305714908960126,-0.0168999996982645,-0.0005166581869181],
        ["30.09.2016","Nipun Capital Ltd","Equities","Less-Correlated Hedged Equities",0.0230082082908857,-0.0181999991320427,-0.0004187493709240],
        ["30.09.2016","Passport Global LS Ltd","Equities","Less-Correlated Hedged Equities",0.0379054735758175,-0.0060289760803650,-0.0002285311935035],
        ["30.09.2016","Ren Inst Div Alpha LP","Equities","Less-Correlated Hedged Equities",0.0246283402187215,-0.0085999998872389,-0.0002118037231039],
        ["30.09.2016","Trian Partners Ltd","Equities","Activists",0.0442144649605980,-0.0273000007890729,-0.0012070549283128],
        ["30.09.2016","Atreaus Overseas Fund Ltd","Macro","Discretionary",0.0267168148723116,-0.0112000010446885,-0.0002992283544806],
        ["30.09.2016","Element Capital Ltd","Macro","Discretionary",0.0300591166602608,0.0123999980500899,0.0003727329879747],
        ["30.09.2016","Graticule Asia Macro Ltd","Macro","Discretionary",0.0439697166447497,0.0026999999674679,0.0001187182335104],
        ["30.09.2016","MKP Opportunity Ltd","Macro","Discretionary",0.0292499136967179,0.0019000000601156,0.0000555748377821],
        ["30.09.2016","GCM COM Ltd","Commodities","Discretionary",0.0230975893960266,-0.0036839811258086,-0.0000850910833866],
        ["30.09.2016","N/A","Uninvested","Bank Loans",-0.0405040381098655,0.0000000000000000,-0.0000192178070251],
        ["30.09.2016","N/A","Uninvested","Cash",0.0014833254353332,0.0000000000000000,0.0000000000000000],
        ["30.09.2016","N/A","Uninvested","Expenses",-0.0000959886262043,0.0000000000000000,-0.0000778658276596],
        ["30.09.2016","N/A","Uninvested","Management Fees",-0.0010010859473951,0.0000000000000000,-0.0005031082157971],
        ["30.09.2016","N/A","Uninvested","Net Rec/(Pay)",0.0408194711664073,0.0000000000000000,0.0000000000000000]
    ];
}