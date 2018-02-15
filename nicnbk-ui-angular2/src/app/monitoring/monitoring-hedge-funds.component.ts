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
        var formatter = new google.visualization.NumberFormat({
            prefix:'$ ',
            groupingSymbol: ' ',
            fractionDigits: 0
        });
        data.addColumn("string", "");
        data.addColumn("number", "NAV");
        data.addRows([
            ["Hedge Funds Portfolio", this.getPortfolioByDate(tableDate)]
        ]);

        formatter.format(data,1);

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
        var formatter = new google.visualization.NumberFormat({
            pattern: '#.##%',
            negativeColor: 'red',
        })
        data.addColumn("string", "");
        data.addColumn("number", "MTD");
        data.addColumn("number", "QTD");
        data.addColumn("number", "YTD");
        data.addColumn("number", "CUMULATIVE");
        data.addRows(this.getPerformanceRowData(tableDate));

        formatter.format(data,1);
        formatter.format(data,2);
        formatter.format(data,3);
        formatter.format(data,4);

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
        var performance = [];
        var portfolio = ["Hedge Funds Portfolio", 0, 0, 0, 0];
        var HFRIFOF = ["HFRIFOF Index", 0, 0, 0, 0];
        for(var i = 0; i < this.performanceMTD.length; i++){
            if(this.performanceMTD[i][0] === date) {
                portfolio[1] = this.performanceMTD[i][1];
                HFRIFOF[1] = this.performanceMTD[i][2];
            }
            if(this.performanceQTD[i][0] === date) {
                portfolio[2] = this.performanceQTD[i][1];
                HFRIFOF[2] = this.performanceQTD[i][2];
            }
            if(this.performanceYTD[i][0] === date) {
                portfolio[3] = this.performanceYTD[i][1];
                HFRIFOF[3] = this.performanceYTD[i][2];
            }
            if(this.performanceCUMUL[i][0] === date) {
                portfolio[4] = this.performanceCUMUL[i][1];
                HFRIFOF[4] = this.performanceCUMUL[i][2];
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
            animation: {
                duration: 700,
                easing: 'out',
                startup: true,
            },
            'allowHtml': true,
            cssClassNames: {},
            vAxis:{
                format:'short',
            }
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
        var formatter = new google.visualization.NumberFormat({
            pattern: '#.##%'
        });
        data.addColumn("string", "Date");
        data.addColumn("number", "Singularity");
        data.addColumn("number", "HFRIFOF");
        data.addRows(this.performanceMTD);
        formatter.format(data, 1);
        formatter.format(data, 2);

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            animation: {
                duration: 700,
                easing: 'out',
                startup: true,
            },
            'allowHtml': true,
            cssClassNames: {},
            vAxis: {
                format: '#.##%'
            }
        };

        var chart = this.createLineChart(document.getElementById('performance_monthly'));
        chart.draw(data, options);
    }

    //private getPerformanceMonthlyRowData(){
    //    var performance = [];
    //    for(var i = 0; i < this.performance.length; i++){
    //        if(this.performance[i][1] === "MTD"){
    //            var item = [];
    //            item.push(this.performance[i][0]);
    //            item.push(this.performance[i][2]);
    //            item.push(this.performance[i][3]);
    //            performance.push(item);
    //        }
    //    }
    //    return performance;
    //}

    drawPerformanceCumulative(){
        var data = new google.visualization.DataTable();
        var formatter = new google.visualization.NumberFormat({
            pattern: '#.##%'
        });
        data.addColumn("string", "Date");
        data.addColumn("number", "Singularity");
        data.addColumn("number", "HFRIFOF");
        data.addRows(this.performanceCUMUL);
        formatter.format(data, 1);
        formatter.format(data, 2);

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            animation: {
                duration: 700,
                easing: 'out',
                startup: true,
            },
            'allowHtml': true,
            cssClassNames: {},
            vAxis: {
                format: '#.##%'
            }
        };

        var chart = this.createLineChart(document.getElementById('performance_cumulative'));
        chart.draw(data, options);

    }

    //private getPerformanceCumulativeRowData(){
    //    var performance = [];
    //    for(var i = 0; i < this.performance.length; i++){
    //        if(this.performance[i][1] === "CUMULATIVE"){
    //            var item = [];
    //            item.push(this.performance[i][0]);
    //            item.push(this.performance[i][2]);
    //            item.push(this.performance[i][3]);
    //            performance.push(item);
    //        }
    //    }
    //    return performance;
    //}

    // HOLDINGS ---------------------------------------------------------------------
    drawHoldings(date){
        var data = new google.visualization.DataTable();
        var formatter = new google.visualization.NumberFormat({
            pattern: '#.##%',
            negativeColor: 'red'
        });
        data.addColumn("string", "Fund");
        data.addColumn("string", "Strategy");
        data.addColumn("string", "Substrategy");
        data.addColumn("number", "Month Opening Allocation");
        data.addColumn("number", "RoR");
        data.addColumn("number", "Contribution to return");
        data.addRows(this.getHoldingsRowData(date));
        formatter.format(data, 3);
        formatter.format(data, 4);
        formatter.format(data, 5);

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
        return holdingsArray;
    }

    // ALLOCATIONS
    drawAllocations(){
        this.drawTargetAllocationsTable();
        this.drawActualAllocationsChart();
    }

    drawTargetAllocationsTable(){
        var data = new google.visualization.DataTable();
        var formatter = new google.visualization.NumberFormat({
            pattern: '#.##%'
        });
        data.addColumn("string", "Strategy");;
        data.addColumn("number", "MIN");
        data.addColumn("number", "MAX");
        data.addRows(this.getTargetAllocationRowTata());
        formatter.format(data, 1);
        formatter.format(data, 2);

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
            ['Strategy', 'Percent'],
            ["Credit",0.2893],
            ["Relative Value",0.1184],
            ["Multi-Strategy",0.0673],
            ["Event Driven",0.0762],
            ["Equities",0.3117],
            ["Macro",0.1051],
            ["Commodities",0],
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
        ["Nov-15",146039155],
        ["Dec-15",145224471],
        ["Jan-16",141704891],
        ["Feb-16",139490031],
        ["Mar-16",139681370],
        ["Apr-16",140746692],
        ["May-16",141913168],
        ["Jun-16",141467836],
        ["Jul-16",142059717],
        ["Aug-16",143313959],
        ["Sep-16",143551395],
        ["Oct-16",143604313],
        ["Nov-16",145427045],
        ["Dec-16",146631106]
    ];
    private performanceMTD = [
        ["Aug-15",-0.01591816,-0.019957404],
        ["Sep-15",-0.021651089,-0.018256459],
        ["Oct-15",0.001216165,0.008536239],
        ["Nov-15",0.0019057,0.002995519],
        ["Dec-15",-0.005578531,-0.004217693],
        ["Jan-16",-0.024235447,-0.026557043],
        ["Feb-16",-0.015630089,-0.012014742],
        ["Mar-16",0.001371704,0.007343589],
        ["Apr-16",0.007626801,0.005214134],
        ["May-16",0.008287768,0.005026068],
        ["Jun-16",-0.00313806,-0.00465344],
        ["Jul-16",0.004183856,0.015049556],
        ["Aug-16",0.008828977,0.004426052],
        ["Sep-16",0.001656754,0.004389658],
        ["Oct-16",0.000368635,-0.00254192],
        ["Nov-16",0.012437377,0.002976088],
        ["Dec-16",0.0082795, 0.0085264]
    ];

    private performanceQTD = [
        ["Aug-15",-0.01591816,-0.016235499],
        ["Sep-15",-0.037224604,-0.034195555],
        ["Oct-15",0.001216165,0.008536239],
        ["Nov-15",0.003124183,0.011557329],
        ["Dec-15",-0.002471777,0.00729089],
        ["Jan-16",-0.024235447,-0.026557043],
        ["Feb-16",-0.039486734,-0.038252708],
        ["Mar-16",-0.038169194,-0.031190031],
        ["Apr-16",0.007626801,0.005214134],
        ["May-16",0.015977779,0.010266408],
        ["Jun-16",0.01278958,0.005565194],
        ["Jul-16",0.004183856,0.015049556],
        ["Aug-16",0.013049772,0.019542218],
        ["Sep-16",0.014728146,0.02401766],
        ["Oct-16",0.000368635,-0.00254192],
        ["Nov-16",0.012810597,0.000426603],
        ["Dec-16",0.0214537,0.0083132]
    ];

    private performanceYTD = [
        ["Aug-15",-0.01591816,0.024937035],
        ["Sep-15",-0.037224604,0.006225314],
        ["Oct-15",-0.03605371,0.014814694],
        ["Nov-15",-0.034216718,0.017854591],
        ["Dec-15",-0.03960437,0.013561592],
        ["Jan-16",-0.024235447,-0.026557043],
        ["Feb-16",-0.039486734,-0.038252708],
        ["Mar-16",-0.038169194,-0.031190031],
        ["Apr-16",-0.030833502,-0.026138527],
        ["May-16",-0.022801274,-0.021243833],
        ["Jun-16",-0.025867782,-0.025798416],
        ["Jul-16",-0.021792154,-0.011137115],
        ["Aug-16",-0.013155579,-0.006760357],
        ["Sep-16",-0.01152062,-0.002400374],
        ["Oct-16",-0.011156233,-0.004936192],
        ["Nov-16",0.00114239,-0.001974795],
        ["Dec-16",0.0096859,0.0048325]
    ];

    private performanceCUMUL = [
        ["Aug-15",-0.01591816,-0.019957404],
        ["Sep-15",-0.037224604,-0.037849511],
        ["Oct-15",-0.03605371,-0.029636364],
        ["Nov-15",-0.034216718,-0.026729621],
        ["Dec-15",-0.03960437,-0.030834577],
        ["Jan-16",-0.062879988,-0.056572745],
        ["Feb-16",-0.077527257,-0.06790778],
        ["Mar-16",-0.076261897,-0.061062878],
        ["Apr-16",-0.069216731,-0.056167134],
        ["May-16",-0.061502614,-0.051423366],
        ["Jun-16",-0.064447675,-0.05583751],
        ["Jul-16",-0.060533459,-0.041628284],
        ["Aug-16",-0.052238931,-0.037386481],
        ["Sep-16",-0.050668724,-0.033160937],
        ["Oct-16",-0.050318767,-0.035618564],
        ["Nov-16",-0.038507224,-0.03274848],
        ["Dec-16",-0.030302039,-0.026151052]
    ];

    private holdings = [
        ["30.09.16","Canyon Opp Cred GRF Ltd","Credit","Long-Biased Credit",0.0583560881498609,0.0071000002152275,0.0004143282384239],
        ["30.09.16","CVI Intl Credit Ltd","Credit","Long-Biased Credit",0.0817850289402005,0.0069999996800597,0.0005724951764151],
        ["30.09.16","Anchorage Cap Ltd","Credit","Long/Short Credit",0.0432248337941347,-0.0018000000529483,-0.0000778047031181],
        ["30.09.16","Chenavari Struct Cred Ltd","Credit","Structured Credit",0.0723710698020410,0.0067000003711994,0.0004848861945378],
        ["30.09.16","Prosiris Gbl Opp Fund Ltd","Credit","Structured Credit",0.0437695923143344,0.0033999987189121,0.0001488165577960],
        ["30.09.16","GS Gamma Investments Ltd","Relative Value","Fixed Income Arbitrage",0.0263483823028455,0.0039999989724824,0.0001053935021380],
        ["30.09.16","Argentiere Enhanced Ltd","Relative Value","Option Volatility Arbitrage",0.0266360841457681,-0.0023999988515511,-0.0000639265713597],
        ["30.09.16","Argentiere Ltd","Relative Value","Option Volatility Arbitrage",0.0068360942626054,-0.0013999991609743,-0.0000095705262320],
        ["30.09.16","Ionic Vol Arb Fund II Ltd","Relative Value","Option Volatility Arbitrage",0.0257688102861139,-0.0019000007042997,-0.0000489607576926],
        ["30.09.16","Whitebox Asymm Opp Ltd","Relative Value","Diversified Relative Value",0.0492407796834440,0.0182350965752332,0.0008979103729674],
        ["30.09.16","Myriad Opportunities Ltd","Multi-Strategy","Multi-Strategy",0.0421928265740045,0.0113000004465156,0.0004767789591260],
        ["30.09.16","MTP Energy Corp and Ltd","Event Driven","Diversified Event Driven",0.0243610803482978,0.0239999975252650,0.0005846658680719],
        ["30.09.16","York Euro Opp Unit Trust","Event Driven","Diversified Event Driven",0.0523423869825814,0.0034912814372807,0.0001827420040553],
        ["30.09.16","Basswood Enhanced LS Ltd","Equities","Long-Biased Hedged Equities",0.0218833317499170,0.0046000000829032,0.0001006633278638],
        ["30.09.16","Discovery Gbl Opp Ltd","Equities","Long-Biased Hedged Equities",0.0322969586902887,0.0046999992438327,0.0001517956814225],
        ["30.09.16","Hitchwood Ltd","Equities","Long-Biased Hedged Equities",0.0432572473848695,-0.0028000002245390,-0.0001211203023906],
        ["30.09.16","Incline Global ELS Ltd","Equities","Long-Biased Hedged Equities",0.0247123973483923,-0.0028000005161463,-0.0000691947253307],
        ["30.09.16","Lagunita Ltd","Equities","Long-Biased Hedged Equities",0.0104941941099231,0.0500999990358815,0.0005257591147895],
        ["30.09.16","Blue Mtn LS Equity Ltd","Equities","Less-Correlated Hedged Equities",0.0305714908960126,-0.0168999996982645,-0.0005166581869181],
        ["30.09.16","Nipun Capital Ltd","Equities","Less-Correlated Hedged Equities",0.0230082082908857,-0.0181999991320427,-0.0004187493709240],
        ["30.09.16","Passport Global LS Ltd","Equities","Less-Correlated Hedged Equities",0.0379054735758175,-0.0060289760803650,-0.0002285311935035],
        ["30.09.16","Ren Inst Div Alpha LP","Equities","Less-Correlated Hedged Equities",0.0246283402187215,-0.0085999998872389,-0.0002118037231039],
        ["30.09.16","Trian Partners Ltd","Equities","Activists",0.0442144649605980,-0.0273000007890729,-0.0012070549283128],
        ["30.09.16","Atreaus Overseas Fund Ltd","Macro","Discretionary",0.0267168148723116,-0.0112000010446885,-0.0002992283544806],
        ["30.09.16","Element Capital Ltd","Macro","Discretionary",0.0300591166602608,0.0123999980500899,0.0003727329879747],
        ["30.09.16","Graticule Asia Macro Ltd","Macro","Discretionary",0.0439697166447497,0.0026999999674679,0.0001187182335104],
        ["30.09.16","MKP Opportunity Ltd","Macro","Discretionary",0.0292499136967179,0.0019000000601156,0.0000555748377821],
        ["30.09.16","GCM COM Ltd","Commodities","Discretionary",0.0230975893960266,-0.0036839811258086,-0.0000850910833866],
        ["30.09.16","N/A","Uninvested","Bank Loans",-0.0405040381098655,0.0000000000000000,-0.0000192178070251],
        ["30.09.16","N/A","Uninvested","Cash",0.0014833254353332,0.0000000000000000,0.0000000000000000],
        ["30.09.16","N/A","Uninvested","Expenses",-0.0000959886262043,0.0000000000000000,-0.0000778658276596],
        ["30.09.16","N/A","Uninvested","Management Fees",-0.0010010859473951,0.0000000000000000,-0.0005031082157971],
        ["30.09.16","N/A","Uninvested","Net Rec/(Pay)",0.0408194711664073,0.0000000000000000,0.0000000000000000],
        ["31.10.16","CVI Intl Credit Ltd","Credit","Long-Biased Credit",0.082237534,0.017,0.001398038],
        ["31.10.16","Anchorage Cap Ltd","Credit","Long/Short Credit",0.043061304,0.006199999,0.00026698],
        ["31.10.16","Chenavari Struct Cred Ltd","Credit","Structured Credit",0.072781482,0.014200001,0.001033497],
        ["31.10.16","Prosiris Gbl Opp Fund Ltd","Credit","Structured Credit",0.033043706,0.007599999,0.000251132],
        ["31.10.16","Argentiere Enhanced Ltd","Relative Value","Option Volatility Arbitrage",0.026500052,-0.0062,-0.0001643],
        ["31.10.16","Argentiere Ltd","Relative Value","Option Volatility Arbitrage",0.006809667,-0.003900005,-2.65577E-05],
        ["31.10.16","Ionic Vol Arb Fund II Ltd","Relative Value","Option Volatility Arbitrage",0.025675254,-0.0021,-5.3918E-05],
        ["31.10.16","Whitebox Asymm Opp Ltd","Relative Value","Diversified Relative Value",0.050154935,0.027099999,0.001359199],
        ["31.10.16","Myriad Opportunities Ltd","Multi-Strategy","Multi-Strategy",0.042587966,0.003000001,0.000127764],
        ["31.10.16","MTP Energy Corp and Ltd","Event Driven","Diversified Event Driven",0.024932772,0.012699999,0.000316646],
        ["31.10.16","York Euro Opp Unit Trust","Event Driven","Diversified Event Driven",0.052470592,-0.004,-0.000209882],
        ["31.10.16","Basswood Enhanced LS Ltd","Equities","Long-Biased Hedged Equities",0.021947103,0.025000003,0.000548678],
        ["31.10.16","Discovery Gbl Opp Ltd","Equities","Long-Biased Hedged Equities",0.032377435,-0.0046,-0.000148936],
        ["31.10.16","Hitchwood Ltd","Equities","Long-Biased Hedged Equities",0.035054969,-0.036799999,-0.001290023],
        ["31.10.16","Incline Global ELS Ltd","Equities","Long-Biased Hedged Equities",0.024600054,-0.0329,-0.000809342],
        ["31.10.16","Lagunita Ltd","Equities","Long-Biased Hedged Equities",0.011002871,-0.112299998,-0.001235622],
        ["31.10.16","Atlas Enhanced Fund Ltd","Equities","Less-Correlated Hedged Equities",0.038313804,-0.0035,-0.000134098],
        ["31.10.16","Blue Mtn LS Equity Ltd","Equities","Less-Correlated Hedged Equities",0.029995805,0.008499998,0.000254964],
        ["31.10.16","Nipun Capital Ltd","Equities","Less-Correlated Hedged Equities",0.022552212,0.022199999,0.000500659],
        ["31.10.16","Passport Global LS Ltd","Equities","Less-Correlated Hedged Equities",0.028225679,-0.028216251,-0.000796423],
        ["31.10.16","Ren Inst Div Alpha LP","Equities","Less-Correlated Hedged Equities",0.024378889,-0.0042,-0.000102391],
        ["31.10.16","Trian Partners Ltd","Equities","Activists",0.042917542,-0.0023,-9.87104E-05],
        ["31.10.16","Atreaus Overseas Fund Ltd","Macro","Discretionary",0.026376913,-0.002899999,-7.6493E-05],
        ["31.10.16","Element Capital Ltd","Macro","Discretionary",0.032813586,-0.017930958,-0.000588379],
        ["31.10.16","Graticule Asia Macro Ltd","Macro","Discretionary",0.044016536,0.0008,3.52132E-05],
        ["31.10.16","MKP Opportunity Ltd","Macro","Discretionary",0.0292424,0.0189,0.000552681],
        ["31.10.16","GCM COM Ltd","Commodities","Discretionary",0.022978556,0.001294445,2.97445E-05],
        ["31.10.16","N/A","Uninvested","Bank Loans",0,0,-9.97117E-05],
        ["31.10.16","N/A","Uninvested","Cash",0.013034944,0,0],
        ["31.10.16","N/A","Uninvested","Expenses",-0.000171696,0,-7.67704E-05],
        ["31.10.16","N/A","Uninvested","Management Fees",-6.32108E-07,0,-0.000500405],
        ["31.10.16","N/A","Uninvested","Net Rec/(Pay)",0.001320208,0,0],
        ["30.11.16","Canyon Opp Cred GRF Ltd","Credit","Long-Biased Credit",0.058744294,0.0114,0.000669685],
        ["30.11.16","CVI Intl Credit Ltd","Credit","Long-Biased Credit",0.083560101,0.011,0.000919161],
        ["30.11.16","Anchorage Cap Ltd","Credit","Long/Short Credit",0.043300581,0.002900001,0.000125572],
        ["30.11.16","Chenavari Struct Cred Ltd","Credit","Structured Credit",0.073747891,0.002200001,0.000162245],
        ["30.11.16","Prosiris Gbl Opp Fund Ltd","Credit","Structured Credit",0.033425798,0.005800001,0.00019387],
        ["30.11.16","Argentiere Enhanced Ltd","Relative Value","Option Volatility Arbitrage",0.026310749,-0.029799999,-0.00078406],
        ["30.11.16","Argentiere Ltd","Relative Value","Option Volatility Arbitrage",0.006777562,-0.019700003,-0.000133518],
        ["30.11.16","Ionic Vol Arb Fund II Ltd","Relative Value","Option Volatility Arbitrage",0.025609348,0.046000001,0.00117803],
        ["30.11.16","Whitebox Asymm Opp Ltd","Relative Value","Diversified Relative Value",0.051622305,0.0257,0.001326693],
        ["30.11.16","Myriad Opportunities Ltd","Multi-Strategy","Multi-Strategy",0.042693745,0.000900001,3.84244E-05],
        ["30.11.16","MTP Energy Corp and Ltd","Event Driven","Diversified Event Driven",0.025233655,-0.000700002,-1.76636E-05],
        ["30.11.16","York Euro Opp Unit Trust","Event Driven","Diversified Event Driven",0.052256732,-0.009982604,-0.000521658],
        ["30.11.16","Basswood Enhanced LS Ltd","Equities","Long-Biased Hedged Equities",0.022510514,0.096799999,0.002179018],
        ["30.11.16","Discovery Gbl Opp Ltd","Equities","Long-Biased Hedged Equities",0.032170155,0.0922,0.002966088],
        ["30.11.16","Hitchwood Ltd","Equities","Long-Biased Hedged Equities",0.03375091,-0.024199999,-0.000816772],
        ["30.11.16","Incline Global ELS Ltd","Equities","Long-Biased Hedged Equities",0.023783568,0.0127,0.000302051],
        ["30.11.16","Lagunita Ltd","Equities","Long-Biased Hedged Equities",0.009766659,0.079800001,0.000779379],
        ["30.11.16","Atlas Enhanced Fund Ltd","Equities","Less-Correlated Hedged Equities",0.038143009,-0.0111,-0.000423387],
        ["30.11.16","Blue Mtn LS Equity Ltd","Equities","Less-Correlated Hedged Equities",0.030241936,0.0073,0.000220766],
        ["30.11.16","Nipun Capital Ltd","Equities","Less-Correlated Hedged Equities",0.023044988,0.0032,7.3744E-05],
        ["30.11.16","Passport Global LS Ltd","Equities","Less-Correlated Hedged Equities",0.027393323,-0.026999999,-0.00073962],
        ["30.11.16","Ren Inst Div Alpha LP","Equities","Less-Correlated Hedged Equities",0.024269079,-0.049000001,-0.001189185],
        ["30.11.16","Trian Partners Ltd","Equities","Activists",0.042806019,0.0528,0.002260158],
        ["30.11.16","Atreaus Overseas Fund Ltd","Macro","Discretionary",0.026298726,0.0323,0.000849449],
        ["30.11.16","Element Capital Ltd","Macro","Discretionary",0.032222709,0.038540112,0.001241867],
        ["30.11.16","Graticule Asia Macro Ltd","Macro","Discretionary",0.044036134,0.0247,0.001087693],
        ["30.11.16","MKP Opportunity Ltd","Macro","Discretionary",0.02977137,0.036900001,0.001098564],
        ["30.11.16","GCM COM Ltd","Commodities","Discretionary",0.023007207,0.000146716,3.37553E-06],
        ["30.11.16","N/A","Uninvested","Bank Loans",-0.014637858,0,-1.77949E-05],
        ["30.11.16","N/A","Uninvested","Cash",0.00056954,0,0],
        ["30.11.16","N/A","Uninvested","Expenses",-5.93704E-05,0,-8.15956E-05],
        ["30.11.16","N/A","Uninvested","Management Fees",-0.000500882,0,-0.000513201],
        ["30.11.16","N/A","Uninvested","Net Rec/(Pay)",0.028129501,0,0],
        ["31.12.16","Canyon Opp Cred GRF Ltd","Credit","Long-Biased Credit",0.058821597,0.0065,0.00038234],
        ["31.12.16","CVI Intl Credit Ltd","Credit","Long-Biased Credit",0.083434308,0.011,0.000917777],
        ["31.12.16","Anchorage Cap Ltd","Credit","Long/Short Credit",0.04287482,0.005899999,0.000252961],
        ["31.12.16","Chenavari Struct Cred Ltd","Credit","Structured Credit",0.07290995,0.0114,0.000831173],
        ["31.12.16","Prosiris Gbl Opp Fund Ltd","Credit","Structured Credit",0.033342554,0.0011,3.66768E-05],
        ["31.12.16","Argentiere Enhanced Ltd","Relative Value","Option Volatility Arbitrage",0.025218026,-0.0077,-0.000194179],
        ["31.12.16","Argentiere Ltd","Relative Value","Option Volatility Arbitrage",0.006561958,-0.004899998,-3.21536E-05],
        ["31.12.16","Ionic Vol Arb Fund II Ltd","Relative Value","Option Volatility Arbitrage",0.026452386,0.002900001,7.67119E-05],
        ["31.12.16","Whitebox Asymm Opp Ltd","Relative Value","Diversified Relative Value",0.052233465,0.0089,0.000464878],
        ["31.12.16","Myriad Opportunities Ltd","Multi-Strategy","Multi-Strategy",0.04977185,0.011699999,0.000582331],
        ["31.12.16","MTP Energy Corp and Ltd","Event Driven","Diversified Event Driven",0.024960133,0.017899994,0.000446786],
        ["31.12.16","York Euro Opp Unit Trust","Event Driven","Diversified Event Driven",0.05111437,0.014472346,0.000739745],
        ["31.12.16","Basswood Enhanced LS Ltd","Equities","Long-Biased Hedged Equities",0.024374508,0.058399999,0.001423471],
        ["31.12.16","Discovery Gbl Opp Ltd","Equities","Long-Biased Hedged Equities",0.034548815,-0.026,-0.000898269],
        ["31.12.16","Hitchwood Ltd","Equities","Long-Biased Hedged Equities",0.032521539,0.002499998,8.13038E-05],
        ["31.12.16","Incline Global ELS Ltd","Equities","Long-Biased Hedged Equities",0.023784153,0.0083,0.000197408],
        ["31.12.16","Lagunita Ltd","Equities","Long-Biased Hedged Equities",0.015571332,0.035599998,0.000554339],
        ["31.12.16","Atlas Enhanced Fund Ltd","Equities","Less-Correlated Hedged Equities",0.037247245,0.0081,0.000301703],
        ["31.12.16","Blue Mtn LS Equity Ltd","Equities","Less-Correlated Hedged Equities",0.030091085,-0.010000001,-0.000300911],
        ["31.12.16","Nipun Capital Ltd","Equities","Less-Correlated Hedged Equities",0.022830442,-0.023500001,-0.000536515],
        ["31.12.16","Passport Global LS Ltd","Equities","Less-Correlated Hedged Equities",0.026327548,-0.005999999,-0.000157965],
        ["31.12.16","Ren Inst Div Alpha LP","Equities","Less-Correlated Hedged Equities",0.036553374,0.045523894,0.001664052],
        ["31.12.16","Trian Partners Ltd","Equities","Activists",0.044545655,0.022899999,0.001020095],
        ["31.12.16","Atreaus Overseas Fund Ltd","Macro","Discretionary",0.026751305,0.003100001,8.29291E-05],
        ["31.12.16","Element Capital Ltd","Macro","Discretionary",0.03302863,0.0318,0.00105031],
        ["31.12.16","Graticule Asia Macro Ltd","Macro","Discretionary",0.044558383,0.011099999,0.000494598],
        ["31.12.16","GCM COM Ltd","Commodities","Discretionary",0.022726297,-0.022240721,-0.000505449],
        ["31.12.16","N/A","Uninvested","Bank Loans",-0.04678887,0,-5.32678E-05],
        ["31.12.16","N/A","Uninvested","Cash",0.004237179,0,0],
        ["31.12.16","N/A","Uninvested","Expenses",2.86469E-05,0,-0.000129702],
        ["31.12.16","N/A","Uninvested","Management Fees",-0.001001625,0,-0.00051369],
        ["31.12.16","N/A","Uninvested","Net Rec/(Pay)",0.060368943,0,0]
    ];
}