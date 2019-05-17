import {Component, AfterViewInit, ViewChild} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {CommonTableau} from "./common-tableau.component";
import {GoogleChartComponent} from "../google-chart/google-chart.component";

declare var google:any;
declare var $: any;

@Component({
    selector: 'monitoring-private-equity',
    templateUrl: 'view/monitoring-private-equity-fund.component.html',
    styleUrls: [],
    providers: [],
})
export class MonitoringPrivateEquityFundComponent extends GoogleChartComponent {

    activeTab = "TARR1_OVERVIEW";

    constructor(
    ) {
        super();

    }

    drawGraph(){
        this.drawSummary();
        this.drawVintageDiversification();

        // PORTFOLIO DIVERSIFICATION
        this.drawGeographicDiversification();
        this.drawStrategyDiversification();
        this.drawDiversificationByInvestments();

        // PORTFOLIO EXPOSURE
        this.drawGeographicExposure();
        this.drawSectorExposure();

        // PORTFOLIO DESCRIPTION
        this.drawPortfolioDescription();

    }


    // Summary -----------------------------------------------------------------
    drawSummary(){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "in M$");
        data.addColumn("string", "Tranche A");
        data.addColumn("string", "Tranche B");
        data.addColumn("string", "Total");
        data.addRows(this.getSummaryRowData());

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {
                tableCell: '',
            }
        };

        var chart = this.createTableChart(document.getElementById('summary'));
        chart.draw(data, options);
    }

    private getSummaryRowData(){
        //return [
        //    ["Size","225", "375", "600"],
        //    ["Committed amount", "100", "146", "246"],
        //    ["Invested amount", "42", "4", "46"],
        //    ["Distributed amount", "4", "-", "4"],
        //    ["# of investments", "13", "4*", "17"]
        //];

        return [
            ["Size","225", "375", "600"],
            ["Committed amount", "220", "375", "595"],
            ["Invested amount", "148", "182", "330"],
            ["Distributed amount", "41", "19", "60"],
            ["# of investments", "32", "11", "43"]
        ];
    }


    drawVintageDiversification(){
        //var data = google.visualization.arrayToDataTable([
        //    ['Year', 'Tranche A', 'Tranche B', 'Total'],
        //    ['1997-2007',7.11,0,7.11],
        //    ['2008', 4.04, 0, 4.04],
        //    ['2014', 10, 0, 10],
        //    ['2015', 15, 21, 36],
        //    ['2016', 52.51, 165.38, 217.89],
        //    ['2017', 43.3, 0, 43.3]
        //]);

        var data = google.visualization.arrayToDataTable([
            ['Year', 'Tranche A', 'Tranche B', 'Total'],
            ['1997-2007',10,0,10],
            ['2008', 4, 0, 4],
            ['2012', 0, 0, 0],
            ['2013', 3, 0, 3],
            ['2014', 0, 0, 0],
            ['2015', 11, 21, 32],
            ['2016', 68, 140, 208],
            ['2017', 85, 139, 224],
            ['2018', 39, 75, 114]
        ]);

        var view = new google.visualization.DataView(data);
        view.setColumns([0, 1,
            { calc: "stringify",
                sourceColumn: 1,
                type: "string",
                role: "annotation" },
            2,{ calc: "stringify",
                sourceColumn: 2,
                type: "string",
                role: "annotation" },
            3, { calc: "stringify",
                sourceColumn: 3,
                type: "string",
                role: "annotation" }]);

        var options = {
            title: 'Vintage Diversification',
            width: '100%',
            height: 300,
            animation: {
                duration: 1200,
                easing: 'out',
                startup: true,
            },
            chartArea: {
                height: '50%',
                width: '90%',
            },
            legend: { position: 'bottom' },
            chart: { //subtitle: 'popularity by percentage'
            },
            axes: {
                x: {
                    0: { side: 'bottom', label: ''} // Top x-axis.
                }
            },
            bar: { groupWidth: "90%" },
            colors: ['#548ec1', '#c15469', '#75d17f']
        };

        var chart = new google.visualization.ColumnChart(document.getElementById('vintageDiversification'));

        chart.draw(view, options);
    }


    // PORTFOLIO DIVERSIFICATION  ----------------------------------------------
    drawGeographicDiversification(){

        var data = new google.visualization.DataTable();
        data.addColumn('string', 'diversification');
        data.addColumn('number', 'value');
        data.addColumn({type: 'string', role: 'tooltip'});
        //data.addRows([
        //    ["North America", 162.13, "162.13"],
        //    ["Europe", 12.34, "12.34"],
        //    ["Global", 275.38, "275.38\nGlobal focused funds:\nTranche A - 5%\nTranche B - 95%"],
        //]);

        data.addRows([
            // TODO: ACTUAL AMOUNTS
            ["Row", 1, "1"],
            ["North America", 40, "40"],
            ["Europe", 8, "8"],
            ["Global", 51, "51\nGlobal focused funds:\nTranche A - 5%\nTranche B - 95%"],
        ]);

        var options = {
            width: '100%',
            height: '100%',
            chartArea: {
                height: '70%',
                width: '90%',
            },
            title: 'Geographic Diversification',
            slices: {
                0: {color: '#548ec1'},
                1: {color: '#c15469'},
                2: {color: '#75d17f'}
            },
            pieHole: 0.3,
        };
        var chart = new google.visualization.PieChart(document.getElementById('geographicDiversification'));
        chart.draw(data, options);
    }
    //drawGeographicDiversification(){
    //    var data = google.visualization.arrayToDataTable([
    //        ['Diversification', 'North America', 'Western Europe', 'Global', { role: 'annotation' } ],
    //        ["Geographic Diversification", 94.6, 12.7, 161.4, '']
    //    ]);
    //
    //    var options = {
    //        width: '100%',
    //        height: '100%',
    //        animation: {
    //            duration: 500,
    //            easing: 'out',
    //            startup: true,
    //        },
    //        chartArea: {width: '50%'},
    //        legend: { position: 'right', maxLines: 3 },
    //        bar: { groupWidth: '30%' },
    //        colors: ['#548ec1', 'grey', 'red'],
    //        isStacked: true,
    //    };
    //
    //    var chart = new google.visualization.ColumnChart(document.getElementById('geographicDiversification'));
    //
    //    chart.draw(data, options);
    //}

    drawStrategyDiversification(){
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'diversification');
        data.addColumn('number', 'value');
        data.addColumn({type: 'string', role: 'tooltip'});
        //data.addRows([
        //    ["Special Situations", 41, "41"],
        //    ["Distressed Debt", 10, "10"],
        //    ["Co/Direct Investment", 22.5, "22.5"],
        //    ["Mezzanine", 12.5, "12.5"],
        //    ["Corporate Finance/Buyout", 363.86, "Buyout equity focused funds:\nTranche A - 23%\nTranche B - 77%"],
        //]);

        data.addRows([
            ["Venture Capital/Growth", 49, "49"],
            ["Special Situations/Credit", 20, "20"],
            ["Special Situations/Distressed", 65, "65"],
            ["Special Situations/Mezzanine", 20, "20"],
            ["Buyout", 442, "Buyout equity focused funds:\nTranche A - 37%\nTranche B - 63%"],
        ]);

        var options = {
            title: "Strategic diversification",
            //titleTextStyle: {
            //    color: 'black',    // any HTML string color ('red', '#cc00cc')
            //    //fontName: <string>, // i.e. 'Times New Roman'
            //    fontSize: 14, // 12, 18 whatever you want (don't specify px)
            //    bold: true,    // true or false
            //    italic: false   // true of false
            //},
            animation: {
                duration: 500,
                easing: 'out',
                startup: true,
            },
            //width: 600,
            //height: 400,
            bar: {groupWidth: "80%"},
            colors: ['#75d17f'],
            chartArea: {
                height: '70%',
                width: '60%',
            },
            legend: { position: "none" },
        };
        var view = new google.visualization.DataView(data);
        view.setColumns([0, 1,
            {calc: "stringify",
                sourceColumn: 1,
                type: "string",
                role: "annotation" },
            2]);
        var chart = new google.visualization.BarChart(document.getElementById('strategyDiversification'));
        chart.draw(view, options);
    }

    //drawStrategyDiversification(){
    //    var data = google.visualization.arrayToDataTable([
    //        ['Diversification', 'Buyout Equity', 'Growth Equity', 'Co-investment' , 'Mezzanine', 'Distressed Debt', 'Special situations', { role: 'annotation' } ],
    //        ["Strategic Diversification", 74, 9, 6, 5, 4, 2, '']
    //    ]);
    //
    //    var options = {
    //        //width: 500,
    //        //height: 300,
    //        animation: {
    //            duration: 500,
    //            easing: 'out',
    //            startup: true,
    //        },
    //        chartArea: {width: '50%'},
    //        legend: { position: 'right', maxLines: 3 },
    //        bar: { groupWidth: '30%' },
    //        colors: ['#548ec1', 'grey', 'red', 'purple', 'turquoise', 'orange'],
    //        isStacked: true,
    //    };
    //
    //    var chart = new google.visualization.ColumnChart(document.getElementById('strategyDiversification'));
    //
    //    chart.draw(data, options);
    //}

    drawDiversificationByInvestments(){
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'diversification');
        data.addColumn('number', 'percent');
        data.addColumn({type: 'string', role: 'tooltip'});
        //data.addRows([
        //    ["Primary", 84, "376.18\nInvestment in primary funds:\nTranche A - 20%\nTranche B - 80%"],
        //    ["Secondary", 10, "43.63"],
        //    ["Co Investment", 7, "30.03"],
        //]);

        data.addRows([
            ["Primary", 81, "81\nInvestment in primary funds:\nTranche A - 22%\nTranche B - 78%"],
            ["Secondary", 11, "11"],
            ["Co Investment", 8, "8"],
        ]);

        var options = {
            title: "Diversification by Investments",
            //titleTextStyle: {
            //    color: 'black',    // any HTML string color ('red', '#cc00cc')
            //    //fontName: <string>, // i.e. 'Times New Roman'
            //    fontSize: 14, // 12, 18 whatever you want (don't specify px)
            //    bold: true,    // true or false
            //    italic: false   // true of false
            //},
            animation: {
                duration: 500,
                easing: 'out',
                startup: true,
            },
            //width: 600,
            //height: 400,
            chartArea: {
                height: '70%',
                width: '90%',
            },
            bar: {groupWidth: "80%"},
            colors: ['#75d17f'],
            legend: { position: "none" },
        };
        var view = new google.visualization.DataView(data);
        view.setColumns([0, 1,
            {calc: "stringify",
                sourceColumn: 1,
                type: "string",
                role: "annotation" },
            2]);
        var chart = new google.visualization.ColumnChart(document.getElementById('diversificationByInvestments'));

        chart.draw(view, options);
    }

    //drawDiversificationByInvestments(){
    //    var data = google.visualization.arrayToDataTable([
    //        ['Diversification', 'Primaries', 'Secondaries', 'Co-investments' , { role: 'annotation' } ],
    //        ["Diversification by Investments", 79.2, 11.7, 9.1, '']
    //    ]);
    //
    //    var options = {
    //        //width: 500,
    //        //height: 300,
    //        animation: {
    //            duration: 500,
    //            easing: 'out',
    //            startup: true,
    //        },
    //        chartArea: {width: '50%'},
    //        legend: { position: 'right', maxLines: 3 },
    //        bar: { groupWidth: '30%' },
    //        colors: ['#548ec1', 'grey', 'red'],
    //        isStacked: true,
    //    };
    //
    //    var chart = new google.visualization.ColumnChart(document.getElementById('diversificationByInvestments'));
    //
    //    chart.draw(data, options);
    //}


    // PORTFOLIO EXPOSURE
    drawGeographicExposure(){
        //var data = google.visualization.arrayToDataTable([
        //    ['Geography', 'Exposure'],
        //    ['North America', 64.2 ],
        //    ['Western Europe', 28.7],
        //    ['Middle East', 1.0],
        //    ['Asia', 3.1],
        //    ['Eastern Europe', 1.6],
        //    ['South America', 0.3],
        //    ['Other', 0.2]
        //]);

        var data = google.visualization.arrayToDataTable([
            ['Geography', 'Exposure'],
            ['North America', 76 ],
            ['Western Europe', 16],
            ['Asia', 6],
            ['Other', 2]
        ]);

        var view = new google.visualization.DataView(data);
        view.setColumns([0, 1,
            { calc: "stringify",
                sourceColumn: 1,
                type: "string",
                role: "annotation" }]);

        var options = {
            title: 'Geographic Exposure**',
            animation: {
                duration: 500,
                easing: 'out',
                startup: true,
            },
            chartArea: {
                height: '50%',
                width: '90%',
            },
            //width: 900,
            legend: { position: 'none' },
            chart: { //subtitle: 'popularity by percentage'
            },

            axes: {
                x: {
                    0: { side: 'bottom', label: ''} // Top x-axis.
                }
            },
            bar: { groupWidth: "60%" },
            colors: ['#548ec1']
        };

        var chart = new google.visualization.ColumnChart(document.getElementById('geographicExposure'));

        chart.draw(view, options);
    }

    drawSectorExposure(){
        //var data = google.visualization.arrayToDataTable([
        //    ['Sector', 'Exposure'],
        //    ['Industrials', 8.7],
        //    ['Materials', 2.63],
        //    ['Energy', 3.53],
        //    ['Information Technology', 45.12],
        //    ['FoF Holding', 2.07],
        //    ['Financials', 3.81],
        //    ['Consumer Discretionary', 15.97],
        //    ['Health Care', 11.02],
        //    ['Real Estate', 0.11],
        //    ['Consumer Staples', 2.7],
        //    ['Utilities', 0.52]
        //]);

        var data = google.visualization.arrayToDataTable([
            ['Sector', 'Exposure'],
            ['Industrials', 21],
            ['Information Technology', 21],
            ['Consumer Discretionary', 167],
            ['Health Care', 11],
            ['Communication Services', 9],
            ['Financials', 7],
            ['Consumer Staples', 6],
            ['Energy', 3],
            ['Materials', 3],
            ['Real Estate', 2],
            ['FoF Holding', 1],
            ['Utilities', 0],
            ['Other investments', 0]
        ]);

        var options = {
            title: 'Sector Exposure**',
            animation: {
                duration: 500,
                easing: 'out',
                startup: true,
            },

            chartArea: {
                height: '50%',
                width: '90%',
            },
            //width: 900,
            legend: { position: 'none' },
            chart: { //subtitle: 'popularity by percentage'
            },
            axes: {
                x: {
                    0: { side: 'bottom', label: ''} // Top x-axis.
                }
            },
            bar: { groupWidth: "60%" },
            colors: ['#548ec1']
        };

        var view = new google.visualization.DataView(data);
        view.setColumns([0, 1,
            { calc: "stringify",
                sourceColumn: 1,
                type: "string",
                role: "annotation" }]);

        var chart = new google.visualization.ColumnChart(document.getElementById('sectorExposure'));

        chart.draw(view, options);
    }


    // PORTFOLIO DESCRIPTION

    drawPortfolioDescription(){}

    public getTrancheAInvestments(){
        var investments = [];
        //investments.push(["A", "A", "A", "A", "A", "A", "A", "A", "A", "A","A", "A", 4]);
        //investments.push([null, "A", "A", "A", "A", "A", "A", "A", "A", "A","A", "A",]);
        //investments.push([null, "A", "A", "A", "A", "A", "A", "A", "A", "A","A", "A",]);
        //investments.push([null, "A", "A", "A", "A", "A", "A", "A", "A", "A","A", "A",]);

        investments.push(["Small Cap Buyout", "Gridiron Capital Fund III, L.P", 2016, 875.6, 10, 7.15, 2.76, 9.33, "41%", "0.39x", "1.69x", "0.82", "Gridiron Capital","Targeting control investments in lower middle-market U.S. businesses and consistent focus on undermanaged businesses in core sectors of expertise", 8]);
        investments.push([null, "BCP Energy Services Fund, L.P.",2015,703.3,10.00,9.03,2.63,10.01,"17%","0.29x","1.40x", "1.32","Bernhard Capital Partners","Target middle-market energy services companies with the   focus on North America with a concentration in Louisiana and surrounding regions"]);
        investments.push([null, "BDC III, L.P.",2017,605.00,8.77, 4.26,0.44, 5.84,"57","0.10x","1.47x","0.53","Bridgepoint Development Capital","Targets small-cap transactions across core European markets (the U.K., the Nordics and France)"]);
        investments.push([null, "Saw Mill Capital Partners II, L.P.",2016,340,10.00,3.78,0.33,3.66,"5%","0.09x","1.06x","0.78","Saw Mill Capital","Targets manufacturing, commercial services and specialized distribution companies in the lower middle market across  U.S.-based businesses"]);
        investments.push([null, "TZP Capital Partners III, L.P.",2017,565.00,10.00,2.20,0.00,2.20,"0%","0.00x","1.00x","0.99", "TZP Group LLC","The Fund will target companies in industries such as franchising, outsourced business and IT services, marketing and media services, primarily in North America"]);
        investments.push([null, "Aurora Equity Partners V, L.P.",2017,1188.48,10.00,5.26,0.00,5.09,"-3%","0.00x","0.97x","0.90","Aurora Capital Group","Targets small-cap transactions in industrial services and distribution, specialty manufacturing and software and tech-enabled services in North America"]);
        investments.push([null, "ECI 11, L.P.",2018,717.50,	9.62,1.23,0.00,1.17,"-6%","0.00x","0.95x","","ECI Capital Partners","ECI invests in the UK middle-market, targeting companies primarily in its core sectors of TMT, Consumer and Business services"]);
        investments.push([null, "Keensight V SLP",2018,750.00,14.29,0.11,0.00,0.00,"0%","0.00x","0.00x","8,10","Keensight Capital","Keensight targets fast-growing, profitable, cash flow positive companies in the Information technology/Internet and Healthcare/Wellbeing sectors in Western Europe, with an international revenue base"]);
        investments.push(["Mid Cap Buyout", "MidOcean Partners V, L.P.",2017, 1000,7.50,2.51 , 0, 2.5,"0%","0x","1x", "0.49","MidOcean US Advisor, L.P.","Targets middle market transactions in the consumer and business industries, as well as media services in North America", 1]);
        investments.push(["Distressed Debt", "OHA Strategic Credit Fund II, L.P.",2017,2700.02,10.00,1.50,0.01,1.68, "24%","0.01x ", "1.13x","0.11","Oak Hill Advisors, L.P.","Invest in stressed and distressed debt of companies, as well as in restructuring situations across North America and Europe",1]);
        //investments.push([null, "OSI Group, LLC", 2016, "", 7.5, 7.5,  0.0, 7.5, "0%", "0.0x", "1.0x", "Prudential Capital Group, L.P."]);

        investments.push(["Co-investment", "OSI Group, LLC",2016,15.00,7.50,7.50,1.39,9.08,"15%","0.19x","1.39x","7.51","Prudential Capital Group, L.P.","OSI Group is a global processor of poultry, beef, pork, and other food products to the food service industry, primarily QSRs, retail, and branded food companies",6]);
        investments.push([null, "ADT Security",2016,	163.85,7.53,7.50,2.23,5.54,"2%","0.30x","1.04x","7.95","Apollo Global Management","A deal in the home and commercial security business"]);
        investments.push([null, "Jimmy John's",2016,107.98,7.50,7.50,0.81,7.45,"4%","0.11x","1.10x","8.10","Roark Capital Group, Inc.","Jimmy John's Is a franchised sandwich restaurant chain specializing in delivery"]);
        investments.push([null, "Vertiv (fka Emerson Network Power)",2016,1203.95,7.50,7.50,6.40,4.47,"33%","0.85x","1.45x","","Platinum Equity Capital Partners","Vertiv is a global leader in designing, manufacturing, and servicing mission-critical infrastructure technologies for data centers, communication networks, and commercial / industrial environments"]);
        investments.push([null, "International Car Wash Ltd.",2017,102.52,7.50,7.50,	0.00,	8.76,	"11%",	"0,00x","1,17x"	,"7,79","Roark Capital Group, Inc.","ICWG is the largest car wash company in the world, which operates express-service style car washes"]);
        investments.push([null, "Expereo",	2018,	19.00,	7.62,	7.62,	0.00,	7.29,"-4%","0,00x","0,96x","","Apax France","Expereo is an aggregator of global Internet managed access that partners with telecom carriers by providing last-mile Internet links for multi-national corporations"]);
        investments.push(["Secondary", "PDC Opportunities V, L.P.",2016,	269.77,	5.00,	5.07,	2.41,	4.11,"14%","0,47x","1,28x","","Pearl Diver Capital","Secondaries (Control CLO equity)",15]);
        investments.push([null, "Bridgepoint Europe IV, L.P.",2008,	4835.00,	4.10,	3.57,	2.37,	1.87,	"8%",	"0,66x","1,19x",	"0,17","Bridgepoint Capital Ltd.","Growth oriented pan-European Middle Market Fund with generalist approach to sector exposure"]);
        investments.push([null, "Capitala Private Credit Fund V, L.P.",	2016,	44.00,	4.30,	3.44,	0.15,	3.60,	"10%",	"0,04x",	"1,09x","","Capital South Partners","Structured deal based on secondary purchase of credit assets and commitment in Capitala Fund V"]);
        investments.push([null, "CapitalSouth Partners Florida Sidecar Fund II, L.P.",	2016,	47.57,	8.18,	8.18,	3.46,6.26,	"9%",	"0,42x",	"1,19x","","",""]);
        investments.push([null, "Secondary Investment SPV-9, L.P.",	1997-2007,	106.65,	7.11,	5.87,	4.35,	1.77,	"4%",	"0,74x",	"1,04x",	"1,18",	"Hamilton Lane","Secondaries of following fundsLevine Leichtman II, III, IV, Deep Value Fund Acon Bastion II"]);
        investments.push([null, "Centre Lane Credit Partners II, L.P.",	2017,	153.00,	15.00,15.00,	4.84,	13.70,	"16%",	"0,32x","1,24x",	"",	"Centre Partners Management LLC","Looks to invest in first and second lien loans in the middle market	"]);
        investments.push([null, "IDG China Media Fund II L.P. (Project Rainbow)",	2008,	151.50,	0.35,	0.34,	0.06,	0.47,	"30%",	"0,16x",	"1,58x","",		"IDG China","China Venture capital focused on the media industry	"]);
        investments.push([null, "IDG Technology Venture Investment IV, L.P. (Project Rainbow)",2007,	260.00,	2.45,	2.44,	0.18,	4.56,	"48%",	"0,07x",	"1,94x", "","IDG China","China Venture capital (seed stage) in the following industries: e-commerce, mobile, enterprise, fintech, media, healthcare, consumer"]);
        investments.push([null, "IDG Technology Venture Investment V, L.P. (Project Rainbow)",2013,	351.75,	3.06,	2.69,	0.02,	3.98,	"28%",	"0,01x",	"1,48x","","IDG China",	"China Venture capital (seed stage) in the following industries: mobile, financial services, consumer, business services		"]);
        investments.push([null, "IDG Venture Vietnam, LP (Project Rainbow)",2005,	101.00,	0.48,	0.48,	0.21,	0.90,	"63%",	"0,43x",	"2,32x",	"0,01",	"IDG Vietnam",	"Venture capital in Vietnam		"]);
        investments.push([null, "IDG Ventures India Fund I LLC (Project Rainbow)",2007,	150.00,	0.46,	0.46,	0.57,	0.29,	"72%",	"1,24x",	"1,88x",	"0,02","IDG India",	"Venture capital in India		"]);
        investments.push([null, "IDG Ventures India Fund II LLC (Project Rainbow)",2012,	69.30,	0.16,	0.18,	0.10,	0.21,	"57%",	"0,57x",	"1,73x",	"0,01",	"IDG India",	"Venture capital in India"]);
        investments.push([null, "IDG Ventures India Fund III LLC (Project Rainbow)",	2015,	123.20,	0.54,	0.32,	0.00,	0.39,"17%",	"0,00x",	"1,22x",	"",	"IDG India",	"Venture capital in India		"]);
        investments.push([null, "Avista Capital Partners IV, L.P.",	2018,	226.40,	7.50,	6.17,	3.53,	4.94,	"103%",	"0,57x",	"1,37x",	"0,78",	"Avista Capital Partners",	"Healthcare mid-market buyout/growth fund"]);
        investments.push([null, "Webster Capital II-A, L.P.",	2017,150.00,	8.50,	7.61,	0.14,	8.23,	"8%",	"0,02x",	"1,10x",	"1,50","Webster Capital Partners","The Fund has a lower middle market buyout strategy and primarily targets the healthcare sector		"]);

        //investments.push(["Total for Tranche A","", "", "", 100, 42, 4, 41, "", "", "", ""]);
        return investments;
    }

    public getTrancheBInvestments() {
        var investments = [];
        investments.push(["Large Buyout", "Blackstone Capital Partners VII, L.P.",2016,18000,55.38,31.78,1.26,35.41,"15%","0.04x","1.15x","0.99","Blackstone Group","Focuses on the large buyout space globally with the focus on US market", 5]);
        investments.push([null, "Advent International GPE VIII-H, L.P.",2016,930.24,30.00,24, 0,25.9,"6%","0.00x","1.08x","0.72","Advent International","Focus on investments in mid-sized to large companies globally"]);
        investments.push([null,"Vista Equity Partners Fund VI, L.P.",2017,10869.59,25.00,27.36,6.19,26.54,"15%","0.23x","1.20x","1.10","Vista Equity Partners","Focus on investments in upper middle-market and large cap companies in North America"]);
        investments.push([null,"Silver Lake Partners V, L.P.",2017, 14500,34.00,13.65,0,12.77,"-11%","0.00x","0.94x","1.27","Silver Lake Partners","The Fund pursues a variety of transactions across investment type, sub-sector, geography and ownership percentage within the technology and tech-enabled space"]);
        investments.push([null,"Apollo Investment Fund IX, L.P.",2017,24000,80.00,7.66,0,4.26,"-44%","0.00x","0.56x","0.00","Apollo Global Management","Global value oriented, contrarian approach focused on strong assets, corporate carve-outs and distressed opportunities"]);
        investments.push(["Mid Cap Buyout", "Platinum Equity Capital Partners IV, L.P.",2016,6500,40.00,27.28,8.20,27.05,"27%","0.30x","1.29x","1.38","Platinum Equity Capital Partners","Fund invests across sectors, primarily in US, and make selective investments in Europe and ROW with deep operational expertise. Target companies are carve-outs, corporate orphans, unmanaged businesses", 2]);
        investments.push([null, "ACON Equity Partners IV, L.P.",2016,1065.18,15.00,7.91,1.03,5.72,"-18%","0.13x","0.85x","0.92","ACON Investments, L.L.C.","The Fund pursues a middle-market private equity investment strategy targeting control-oriented deep-value, complex transactions"]);
        investments.push(["Growth Equity", "Warburg Pincus Private Equity XII, L.P.",2015,13386.70,21.00,15.28,0.92,16.69,"10%","0.06x","1.15x","0.16","Warburg Pincus LLC","Growth-oriented investment strategy across five core sectors", 2]);
        investments.push([null,"Insight Venture Partners X, L.P.",2018,5459.56,19.63,9.86,0.00,9.91,	"1%",	"0,00x",	"1,01x",	"0,15",	"Insight Venture Partners",	"Focus on two main deal types - minority growth investments in bootstrapped companies and venture buyouts"]);
        investments.push(["Distressed", "Clearlake Capital Partners V, L.P.",2018,	2500.00,	25.00,	12.01,	0.38,	13.21,	"24%",	"0,03x"	,"1,13x","0,97",	"Clearlake Capital Partners",	"Distressed, special situations, and deep-value private equity investments in middle market with a goal of obtaining control or significant influence", 2]);
        investments.push([null,"GSO Capital Solutions Fund III, L.P.",2018,	7450.00,	30.00,	5.02,	1.51,	3.57,	"4%",	"0,30x",	"1,01x",	"0,80",	"GSO Capital Partners",	"Focused on investing in stressed and distressed companies, including private rescue debt financings, structured equity and common equity investments and select public debt securities"]);
        //investments.push(["Total for Tranche B","", "", "", 146, 4, "", 3, "", "", "", ""]);

        return investments;
    }

}