import {Component, AfterViewInit, ViewChild} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {CommonTableau} from "./common-tableau.component";
import {GoogleChartComponent} from "../google-chart/google-chart.component";

declare var google:any;
declare var $: any;

@Component({
    selector: 'monitoring-private-equity',
    templateUrl: 'view/monitoring-private-equity.component.html',
    styleUrls: [],
    providers: [],
})
export class MonitoringPrivateEquityComponent extends GoogleChartComponent {
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

        this.drawValuesTable(tableDate);
        this.drawIRR();
        this.drawHoldingsA(tableDate);
        this.drawHoldingsB(tableDate);
        this.drawCashFlow();
        this.drawExposure();
    }

    redraw(){
        this.tableDate = $("#tableDate").val();
        this.drawValuesTable(this.tableDate);

        this.drawHoldingsA(this.tableDate);

        this.drawHoldingsB(this.tableDate);
    }

    // VALUE TABLES --------------------------------
    drawValuesTable(tableDate){
        var data = new google.visualization.DataTable();
        var formatter = new google.visualization.NumberFormat({
            prefix:'$ ',
            groupingSymbol: ' ',
            fractionDigits: 0
        });
        data.addColumn("string", "");
        data.addColumn("number", "");
        data.addRows([
            ["Private Equity Portfolio", this.getNAVByDate(tableDate)],
            ["Commitment", this.getCommitmentByDate(tableDate)]
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

        var chart = this.createTableChart(document.getElementById('net_value'));
        chart.draw(data, options);
    }
    private getNAVByDate(tableDate){
        for(var i = 0; i < this.nav.length; i++){
            if(this.nav[i][0] === tableDate){
                return this.nav[i][1];
            }
        }
        return null;
    }
    private getCommitmentByDate(date){
        for(var i = 0; i < this.commitments.length; i++){
            if(this.commitments[i][0] === date){
                return this.commitments[i][1];
            }
        }
        return null;
    }

    // IRR LINE CHART ------------------------------
    drawIRR(){
        var data = new google.visualization.DataTable();
        var formatter = new google.visualization.NumberFormat({
            pattern:'#.##%'
        });
        data.addColumn("string", "Date");
        data.addColumn("number", "IRR");
        data.addRows(this.getIRRRowData());
        formatter.format(data, 1);

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            vAxis: {
                format: '#.##%',
            },
            cssClassNames: {}
        };

        var chart = this.createLineChart(document.getElementById('irr'));
        chart.draw(data, options);
    }

    private getIRRRowData(){
        return this.irr;
    }

    // HOLDINGS A ----------------------------------
    drawHoldingsA(date){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "Fund");
        data.addColumn("string", "Currency");
        data.addColumn("string", "Geography");
        data.addColumn("string", "Strategy");
        data.addColumn("number", "Commitment");
        data.addColumn("number", "Vintage");
        data.addColumn("number", "Size");
        data.addColumn("number", "Contribution");
        data.addColumn("number", "Distribution");
        data.addColumn("number", "NET cost");
        data.addColumn("number", "Fair value");
        data.addColumn("number", "Valuation");
        data.addColumn("number", "Return");
        data.addColumn("number", "TVPI");
        data.addRows(this.getHoldingsARowData(date));

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {
                tableCell: 'googleChartTable',
            }
        };

        var chart = this.createTableChart(document.getElementById('holdings_a'));
        chart.draw(data, options);
    }

    private getHoldingsARowData(date){
        var holdings = [];
        var month = this.getMonthNumber(date);
        for(var i = 0; i < this.holdingsA.length; i++){
            if(typeof this.holdingsA[i][0] === 'string' && (this.holdingsA[i][0] + "").substring(3, 5) === month){
                holdings.push(this.holdingsA[i][1]);
            }
        }
        return holdings;
    }


    // HOLDINGS B ----------------------------------
    drawHoldingsB(date){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "Fund");
        data.addColumn("string", "Currency");
        data.addColumn("string", "Geography");
        data.addColumn("string", "Strategy");
        data.addColumn("number", "Commitment");
        data.addColumn("number", "Vintage");
        data.addColumn("number", "Size");
        data.addColumn("number", "Contribution");
        data.addColumn("number", "Distribution");
        data.addColumn("number", "NET cost");
        data.addColumn("number", "Fair value");
        data.addColumn("number", "Valuation");
        data.addColumn("number", "Return");
        data.addColumn("number", "TVPI");
        data.addRows(this.getHoldingsBRowData(date));

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {
                tableCell: 'googleChartTable',
            }
        };

        var chart = this.createTableChart(document.getElementById('holdings_b'));
        chart.draw(data, options);
    }

    private getHoldingsBRowData(date){
        var holdings = [];
        var month = this.getMonthNumber(date);
        for(var i = 0; i < this.holdingsB.length; i++){
            if(typeof this.holdingsB[i][0] === 'string' && (this.holdingsB[i][0] + "").substring(3, 5) === month){
                holdings.push(this.holdingsB[i][1]);
            }
        }
        return holdings;
    }

    // CASH FLOW ----------------------------------------
    drawCashFlow(){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "Fund");
        data.addColumn("string", "Transaction Date");
        data.addColumn("string", "Type");
        data.addColumn("string", "Transaction");
        data.addColumn("number", "Amount");
        data.addColumn("string", "Scenario");
        data.addColumn("string", "AsOfDate");
        data.addColumn("string", "Currency");
        data.addColumn("number", "Local amount");
        data.addColumn("string", "Local currency");
        data.addColumn("string", "Commitment");
        data.addColumn("number", "Local Fx Rate");
        data.addRows(this.getCashFlowRowData());

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '600',
            'allowHtml': true,
            cssClassNames: {
                tableCell: 'googleChartTable',
            }
        };

        var chart = this.createTableChart(document.getElementById('cash_flow'));
        chart.draw(data, options);
    }

    private getCashFlowRowData(){
        return this.cashFlow;
    }

    // EXPOSURE -----------------------------------------
    drawExposure(){
        this.drawExposureBySector();
        this.drawExposureByGeography();
    }

    drawExposureBySector(){
        var data = google.visualization.arrayToDataTable([
            ['Sector', 'Amount'],
            ["Industrials",1887826.61],
            ["Materials",1206084.496],
            ["Consumer Discretionary",4650664.284],
            ["Health Care",1511992.057],
            ["Consumer Staples",94041.67209],
            ["Financials",191050.8651],
            ["Information Technology",230878.4706],
            ["Energy",666676.9288]
        ]);

        var options = {
            //title: 'Market Exposure by Sector'
        };

        var chart = new google.visualization.PieChart(document.getElementById('market_exposure_by_sector'));
        chart.draw(data, options);
    }

    drawExposureByGeography(){
        var data = google.visualization.arrayToDataTable([
            ['Country', 'Market Value'],
            ["Austria",269905000],
            ["Canada",52825000],
            ["China",66156000],
            ["France",1317153000],
            ["India",13008000],
            ["Poland",213798000],
            ["Spain",435551000],
            ["Turkey",131900000],
            ["United Kingdom",1437878000],
            ["United States",5397892308]
        ]);

        var options = {
            //colorAxis: {colors: ['#e7711c', '#4374e0']} // orange to blue
            colorAxis: {colors: ['#7bdef7', 'blue']} // orange to blue
        };

        var chart = new google.visualization.GeoChart(document.getElementById('market_exposure_by_geography'));
        chart.draw(data, options);
    }


    // DATA SOURCE --------------------------------------
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
        for(var i = this.commitments.length - 1; i >= 0 ; i--){
            dates.push(this.commitments[i][0]);
        }
        return dates;
    }

    private nav = [
        ["Dec-14",0],
        ["Jan-15",0],
        ["Feb-15",0],
        ["Mar-15",0],
        ["Apr-15",0],
        ["May-15",0],
        ["Jun-15",0],
        ["Jul-15",0],
        ["Aug-15",0],
        ["Sep-15",0],
        ["Oct-15",3965000],
        ["Nov-15",3163463],
        ["Dec-15",8601146],
        ["Jan-16",11509729],
        ["Feb-16",11593892],
        ["Mar-16",13135835],
        ["Apr-16",17090141],
        ["May-16",17001537],
        ["Jun-16",21365316],
        ["Jul-16",28882486],
        ["Aug-16",36981406],
        ["Sep-16",46115007],
        ["Oct-16",57581011],
        ["Nov-16",68373409],
        ["Dec-16",72629726],
    ];

    private commitments = [
            ["Oct-15",0],
            ["Nov-15",0],
            ["Dec-15",76375000],
            ["Jan-16",100455319],
            ["Feb-16",130475393],
            ["Mar-16",138116088],
            ["Apr-16",147964960],
            ["May-16",158048774],
            ["Jun-16",163026975],
            ["Jul-16",161849170],
            ["Aug-16",188941388],
            ["Sep-16",188941388],
            ["Oct-16",188186946],
            ["Nov-16",268093661],
            ["Dec-16",268093661],
    ];

    private irr = [
        ["Dec-15", -0.061680851],
        ["Mar-16", 0.048384799],
        ["Jun-16", 0.106167063],
        ["Sep-16", 0.169112596],
        ["Dec-16", 0.026016088]
    ];

    private holdingsA = [
        ["29.01.2016",["Bridgepoint Europe IV, L.P.","Euro","Western Europe","Buyout (Secondaries)",3758464,2008,5369537453,3508085.01,0,3508085,3483155,-24930,-0.007106441,0.992893559]],
        ["29.01.2016",["Saw Mill Capital Partners II, L.P.","USD","North America","Buyout",10000000,2015,108744444,0,0,0,0,0,0,0]],
        ["29.01.2016",["BCP Energy Services Fund, L.P.","USD","North America","Buyout",10000000,2015,694500000,3444350,1851,3241755,2833252,-408503,-0.126012916,0.873987084]],
        ["29.01.2016",["ADT Security","USD","North America","Co/Direct Investment",7500000,2016,15000000000,0,0,0,0,0,0,0]],
        ["29.01.2016",["Bridgepoint Development Capital III, L.P.","GBP**","Western Europe","Buyout",10000000,2016,500000000,0,0,0,0,0,0,0]],
        ["29.01.2016",["OHA Strategic Credit Fund II, LP.","USD","Global","Distressed Debt",10000000,2016,2055820000,0,0,0,0,0,0,0]],
        ["29.01.2016",["Total Tranche A","","","",0,0,0,6952435.01,1851,6749840,6316407,-433433,-0.064213818,0]],
        ["29.02.2016",["Bridgepoint Europe IV, L.P.","Euro","Western Europe","Buyout (Secondaries)",3758464,2008,5369537453,3508085.01,0,3508085,3515711,7626,0.002173836,1.002173836]],
        ["29.02.2016",["Saw Mill Capital Partners II, L.P.","USD","North America","Buyout",10000000,2015,108744444,0,0,0,0,0,0,0]],
        ["29.02.2016",["BCP Energy Services Fund, L.P.","USD","North America","Buyout",10000000,2015,694500000,3444350,1851,3369824,2961421,-408403,-0.121194163,0.878805837]],
        ["29.02.2016",["ADT Security","USD","North America","Co/Direct Investment",7500000,2016,15000000000,0,0,0,0,0,0,0]],
        ["29.02.2016",["Bridgepoint Development Capital III, L.P.","GBP**","Western Europe","Buyout",10000000,2016,500000000,0,0,0,0,0,0,0]],
        ["29.02.2016",["OHA Strategic Credit Fund II, LP.","USD","Global","Distressed Debt",10000000,2016,2055820000,0,0,0,0,0,0,0]],
        ["29.02.2016",["Total Tranche A","","","",0,0,0,6952435.01,1851,6877909,6477132,-400777,-0.058270181,0]],
        ["31.03.2016",["Bridgepoint Europe IV, L.P.","Euro","Western Europe","Buyout (Secondaries)",3758464,2008,5369537453,3508085.01,151077.42,3348571,3830366,481795,0.143880778,1.143880778]],
        ["31.03.2016",["Saw Mill Capital Partners II, L.P.","USD","North America","Buyout",10000000,2015,108744444,0,0,0,0,0,0,0]],
        ["31.03.2016",["BCP Energy Services Fund, L.P.","USD","North America","Buyout",10000000,2015,694500000,3444350,1851,3369824,3222022,-147802,-0.043860451,0.956139549]],
        ["31.03.2016",["ADT Security","USD","North America","Co/Direct Investment",7500000,2016,15000000000,0,0,0,0,0,0,0]],
        ["31.03.2016",["Bridgepoint Development Capital III, L.P.","GBP**","Western Europe","Buyout",10000000,2016,500000000,0,0,0,0,0,0,0]],
        ["31.03.2016",["OHA Strategic Credit Fund II, LP.","USD","Global","Distressed Debt",10000000,2016,2055820000,0,0,0,0,0,0,0]],
        ["31.03.2016",["Total Tranche A","","","",0,0,0,6952435.01,152928.42,6718395,7052388,333993,0.049713213,0]],
        ["30.04.2016",["Bridgepoint Europe IV, L.P.","Euro","Western Europe","Buyout (Secondaries)",3758464,2008,5369537453,3508085.01,151077.42,3348571,3846658,498087,0.148746137,1.148746137]],
        ["30.04.2016",["Saw Mill Capital Partners II, L.P.","USD","North America","Buyout",10000000,2015,108744444,1018829,0,1018829,1007514,-11315,-0.011105887,0.988894113]],
        ["30.04.2016",["BCP Energy Services Fund, L.P.","USD","North America","Buyout",10000000,2015,694500000,3444350,0,3369824,3222022,-147802,-0.043860451,0.956139549]],
        ["30.04.2016",["ADT Security","USD","North America","Co/Direct Investment",7500000,2016,15000000000,7532819.1,32200,7500619,7500619,0,0,1]],
        ["30.04.2016",["Bridgepoint Development Capital III, L.P.","GBP**","Western Europe","Buyout",10000000,2016,500000000,0,0,0,0,0,0,0]],
        ["30.04.2016",["OHA Strategic Credit Fund II, LP.","USD","Global","Distressed Debt",10000000,2016,2055820000,0,0,0,0,0,0,0]],
        ["30.04.2016",["Total Tranche A","","","",0,0,0,15504083.11,183277.42,15237843,15576813,338970,0.022245274,0]],
        ["31.05.2016",["Bridgepoint Europe IV, L.P.","Euro","Western Europe","Buyout (Secondaries)",3758464,2008,5369537453,0,151077.42,3348571,3752946,404375,0.120760468,1.120760468]],
        ["31.05.2016",["Saw Mill Capital Partners II, L.P.","USD","North America","Buyout",10000000,2015,108744444,0,0,1018829,1007514,-11315,-0.011105887,0.988894113]],
        ["31.05.2016",["BCP Energy Services Fund, L.P.","USD","North America","Buyout",10000000,2015,694500000,0,0,3369824,3357521,-12303,-0.003650933,0.996349067]],
        ["31.05.2016",["ADT Security","USD","North America","Co/Direct Investment",7500000,2016,15000000000,0,32200,7500619,7500619,0,0,1]],
        ["31.05.2016",["Bridgepoint Development Capital III, L.P.","GBP**","Western Europe","Buyout",10000000,2016,500000000,0,0,0,0,0,0,0]],
        ["31.05.2016",["OHA Strategic Credit Fund II, LP.","USD","Global","Distressed Debt",10000000,2016,2055820000,0,0,0,0,0,0,0]],
        ["31.05.2016",["Total Tranche A","","","",0,0,0,15504083.11,183277.42,15237843,15618600,380757,0.024987592,0]],
        ["30.06.2016",["Bridgepoint Europe IV, L.P.","Euro","Western Europe","Buyout (Secondaries)",3758464,2008,5369537453,3508085.01,151077.42,3348571,3739220,390649,0.116661406,1.116661406]],
        ["30.06.2016",["Saw Mill Capital Partners II, L.P.","USD","North America","Buyout",10000000,2015,108744444,1128331,363,909327,899228,-10099,-0.011106016,0.988893984]],
        ["30.06.2016",["BCP Energy Services Fund, L.P.","USD","North America","Buyout",10000000,2015,694500000,3609925,27774,3272811,3225479,-47332,-0.014462186,0.985537814]],
        ["30.06.2016",["ADT Security","USD","North America","Co/Direct Investment",7500000,2016,15000000000,7533677,32200,7501477,7501477,0,0,1]],
        ["30.06.2016",["Bridgepoint Development Capital III, L.P.","GBP**","Western Europe","Buyout",10000000,2016,500000000,0,0,0,0,0,0,0]],
        ["30.06.2016",["OHA Strategic Credit Fund II, LP.","USD","Global","Distressed Debt",10000000,2016,2055820000,0,0,0,0,0,0,0]],
        ["30.06.2016",["PDC Opportunities V LP","USD","Global","Special Situations",5000000,2016,269773377,3345564.2,212780,3050044,3611037,560993,0.183929478,1.183929478]],
        ["30.06.2016",["Total Tranche A","","","",0,0,0,19125582.21,424194.42,18082230,18976441,894211,0.049452474,0]],
        ["31.07.2016",["Bridgepoint Europe IV, L.P.","Euro","Western Europe","Buyout (Secondaries)",3758464,2008,5369537453,3225508.76,551431.78,2886096,3402091,515995,0.178786499,1.178786499]],
        ["31.07.2016",["Saw Mill Capital Partners II, L.P.","USD","North America","Buyout",10000000,2015,108744444,909327,363,909327,899228,-10099,-0.011106016,0.988893984]],
        ["31.07.2016",["BCP Energy Services Fund, L.P.","USD","North America","Buyout",10000000,2015,694500000,3601756,29625,3527230,3479898,-47332,-0.013419029,0.986580971]],
        ["31.07.2016",["ADT Security","USD","North America","Co/Direct Investment",7500000,2016,15000000000,7501477,0,7501477,7501477,0,0,1]],
        ["31.07.2016",["Bridgepoint Development Capital III, L.P.","GBP**","Western Europe","Buyout",6666667,2016,500000000,0,0,0,0,0,0,0]],
        ["31.07.2016",["OHA Strategic Credit Fund II, LP.","USD","Global","Distressed Debt",10000000,2016,2055820000,0,0,0,0,0,0,0]],
        ["31.07.2016",["PDC Opportunities V LP","USD","Global","Special Situations",5000000,2016,269773377,3326814.2,212780,3050044,3611037,560993,0.183929478,1.183929478]],
        ["31.07.2016",["Total Tranche A","","","",0,0,0,18564882.96,794199.78,17874174,18893731,1019557,0.05704079,0]],
        ["31.08.2016",["Bridgepoint Europe IV, L.P.","Euro","Western Europe","Buyout (Secondaries)",3758464,2008,5369537453,3508085.01,613552.43,2886096,3399242,513146,0.177799352,1.177799352]],
        ["31.08.2016",["Saw Mill Capital Partners II, L.P.","USD","North America","Buyout",10000000,2015,108744444,1018829,203043,909327,953449,44122,0.048521599,1.048521599]],
        ["31.08.2016",["BCP Energy Services Fund, L.P.","USD","North America","Buyout",10000000,2015,694500000,3770249,29625,3695723,3545929,-149794,-0.040531717,0.959468283]],
        ["31.08.2016",["ADT Security","USD","North America","Co/Direct Investment",7500000,2016,15000000000,7501477,0,7501477,7501477,0,0,1]],
        ["31.08.2016",["[Bridgepoint Development Capital III, L.P.","GBP**","Western Europe","Buyout",6666667,2016,500000000,0,0,0,0,0,0,0]],
        ["31.08.2016",["OHA Strategic Credit Fund II, LP.","USD","Global","Distressed Debt",10000000,2016,2055820000,0,0,0,0,0,0,0]],
        ["31.08.2016",["PDC Opportunities V LP","USD","Global","Special Situations",5000000,2016,269773377,3862406.1,522142.92,2967845,3872898,905053,0.30495292,1.30495292]],
        ["31.08.2016",["HL Secondary Investment SPV-9 LP","USD","North America","Growth Equity",7113753,2016,106652968,5576209,1418709,5576209,5569539,0,0,0]],
        ["31.08.2016",["OSI Group, LLC","USD","North America","Co/Direct Investment",7500000,2016,15000000,7500000,0,7500000,7500000,0,0,0]],
        ["31.08.2016",["CapitalSouth Partners Florida Sidecar Fund II, L.P.","USD","North America","Mezzanine",8179978,2016,44000000,0,0,1635996,1635996,0,0,1]],
        ["31.08.2016",["Capitala Private Credit Fund V, L.P.","USD","North America","Mezzanine",4298487,2016,28700000,0,0,0,0,0,0,0]],
        ["31.08.2016",["Total Tranche A","","","",0,0,0,32737255.11,2787072.35,32672673,33978530,1305857,0.039967866,0]],
        ["30.09.2016",["Bridgepoint Europe IV, L.P.","Euro","Western Europe","Buyout (Secondaries)",3758464,2008,5369537453,3508085.01,613552.43,2886095.07,3416790,530694.93,0.18387992,1.18387992]],
        ["30.09.2016",["Saw Mill Capital Partners II, L.P.","USD","North America","Buyout",10000000,2015,108744444,1018829,201079,817750,861872,44122,0.053955365,1.053955365]],
        ["30.09.2016",["BCP Energy Services Fund, L.P.","USD","North America","Buyout",10000000,2015,694500000,3770249,29625,3695723,3545929,-149794,-0.040531717,0.959468283]],
        ["30.09.2016",["ADT Security","USD","North America","Co/Direct Investment",7500000,2016,15000000000,7502524.1,0,7502524.1,7615619,113094.9,0.015074247,1.015074247]],
        ["30.09.2016",["Bridgepoint Development Capital III, L.P.","GBP**","Western Europe","Buyout",6666667,2016,500000000,0,0,0,0,0,0,0]],
        ["30.09.2016",["OHA Strategic Credit Fund II, LP.","USD","Global","Distressed Debt",10000000,2016,2055820000,0,0,0,0,0,0,0]],
        ["30.09.2016",["PDC Opportunities V LP","USD","Global","Special Situations",5000000,2016,269773377,3862406.1,522142.92,3276273.26,4181326,905052.74,0.276244583,1.276244583]],
        ["30.09.2016",["HL Secondary Investment SPV-9 LP","USD","North America","Growth Equity",7113753,2016,106652968,5576209,1418709,4157500,5083038,925538,0.222618882,1.222618882]],
        ["30.09.2016",["OSI Group, LLC","USD","North America","Co/Direct Investment",7500000,2016,15000000,7500000,0,7500000,7500000,0,0,1]],
        ["30.09.2016",["CapitalSouth Partners Florida Sidecar Fund II, L.P.","USD","North America","Mezzanine",8179978,2016,44000000,8179977.99,0,8179977.99,8241054,61076.01,0.007466525,1.007466525]],
        ["30.09.2016",["Capitala Private Credit Fund V, L.P.","USD","North America","Mezzanine",4298487,2016,28700000,5600,0,5600,0,-5600,-1,0]],
        ["30.09.2016",["Total Tranche A","","","",0,0,0,40923880.2,2785108.35,38021443.42,40445628,2424184.58,0.063758352,0]],
        ["31.10.2016",["Saw Mill Capital Partners II, L.P.","USD","North America","Buyout",10000000,2015,108744444,1018829,274710,744119,788241,44122,0.05929428,1.04330658]],
        ["31.10.2016",["BCP Energy Services Fund, L.P.","USD","North America","Buyout",10000000,2015,694500000,3996934,42303,3880105,3720076,-160029,-0.041243472,0.941316269]],
        ["31.10.2016",["ADT Security","USD","North America","Co/Direct Investment",7500000,2016,15000000000,7502524.1,0,7502524.1,7590687,88162.9,0.011751099,1.011751099]],
        ["31.10.2016",["Bridgepoint Development Capital III, L.P.","GBP**","Western Europe","Buyout",6666667,2016,500000000,0,0,0,0,0,0,0]],
        ["31.10.2016",["OHA Strategic Credit Fund II, LP.","USD","Global","Distressed Debt",10000000,2016,2055820000,0,0,0,0,0,0,0]],
        ["31.10.2016",["PDC Opportunities V LP","USD","Global","Special Situations",5000000,2016,269773377,3862406.1,522142.92,3276273.26,4181326,905052.74,0.276244583,1.217756186]],
        ["31.10.2016",["HL Secondary Investment SPV-9 LP","USD","North America","Growth Equity",7113753,2016,106652968,5576209,2052359,3523850,4449388,925538,0.262649659,1.16597979]],
        ["31.10.2016",["OSI Group, LLC","USD","North America","Co/Direct Investment",7500000,2016,15000000,7500000,0,7500000,7500000,0,0,1]],
        ["31.10.2016",["CapitalSouth Partners Florida Sidecar Fund II, L.P.","USD","North America","Mezzanine",8179978,2016,44000000,8179977.99,0,8179977.99,8241054,61076.01,0.007466525,1.007466525]],
        ["31.10.2016",["Capitala Private Credit Fund V, L.P.","USD","North America","Mezzanine",4304087,2016,28700000,859697.38,0,865297,859697,-5600,-0.006471766,0.999999558]],
        ["31.10.2016",["Cridiron Capital Fund III, L.P.","USD","North America","Corporate Finance/Buyout",10000000,2015,785000000,0,0,0,0,0,0,0]],
        ["31.10.2016",["Jimmy John's LLC","USD","North America","Co/Direct Investment",7500000,2016,100000000,7500000,0,7500000,7500000,0,0,1]],
        ["31.10.2016",["Total Tranche A","","","",0,0,0,49504662.58,3505067.35,45858241.42,48157199,2298957.58,0.05013183,0]],

        ["30.11.2016",["Bridgepoint Europe IV, L.P.","Euro","Western Europe","Buyout (Secondaries)",3758464,2008,5369537453,3508085.01,613552.43,2886095.07,3212033,325937.93,0.112933885,1.090505338]],
        ["30.11.2016",["Saw Mill Capital Partners II, L.P.","USD","North America","Buyout",10000000,2015,108744444,1018829,274710,744119,801936,57817,0.077698594,1.056748483]],
        ["30.11.2016",["BCP Energy Services Fund, L.P.","USD","North America","Buyout",10000000,2015,694500000,3996934,42303,3880105,3740769,-139336,-0.035910368,0.946493487]],
        ["30.11.2016",["ADT Security","USD","North America","Co/Direct Investment",7500000,2016,15000000000,7503225.1,977563.89,6612819,6612819,0,0,1.011616044]],
        ["30.11.2016",["Bridgepoint Development Capital III, L.P.","GBP**","Western Europe","Corporate Finance/Buyout",6666667,2016,600000000,0,0,0,0,0,0,0]],
        ["30.11.2016",["OHA Strategic Credit Fund II, LP.","USD","Global","Distressed Debt",10000000,2016,2055820000,0,0,0,0,0,0,0]],
        ["30.11.2016",["PDC Opportunities V LP","USD","Global","Special Situations",5000000,2016,269773377,4633484.34,705606.27,3863888.15,4710899,847010.85,0.219212052,1.168991815]],
        ["30.11.2016",["HL Secondary Investment SPV-9 LP","USD","North America","Growth Equity",7113753,2016,106652968,5576209,2702684,2873525,3794008,920483,0.320332344,1.16507326]],
        ["30.11.2016",["OSI Group, LLC","USD","North America","Co/Direct Investment",7500000,2016,15000000,7500000,86069.63,7500000,7500000,0,0,1.011475951]],
        ["30.11.2016",["CapitalSouth Partners Florida Sidecar Fund II, L.P.","USD","North America","Mezzanine",8179978,2016,44000000,8179977.99,0,8179977.99,8241054,61076.01,0.007466525,1.007466525]],
        ["30.11.2016",["Capitala Private Credit Fund V, L.P.","USD","North America","Mezzanine",4304087,2016,28700000,859697.38,0,865297,859697,-5600,-0.006471766,0.999999558]],
        ["30.11.2016",["Cridiron Capital Fund III, L.P.","USD","North America","Corporate Finance/Buyout",10000000,2015,785000000,3104034,0,2931755,3292778,361023,0.123142282,1.060806035]],
        ["30.11.2016",["Jimmy John's LLC","USD","North America","Co/Direct Investment",7500000,2016,100000000,7500000,0,7500000,7500000,0,0,1]],
        ["30.11.2016",["Vertiv (fka Emerson Network Power)","USD","North America","Co/Direct Investment",7500000,2016,160000000,7500000,0,7500000,7500000,0,0,0]],
        ["30.11.2016",["Total Tranche A","","","",0,0,0,60880475.82,5402489.22,55337581.21,57765993,2428411.79,0.043883591,0]],

        ["31.12.2016",["BCP Energy Services Fund, L.P.","USD","North America","Buyout",10000000,2015,694500000,3996934,42303,3880105,3740769,-139336,-0.035910368,0.946493487]],
        ["31.12.2016",["Cridiron Capital Fund III, L.P.","USD","North America","Corporate Finance/Buyout",10000000,2015,785000000,3104034,0,2931755,3292778,361023,0.123142282,1.060806035]],
        ["31.12.2016",["HL Secondary Investment SPV-9 LP","USD","North America","Corporate Finance/Buyout",7113753,2016,106652968,5576209,2702684,2873525,3772413,898888,0.312817184,1.161200558]],
        ["31.12.2016",["Saw Mill Capital Partners II, L.P.","USD","North America","Buyout",10000000,2015,108744444,1018829,274710,744119,801936,57817,0.077698594,1.056748483]],
        ["31.12.2016",["Bridgepoint Europe IV, L.P.","Euro","Western Europe","Buyout (Secondaries)",3758464,2008,5369537453,3599610.76,1308816.51,2282356.74,2540133,257776.26,0.112943019,1.069268253]],
        ["31.12.2016",["Bridgepoint Development Capital III, L.P.","GBP**","Western Europe","Corporate Finance/Buyout",6666667,2016,600000000,0,0,0,0,0,0,0]],
        ["31.12.2016",["Capitala Private Credit Fund V, L.P.","USD","North America","Mezzanine",4304087,2016,28700000,859697.38,0,865297,859697,-5600,-0.006471766,0.999999558]],
        ["31.12.2016",["CapitalSouth Partners Florida Sidecar Fund II, L.P.","USD","North America","Mezzanine",8179978,2016,44000000,8179977.99,0,8179977.99,8241054,61076.01,0.007466525,1.007466525]],
        ["31.12.2016",["OHA Strategic Credit Fund II, LP.","USD","Global","Distressed Debt",10000000,2016,2055820000,0,0,0,0,0,0,0]],
        ["31.12.2016",["PDC Opportunities V LP","USD","Global","Special Situations",5000000,2016,269773377,5044304.85,705606.27,4274708.66,5121720,847011.34,0.198144811,1.155228806]],
        ["31.12.2016",["Jimmy John's LLC","USD","North America","Co/Direct Investment",7500000,2016,100000000,7500000,0,7500000,7500000,0,0,1]],
        ["31.12.2016",["OSI Group, LLC","USD","North America","Co/Direct Investment",7500000,2016,15000000,7500000,86069.63,7500000,7500000,0,0,1.011475951]],
        ["31.12.2016",["ADT Security","USD","North America","Co/Direct Investment",7500000,2016,15000000000,7503225.1,1023186.95,6612819,6612819,0,0,1.017696504]],
        ["31.12.2016",["Vertiv (fka Emerson Network Power)","USD","North America","Co/Direct Investment",7500000,2016,160000000,7500000,0,7500000,7500000,0,0,1]],
        ["31.12.2016",["Total Tranche A","","","",106718661.6,0,0,61382822.08,6143376.36,55144663.39,57483319,2338655.61,0.042409464,0]]
    ];

    private holdingsB = [
        ["29.01.2016", ["Blackstone Capital Partners VII, L.P.","USD","Global","Buyout",55375000,2015,17500000000,0,0,0,0,0,0,0]],
        ["29.01.2016",["Warburg Pincus Private Equity XII, L.P.","USD","Global","Special Situations",21000000,2015,13250000000,567000,0,325500,221592,-103908,-0.319225806,0.680774194]],
        ["29.01.2016",["Advent GPE VIII, L.P.","USD","Global","Buyout",30000000,2016,12000000000,0,0,0,0,0,0,0]],
        ["29.01.2016",["Total Tranche B","","","",0,0,0,567000,0,325500,221592,-103908,-0.319225806,0]],
        ["29.01.2016",["Total Tranche A and Tranche B","","","",0,0,0,7449910.66,1832.49,7007841.6,6474834.93,-533006.67,-0.076058607,0]],
        ["29.01.2016",["Funds available for investments","","","",0,0,0,4766589.34,0,5208658.4,5034894.07,-173764.33,0,0]],
        ["29.01.2016",["Total","","","",157964960.5,0,0,12216500,0,12216500,11509729,0,0,0]],
        ["29.02.2016",["Blackstone Capital Partners VII, L.P.","USD","Global","Buyout",55375000,2015,17500000000,0,0,0,0,0,0,0]],
        ["29.02.2016",["Warburg Pincus Private Equity XII, L.P.","USD","Global","Special Situations",21000000,2015,13250000000,567000,0,567000,528611,-38389,-0.067705467,0.932294533]],
        ["29.02.2016",["Advent GPE VIII, L.P.","USD","Global","Buyout",30000000,2016,12000000000,0,0,0,0,0,0,0]],
        ["29.02.2016",["Total Tranche B","","","",0,0,0,567000,0,567000,528611,-38389,-0.067705467,0]],
        ["29.02.2016",["Total Tranche A and Tranche B","","","",0,0,0,7449910.66,1832.49,7376129.91,6940971.68,-435158.23,-0.058995467,0]],
        ["29.02.2016",["Funds available for investments","","","",0,0,0,4435089.34,0,4508870.09,4652920.32,144050.23,0,0]],
        ["29.02.2016",["Total","","","",157964960.5,0,0,11885000,0,11885000,11593892,0,0,0]],
        ["31.03.2016",["Blackstone Capital Partners VII, L.P.","USD","Global","Buyout",55375000,2015,17500000000,0,0,0,0,0,0,0]],
        ["31.03.2016",["Warburg Pincus Private Equity XII, L.P.","USD","Global","Special Situations",21000000,2015,13250000000,777000,0,777000,670181,-106819,-0.13747619,0.86252381]],
        ["31.03.2016",["Advent GPE VIII, L.P.","USD","Global","Buyout",30000000,2016,12000000000,0,0,0,0,0,0,0]],
        ["31.03.2016",["Total Tranche B","","","",0,0,0,777000,0,777000,670181,-106819,-0.13747619,0]],
        ["31.03.2016",["Total Tranche A and Tranche B","","","",0,0,0,7659910.66,151399.1358,7428211.05,7652045.12,223834.07,0.030132971,0]],
        ["31.03.2016",["Funds available for investments","","","",0,0,0,5556589.34,0,5788288.95,5483789.88,-304499.07,0,0]],
        ["31.03.2016",["Total","","","",157964960.5,0,0,13216500,0,13216500,13135835,0,0,0]],
        ["30.04.2016",["Blackstone Capital Partners VII, L.P.","USD","Global","Buyout",55375000,2015,17500000000,0,0,0,0,0,0,0]],
        ["30.04.2016",["Warburg Pincus Private Equity XII, L.P.","USD","Global","Special Situations",21000000,2015,13250000000,934500,0,934500,815288,-119212,-0.127567683,0.872432317]],
        ["30.04.2016",["Advent GPE VIII, L.P.","USD","Global","Buyout",30000000,2016,12000000000,0,0,0,0,0,0,0]],
        ["30.04.2016",["Total Tranche B","","","",0,0,0,934500,0,934500,815288,-119212,-0.127567683,0]],
        ["30.04.2016",["Total Tranche A and Tranche B","","","",0,0,0,16283542.28,181444.6458,16019964.57,16236332.87,216368.3,0.013506166,0]],
        ["30.04.2016",["Funds available for investments","","","",0,0,0,905563.7211,0,1169141.43,853808.13,-315333.3,0,0]],
        ["30.04.2016",["Total","","","",157964960.5,0,0,17189106,0,17189106,17090141,0,0,0]],
        ["31.05.2016",["Blackstone Capital Partners VII, L.P.","USD","Global","Buyout",55375000,2015,17500000000,111519.1,0,111519,0,-111519,-1,0]],
        ["31.05.2016",["Warburg Pincus Private Equity XII, L.P.","USD","Global","Special Situations",21000000,2015,13250000000,934500,0,934500,801916,-132584,-0.14187694,0.85812306]],
        ["31.05.2016",["Advent GPE VIII, L.P.","USD","Global","Buyout",30000000,2016,12000000000,0,0,0,0,0,0,0]],
        ["31.05.2016",["Total Tranche B","","","",0,0,0,1046019.1,0,1046019,801916,-244103,-0.23336383,0]],
        ["30.06.2016",["Blackstone Capital Partners VII, L.P.","USD","Global","Buyout",55375000,2015,17500000000,111519.1,0,111519,0,-111519,-1,0]],
        ["30.06.2016",["Warburg Pincus Private Equity XII, L.P.","USD","Global","Special Situations",21000000,2015,13250000000,1470000,0,1470000,1269008,-200992,-0.136729252,0.863270748]],
        ["30.06.2016",["Advent GPE VIII, L.P.","USD","Global","Buyout",30000000,2016,12000000000,0,0,0,0,0,0,0]],
        ["30.06.2016",["Total Tranche B","","","",0,0,0,1581519.1,0,1581519,1269008,-312511,-0.197601799,0]],
        ["31.07.2016",["Blackstone Capital Partners VII, L.P.","USD","Global","Buyout",55375000,2015,17500000000,284565.98,0,304480,0,-304480,0,0]],
        ["31.07.2016",["Warburg Pincus Private Equity XII, L.P.","USD","Global","Special Situations",21000000,2015,13250000000,1470000,0,1470000,1269008,-200992,-0.136729252,0.863270748]],
        ["31.07.2016",["Advent GPE VIII, L.P.","USD","Global","Buyout",30000000,2016,12000000000,88901,0,88901,0,-88901,0,0]],
        ["31.07.2016",["Total Tranche B","","","",0,0,0,1843466.98,0,1863381,1269008,-594373,-0.318975561,0]],
        ["31.08.2016",["Blackstone Capital Partners VII, L.P.","USD","Global","Buyout",55375000,2015,17500000000,284565.98,0,304480,0,-304480,0,0]],
        ["31.08.2016",["Warburg Pincus Private Equity XII, L.P.","USD","Global","Special Situations",21000000,2015,13250000000,1732500, ,1732500,1486357,-246143,-0.142073882,0.857926118]],
        ["31.08.2016",["Advent GPE VIII, L.P.","USD","Global","Buyout",30000000,2016,12000000000,88901,0,88901,0,-88901,0,0]],
        ["31.08.2016",["Total Tranche B","","","",0,0,0,2105966.98,0,2125881,1486357,-639524,-0.300827751,0]],
        ["30.09.2016",["Blackstone Capital Partners VII, L.P.","USD","Global","Buyout",55375000,2015,17500000000,304479.98,0,304479.98,0,-304479.98,-1,0]],
        ["30.09.2016",["Warburg Pincus Private Equity XII, L.P.","USD","Global","Special Situations",21000000,2015,13250000000,2341500,0,2341500,2026958,-314542,-0.134333547,0.865666453]],
        ["30.09.2016",["Advent GPE VIII, L.P.","USD","Global","Buyout",30000000,2016,12000000000,630000,0,630000,302162,-327838,-0.520377778,0.479622222]],
        ["30.09.2016",["Total Tranche B","","","",0,0,0,3275979.98,0,3275979.98,2329120,-946859.98,-0.289031064,0]],
        ["31.10.2016",["Warburg Pincus Private Equity XII, L.P.","USD","Global","Special Situations",21000000,2015,13250000000,2341500,0,2341500,2026958,-314542,-0.134333547,0.865666453]],
        ["31.10.2016",["Advent GPE VIII, L.P.","USD","Global","Buyout",30000000,2016,12000000000,630000,0,630000,480070,-149930,-0.237984127,0.762015873]],
        ["31.10.2016",["Total Tranche B","","","",0,0,0,3593591.25,0,3593591.25,2621767,-971824.25,-0.270432607,0]],

        ["30.11.2016",["Blackstone Capital Partners VII, L.P.","USD","Global","Buyout",55375000,2015,17500000000,622091.25,0,622091.25,114739,-507352.25,-0.815559213,0.184440787]],
        ["30.11.2016",["Warburg Pincus Private Equity XII, L.P.","USD","Global","Special Situations",21000000,2015,13250000000,2572500,0,2572500,2241350,-331150,-0.128726919,0.871273081]],
        ["30.11.2016",["Advent GPE VIII, L.P.","USD","Global","Buyout",30000000,2016,12000000000,630000,0,630000,480070,-149930,-0.237984127,0.762015873]],
        ["30.11.2016",["ACON Equity Partners IV, L.P.","USD","North America","Corporate Finance/Buyout",15000000,2016,965000000,0,0,0,0,0,0,0]],
        ["30.11.2016",["Platinum Equity Capital Partners IV, L.P. ","USD","Global","Corporate Finance/Buyout",40000000,2016,4358300000,3553873,0,3553873,3553873,0,0,0]],
        ["30.11.2016",["Total Tranche B","","","",0,0,0,7378464.25,0,7378464.25,6390032,-988432.25,-0.133961786,0]],

        ["31.12.2016",["Advent GPE VIII, L.P.","USD","Global","Buyout",30000000,2016,12000000000,630000,0,630000,480070,-149930,-0.237984127,0.762015873]],
        ["31.12.2016",["Blackstone Capital Partners VII, L.P.","USD","Global","Buyout",55375000,2015,17500000000,808462.66,3539.48,808462.66,298810,-509652.66,-0.630397278,0.37398076]],
        ["31.12.2016",["Platinum Equity Capital Partners IV, L.P. ","USD","Global","Corporate Finance/Buyout",40000000,2016,4358300000,3553873,0,4092232,3913876,-178356,-0.043584039,1.10129878]],
        ["31.12.2016",["ACON Equity Partners IV, L.P.","USD","North America","Corporate Finance/Buyout",15000000,2016,965000000,1466936,0,1466936,1093617,-373319,-0.254488948,0]],
        ["31.12.2016",["Warburg Pincus Private Equity XII, L.P.","USD","Global","Special Situations",21000000,2015,13250000000,2572500,0,3769500,3369707,-399793,-0.106059955,1.309895821]],
        ["31.12.2016",["Total Tranche B","","","",161375000,0,0,9031771.66,0,10767130.66,9156080,-1611050.66,-0.14962674,0]]
    ];

    private cashFlow = [
        ["CapitalSouth Partners Florida Sidecar Fund II, L.P.","30.09.16","Call","Capital Call",-6543982.39,"Actual","04.10.2016","USD",-6543982.39,"USD","Second Closing Purchase",1],
        ["Capitala Private Credit Fund V, L.P.","30.09.16","Call","Cost Basis Adjustment",-5599.81,"Actual","07.10.2016","USD",-5599.81,"USD","Adjustment for Legal Fees",1],
        ["ADT Security","30.09.16","Call","Capital Call",-1047.1,"Actual","06.10.2016","USD",-1047.1,"USD","Fee Payable Amortization",1],
        ["Warburg Pincus Private Equity XII, L.P.","29.09.16","Call","Capital Call",-540601,"Actual","19.09.2016","USD",-540601,"USD","Contribution to fund investments in Reiss, Intelligent Medical Objects, PT Aplikasi Karya Anak Bangsa (Go-Jek), and Stellar Value Chain",1],
        ["Warburg Pincus Private Equity XII, L.P.","29.09.16","Call","Management Fee",-68399,"Actual","19.09.2016","USD",-68399,"USD","4Q16 Mgmt Fees",1],
        ["Advent International GPE VIII-H, L.P.","26.09.16","Call","Capital Call",-328160,"Actual","19.09.2016","USD",-328160,"USD","Contributions to fund investments in Quala and Mattress Firm",1],
        ["Advent International GPE VIII-H, L.P.","26.09.16","Call","Management Fee",-287903,"Actual","19.09.2016","USD",-287903,"USD","Management fees",1],
        ["Advent International GPE VIII-H, L.P.","26.09.2016","Call","Partnership Expenses",-13937,"Actual","19.09.2016","USD",-13937,"USD","Organizational costs & operating expenses",1],
        ["OSI Group, LLC","19.09.2016","Distribution","Income",9402.96,"Actual","04.10.2016","USD",9402.96,"USD","Tax Distribution",1],
        ["Secondary Investment SPV-9, L.P.","15.09.2016","Distribution","Recallable Return of Capital",1418709,"Actual","15.09.2016","USD",1418709,"USD","Proceeds from underlying investments",1],
        ["Saw Mill Capital Partners II, L.P.","12.09.2016","Distribution","Recallable Return of Partnership Expenses",1017,"Actual","15.09.2016","USD",1017,"USD","Return of fees called due to subsequent close",1],
        ["Saw Mill Capital Partners II, L.P.","12.09.2016","Distribution","Late Closing Interest - Distribution",1601,"Actual","15.09.2016","USD",1601,"USD","Subsequent close interest",1],
        ["Saw Mill Capital Partners II, L.P.","12.09.2016","Distribution","Recallable Return of Capital",90560,"Actual","15.09.2016","USD",90560,"USD","Return of Capital called due to subsequent close",1],
        ["PDC Opportunities V, L.P.","09.09.2016","Call","Capital Call",-308427.85,"Actual","06.09.2016","USD",-308427.85,"USD","Investment in Canyon Capital CLO Ltd",1],
        ["CapitalSouth Partners Florida Sidecar Fund II, L.P.","31.08.2016","Call","Capital Call",-1635995.6,"Actual","04.10.2016","USD",-1635995.6,"USD","First Closing Purchase",1],
        ["BCP Energy Services Fund, L.P.","30.08.2016","Call","Partnership Expenses",-89331,"Actual","18.08.2016","USD",-89331,"USD","Partnership Expenses",1],
        ["BCP Energy Services Fund, L.P.","30.08.2016","Call","Capital Call",-72438,"Actual","18.08.2016","USD",-72438,"USD","Investment in United WELD Holdings, LP",1],
        ["BCP Energy Services Fund, L.P.","30.08.2016","Call","Management Fee",-6724,"Actual","18.08.2016","USD",-6724,"USD","Management Fees",1],
        ["PDC Opportunities V, L.P.","26.08.2016","Call","Capital Call",-227164.05,"Actual","23.08.2016","USD",-227164.05,"USD","Investmet in Tryon Park CLO Investment",1],
        ["OSI Group, LLC","15.08.2016","Call","Capital Call",-7500000,"Actual","06.09.2016","USD",-7500000,"USD","Initial Capital Call",1],
        ["Secondary Investment SPV-9, L.P.","15.08.2016","Call","Capital Call",-5569539,"Actual","06.09.2016","USD",-5569539,"USD","Capital called to fund underlying investments",1],
        ["Warburg Pincus Private Equity XII, L.P.","15.08.2016","Call","Capital Call",-235356,"Actual","03.08.2016","USD",-235356,"USD","Conribution to fund investment in Hygiena",1],
        ["Warburg Pincus Private Equity XII, L.P.","15.08.2016","Call","Partnership Expenses",-27144,"Actual","03.08.2016","USD",-27144,"USD","Partnership expenses",1],
        ["Secondary Investment SPV-9, L.P.","15.08.2016","Call","Partnership Expenses",-6670,"Actual","06.09.2016","USD",-6670,"USD","Partnership Expenses",1],
        ["PDC Opportunities V, L.P.","12.08.2016","Distribution","Return of Capital",309362.92,"Actual","11.08.2016","USD",309362.92,"USD","Distribution from underlying investments (Net of $14,835.03 retained for expenses)",1],
        ["Blackstone Capital Partners VII, L.P.","31.07.2016","Call","Deemed GP Contribution",-19914,"Actual","12.10.2016","USD",-19914,"USD","Deemed GP Contribution",1],
        ["Blackstone Capital Partners VII, L.P.","28.07.2016","Call","Management Fee (outside commitment)",-173046.88,"Actual","28.07.2016","USD",-173046.88,"USD","Mgmt Fee for July 1 - September 30, 2016",1],
        ["Blackstone Capital Partners VII, L.P.","28.07.2016","Call","Cost Basis Adjustment",-173046.88,"Actual","28.07.2016","USD",-173046.88,"USD","Mgmt Fee for July 1 - September 30, 2016",1],
        ["BCP Energy Services Fund, L.P.","01.07.2016","Call","Capital Call",-254419,"Actual","20.06.2016","USD",-254419,"USD","Investment in Arrow Environmental Holdings, LP",1],
        ["Bridgepoint Europe IV, L.P.","01.07.2016","Distribution","Return of Capital",462475.01,"Actual","05.07.2016","USD",464706.6235,"EUR","Return of Capital Distribution from investment in Foncia",1],
        ["PDC Opportunities V, L.P.","30.06.2016","Call","Capital Call",-3203608.92,"Actual","07.07.2016","USD",-3203608.92,"USD","Equalisation Amount in respect of Committed Capital",1],
        ["PDC Opportunities V, L.P.","30.06.2016","Call","Management Fee",-75000,"Actual","07.07.2016","USD",-75000,"USD","General Partner's Profit Share",1],
        ["PDC Opportunities V, L.P.","30.06.2016","Call","Late Closing Interest - Contribution",-63989.92,"Actual","07.07.2016","USD",-63989.92,"USD","Additional Amount due",1],
        ["PDC Opportunities V, L.P.","30.06.2016","Call","Partnership Expenses",-2965.36,"Actual","07.07.2016","USD",-2965.36,"USD","Fees & expenses",1],
        ["ADT Security","30.06.2016","Call","Capital Call",-857.9,"Actual","11.07.2016","USD",-857.9,"USD","Fee Payable Amortization",1],
        ["PDC Opportunities V, L.P.","30.06.2016","Call","Management Fee (outside commitment)",18750,"Actual","07.07.2016","USD",18750,"USD","General Partner's Profit Share Rebate",1],
        ["PDC Opportunities V, L.P.","30.06.2016","Call","Cost Basis Adjustment",18750,"Actual","08.07.2016","USD",18750,"USD","General Partner's Profit Share Rebate",1],
        ["PDC Opportunities V, L.P.","30.06.2016","Distribution","Return of Capital",212780.04,"Actual","07.07.2016","USD",212780.04,"USD","Equalisation Amount in respect of Distributions",1],
        ["Warburg Pincus Private Equity XII, L.P.","20.06.2016","Call","Capital Call",-467092,"Actual","13.06.2016","USD",-467092,"USD","Contribution to fund investments in DocuTAP, Inc., UIB (China) Limited, Wanse, Zimperium, and Varo Money, Inc.",1],
        ["Warburg Pincus Private Equity XII, L.P.","20.06.2016","Call","Management Fee",-68408,"Actual","13.06.2016","USD",-68408,"USD","3Q16 Mgmt Fees",1],
        ["Saw Mill Capital Partners II, L.P.","08.06.2016","Distribution","Late Closing Interest - Distribution",363,"Actual","13.06.2016","USD",363,"USD","Subsequent close interest",1],
        ["Saw Mill Capital Partners II, L.P.","08.06.2016","Call","Partnership Expenses",1216,"Actual","13.06.2016","USD",1216,"USD","Return of fees called due to subsequent close",1],
        ["Saw Mill Capital Partners II, L.P.","08.06.2016","Call","Capital Call",108286,"Actual","13.06.2016","USD",108286,"USD","Return of Capital called due to subsequent close",1],
        ["BCP Energy Services Fund, L.P.","06.06.2016","Call","Partnership Expenses",-21342,"Actual","24.05.2016","USD",-21342,"USD","Partnership Expenses",1],
        ["BCP Energy Services Fund, L.P.","06.06.2016","Call","Management Fee",-13687,"Actual","24.05.2016","USD",-13687,"USD","Management Fees",1],
        ["BCP Energy Services Fund, L.P.","06.06.2016","Call","Temporary Return of Capital",1496,"Actual","24.05.2016","USD",1496,"USD","Return of previously contributed capital",1],
        ["BCP Energy Services Fund, L.P.","06.06.2016","Distribution","Late Closing Interest - Distribution",27774,"Actual","24.05.2016","USD",27774,"USD","Late closing interest income",1],
        ["BCP Energy Services Fund, L.P.","06.06.2016","Call","Capital Call",130546,"Actual","24.05.2016","USD",130546,"USD","Investment in United WELD Holdings LP, Brown & ROOT Indsutrial Services Holdings, LLC, and ATC Environmental Holdings, LP",1],
        ["Blackstone Capital Partners VII, L.P.","31.05.2016","Call","Management Fee (outside commitment)",-111519.1,"Actual","24.05.2016","USD",-111519.1,"USD","Mgmt Fee for May 4 - June 30, 2016",1],
        ["Blackstone Capital Partners VII, L.P.","31.05.2016","Call","Cost Basis Adjustment",-111519.1,"Actual","15.06.2016","USD",-111519.1,"USD","Mgmt Fee for May 4 - June 30, 2016",1],
        ["ADT Security","27.04.2016","Call","Capital Call",-6579755,"Actual","13.04.2016","USD",-6579755,"USD","Intitial Capital Call - Equity Portion",1],
        ["ADT Security","27.04.2016","Call","Capital Call",-920000,"Actual","13.04.2016","USD",-920000,"USD","Intitial Capital Call - Debt Portion",1],
        ["ADT Security","27.04.2016","Call","Partnership Expenses",-33064.1,"Actual","13.04.2016","USD",-33064.1,"USD","Expense Reserve",1],
        ["ADT Security","27.04.2016","Distribution","Income",32200,"Actual","13.04.2016","USD",32200,"USD","Sponsor Commitment Fee Payable",1],
        ["Saw Mill Capital Partners II, L.P.","18.04.2016","Call","Capital Call",-1007514,"Actual","06.04.2016","USD",-1007514,"USD","Call to fund investment in Wolf-Gordon",1],
        ["Saw Mill Capital Partners II, L.P.","18.04.2016","Call","Partnership Expenses",-11315,"Actual","06.04.2016","USD",-11315,"USD","Call for fees and interest",1],
        ["Warburg Pincus Private Equity XII, L.P.","05.04.2016","Call","Capital Call",-145107,"Actual","23.03.2016","USD",-145107,"USD","Contribution to fund investments in Terra Energy",1],
        ["Warburg Pincus Private Equity XII, L.P.","05.04.2016","Call","Partnership Expenses",-12393,"Actual","23.03.2016","USD",-12393,"USD","Partnership Expenses",1],
        ["Bridgepoint Europe IV, L.P.","24.03.2016","Distribution","Return of Capital",151077.42,"Actual","24.03.2016","USD",153102.4685,"EUR","Return of Capital Distribution from investment in Borawind",1.1047],
        ["Warburg Pincus Private Equity XII, L.P.","21.03.2016","Call","Capital Call",-141570,"Actual","08.03.2016","USD",-141570,"USD","Contribution to fund investments in US Tech Company and Terra Energy",1],
        ["Warburg Pincus Private Equity XII, L.P.","21.03.2016","Call","Management Fee",-68430,"Actual","08.03.2016","USD",-68430,"USD","2Q16 Mgmt Fees",1],
        ["Warburg Pincus Private Equity XII, L.P.","17.02.2016","Call","Capital Call",-241500,"Actual","10.02.2016","USD",-241500,"USD","Contribution to fund investment in Gemini",1],
        ["BCP Energy Services Fund, L.P.","05.02.2016","Call","Capital Call",-75216,"Actual","25.01.2016","USD",-75216,"USD","Investment in ATC Group Holdings, LLC and return of previously contributed capital",1],
        ["BCP Energy Services Fund, L.P.","05.02.2016","Call","Partnership Expenses",-35771,"Actual","25.01.2016","USD",-35771,"USD","Partnership Expenses",1],
        ["BCP Energy Services Fund, L.P.","05.02.2016","Call","Management Fee",-17082,"Actual","25.01.2016","USD",-17082,"USD","Management Fees",1],
        ["BCP Energy Services Fund, L.P.","05.02.2016","Distribution","Late Closing Interest - Distribution",1851,"Actual","25.01.2016","USD",1851,"USD","Late closing interest income",1],
        ["Bridgepoint Europe IV, L.P.","22.01.2016","Call","Capital Call",-3499647.5,"Actual","04.02.2016","USD",-3497739.517,"EUR","Initial Capital Call",1.0882],
        ["Bridgepoint Europe IV, L.P.","22.01.2016","Call","Late Closing Interest - Contribution",-8437.51,"Actual","08.04.2016","USD",-8432.910976,"EUR","Interest expense",1.0882],
        ["Warburg Pincus Private Equity XII, L.P.","28.12.2015","Call","Capital Call",-31500,"Actual","17.12.2015","USD",-31500,"USD","Investment in Embassy Industrial Parks and other pending transactions.",1],
        ["Warburg Pincus Private Equity XII, L.P.","21.12.2015","Call","Capital Call",-190091.81,"Actual","17.12.2015","USD",-190091.81,"USD","Investment in ITG Energy Research, Yunnaio, and other pending transactions",1],
        ["Warburg Pincus Private Equity XII, L.P.","21.12.2015","Call","Management Fee",-94151.55,"Actual","17.12.2015","USD",-94151.55,"USD","4Q15 and 1Q16 Management fees",1],
        ["Warburg Pincus Private Equity XII, L.P.","21.12.2015","Call","Partnership Expenses",-9756.64,"Actual","17.12.2015","USD",-9756.64,"USD","Partnership Expenses",1],
        ["BCP Energy Services Fund, L.P.","03.11.2015","Call","Capital Call",-1810866,"Actual","23.11.2015","USD",-1810866,"USD","Investment in Fund",1],
        ["BCP Energy Services Fund, L.P.","03.11.2015","Call","Capital Call",-852408,"Actual","23.11.2015","USD",-852408,"USD","Capital call for Brown & Root Industrial Holdings, LLC",1],
        ["BCP Energy Services Fund, L.P.","03.11.2015","Call","Capital Call",-276316,"Actual","23.11.2015","USD",-276316,"USD","Capital call for United WELD Holdings, LP",1],
        ["BCP Energy Services Fund, L.P.","03.11.2015","Call","Management Fee",-196453,"Actual","23.11.2015","USD",-196453,"USD","Management fees",1],
        ["BCP Energy Services Fund, L.P.","03.11.2015","Call","Partnership Expenses",-105712,"Actual","23.11.2015","USD",-105712,"USD","Partnership expenses (Organizational, Operational, Placement, Travel and Admin)",1],
        ["BCP Energy Services Fund, L.P.","03.11.2015","Call","Late Closing Interest - Contribution",-74526,"Actual","23.11.2015","USD",-74526,"USD","Late Closing Interest Expense",1],
        ["Saw Mill Capital Partners II, L.P.","31/10/2016","Distribution","Late Closing Interest - Distribution",1975,"Actual","10/28/2016","USD",1975,"USD","Subsequent close interest",1],
        ["Saw Mill Capital Partners II, L.P.","31/10/2016","Distribution","Recallable Return of Capital",72813,"Actual","10/28/2016","USD",72813,"USD","Return of Capital called due to subsequent close",1],
        ["BCP Energy Services Fund, L.P.","21/10/2016","Call","Capital Call",-217990,"Actual","10/10/2016","USD",-217990,"USD","Investment in United WELD Holdings, LP",1],
        ["Blackstone Capital Partners VII, L.P.","21/10/2016","Call","Capital Call",-89932.89,"Actual","10/17/2016","USD",-89932.89,"USD","Investment is Primexx Energy Partners",1],
        ["Blackstone Capital Partners VII, L.P.","21/10/2016","Call","Partnership Expenses",-60603.82,"Actual","10/17/2016","USD",-60603.82,"USD","Partnership Expenses",1],
        ["Blackstone Capital Partners VII, L.P.","21/10/2016","Call","Partnership Expenses (outside commitment)",-13941.68,"Actual","10/17/2016","USD",-13941.68,"USD","Organizational expenses",1],
        ["Blackstone Capital Partners VII, L.P.","21/10/2016","Call","Cost Basis Adjustment",-13941.68,"Actual","10/17/2016","USD",-13941.68,"USD","Organizational expenses",1],
        ["BCP Energy Services Fund, L.P.","21/10/2016","Call","Partnership Expenses",-7199,"Actual","10/10/2016","USD",-7199,"USD","Partnership Expenses",1],
        ["BCP Energy Services Fund, L.P.","21/10/2016","Distribution","Realized Gain/Loss",3036,"Actual","10/10/2016","USD",3036,"USD","Gain on sale of assets to the Executive Fund",1],
        ["BCP Energy Services Fund, L.P.","21/10/2016","Distribution","Recallable Return of Capital",40807,"Actual","10/10/2016","USD",40807,"USD","Recallable Return of Capital - Investment in the Fund",1],
        ["Jimmy John's","19/10/2016","Call","Capital Call",-7500000,"Actual","11/8/2016","USD",-7500000,"USD","Initial Capital Call",1],
        ["Secondary Investment SPV-9, L.P.","19/10/2016","Distribution","Recallable Return of Capital",633650,"Actual","10/20/2016","USD",633650,"USD","Proceeds from underlying investments",1],
        ["Blackstone Capital Partners VII, L.P.","17/10/2016","Call","Cost Basis Adjustment",-173046.88,"Actual","10/10/2016","USD",-173046.88,"USD","Mgmt Fee for October 1 - December 31, 2016",1],
        ["Blackstone Capital Partners VII, L.P.","17/10/2016","Call","Management Fee (outside commitment)",-173046.88,"Actual","10/10/2016","USD",-173046.88,"USD","Mgmt Fee for October 1 - December 31, 2016",1],
        ["Capitala Private Credit Fund V, L.P.","14/10/2016","Call","Capital Call",-429848.69,"Actual","10/4/2016","USD",-429848.69,"USD","Capital Call to originate investments",1],
        ["Capitala Private Credit Fund V, L.P.","3/10/2016","Call","Capital Call",-429848.69,"Actual","9/23/2016","USD",-429848.69,"USD","Capital call for initial capitalization of the fund",1]
    ];

}