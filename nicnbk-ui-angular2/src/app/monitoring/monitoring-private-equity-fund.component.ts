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
            ['1997-2007',7.11,0,7.11],
            ['2008', 4.04, 0, 4.04],
            ['2014', 10, 0, 10],
            ['2015', 15, 21, 36],
            ['2016', 52.51, 165.38, 217.89],
            ['2017', 43.3, 0, 43.3]
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
        data.addRows([
            ["North America", 144.63, "144.63"],
            ["Europe", 12.34, "12.34"],
            ["Global", 161.38, "161.38\nGlobal focused funds:\nTranche A - 9%\nTranche B - 91%"],
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
        data.addRows([
            ["Special Situations", 41, "41"],
            ["Distressed Debt", 10, "10"],
            ["Co/Direct Investment", 22.5, "22.5"],
            ["Mezzanine", 12.5, "12.5"],
            ["Corporate Finance/Buyout", 232.4, "Buyout equity focused funds:\nTranche A - 29%\nTranche B - 71%"],
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
        data.addRows([
            ["Primary", 74, "234.68\nInvestment in primary funds:\nTranche A - 21%\nTranche B - 79%"],
            ["Secondary", 9, "28.63"],
            ["Co Investment", 17, "55.03"],
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
        var data = google.visualization.arrayToDataTable([
            ['Geography', 'Exposure'],
            ['North America', 64.2 ],
            ['Western Europe', 28.7],
            ['Middle East', 1.00],
            ['Asia', 3.1],
            ['Eastern Europe', 1.6],
            ['South America', 0.3],
            ['Other', 0.2]
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
        var data = google.visualization.arrayToDataTable([
            ['Sector', 'Exposure'],
            ['Industrials', 8.5],
            ['Materials', 2.6],
            ['Energy', 3.4],
            ['Information Technology', 44],
            ['Financials', 5.5],
            ['Consumer Discretionary', 15.6 ],
            ['Health Care', 10.8],
            ['Real Estate', 0.01],
            ['Consumer Staples', 2.6],
            ['Utilities', 0.5]
        ]);

        var options = {
            title: 'Sector Exposure',
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

        investments.push(["Small Cap Buyout", "Gridiron Capital Fund III, L.P", 2015, 850, 10, 3.1, 0.37, 2.96, "11%", "0.12x", "1.08x", "Gridiron Capital","Target middle-market energy services companies with the   focus on North America with a concentration in Louisiana and surrounding regions", 5]);
        investments.push([null, "BCP Energy Services Fund, L.P.",2014,703.3,10.00,6.94,0.07,7.06,"4%","0.01x","1.03x","Bernhard Capital Partners","Target middle-market energy services companies with the   focus on North America with a concentration in Louisiana and surrounding regions"]);
        investments.push([null, "BDC III, L.P.",2017,600.00,8.30, , ,0.00," ","0.00x"," ","Bridgepoint Development Capital","Targets small-cap transactions across core European markets (the U.K., the Nordics and France)"]);
        investments.push([null, "Saw Mill Capital Partners II, L.P.",2016,318.13,10.00,1.02,0.28,0.79,"5%","0.27x","1.05x","Saw Mill Capital","Targets manufacturing, commercial services and specialized distribution companies in the lower middle market across  U.S.-based businesses  "]);
        investments.push([null, "TZP Capital Partners III, L.P.",2017,565.00,10.00, , ,0.00," ","0.00x"," ","TZP Group LLC","The Fund will target companies in industries such as franchising, outsourced business and IT services, marketing and media services, primarily in North America"]);

        investments.push(["Distressed Debt", "OHA Strategic Credit Fund II, L.P.",2017,2700.02,10.00, , ,0.00, ,"0.00x", ,"Oak Hill Advisors, L.P.","Invest in stressed and distressed debt of companies, as well as in restructuring situations across North America and Europe",1]);
        //investments.push([null, "OSI Group, LLC", 2016, "", 7.5, 7.5,  0.0, 7.5, "0%", "0.0x", "1.0x", "Prudential Capital Group, L.P."]);

        investments.push(["Co-investment", "OSI Group, LLC",2016,15.00,7.50,7.50,0.10,7.50,"1%","0.01x","1.01x","Prudential Capital Group, L.P.","OSI Group is a global processor of poultry, beef, pork, and other food products to the food service industry, primarily QSRs, retail, and branded food companies",4]);
        investments.push([null, "ADT Security",2016,164.14,7.53,7.50,1.81,5.96,"9%","0.24x","1.04x","Apollo Management","The deal in the home and commercial security business"]);
        investments.push([null, "Jimmy John's",2016,100.00,7.50,7.50, ,7.50,"0%","0.00x","1.00x","Roark Capital Group, Inc.","Jimmy John's Is a franchised sandwich restaurant chain specializing in delivery"]);
        investments.push([null, "Vertiv (fka Emerson Network Power)",2016,1203.95,7.50,7.50,1.93,5.57,"-10%","0.26x","1.00x","Platinum Equity Capital Partners","Vertiv is a global leader in designing, manufacturing, and servicing mission-critical infrastructure technologies for data centers, communication networks, and commercial / industrial environments"]);

        investments.push(["Secondary", "PDC Opportunities V, L.P.",2015,269.77,5.00,5.03,0.94,5.24,"25%","0.19x","1.23x","Pearl Diver Capital","Secondaries (Control CLO equity)",6]);
        investments.push([null, "Bridgepoint Europe IV, L.P.",2008,4835.00,4.04,3.57,1.38,2.85,"10%","0.39x","1.19x","Bridgepoint Capital Ltd.","Growth oriented pan-European Middle Market Fund with generalist approach to sector exposure"]);
        investments.push([null, "Capitala Private Credit Fund V, L.P.",2016,44.00,4.30,0.87, ,0.86,"2%","0.00x","0.99x","Capital South Partners","Structured deal based on secondary purchase of credit assets and commitment in Capitala Fund V"]);
        investments.push([null, "CapitalSouth Partners Florida Sidecar Fund II, L.P.",2016,47.57,8.18,8.18, ,8.24,"3%","0.00x","1.01x","Capital South Partners",""]);
        investments.push([null, "Secondary Investment SPV-9, L.P.",2016,106.65,7.11,5.58,2.79,3.68,"25%","0.50x","1.16x","Hamilton Lane","Secondaries of following funds \n• Levine Leichtman II, III, IV, Deep Value Fund • Acon Bastion II"]);
        investments.push([null,"Centre Lane Credit Partners II, L.P.",2017,147.50,15.00, , ,0.00,"0%","0.00x", ,"Centre Partners Management LLC","Looks to invest in first and second lien loans in the middle market"]);
        //investments.push(["Total for Tranche A","", "", "", 100, 42, 4, 41, "", "", "", ""]);
        return investments;
    }

    public getTrancheBInvestments() {
        var investments = [];
        investments.push(["Large Buyout", "Blackstone Capital Partners VII, L.P.",2016,18000,55.38,6.46,0.01,5.76,"-31%","0.00x","0.89x","Blackstone Group","Focuses on the large buyout space globally with the focus on US market", 3]);
        investments.push([null, "Advent International GPE VIII-H, L.P.",2016,930,30.00,7.02, ,6.42,"-30%","0.00x","0.91x","Advent International","Focus on investments in mid-sized to large companies globally"]);
        investments.push([null,"Vista Equity Partners Fund VI, L.P.",2016,10870,25.00, , ,0.00,"-3%","0.00x", ,"Vista Equity Partners","Focus on investments in upper middle-market and large cap companies in North America"]);
        investments.push(["Mid Cap Buyout", "Platinum Equity Capital Partners IV, L.P.",2016,4413,40.00,3.46,0.30,2.30,"-6%","0.09x","0.75x","Platinum Equity Capital Partners","Fund invests across sectors, primarily in US, and make selective investments in Europe and ROW with deep operational expertise. Target companies are carve-outs, corporate orphans, unmanaged businesses", 2]);
        investments.push([null, "ACON Equity Partners IV, L.P.",2016,965,15.00,1.62,0.00,1.09,"-29%","0.00x","0.68x","ACON Investments, L.L.C.","The Fund pursues a middle-market private equity investment strategy targeting control-oriented deep-value, complex transactions"]);
        investments.push(["Growth Equity", "Warburg Pincus Private Equity XII, L.P.",2016,12471,21.00,5.02,0.06,4.51,"-13%","0.01x","0.91x","Warburg Pincus LLC","Growth-oriented investment strategy across five core sectors", 1]);
        //investments.push(["Total for Tranche B","", "", "", 146, 4, "", 3, "", "", "", ""]);

        return investments;
    }

}