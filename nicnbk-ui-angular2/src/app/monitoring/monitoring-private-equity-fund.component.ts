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

    constructor(
    ) {
        super();

    }

    drawGraph(){
        //this.drawSummary();
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
    //drawSummary(){
    //    var data = new google.visualization.DataTable();
    //    data.addColumn("string", "in M$");
    //    data.addColumn("string", "Tranche A");
    //    data.addColumn("string", "Tranche B");
    //    data.addColumn("string", "Total");
    //    data.addRows(this.getSummaryRowData());
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
    //    var chart = this.createTableChart(document.getElementById('summary'));
    //    chart.draw(data, options);
    //}

    //private getSummaryRowData(){
    //    return [
    //        ["Size","225", "375", "600"],
    //        ["Committed amount", "100", "146", "246"],
    //        ["Invested amount", "42", "4", "46"],
    //        ["Distributed amount", "4", "-", "4"],
    //        ["# of investments", "13", "4*", "17"]
    //    ];
    //}


    drawVintageDiversification(){
        var data = google.visualization.arrayToDataTable([
            ['Year', 'Tranche A', 'Tranche B', 'Total'],
            ['2008', 3.96, 0, 3.96],
            ['2014', 10, 0, 10],
            ['2015', 15, 21, 36],
            ['2016', 59.63, 140.38, 200],
            ['2017', 18.2, 0, 18.2]
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
            animation: {
                duration: 1200,
                easing: 'out',
                startup: true,
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
        data.addRows([
            ["North America", 94.6, "94.6"],
            ["Western Europe", 12.2, "12.2"],
            ["Global", 161.4, "161.4\nGlobal focused funds:\nTranche A - 9%\nTranche B - 91%"],
        ]);

        var options = {
            width: '100%',
            height: '100%',
            chartArea: {
                width: '100%'
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
        data.addRows([
            ["Special Situations", 26, "26"],
            ["Distressed Debt", 10, "10"],
            ["Co/Direct Investment", 22.5, "22.5"],
            ["Mezzanine", 12.48, "12.48"],
            ["Corporate Finance/Buyout", 197.18, "Buyout equity focused funds:\nTranche A - 29%\nTranche B - 71%"],
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
            colors: ['#548ec1'],
            chartArea: {left:100},
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
        data.addRows([
            ["Primary", 78, "209.58\nInvestment in primary funds:\nTranche A - 23%\nTranche B - 77%"],
            ["Secondary", 11, "28.55"],
            ["Co Investment", 11, "30.03"],
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
            bar: {groupWidth: "80%"},
            colors: ['#548ec1'],
            chartArea: {left:100},
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
        var data = google.visualization.arrayToDataTable([
            ['Geography', 'Exposure'],
            ['North America', 89.74 ],
            ['Western Europe', 8.54],
            ['Eastern Europe', 1.21],
            ['Asia Pacific', 0.50]
        ]);

        var view = new google.visualization.DataView(data);
        view.setColumns([0, 1,
            { calc: "stringify",
                sourceColumn: 1,
                type: "string",
                role: "annotation" }]);

        var options = {
            title: 'Geographic Exposure',
            animation: {
                duration: 500,
                easing: 'out',
                startup: true,
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
        var data = google.visualization.arrayToDataTable([
            ['Geography', 'Exposure'],
            ['Consumer Discretionary', 11.14 ],
            ['Health Care', 5.59],
            ['Materials', 7.83],
            ['Energy', 3.1],
            ['Industrials', 27.11],
            ['Real Estate', 0.07],
            ['Utilities', 0.10],
            ['Information Technology', 1.74],
            ['Financials', 12.67],
            ['Consumer Staples', 23.84],
            ['FoF Holding', 6.82]
        ]);

        var options = {
            title: 'Sector Exposure',
            animation: {
                duration: 500,
                easing: 'out',
                startup: true,
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

        investments.push(["Small Cap Buyout", "Gridiron Capital Fund III, L.P", 2015, 760.27, 10, 3.1, 0.37, 2.92, "4%", "0.12x", "1.06x", "Gridiron Capital","Target middle-market energy services companies with the   focus on North America with a concentration in Louisiana and surrounding regions", 4]);
        investments.push([null, "BCP Energy Services Fund, L.P.",2014,694.50,10.00,5.07,0.07,4.74,"-2%","0.01x","0.95x","Bernhard Capital Partners","Target middle-market energy services companies with the   focus on North America with a concentration in Louisiana and surrounding regions"]);
        investments.push([null, "BDC III, L.P.",2017,605.00,8.20, , ,0.00," ","0.00x"," ","Bridgepoint Development Capital","Targets small-cap transactions across core European markets (the U.K., the Nordics and France)"]);
        investments.push([null, "Saw Mill Capital Partners II, L.P.",2016,289.80,10.00,1.02,0.28,0.80,"7%","0.27x","1.06x","Saw Mill Capital","Targets manufacturing, commercial services and specialized distribution companies in the lower middle market across  U.S.-based businesses  "]);

        investments.push(["Distressed Debt", "OHA Strategic Credit Fund II, L.P.",2017,2700.02,10.00, , ,0.00, ,"0.00x", ,"Oak Hill Advisors, L.P.","Invest in stressed and distressed debt of companies, as well as in restructuring situations across North America and Europe",1]);
        //investments.push([null, "OSI Group, LLC", 2016, "", 7.5, 7.5,  0.0, 7.5, "0%", "0.0x", "1.0x", "Prudential Capital Group, L.P."]);

        investments.push(["Co-investment", "OSI Group, LLC",2016,15.00,7.50,7.50,0.10,7.50,"1%","0.01x","1.01x","Prudential Capital Group, L.P.","OSI Group is a global processor of poultry, beef, pork, and other food products to the food service industry, primarily QSRs, retail, and branded food companies",4]);
        investments.push([null, "ADT Security",2016,164.14,7.53,7.50,1.81,5.82,"2%","0.24x","1.02x","Apollo Management","The deal in the home and commercial security business"]);
        investments.push([null, "Jimmy John's",2016,100.00,7.50,7.50, ,7.50,"0%","0.00x","1.00x","Roark Capital Group, Inc.","Jimmy John's Is a franchised sandwich restaurant chain specializing in delivery"]);
        investments.push([null, "Vertiv (fka Emerson Network Power)",2016,160.00,7.50,7.50, ,7.50,"0%","0.00x","1.00x","Platinum Equity Capital Partners","Vertiv is a global leader in designing, manufacturing, and servicing mission-critical infrastructure technologies for data centers, communication networks, and commercial / industrial environments"]);

        investments.push(["Secondary", "PDC Opportunities V, L.P.",2015,269.77,5.00,5.03,0.94,4.88,"21%","0.19x","1.16x","Pearl Diver Capital","Secondaries (Control CLO equity)",5]);
        investments.push([null, "Bridgepoint Europe IV, L.P.",2008,4835.00,3.96,3.57,1.28,2.91,"10%","0.36x","1.17x","Bridgepoint Capital Ltd.","Growth oriented pan-European Middle Market Fund with generalist approach to sector exposure"]);
        investments.push([null, "Capitala Private Credit Fund V, L.P.",2016,44.00,4.30,0.87, ,0.86,"0%","0.00x","0.99x","Capital South Partners","Structured deal based on secondary purchase of credit assets and commitment in Capitala Fund V"]);
        investments.push([null, "CapitalSouth Partners Florida Sidecar Fund II, L.P.",2016,47.57,8.18,8.18, ,8.24,"1%","0.00x","1.01x","",""]);
        investments.push([null, "Secondary Investment SPV-9, L.P.",2016,106.65,7.11,5.58,2.79,3.68,"25%","0.50x","1.16x","Hamilton Lane","Secondaries of following funds \n• Levine Leichtman II, III, IV, Deep Value Fund • Acon Bastion II"]);
        //investments.push(["Total for Tranche A","", "", "", 100, 42, 4, 41, "", "", "", ""]);
        return investments;
    }

    public getTrancheBInvestments() {
        var investments = [];
        investments.push(["Large Buyout", "Blackstone Capital Partners VII, L.P.",2016,18000,55.38,5.49,0.01,4.79,"-41%","0.00x","0.87x","Blackstone Group","Focuses on the large buyout space globally with the focus on US market", 3]);
        investments.push([null, "Advent International GPE VIII-H, L.P.",2016,930,30.00,2.82, ,2.48,"-32%","0.00x","0.88x","Advent International","Focus on investments in mid-sized to large companies globally"]);
        investments.push([null, "ACON Equity Partners IV, L.P.",2016,965,15.00,1.62,0.00,1.09,"-29%","0.00x","0.68x","ACON Investments, L.L.C.","The Fund pursues a middle-market private equity investment strategy targeting control-oriented deep-value, complex transactions"]);
        investments.push(["Mid Cap Buyout", "Platinum Equity Capital Partners IV, L.P.",2016,6500,40.00,4.30, ,4.12,"-4%","0.00x","0.96x","Platinum Equity Capital Partners","Fund invests across sectors, primarily in US, and make selective investments in Europe and ROW with deep operational expertise. Target companies are carve-outs, corporate orphans, unmanaged businesses", 1]);
        investments.push(["Growth Equity", "Warburg Pincus Private Equity XII, L.P.",2016,12471,21.00,4.26, ,3.90,"-13%","0.00x","0.91x","Warburg Pincus LLC","Growth-oriented investment strategy across five core sectors", 1]);
        //investments.push(["Total for Tranche B","", "", "", 146, 4, "", 3, "", "", "", ""]);

        return investments;
    }

}