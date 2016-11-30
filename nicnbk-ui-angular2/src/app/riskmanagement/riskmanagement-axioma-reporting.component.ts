import {Component} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {GoogleChartComponent} from "../google-chart/google-chart.component";
import {TableChartDto, TableColumnDto} from "../google-chart/table-chart.dto";
import {RiskManagementReportService} from "./riskmanagement.report.service";

declare var google:any;
declare var $: any;

@Component({
    selector: 'monitoring-axioma-reporting',
    templateUrl: 'view/riskmanagement-axioma-reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class RiskManagementAxiomaReportingComponent extends GoogleChartComponent {

    //ngAfterViewInit():void {
    //    this.tableau_func();
    //}

    //private tableChart;
    //private options;
    //private data;
    //private chart;

    constructor(
        private reportService: RiskManagementReportService
    ){
        super();

        //this.getTableChart();

        // TODO: wait/sync on data loading
        // TODO: sync on subscribe results
        //this.waitSleep(700);

    }

    drawGraph(){
        this.drawLiquidity();
        this.drawSingularity();
        this.drawTarragon();
    }

    // LIQUIDITY -------------------------------------
    private drawLiquidity(){
        var data = new google.visualization.DataTable();
        for(var i = 0; i < this.types.length; i ++){
            data.addColumn(this.types[i], this.columns[i]);
        }
        data.addRows(this.getLiquidityRowData());

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {
                headerRow: 'googleChartTable',
                tableCell: 'googleChartTable',
            }
        };

        var chart = this.createTableChart(document.getElementById('report_liquidity'));
        chart.draw(data, options);
    }

    private getLiquidityRowData(){
        return this.liquidityData;
    }

    // SINGULARITY ------------------------------------
    private drawSingularity(){
        var data = new google.visualization.DataTable();
        for(var i = 0; i < this.types.length; i ++){
            data.addColumn(this.types[i], this.columns[i]);
        }
        data.addRows(this.getSingularityRowData());

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {
                headerRow: 'googleChartTable',
                tableCell: 'googleChartTable',
            }
        };

        var chart = this.createTableChart(document.getElementById('report_singularity'));
        chart.draw(data, options);
    }

    private getSingularityRowData(){
        return this.singularityData;
    }

    // TARRAGON ---------------------------------------
    private drawTarragon(){
        var data = new google.visualization.DataTable();
        for(var i = 0; i < this.types.length; i ++){
            data.addColumn(this.types[i], this.columns[i]);
        }
        data.addRows(this.getTarragonRowData());

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {
                headerRow: 'googleChartTable',
                tableCell: 'googleChartTable',
            }
        };

        var chart = this.createTableChart(document.getElementById('report_tarragon'));
        chart.draw(data, options);
    }

    private getTarragonRowData(){
        return this.tarragonData;
    }



    private types = ["string", "string", "string", "string", "string", "number", "number", "string", "number", "number", "number", "number", "number", "number"];

    private columns = ["PortfolioName", "Category Name", "ClientId", "Issuer Full Name ",
        "Country", "PV, USD", "Weights, %", "Rating", "YTM, %", "Option Adjusted Spread, %",
        "Eff. Duration", "Spread Duration", "VaR, Historical % @95 monthly", "Interest Rates +50bp, loss %"];

    private liquidityData = [
        ["NBK_Liquidity",null,null,null,null,493075205.20,74.65,null,1.1174,0.6892,0.6868,0.6708,0.0262,-0.3464],
        [null,"Bond",null,null,null,410997456.80,62.22,null,0.9973,0.6123,0.6282,0.5103,0.0231,-0.3127],
        [null,null,"FR0011616416","CAISSE DES DEPOTS ET CONSIGNATIONS - 0.875% STR BOND DUE 7TH NOV 2016","FR",8001280.00,1.21,"A_A",0.7067,0.4276,0.1041,0.1065,0.0011,-0.0524],
        [null,null,"US06120TAA60","BANK OF CHINA LTD - STR Due 2024 RegS","CN",3233790.00,0.49,"B_B_B",3.9060,2.3602,6.7027,6.2101,1.1783,-3.3466],
        [null,null,"US064159AM82","BANK OF NOVA SCOTIA - 2.55% Sr Nts Due 2017","CA",20083940.00,3.04,"A_A",1.0564,0.7585,0.2849,0.2850,0.0081,-0.1431],
        [null,null,"US06738EAN58","BARCLAYS PLC - 4.375% Sr Nts Due 2026","GB",11586568.00,1.75,"B_B_B",3.9626,2.3510,7.6862,7.6890,0.3638,-3.7954],
        [null,null,"US25215DAA72","DEXIA CREDIT LOCAL SA (NEW YORK BRANCH) - 1.25%  Reg S Sr Nts due 2016","US",40799632.80,6.18,"A_A",1.2862,1.0014,0.0493,0.0493,null,-0.0248],
        [null,null,"US298785GE09","EUROPEAN INVESTMENT BANK - 1.125%  Nts Due 2016","LU",50029700.00,7.57,"A_A_A",0.8399,0.5597,0.2082,0.2082,0.0049,-0.1044],
        [null,null,"US4581X0CB23","INTER-AMERICAN DEVELOPMENT BANK - 0.875%Fxd Rt MTN Due 2016","US",5000185.00,0.76,"A_A_A",0.8388,0.5587,0.1260,0.1260,0.0017,-0.0632],
        [null,null,"US45950KBQ22","INTERNATIONAL FINANCE CORP - 1.125% Fix Rt MTN Due 2016","US",50042100.00,7.58,"A_A_A",0.5490,0.2709,0.1479,0.1480,0.0025,-0.0742],
        [null,null,"US50050HAE62","KOOKMIN BANK - STR due 2017 MTN RegS","KR",5006050.00,0.76,"A",1.4794,0.9638,0.7824,0.7832,0.0262,-0.3918],
        [null,null,"US50064FAF18","KOREA, REPUBLIC OF (GOVERNMENT) - 5.125%Sr Nts Due 2016","KR",6043800.00,0.92,"A_A",1.1795,0.8960,0.1863,0.1905,0.0054,-0.0946],
        [null,null,"US50065XAB01","KOREA NATIONAL OIL CORP - 4.000% RegS Sr Nts Due 2016","KR",5008385.00,0.76,"A_A",1.7701,1.4779,0.0740,0.0757,null,-0.0376],
        [null,null,"US500769FZ20","KFW - 0.625% Gtd Nts Due 2016","DE",24998150.00,3.78,"A_A_A",0.6615,0.3827,0.2082,0.2110,0.0056,-0.1042],
        [null,null,"US78010UNX18","ROYAL BANK OF CANADA - 1.2% Fxd Rt MTN Due 2017","CA",9999930.00,1.51,"A_A",1.2000,0.8870,0.3151,0.3152,0.0092,-0.1578],
        [null,null,"US912796KP37","UNITED STATES TREASURY - 6mt Bill Due on 03/23/2017","US",99800500.00,15.11,"A_A",0.4198,0.0413,0.4767,0.0000,0.0130,-0.2381],
        [null,null,"USY1391CDU28","BANK OF CHINA HONG KONG LTD - STR DUE2016 REGS","HK",5012150.00,0.76,"A_A",1.4150,1.1285,0.1068,0.1069,0.0010,-0.0542],
        [null,null,"XS0285449368","DEVELOPMENT BANK OF JAPAN INC - 5 1/8% NOTES 2007-2017","JP",5064250.00,0.77,"A",1.2665,0.9417,0.3397,0.3515,0.0198,-0.1711],
        [null,null,"XS0735390303","TOKYO METROPOLITAN GOVERNMENT - STR 1.875% DUE 2017","JP",8209676.00,1.24,"A",1.4940,1.1723,0.3260,0.3373,0.0188,-0.1649],
        [null,null,"XS0755979753","DEVELOPMENT BANK OF JAPAN INC - EMTN  1.5{%} STR due 2017","JP",16008480.00,2.42,"A",1.3972,1.0221,0.4493,0.4645,0.0257,-0.2246],
        [null,null,"XS0789060653","TOKYO METROPOLITAN GOVERNMENT - STR 1.75% DUE 2017","JP",5012450.00,0.76,"A",1.3830,0.9087,0.6877,0.7097,0.0376,-0.3451],
        [null,null,"XS0975574624","AGENCE FRANCAISE DE DEVELOPPEMENT EPIC - EMTN STR 1.125% Due 2016","FR",20000000.00,3.03,"A_A",1.1344,0.8514,0.0082,0.0084,null,-0.0042],
        [null,null,"XS1395052639","STANDARD CHARTERED PLC - DIP STR 4.05% Due 2026 RegS","GB",2063840.00,0.31,"A",3.6812,2.0653,7.8825,7.8840,0.3382,-3.9251],
        [null,null,"XS1437622548","BANK OF CHINA LTD (LUXEMBOURG BRANCH) - STR 1.875% DUE 2019 REG S EMTN","LU",9992600.00,1.51,"A",1.9127,1.0454,2.7121,2.7136,0.1048,-1.3522],
        [null,"Callable Bond",null,null,null,33004983.75,5.00,null,1.7040,0.9718,2.0758,2.0768,0.0876,-1.0993],
        [null,null,"US06366QW868","BANK OF MONTREAL - 2.50% Fxd Rt MTN Due 2017","CA",17988633.75,2.72,"A_A",1.2218,0.9232,0.2822,0.2823,0.0079,-0.1418],
        [null,null,"US46623EKG34","JPMORGAN CHASE & CO - 2.295% Sr Nts Due 2021","US",15016350.00,2.27,"A",2.2816,1.0300,4.2244,4.2266,0.1897,-2.2463],
        [null,"FRN",null,null,null,49072764.60,7.43,null,1.7294,1.1435,0.2433,1.0695,0.0279,-0.1218],
        [null,null,"US05574LTV08","BNP PARIBAS SA - %NA Flt Rt MTN Due 2016","FR",8048964.60,1.22,"A",0.8995,0.6188,0.2000,0.2069,0.0050,-0.1000],
        [null,null,"USJ46186AT93","MIZUHO BANK LTD - FRN Due 2017 RegS","JP",21000000.00,3.18,"A",1.9737,1.3681,0.2522,1.0073,0.0345,-0.1262],
        [null,null,"XS1383319461","DEXIA CREDIT LOCAL SA - FRN Due 2018 RegS","FR",20023800.00,3.03,"A_A",1.8067,1.1188,0.2513,1.4815,0.0419,-0.1258],
    ];

    private singularityData = [
        ["NBK_Singularity",null,null,"Unspecified (Name)","Unspecified (Country)",132153573.10,20.01,"Unspecified (Rating)",null,0.0000,null,0,0.5811,1.3080],
        [null,"Generic Security",null,"Unspecified (Name)","Unspecified (Country)",132153573.10,20.01,"Unspecified (Rating)",null,0.0000,null,0,0.5811,1.3080],
        [null,null,"Anchorage Cap Ltd Credit","Unspecified (Name)","Unspecified (Country)",6183571.54,0.94,"Unspecified (Rating)",null,0.0000,null,0,0.4436,1.6212],
        [null,null,"Argentiere Enhanced Ltd Relative Value","Unspecified (Name)","Unspecified (Country)",null,0,"Unspecified (Rating)",null,0,null,0,null,0],
        [null,null,"Argentiere Ltd Relative Value","Unspecified (Name)","Unspecified (Country)",978336.14,0.15,"Unspecified (Rating)",null,0.0000,null,0,0.5227,-6.1738],
        [null,null,"Atreaus Overseas Fund Ltd Macro","Unspecified (Name)","Unspecified (Country)",3786008.90,0.57,"Unspecified (Rating)",null,0.0000,null,0,0.6228,-4.0789],
        [null,null,"Basswood Enhanced LS Ltd Equities","Unspecified (Name)","Unspecified (Country)",3150613.36,0.48,"Unspecified (Rating)",null,0.0000,null,0,1.2116,0.0000],
        [null,null,"Blue Mtn LS Equity Ltd Equities","Unspecified (Name)","Unspecified (Country)",4307277.05,0.65,"Unspecified (Rating)",null,0.0000,null,0,1.1369,0.0000],
        [null,null,"Canyon Opp Cred GRF Ltd Credit","Unspecified (Name)","Unspecified (Country)",8422621.02,1.28,"Unspecified (Rating)",null,0.0000,null,0,0.7776,-1.2514],
        [null,null,"Chenavari Struct Cred Ltd Credit","Unspecified (Name)","Unspecified (Country)",10441275.46,1.58,"Unspecified (Rating)",null,0.0000,null,0,0.6996,3.5213],
        [null,null,"CVI Intl Credit Ltd Credit","Unspecified (Name)","Unspecified (Country)",11802982.80,1.79,"Unspecified (Rating)",null,0.0000,null,0,0.6796,1.5102],
        [null,null,"Discovery Gbl Opp Ltd Equities","Unspecified (Name)","Unspecified (Country)",4650359.44,0.70,"Unspecified (Rating)",null,0.0000,null,0,1.5552,0.0000],
        [null,null,"Element Capital Ltd Macro","Unspecified (Name)","Unspecified (Country)",4711308.84,0.71,"Unspecified (Rating)",null,0.0000,null,0,1.4660,-0.9322],
        [null,null,"GCM COM Ltd Commodities","Unspecified (Name)","Unspecified (Country)",3298012.23,0.50,"Unspecified (Rating)",null,0.0000,null,0,0.3076,0.0000],
        [null,null,"Graticule Asia Macro Ltd Macro","Unspecified (Name)","Unspecified (Country)",6318488.13,0.96,"Unspecified (Rating)",null,0.0000,null,0,0.9847,12.0039],
        [null,null,"GS Gamma Investments Ltd Relative Value","Unspecified (Name)","Unspecified (Country)",0.00,0.00,"Unspecified (Rating)",null,0.0000,null,0,null,0],
        [null,null,"Hitchwood Ltd Equities","Unspecified (Name)","Unspecified (Country)",5032009.13,0.76,"Unspecified (Rating)",null,0.0000,null,0,1.3537,0.0000],
        [null,null,"Incline Global ELS Ltd Equities","Unspecified (Name)","Unspecified (Country)",3531714.92,0.53,"Unspecified (Rating)",null,0.0000,null,0,2.1016,0.0000],
        [null,null,"Ionic Vol Arb Fund II Ltd Relative Value","Unspecified (Name)","Unspecified (Country)",3686013.45,0.56,"Unspecified (Rating)",null,0.0000,null,0,1.3126,4.9974],
        [null,null,"Lagunita Ltd Equities","Unspecified (Name)","Unspecified (Country)",1579313.12,0.24,"Unspecified (Rating)",null,0.0000,null,0,4.3101,0.0000],
        [null,null,"MKP Opportunity Ltd Macro","Unspecified (Name)","Unspecified (Country)",4199885.57,0.64,"Unspecified (Rating)",null,0.0000,null,0,0.4825,3.4412],
        [null,null,"MTP Energy Corp and Ltd Event Driven","Unspecified (Name)","Unspecified (Country)",3575073.64,0.54,"Unspecified (Rating)",null,0.0000,null,0,1.8898,0.0000],
        [null,null,"Myriad Opportunities Ltd Multi-Strategy","Unspecified (Name)","Unspecified (Country)",6115150.08,0.93,"Unspecified (Rating)",null,0.0000,null,0,0.4915,-5.4852],
        [null,null,"Nipun Capital Ltd Equities","Unspecified (Name)","Unspecified (Country)",3237384.78,0.49,"Unspecified (Rating)",null,0.0000,null,0,0.1425,0.0000],
        [null,null,"Passport Global LS Ltd Equities","Unspecified (Name)","Unspecified (Country)",4049631.76,0.61,"Unspecified (Rating)",null,0.0000,null,0,0.2165,0.0000],
        [null,null,"Prosiris Gbl Opp Fund Ltd Credit","Unspecified (Name)","Unspecified (Country)",4720590.77,0.71,"Unspecified (Rating)",null,0.0000,null,0,0.4069,4.3568],
        [null,null,"Ren Inst Div Alpha LP Equities","Unspecified (Name)","Unspecified (Country)",3499230.50,0.53,"Unspecified (Rating)",null,0.0000,null,0,0.7112,0.0000],
        [null,null,"Trian Partners Ltd Equities","Unspecified (Name)","Unspecified (Country)",6163562.18,0.93,"Unspecified (Rating)",null,0.0000,null,0,1.7010,0.0000],
        [null,null,"Whitebox Asymm Opp Ltd Relative Value","Unspecified (Name)","Unspecified (Country)",7185574.15,1.09,"Unspecified (Rating)",null,0.0000,null,0,0.5865,6.8072],
        [null,null,"York Euro Opp Unit Trust Event Driven","Unspecified (Name)","Unspecified (Country)",7527584.16,1.14,"Unspecified (Rating)",null,0.0000,null,0,0.5450,0.0000],
        [null,"Unspecified (CategoryNameEnum)",null,"Unspecified (Name)","Unspecified (Country)",null,0,"Unspecified (Rating)",null,0,null,0,null,0],
        [null,null,"Atlas Enhanced Fund Ltd Equities","Unspecified (Name)","Unspecified (Country)",null,0,"Unspecified (Rating)",null,0,null,0,null,0],
    ];

    private tarragonData = [
        ["NBK_Tarragon",null,null,"Unspecified (Name)","Unspecified (Country)",35279401.60,5.34,"Unspecified (Rating)",null,0.0000,null,0,0.6674,0.0000],
        [null,"Generic Security",null,"Unspecified (Name)","Unspecified (Country)",35279401.60,5.34,"Unspecified (Rating)",null,0.0000,null,0,0.6674,0.0000],
        [null,null,"ADT Security","Unspecified (Name)","Unspecified (Country)",7615619.00,1.15,"Unspecified (Rating)",null,0.0000,null,0,0.7055,0.0000],
        [null,null,"Advent International GPE VIII-H LP","Unspecified (Name)","Unspecified (Country)",302162.00,0.05,"Unspecified (Rating)",null,0.0000,null,0,0.7055,0.0000],
        [null,null,"BCP Energy Services Fund LP","Unspecified (Name)","Unspecified (Country)",3545929.00,0.54,"Unspecified (Rating)",null,0.0000,null,0,0.7055,0.0000],
        [null,null,"BDC III E LP","Unspecified (Name)","Unspecified (Country)",0.00,0.00,"Unspecified (Rating)",null,0.0000,null,0,null,0],
        [null,null,"Blackstone Capital Partners VII LP","Unspecified (Name)","Unspecified (Country)",0.00,0.00,"Unspecified (Rating)",null,0.0000,null,0,null,0],
        [null,null,"Bridgepoint Europe IV LP","Unspecified (Name)","Unspecified (Country)",3421443.60,0.52,"Unspecified (Rating)",null,0.0000,null,0,1.0040,0.0000],
        [null,null,"Capitala Private Credit Fund V LP","Unspecified (Name)","Unspecified (Country)",0.00,0.00,"Unspecified (Rating)",null,0.0000,null,0,null,0],
        [null,null,"CapitalSouth Partners Florida Sidecar Fund II LP","Unspecified (Name)","Unspecified (Country)",8241054.00,1.25,"Unspecified (Rating)",null,0.0000,null,0,0.7055,0.0000],
        [null,null,"Gridiron Capital Fund III LP","Unspecified (Name)","Unspecified (Country)",0.00,0.00,"Unspecified (Rating)",null,0.0000,null,0,null,0],
        [null,null,"OHA Strategic Credit Fund II (Offshore) LP","Unspecified (Name)","Unspecified (Country)",0.00,0.00,"Unspecified (Rating)",null,0.0000,null,0,null,0],
        [null,null,"PDC Opportunities V LP","Unspecified (Name)","Unspecified (Country)",4181326.00,0.63,"Unspecified (Rating)",null,0.0000,null,0,0.7055,0.0000],
        [null,null,"Platinum Equity Capital Partners IV LP","Unspecified (Name)","Unspecified (Country)",0.00,0.00,"Unspecified (Rating)",null,0.0000,null,0,null,0],
        [null,null,"Saw Mill Capital Partners II LP","Unspecified (Name)","Unspecified (Country)",861872.00,0.13,"Unspecified (Rating)",null,0.0000,null,0,0.7055,0.0000],
        [null,null,"Secondary Investment SPV-9 LP","Unspecified (Name)","Unspecified (Country)",5083038.00,0.77,"Unspecified (Rating)",null,0.0000,null,0,0.7055,0.0000],
        [null,null,"Warburg Pincus Private Equity XII LP","Unspecified (Name)","Unspecified (Country)",2026958.00,0.31,"Unspecified (Rating)",null,0.0000,null,0,0.7055,0.0000]
    ];


    //private getTableChart(){
    //    //this.reportService.getPortfolioReport()
    //    //    .subscribe(
    //    //        report => {
    //    //            console.log(report);
    //    //            this.tableChart = report;
    //    //        },
    //    //        error => {
    //    //            // TODO: error
    //    //        }
    //    //    );
    //
    //    this.tableChart = new TableChartDto();
    //    this.setStaticColumns();
    //    this.setStaticRows();
    //
    //}
    //
    //private drawGraphAll(){
    //    this.data = new google.visualization.DataTable();
    //    for(var i = 0; i < this.tableChart.columns.length; i++){
    //        this.data.addColumn(this.tableChart.columns[i].type, this.tableChart.columns[i].name);
    //    }
    //    this.data.addRows(this.tableChart.rows);
    //
    //    this.options = {
    //        showRowNumber: false,
    //        width: '100%',
    //        height: '100%',
    //        'allowHtml': true,
    //        cssClassNames: {
    //            headerRow: 'googleChartTable',
    //            tableCell: 'googleChartTable',
    //        }
    //    };
    //
    //    this.chart = this.createTableChart(document.getElementById('report_liquidity'));
    //    this.chart.draw(this.data, this.options);
    //
    //    var title = "Name";
    //    var width = "700px";
    //    $('.google-visualization-table-th:contains(' + title + ')').css('width', width);
    //    $('.google-visualization-table-th:contains(' + title + ')').css('padding', 0);
    //}
    //
    //private setStaticColumns(){
    //
    //    this.tableChart.columns = [];
    //
    //    var types = ["string", "string", "string", "string", "string", "number", "number", "string", "number", "number", "number", "number", "number", "number"];
    //    var names = ["PortfolioName", "Category Name", "ClientId", "Issuer Full Name ",
    //        "Country", "PV, USD", "Weights, %", "Rating", "YTM, %", "Option Adjusted Spread, %",
    //        "Eff. Duration", "Spread Duration", "VaR, Historical % @95 monthly", "Interest Rates +50bp, loss %"];
    //    for(var i = 0; i < names.length; i++){
    //        var column = new TableColumnDto();
    //        column.type = types[i];
    //        column.name = names[i];
    //        this.tableChart.columns.push(column);
    //    }
    //    console.log(this.tableChart);
    //}
    //
    //private setStaticRows(){
    //    this.tableChart.rows =
    //        [
    //            ["NBK_Liquidity",null,null,null,null,493075205.20,74.65,null,1.1174,0.6892,0.6868,0.6708,0.0262,-0.3464],
    //            [null,"Bond",null,null,null,410997456.80,62.22,null,0.9973,0.6123,0.6282,0.5103,0.0231,-0.3127],
    //            [null,null,"FR0011616416","CAISSE DES DEPOTS ET CONSIGNATIONS - 0.875% STR BOND DUE 7TH NOV 2016","FR",8001280.00,1.21,"A_A",0.7067,0.4276,0.1041,0.1065,0.0011,-0.0524],
    //            [null,null,"US06120TAA60","BANK OF CHINA LTD - STR Due 2024 RegS","CN",3233790.00,0.49,"B_B_B",3.9060,2.3602,6.7027,6.2101,1.1783,-3.3466],
    //            [null,null,"US064159AM82","BANK OF NOVA SCOTIA - 2.55% Sr Nts Due 2017","CA",20083940.00,3.04,"A_A",1.0564,0.7585,0.2849,0.2850,0.0081,-0.1431],
    //            [null,null,"US06738EAN58","BARCLAYS PLC - 4.375% Sr Nts Due 2026","GB",11586568.00,1.75,"B_B_B",3.9626,2.3510,7.6862,7.6890,0.3638,-3.7954],
    //            [null,null,"US25215DAA72","DEXIA CREDIT LOCAL SA (NEW YORK BRANCH) - 1.25%  Reg S Sr Nts due 2016","US",40799632.80,6.18,"A_A",1.2862,1.0014,0.0493,0.0493,null,-0.0248],
    //            [null,null,"US298785GE09","EUROPEAN INVESTMENT BANK - 1.125%  Nts Due 2016","LU",50029700.00,7.57,"A_A_A",0.8399,0.5597,0.2082,0.2082,0.0049,-0.1044],
    //            [null,null,"US4581X0CB23","INTER-AMERICAN DEVELOPMENT BANK - 0.875%Fxd Rt MTN Due 2016","US",5000185.00,0.76,"A_A_A",0.8388,0.5587,0.1260,0.1260,0.0017,-0.0632],
    //            [null,null,"US45950KBQ22","INTERNATIONAL FINANCE CORP - 1.125% Fix Rt MTN Due 2016","US",50042100.00,7.58,"A_A_A",0.5490,0.2709,0.1479,0.1480,0.0025,-0.0742],
    //            [null,null,"US50050HAE62","KOOKMIN BANK - STR due 2017 MTN RegS","KR",5006050.00,0.76,"A",1.4794,0.9638,0.7824,0.7832,0.0262,-0.3918],
    //            [null,null,"US50064FAF18","KOREA, REPUBLIC OF (GOVERNMENT) - 5.125%Sr Nts Due 2016","KR",6043800.00,0.92,"A_A",1.1795,0.8960,0.1863,0.1905,0.0054,-0.0946],
    //            [null,null,"US50065XAB01","KOREA NATIONAL OIL CORP - 4.000% RegS Sr Nts Due 2016","KR",5008385.00,0.76,"A_A",1.7701,1.4779,0.0740,0.0757,null,-0.0376],
    //            [null,null,"US500769FZ20","KFW - 0.625% Gtd Nts Due 2016","DE",24998150.00,3.78,"A_A_A",0.6615,0.3827,0.2082,0.2110,0.0056,-0.1042],
    //            [null,null,"US78010UNX18","ROYAL BANK OF CANADA - 1.2% Fxd Rt MTN Due 2017","CA",9999930.00,1.51,"A_A",1.2000,0.8870,0.3151,0.3152,0.0092,-0.1578],
    //            [null,null,"US912796KP37","UNITED STATES TREASURY - 6mt Bill Due on 03/23/2017","US",99800500.00,15.11,"A_A",0.4198,0.0413,0.4767,0.0000,0.0130,-0.2381],
    //            [null,null,"USY1391CDU28","BANK OF CHINA HONG KONG LTD - STR DUE2016 REGS","HK",5012150.00,0.76,"A_A",1.4150,1.1285,0.1068,0.1069,0.0010,-0.0542],
    //            [null,null,"XS0285449368","DEVELOPMENT BANK OF JAPAN INC - 5 1/8% NOTES 2007-2017","JP",5064250.00,0.77,"A",1.2665,0.9417,0.3397,0.3515,0.0198,-0.1711],
    //            [null,null,"XS0735390303","TOKYO METROPOLITAN GOVERNMENT - STR 1.875% DUE 2017","JP",8209676.00,1.24,"A",1.4940,1.1723,0.3260,0.3373,0.0188,-0.1649],
    //            [null,null,"XS0755979753","DEVELOPMENT BANK OF JAPAN INC - EMTN  1.5{%} STR due 2017","JP",16008480.00,2.42,"A",1.3972,1.0221,0.4493,0.4645,0.0257,-0.2246],
    //            [null,null,"XS0789060653","TOKYO METROPOLITAN GOVERNMENT - STR 1.75% DUE 2017","JP",5012450.00,0.76,"A",1.3830,0.9087,0.6877,0.7097,0.0376,-0.3451],
    //            [null,null,"XS0975574624","AGENCE FRANCAISE DE DEVELOPPEMENT EPIC - EMTN STR 1.125% Due 2016","FR",20000000.00,3.03,"A_A",1.1344,0.8514,0.0082,0.0084,null,-0.0042],
    //            [null,null,"XS1395052639","STANDARD CHARTERED PLC - DIP STR 4.05% Due 2026 RegS","GB",2063840.00,0.31,"A",3.6812,2.0653,7.8825,7.8840,0.3382,-3.9251],
    //            [null,null,"XS1437622548","BANK OF CHINA LTD (LUXEMBOURG BRANCH) - STR 1.875% DUE 2019 REG S EMTN","LU",9992600.00,1.51,"A",1.9127,1.0454,2.7121,2.7136,0.1048,-1.3522],
    //            [null,"Callable Bond",null,null,null,33004983.75,5.00,null,1.7040,0.9718,2.0758,2.0768,0.0876,-1.0993],
    //            [null,null,"US06366QW868","BANK OF MONTREAL - 2.50% Fxd Rt MTN Due 2017","CA",17988633.75,2.72,"A_A",1.2218,0.9232,0.2822,0.2823,0.0079,-0.1418],
    //            [null,null,"US46623EKG34","JPMORGAN CHASE & CO - 2.295% Sr Nts Due 2021","US",15016350.00,2.27,"A",2.2816,1.0300,4.2244,4.2266,0.1897,-2.2463],
    //            [null,"FRN",null,null,null,49072764.60,7.43,null,1.7294,1.1435,0.2433,1.0695,0.0279,-0.1218],
    //            [null,null,"US05574LTV08","BNP PARIBAS SA - %NA Flt Rt MTN Due 2016","FR",8048964.60,1.22,"A",0.8995,0.6188,0.2000,0.2069,0.0050,-0.1000],
    //            [null,null,"USJ46186AT93","MIZUHO BANK LTD - FRN Due 2017 RegS","JP",21000000.00,3.18,"A",1.9737,1.3681,0.2522,1.0073,0.0345,-0.1262],
    //            [null,null,"XS1383319461","DEXIA CREDIT LOCAL SA - FRN Due 2018 RegS","FR",20023800.00,3.03,"A_A",1.8067,1.1188,0.2513,1.4815,0.0419,-0.1258],
    //            ["NBK_Singularity",null,null,"Unspecified (Name)","Unspecified (Country)",132153573.10,20.01,"Unspecified (Rating)",null,0.0000,null,0,0.5811,1.3080],
    //            [null,"Generic Security",null,"Unspecified (Name)","Unspecified (Country)",132153573.10,20.01,"Unspecified (Rating)",null,0.0000,null,0,0.5811,1.3080],
    //            [null,null,"Anchorage Cap Ltd Credit","Unspecified (Name)","Unspecified (Country)",6183571.54,0.94,"Unspecified (Rating)",null,0.0000,null,0,0.4436,1.6212],
    //            [null,null,"Argentiere Enhanced Ltd Relative Value","Unspecified (Name)","Unspecified (Country)",null,0,"Unspecified (Rating)",null,0,null,0,null,0],
    //            [null,null,"Argentiere Ltd Relative Value","Unspecified (Name)","Unspecified (Country)",978336.14,0.15,"Unspecified (Rating)",null,0.0000,null,0,0.5227,-6.1738],
    //            [null,null,"Atreaus Overseas Fund Ltd Macro","Unspecified (Name)","Unspecified (Country)",3786008.90,0.57,"Unspecified (Rating)",null,0.0000,null,0,0.6228,-4.0789],
    //            [null,null,"Basswood Enhanced LS Ltd Equities","Unspecified (Name)","Unspecified (Country)",3150613.36,0.48,"Unspecified (Rating)",null,0.0000,null,0,1.2116,0.0000],
    //            [null,null,"Blue Mtn LS Equity Ltd Equities","Unspecified (Name)","Unspecified (Country)",4307277.05,0.65,"Unspecified (Rating)",null,0.0000,null,0,1.1369,0.0000],
    //            [null,null,"Canyon Opp Cred GRF Ltd Credit","Unspecified (Name)","Unspecified (Country)",8422621.02,1.28,"Unspecified (Rating)",null,0.0000,null,0,0.7776,-1.2514],
    //            [null,null,"Chenavari Struct Cred Ltd Credit","Unspecified (Name)","Unspecified (Country)",10441275.46,1.58,"Unspecified (Rating)",null,0.0000,null,0,0.6996,3.5213],
    //            [null,null,"CVI Intl Credit Ltd Credit","Unspecified (Name)","Unspecified (Country)",11802982.80,1.79,"Unspecified (Rating)",null,0.0000,null,0,0.6796,1.5102],
    //            [null,null,"Discovery Gbl Opp Ltd Equities","Unspecified (Name)","Unspecified (Country)",4650359.44,0.70,"Unspecified (Rating)",null,0.0000,null,0,1.5552,0.0000],
    //            [null,null,"Element Capital Ltd Macro","Unspecified (Name)","Unspecified (Country)",4711308.84,0.71,"Unspecified (Rating)",null,0.0000,null,0,1.4660,-0.9322],
    //            [null,null,"GCM COM Ltd Commodities","Unspecified (Name)","Unspecified (Country)",3298012.23,0.50,"Unspecified (Rating)",null,0.0000,null,0,0.3076,0.0000],
    //            [null,null,"Graticule Asia Macro Ltd Macro","Unspecified (Name)","Unspecified (Country)",6318488.13,0.96,"Unspecified (Rating)",null,0.0000,null,0,0.9847,12.0039],
    //            [null,null,"GS Gamma Investments Ltd Relative Value","Unspecified (Name)","Unspecified (Country)",0.00,0.00,"Unspecified (Rating)",null,0.0000,null,0,null,0],
    //            [null,null,"Hitchwood Ltd Equities","Unspecified (Name)","Unspecified (Country)",5032009.13,0.76,"Unspecified (Rating)",null,0.0000,null,0,1.3537,0.0000],
    //            [null,null,"Incline Global ELS Ltd Equities","Unspecified (Name)","Unspecified (Country)",3531714.92,0.53,"Unspecified (Rating)",null,0.0000,null,0,2.1016,0.0000],
    //            [null,null,"Ionic Vol Arb Fund II Ltd Relative Value","Unspecified (Name)","Unspecified (Country)",3686013.45,0.56,"Unspecified (Rating)",null,0.0000,null,0,1.3126,4.9974],
    //            [null,null,"Lagunita Ltd Equities","Unspecified (Name)","Unspecified (Country)",1579313.12,0.24,"Unspecified (Rating)",null,0.0000,null,0,4.3101,0.0000],
    //            [null,null,"MKP Opportunity Ltd Macro","Unspecified (Name)","Unspecified (Country)",4199885.57,0.64,"Unspecified (Rating)",null,0.0000,null,0,0.4825,3.4412],
    //            [null,null,"MTP Energy Corp and Ltd Event Driven","Unspecified (Name)","Unspecified (Country)",3575073.64,0.54,"Unspecified (Rating)",null,0.0000,null,0,1.8898,0.0000],
    //            [null,null,"Myriad Opportunities Ltd Multi-Strategy","Unspecified (Name)","Unspecified (Country)",6115150.08,0.93,"Unspecified (Rating)",null,0.0000,null,0,0.4915,-5.4852],
    //            [null,null,"Nipun Capital Ltd Equities","Unspecified (Name)","Unspecified (Country)",3237384.78,0.49,"Unspecified (Rating)",null,0.0000,null,0,0.1425,0.0000],
    //            [null,null,"Passport Global LS Ltd Equities","Unspecified (Name)","Unspecified (Country)",4049631.76,0.61,"Unspecified (Rating)",null,0.0000,null,0,0.2165,0.0000],
    //            [null,null,"Prosiris Gbl Opp Fund Ltd Credit","Unspecified (Name)","Unspecified (Country)",4720590.77,0.71,"Unspecified (Rating)",null,0.0000,null,0,0.4069,4.3568],
    //            [null,null,"Ren Inst Div Alpha LP Equities","Unspecified (Name)","Unspecified (Country)",3499230.50,0.53,"Unspecified (Rating)",null,0.0000,null,0,0.7112,0.0000],
    //            [null,null,"Trian Partners Ltd Equities","Unspecified (Name)","Unspecified (Country)",6163562.18,0.93,"Unspecified (Rating)",null,0.0000,null,0,1.7010,0.0000],
    //            [null,null,"Whitebox Asymm Opp Ltd Relative Value","Unspecified (Name)","Unspecified (Country)",7185574.15,1.09,"Unspecified (Rating)",null,0.0000,null,0,0.5865,6.8072],
    //            [null,null,"York Euro Opp Unit Trust Event Driven","Unspecified (Name)","Unspecified (Country)",7527584.16,1.14,"Unspecified (Rating)",null,0.0000,null,0,0.5450,0.0000],
    //            [null,"Unspecified (CategoryNameEnum)",null,"Unspecified (Name)","Unspecified (Country)",null,0,"Unspecified (Rating)",null,0,null,0,null,0],
    //            [null,null,"Atlas Enhanced Fund Ltd Equities","Unspecified (Name)","Unspecified (Country)",null,0,"Unspecified (Rating)",null,0,null,0,null,0],
    //            ["NBK_Tarragon",null,null,"Unspecified (Name)","Unspecified (Country)",35279401.60,5.34,"Unspecified (Rating)",null,0.0000,null,0,0.6674,0.0000],
    //            [null,"Generic Security",null,"Unspecified (Name)","Unspecified (Country)",35279401.60,5.34,"Unspecified (Rating)",null,0.0000,null,0,0.6674,0.0000],
    //            [null,null,"ADT Security","Unspecified (Name)","Unspecified (Country)",7615619.00,1.15,"Unspecified (Rating)",null,0.0000,null,0,0.7055,0.0000],
    //            [null,null,"Advent International GPE VIII-H LP","Unspecified (Name)","Unspecified (Country)",302162.00,0.05,"Unspecified (Rating)",null,0.0000,null,0,0.7055,0.0000],
    //            [null,null,"BCP Energy Services Fund LP","Unspecified (Name)","Unspecified (Country)",3545929.00,0.54,"Unspecified (Rating)",null,0.0000,null,0,0.7055,0.0000],
    //            [null,null,"BDC III E LP","Unspecified (Name)","Unspecified (Country)",0.00,0.00,"Unspecified (Rating)",null,0.0000,null,0,null,0],
    //            [null,null,"Blackstone Capital Partners VII LP","Unspecified (Name)","Unspecified (Country)",0.00,0.00,"Unspecified (Rating)",null,0.0000,null,0,null,0],
    //            [null,null,"Bridgepoint Europe IV LP","Unspecified (Name)","Unspecified (Country)",3421443.60,0.52,"Unspecified (Rating)",null,0.0000,null,0,1.0040,0.0000],
    //            [null,null,"Capitala Private Credit Fund V LP","Unspecified (Name)","Unspecified (Country)",0.00,0.00,"Unspecified (Rating)",null,0.0000,null,0,null,0],
    //            [null,null,"CapitalSouth Partners Florida Sidecar Fund II LP","Unspecified (Name)","Unspecified (Country)",8241054.00,1.25,"Unspecified (Rating)",null,0.0000,null,0,0.7055,0.0000],
    //            [null,null,"Gridiron Capital Fund III LP","Unspecified (Name)","Unspecified (Country)",0.00,0.00,"Unspecified (Rating)",null,0.0000,null,0,null,0],
    //            [null,null,"OHA Strategic Credit Fund II (Offshore) LP","Unspecified (Name)","Unspecified (Country)",0.00,0.00,"Unspecified (Rating)",null,0.0000,null,0,null,0],
    //            [null,null,"PDC Opportunities V LP","Unspecified (Name)","Unspecified (Country)",4181326.00,0.63,"Unspecified (Rating)",null,0.0000,null,0,0.7055,0.0000],
    //            [null,null,"Platinum Equity Capital Partners IV LP","Unspecified (Name)","Unspecified (Country)",0.00,0.00,"Unspecified (Rating)",null,0.0000,null,0,null,0],
    //            [null,null,"Saw Mill Capital Partners II LP","Unspecified (Name)","Unspecified (Country)",861872.00,0.13,"Unspecified (Rating)",null,0.0000,null,0,0.7055,0.0000],
    //            [null,null,"Secondary Investment SPV-9 LP","Unspecified (Name)","Unspecified (Country)",5083038.00,0.77,"Unspecified (Rating)",null,0.0000,null,0,0.7055,0.0000],
    //            [null,null,"Warburg Pincus Private Equity XII LP","Unspecified (Name)","Unspecified (Country)",2026958.00,0.31,"Unspecified (Rating)",null,0.0000,null,0,0.7055,0.0000]
    //        ];
    //
    //}
    //
    //private getDummyData(){
    //    var tableChart = new TableChartDto();
    //    tableChart.columns = [];
    //    var column = new TableColumnDto();
    //    column.type = "string";
    //    column.name = "Name";
    //    tableChart.columns.push(column);
    //    var column = new TableColumnDto();
    //    column.type = "number";
    //    column.name = "Salary";
    //    tableChart.columns.push(column);
    //    var column = new TableColumnDto();
    //    column.type = "boolean";
    //    column.name = "Full Time Employee";
    //    tableChart.columns.push(column);
    //
    //    tableChart.rows = [
    //        ['Mike',  {v: 10000, f: '$10,000'}, true],
    //        ['Jim',   {v:8000,   f: '$8,000'},  false],
    //        ['Alice', {v: 12500, f: '$12,500'}, true],
    //        ['Bob',   {v: 7000,  f: '$7,000'},  true],
    //        ['Bob',   7000,  true],
    //        ['Bob',   7000,  false]
    //    ];
    //
    //    return tableChart;
    //}
}