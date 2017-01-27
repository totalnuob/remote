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
        return [
            ["Size","225", "375", "600"],
            ["Committed amount", "100", "146", "246"],
            ["Invested amount", "42", "4", "46"],
            ["Distributed amount", "4", "-", "4"],
            ["# of investments", "13", "4*", "17"]
        ];
    }


    drawVintageDiversification(){
        var data = google.visualization.arrayToDataTable([
            ['Year', 'Tranche A', 'Tranche B', 'Total'],
            ['1997-2007', 7.1, 0, 7.1 ],
            ['2008', 4.2, 0, 4.2],
            ['2015', 19.3, 76.4, 95.7],
            ['2016', 69.4, 70.0, 139.4]
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
        var data = google.visualization.arrayToDataTable([
            ['Diversification', 'Global', 'North America', 'Europe' , { role: 'annotation' } ],
            ["Geographic Diversification", 65.5, 29.3, 5.2, '']
        ]);

        var options = {
            width: '100%',
            height: '100%',
            animation: {
                duration: 500,
                easing: 'out',
                startup: true,
            },
            chartArea: {width: '50%'},
            legend: { position: 'right', maxLines: 3 },
            bar: { groupWidth: '30%' },
            colors: ['#548ec1', 'grey', 'red'],
            isStacked: true,
        };

        var chart = new google.visualization.ColumnChart(document.getElementById('geographicDiversification'));

        chart.draw(data, options);
    }

    drawStrategyDiversification(){
        var data = google.visualization.arrayToDataTable([
            ['Diversification', 'Buyout Equity', 'Growth Equity', 'Co-investment' , 'Mezzanine', 'Distressed Debt', 'Special situations', { role: 'annotation' } ],
            ["Strategic Diversification", 74, 9, 6, 5, 4, 2, '']
        ]);

        var options = {
            //width: 500,
            //height: 300,
            animation: {
                duration: 500,
                easing: 'out',
                startup: true,
            },
            chartArea: {width: '50%'},
            legend: { position: 'right', maxLines: 3 },
            bar: { groupWidth: '30%' },
            colors: ['#548ec1', 'grey', 'red', 'purple', 'turquoise', 'orange'],
            isStacked: true,
        };

        var chart = new google.visualization.ColumnChart(document.getElementById('strategyDiversification'));

        chart.draw(data, options);
    }

    drawDiversificationByInvestments(){
        var data = google.visualization.arrayToDataTable([
            ['Diversification', 'Primaries', 'Secondaries', 'Co-investments' , { role: 'annotation' } ],
            ["Diversification by Investments", 79.2, 11.7, 9.1, '']
        ]);

        var options = {
            //width: 500,
            //height: 300,
            animation: {
                duration: 500,
                easing: 'out',
                startup: true,
            },
            chartArea: {width: '50%'},
            legend: { position: 'right', maxLines: 3 },
            bar: { groupWidth: '30%' },
            colors: ['#548ec1', 'grey', 'red'],
            isStacked: true,
        };

        var chart = new google.visualization.ColumnChart(document.getElementById('diversificationByInvestments'));

        chart.draw(data, options);
    }


    // PORTFOLIO EXPOSURE
    drawGeographicExposure(){
        var data = google.visualization.arrayToDataTable([
            ['Geography', 'Exposure'],
            ['North America', 59 ],
            ['Western Europe', 36],
            ['Eastern Europe', 3],
            ['Middle East', 1],
            ['Asia Pacific', 1]
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
            ['Consumer Discretionary', 27 ],
            ['Industrials', 26],
            ['Materials', 15],
            ['Health Care', 11],
            ['Energy', 9],
            ['Financials (Real Estate)', 5],
            ['Information Technology', 3],
            ['Financials', 2],
            ['Consumer Staples', 1]
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

        investments.push(["Small Cap Buyout", "Gridiron Capital Fund III", 2016, 785, 10, null, null, 0, null, null, null, "Gridiron Capital", 4]);
        investments.push([null, "BCP Energy Services Fund", 2015, 750, 10, 4, 0.1, 3.7, "-6%", "0.0x", "0.9x", "Bernhard Capital Partners"]);
        investments.push([null, "BDC III",2016,	632, 8.67, "", "", 0, "", "", "", "Bridgepoint Development Capital"]);
        investments.push([null, "Saw Mill Capital Partners II",	2016,300, 10, 1, 0.3, 0.8, "5%", "0.3x", "1.0x", "Saw Mill Capital"]);

        investments.push(["Distressed Debt", "OHA Strategic Credit Fund II", 2016, 2000, 10, "", "", 0, "", "", "", "Oak Hill Advisors, L.P.", 1]);
        //investments.push([null, "OSI Group, LLC", 2016, "", 7.5, 7.5,  0.0, 7.5, "0%", "0.0x", "1.0x", "Prudential Capital Group, L.P."]);
        investments.push(["Co-investment", "OSI Group, LLC", 2016, "", 7.5, 7.5,  0.0, 7.5, "0%", "0.0x", "1.0x", "Prudential Capital Group, L.P.", 3]);
        investments.push([null, "ADT Security", 2016, "", 7.5, 7.5, "", 7.6, "2%", "0.0x", "1.0x", "Apollo Management"]);
        investments.push([null,"Jimmy John's","2016","",7.5,"","",0,"","","","Roark Capital Group, Inc."]);
        investments.push(["Secondary", "PDC Opportunities V","2015","250",5,3.9,0.5,4.2,"27%","0.1x","1.2x","Pearl Diver Capital", 5]);
        investments.push([null, "Bridgepoint Europe IV","2008","4 866",4.22,3.5,0.6,3.4,"16%","0.2x","1.1x","Bridgepoint Capital Ltd."]);
        investments.push([null, "Capitala Private Credit Fund V","2014-2015","350",4.3,0.9,"",0.9,"0%","0.0x","1.0x","Capital South Partners"]);
        investments.push([null, "CapitalSouth Partners Florida Sidecar Fund II","2016","29",8.18,8.2,"",8.2,"1%","0.0x","1.0x",""]);
        investments.push([null, "Secondary Investment SPV-9","1997-2007","",7.1,5.6,2.1,4.5,"20%","0.4x","1.2x","Hamilton Lane"]);
        //investments.push(["Total for Tranche A","", "", "", 100, 42, 4, 41, "", "", "", ""]);
        return investments;
    }

    public getTrancheBInvestments() {
        var investments = [];
        investments.push(["Large Buyout","Blackstone Capital Partners VII", 2015, 17500, 55.4, 0.6, "", 0.1, "", 0, "0.1x", "Blackstone Group", 2]);
        investments.push([null, "Advent International GPE VIII-H", 2016, "12 000", 30, 0.6, "", 0.3, "-52%", 0, "0.5x", "Advent International"]);
        investments.push(["Mid Cap Buyout", "Platinum Equity Capital Partners IV", 2016, "4 358", 40, "", "", 0, "", "", "", "Platinum Equity Capital Partners", 1]);
        investments.push(["Growth Equity", "Warburg Pincus Private Equity XII", 2015, "13 250", 21, 2.6, "", 2.2, "-28%", 0, "0.9x", "Warburg Pincus LLC", 1]);
        //investments.push(["Total for Tranche B","", "", "", 146, 4, "", 3, "", "", "", ""]);

        return investments;
    }

}