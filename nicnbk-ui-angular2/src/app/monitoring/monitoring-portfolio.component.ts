import {Component, AfterViewInit, ViewChild} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {CommonFormViewComponent} from "../common/common.component";
import {CommonTableau} from "./common-tableau.component";
import {GoogleChartComponent} from "../google-chart/google-chart.component";
import {TableChartDto, TableColumnDto} from "../google-chart/table-chart.dto";

declare var google:any;
declare var $: any;

@Component({
    selector: 'monitoring-portfolio',
    templateUrl: 'view/monitoring-portfolio.component.html',
    styleUrls: [],
    providers: [],
})
export class MonitoringPortfolioComponent extends GoogleChartComponent {
    //ngAfterViewInit():void {
    //    this.tableau_func()
    //}

    private tableDate;
    private performanceType;

    constructor(
    ){
        super();

        //this.getLineChartLAternativePerformance();
        //this.getLineChartLPublicPerformance();

        // TODO: wait/sync on data loading
        // TODO: sync on subscribe results
        //this.waitSleep(700);

        //$("#tableDate").val($("#tableDate option:first").val());
        //$("#performanceType").val($("#performanceType option:first").val());

    }

    drawGraph(){
        var tableDate = this.getAllDates()[0];
        this.tableDate = tableDate;
        this.performanceType = "TOTAL";

        this.drawTables(tableDate);
        this.drawAlternativePerformanceChart("TOTAL");
        this.drawTargetAllocationChart();
        this.drawActualAllocationChart();
        this.drawPublicPerformanceChart();
    }

    public redrawTables(){
        //alert($('#tableDate').val());
        this.drawTables($("#tableDate").val());
    }

    public redrawLineChart(){
        this.drawAlternativePerformanceChart($('#performanceType').val());
    }

    drawTables(tableDate) {
        var NAVdata = this.getNAVData(tableDate);
        this.drawPortfolioValueTable(NAVdata);

        var portfolioPerformanceData = this.getPortfolioPerformanceData(tableDate);
        this.drawPortfolioPerformanceTable(portfolioPerformanceData);

        var benchmarkPerformanceData = this.getBenchmarkPerformanceData(tableDate);
        this.drawBenchmarksPerformanceTable(benchmarkPerformanceData);
    }

    // NAV ------------------------------
    getNAVData(tableDate){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "");
        data.addColumn("number", "");
        var NAVarray = this.getNAVByDate(tableDate);
        data.addRows([
            ["NIC Total", NAVarray[1]],
            ["Liquid Portfolio", NAVarray[2]],
            ["NICK Master Fund", NAVarray[3]],
            ["Hedge Funds Portfolio", NAVarray[4]],
            ["Private Equity Portfolio", NAVarray[5]],
            ["NICK Master Fund Cash", NAVarray[6]]
        ]);
        return data;
    }

    private getNAVByDate(tableDate){

        for(var i = 0; i < this.nav.length; i++){
            if(this.nav[i][0] === tableDate){
                return this.nav[i];
            }
        }
        return null;
    }

    drawPortfolioValueTable(data){
        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {}
        };

        var chart = this.createTableChart(document.getElementById('portfolio_value'));
        chart.draw(data, options);
    }

    // PORTFOLIO PERFORMANCE -------------

    getPortfolioPerformanceData(tableDate){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "");
        data.addColumn("number", "MTD");
        data.addColumn("number", "QTD");
        data.addColumn("number", "YTD");

        var performanceArray = this.getPerformanceByDate(tableDate);
        data.addRows([
            ["NIC Portfolio Total", performanceArray[0][2], performanceArray[1][2], performanceArray[2][2],],
            ["Liquid Portfolio", performanceArray[0][3], performanceArray[1][3],performanceArray[2][3],],
            ["NICK Master Fund", performanceArray[0][6], performanceArray[1][6], performanceArray[2][6]],
            ["Hedge Funds Portfolio", performanceArray[0][5], performanceArray[1][5], performanceArray[2][5]],
            ["Private Equity Portfolio", performanceArray[0][4], performanceArray[1][4], performanceArray[1][4]]
        ]);
        return data;
    }

    private getPerformanceByDate(tableDate){
        var performanceArray = [];
        for(var i = 0; i < this.performance.length; i++){
            if(this.performance[i][0] === tableDate){
                performanceArray.push(this.performance[i]);
            }
        }
        return performanceArray;
    }

    drawPortfolioPerformanceTable(data){
        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {}
        };

        var chart = this.createTableChart(document.getElementById('portfolio_performance'));
        chart.draw(data, options);
    }

    // BENCHMARK PERFORMANCE ------------------
    getBenchmarkPerformanceData(tableDate){

        var data = new google.visualization.DataTable();
        data.addColumn("string", "");
        data.addColumn("number", "MTD");
        data.addColumn("number", "QTD");
        data.addColumn("number", "YTD");

        var performanceArray = this.getPerformanceByDate(tableDate)
        data.addRows([
            ["Composite Benchmark", performanceArray[0][11], performanceArray[1][11], performanceArray[2][11]],
            ["Cpi + 5%", performanceArray[0][9], performanceArray[1][9], performanceArray[2][9]],
            ["US 6m T-bills1", performanceArray[0][8], performanceArray[1][8], performanceArray[2][8]],
            ["HFRI FoF indez", performanceArray[0][10], performanceArray[1][10], performanceArray[2][10]]
        ]);
        return data;
    }

    drawBenchmarksPerformanceTable(data){
        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {}
        };

        var chart = this.createTableChart(document.getElementById('benchmarks_performance'));
        chart.draw(data, options);
    }

    // ALTERNATIVE PERFORMANCE -----------------
    drawAlternativePerformanceChart(type){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "Date");
        data.addColumn("number", this.getLabelsByType(type, 0));
        data.addColumn("number", this.getLabelsByType(type, 1));

        var compareValue = this.getPerformanceWithBenchmarks(type);
        data.addRows(compareValue);

        var options = {
            chart: {
                title: "Alternative portfolio performance",
                subtitle: "monthly",
                legend: { position: 'top' },
                width: 600,
                height: 500
            },
            showRowNumber: false,
            width: '100%',
            height: '100%'
        };

        var chart = this.createLineChart(document.getElementById('alternative_performance'));
        chart.draw(data, options);

        //this.data_alt_perf = new google.visualization.DataTable();
        //for(var i = 0; i < this.tableChart_alt_perf.columns.length; i++){
        //    this.data_alt_perf.addColumn(this.tableChart_alt_perf.columns[i].type, this.tableChart_alt_perf.columns[i].name);
        //}
        //this.data_alt_perf.addRows(this.tableChart_alt_perf.rows);
        //
        //this.options_alt_perf = {
        //    chart: {
        //        title: "Alternative portfolio performance",
        //        subtitle: "monthly",
        //        legend: { position: 'right' },
        //    },
        //    showRowNumber: false,
        //    width: '100%',
        //    height: '100%'
        //};
        //var chart = this.createLineChart(document.getElementById('alternative_performance'));
        //chart.draw(this.data_alt_perf, this.options_alt_perf);

    }

    private getLabelsByType(type, index){
        if(type === "LIQUID"){
            if(index == 0){
                return "NIC Liquid";
            }else{
                return "US 6mT-bills";
            }
        }else if(type === "PE"){
            if(index == 0){
                return "NIC Private Equity";
            }else{
                return "CPI + 5%";
            }

        }else if(type === "HF"){
            if(index == 0){
                return "NIC Hedge Funds";
            }else{
                return "HFRI FoF Index";
            }
        }else{
            if(index == 0){
                return "NIC Portfolio";
            }else{
                return "Composite Benchmark";
            }
        }
    }

    private getPerformanceWithBenchmarks(type){
        var values = [];

        var index1;
        var index2;
        if(type === "TOTAL"){
            index1 = 2;
            index2 = 11;
        }else if(type === "LIQUID"){
            index1 = 3;
            index2 = 8;
        }else if(type === "PE"){
            index1 = 4;
            index2 = 9;
        }else if(type === "HF"){
            index1 = 5;
            index2 = 10;
        }
        for(var i = 0; i < this.performance.length; i++ ){
            if(this.performance[i][1] === "MTD"){
                var item = [this.performance[i][0], this.performance[i][index1], this.performance[i][index2]];
                values.push(item);
            }
        }
        return values;
    }

    // ALLOCATIONS -----------------------------
    drawTargetAllocationChart(){
        var data = google.visualization.arrayToDataTable(
        [
            ["Allocation", "%"],
            ["Private Equity",40],
            ["Hedge Funds",15],
            ["Liquid portfolio", 0],
            ["Real Estate",10],
            ["Infrastructure ", 15],
            ["Public Equity",20]
        ]);

        var options = {
            title: 'Target Allocation'
        };

        var chart = new google.visualization.PieChart(document.getElementById('target_allocation'));
        chart.draw(data, options);
    }

    drawActualAllocationChart(){
        var data = google.visualization.arrayToDataTable(
            [
                ["Allocation", "%"],
                ["Private Equity",6],
                ["Hedge Funds",18],
                ["Liquid portfolio", 76],
                ["Real Estate",0],
                ["Infrastructure ",0],
                ["Public Equity",0]
            ]
        );

        var options = {
            title: 'Actual Allocation'
        };

        var chart = new google.visualization.PieChart(document.getElementById('actual_allocation'));
        chart.draw(data, options);
    }

    // PUBLIC PERFORMANCE ----------------------
    drawPublicPerformanceChart(){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "Date");
        data.addColumn("number", "S&P500 Index");
        data.addColumn("number", "MSCI World Index");
        data.addColumn("number", "HFRX Global Hedge Fund Index");
        data.addColumn("number", "Barclays Capital Bond Index");

        var benchmarks = this.getPublicPerformance();
        data.addRows(benchmarks);

        var options = {
            chart: {
                title: "Public markets performance",
                legend: { position: 'top' },
                width: 600,
                height: 500
            },
            showRowNumber: false,
            width: '100%',
            height: '100%'
        };

        var chart = this.createLineChart(document.getElementById('public_performance'));
        chart.draw(data, options);


        //this.data_public_perf = new google.visualization.DataTable();
        //for(var i = 0; i < this.tableChart_public_perf.columns.length; i++){
        //    this.data_public_perf.addColumn(this.tableChart_public_perf.columns[i].type, this.tableChart_public_perf.columns[i].name);
        //}
        //this.data_public_perf.addRows(this.tableChart_public_perf.rows);
        //
        //this.options_public_perf = {
        //    chart: {
        //        title: "Alternative portfolio performance",
        //        subtitle: "since inception",
        //        legend: { position: 'right' },
        //    },
        //    showRowNumber: false,
        //    width: '100%',
        //    height: '100%'
        //};
        //
        //var chart = this.createLineChart(document.getElementById('public_performance'));
        //chart.draw(this.data_public_perf, this.options_public_perf);

    }

    private getPublicPerformance(){
        return this.publicMarketsPerformance;
    }

    public getAllDates(){
        var dates = [];
        for(var i = this.nav.length - 1; i >= 0; i--){
            dates.push(this.nav[i][0]);
        }
        return dates;
    }

    public getAssetTypes(){
        var assetTypes = ["TOTAL", "LIQUID", "PE", "HF"];
        return assetTypes;

    }

    private nav = [
        ["Dec-14",799882987,799882987,0,0,0,0],
        ["Jan-15",801943108,801943108,0,0,0,0],
        ["Feb-15",801406672,801406672,0,0,0,0],
        ["Mar-15",802150976,802150976,0,0,0,0],
        ["Apr-15",802783935,802783935,0,0,0,0],
        ["May-15",803051737,803051737,0,0,0,0],
        ["Jun-15",803394104,803394104,0,0,0,0],
        ["Jul-15",803667686,728467686,75200000,0,75000000,200000],
        ["Aug-15",802500296,653509334,148990962,0,73806138,184824],
        ["Sep-15",799649211,653880251,145768960,0,145584323,184637],
        ["Oct-15",800087405,650177665,149909740,3965000,145761377,183363],
        ["Nov-15",799268030,649883201,149384829,3163463,146039154,182212],
        ["Dec-15",798611908,644606893,154005015,8601146,145224471,179398],
        ["Jan-16",795560566,642166580,153393987,11509729,141704890,179368],
        ["Feb-16",793922449,642659327,151263123,11593892,139490031,179200],
        ["Mar-16",795539712,642564052,152975660,13135835,139660777,179048],
        ["Apr-16",796760017,638773831,157986185,17090141,140717168,178876],
        ["May-16",798052761,638972351,159080410,17001537,141900131,178742],
        ["Jun-16",800253478,637271323,162982154,21365316,141459937,156901],
        ["Jul-16",801305788,630241088,171064700,28882486,142025472,156742],
        ["Aug-16",803435223,623049078,180386145,36981406,143248121,156618],
        ["Sep-16",804756535,614994068,189762468,46115007,143491005,156456]
    ];

    private performance = [
        ["Dec-14","MTD",0,0,0,0,0,0,0,0,0,0],
        ["Dec-14","QTD",0,0,0,0,0,0,0,0,0,0],
        ["Dec-14","YTD",0,0,0,0,0,0,0,0,0,0],
        ["Jan-15","MTD",0.0026,0.0026,0,0,0,0,0.0003,-0.0011,0.0013,0.0003],
        ["Jan-15","QTD",0.0026,0.0026,0,0,0,0,0.0003,-0.0011,0.0013,0.0003],
        ["Jan-15","YTD",0.0026,0.0026,0,0,0,0,0.0003,-0.0011,0.0013,0.0003],
        ["Feb-15","MTD",-0.0007,-0.0007,0,0,0,0,0.0001,0.0082,0.0169,0.0001],
        ["Feb-15","QTD",0.0019,0.0019,0,0,0,0,0.0004,0.0071,0.0183,0.0004],
        ["Feb-15","YTD",0.0019,0.0019,0,0,0,0,0.0004,0.0071,0.0183,0.0004],
        ["Mar-15","MTD",0.0009,0.0009,0,0,0,0,0.0000,0.0093,0.0066,0.0000],
        ["Mar-15","QTD",0.0009,0.0028,0,0,0,0,0.0005,0.0165,0.0250,0.0005],
        ["Mar-15","YTD",0.0028,0.0028,0,0,0,0,0.0005,0.0165,0.0250,0.0005],
        ["Apr-15","MTD",0.0008,0.0008,0,0,0,0,0.0004,0.0061,0.0025,0.0004],
        ["Apr-15","QTD",0.0008,0.0008,0,0,0,0,0.0004,0.0061,0.0025,0.0004],
        ["Apr-15","YTD",0.0036,0.0036,0,0,0,0,0.0009,0.0227,0.0275,0.0009],
        ["May-15","MTD",0.0003,0.0003,0,0,0,0,0.0001,0.0080,0.0080,0.0001],
        ["May-15","QTD",0.0011,0.0011,0,0,0,0,0.0005,0.0141,0.0105,0.0005],
        ["May-15","YTD",0.0040,0.0033,0,0,0,0,0.0010,0.0309,0.0357,0.0010],
        ["Jun-15","MTD",0.0004,0.0004,0,0,0,0,0.0001,0.0060,0.0060,0.0001],
        ["Jun-15","QTD",0.0015,0.0015,0,0,0,0,0.0006,0.0202,0.0166,0.0006],
        ["Jun-15","YTD",0.0044,0.0044,0,0,0,0,0.0011,0.0371,0.0419,0.0011],
        ["Jul-15","MTD",0.0003,0.0004,0,0,0,0,-0.0001,0.0039,0.0039,0.0003],
        ["Jul-15","QTD",0.0003,0.0004,0,0,0,0,-0.0001,0.0039,0.0039,0.0003],
        ["Jul-15","YTD",0.0047,0.0048,0,0,0,0,0.0011,0.0411,0.0460,0.0014],
        ["Aug-15","MTD",-0.0015,0.0001,0,-0.0159,-0.0161,-0.0759,0.0001,0.0041,-0.0200,-0.0036],
        ["Aug-15","QTD",-0.0011,0.0004,0,-0.0159,-0.0161,-0.0759,0.0000,0.0080,-0.0161,-0.0033],
        ["Aug-15","YTD",0.0033,0.0048,0,-0.0159,-0.0161,-0.0759,0.0011,0.0454,0.0251,-0.0022],
        ["Sep-15","MTD",-0.0036,0.0006,0,-0.0217,-0.0216,-0.0010,0.0011,0.0026,-0.0183,-0.0025],
        ["Sep-15","QTD",-0.0047,0.0010,0,-0.0372,-0.0374,-0.0768,0.0011,0.0106,-0.0341,-0.0058],
        ["Sep-15","YTD",-0.0003,0.0054,0,-0.0372,-0.0374,-0.0768,0.0022,0.0481,0.0064,-0.0047],
        ["Oct-15","MTD",0.0005,0.0004,0,0.0012,0.0012,-0.0069,-0.0003,0.0041,0.0085,0.0013],
        ["Oct-15","QTD",0.0005,0.0004,0,0.0012,0.0012,-0.0069,-0.0003,0.0041,0.0085,0.0013],
        ["Oct-15","YTD",0.0003,0.0058,0,-0.0361,-0.0362,-0.0832,0.0018,0.0523,0.0150,-0.0034],
        ["Nov-15","MTD",-0.0010,-0.0005,-0.2022,0.0019,-0.0035,-0.0063,-0.0001,0.0023,0.0030,0.0005],
        ["Nov-15","QTD",-0.0005,0.0000,-0.2022,0.0031,-0.0023,-0.0131,-0.0004,0.0064,0.0116,0.0018],
        ["Nov-15","YTD",-0.0008,0.0054,-0.2022,-0.0342,-0.0396,-0.0889,0.0018,0.0548,0.0180,-0.0029],
        ["Dec-15","MTD",0.0029,0.0000,0.0494,-0.0056,-0.0044,-0.0154,0.0004,0.0020,-0.0042,-0.0004],
        ["Dec-15","QTD",0.0024,0.0000,-0.1628,-0.0025,-0.0067,-0.0284,0.0000,0.0085,0.0073,0.0014],
        ["Dec-15","YTD",0.0021,0.0054,-0.1628,-0.0396,-0.0438,-0.1030,0.0022,0.0569,0.0137,-0.0033],
        ["Jan-16","MTD",-0.0038,0.0008,-0.0053,-0.0242,-0.0228,-0.0002,0.0009,0.0026,-0.0266,-0.0040],
        ["Jan-16","QTD",-0.0038,0.0008,-0.0053,-0.0242,-0.0228,-0.0002,0.0009,0.0026,-0.0266,-0.0040],
        ["Jan-16","YTD",-0.0038,0.0008,-0.0053,-0.0242,-0.0228,-0.0002,0.0009,0.0026,-0.0266,-0.0040],
        ["Feb-16","MTD",-0.0021,0.0008,0.0073,-0.0156,-0.0139,-0.0009,0.0004,0.0056,-0.0120,-0.0017],
        ["Feb-16","QTD",-0.0059,0.0016,0.0020,-0.0395,-0.0364,-0.0011,0.0012,0.0082,-0.0383,-0.0057],
        ["Feb-16","YTD",-0.0059,0.0016,0.0020,-0.0395,-0.0364,-0.0011,0.0012,0.0082,-0.0383,-0.0057],
        ["Mar-16","MTD",0.0020,0.0014,0.0467,0.0012,0.0047,-0.0008,0.0009,0.0084,0.0074,0.0022],
        ["Mar-16","QTD",-0.0038,0.0030,0.0488,-0.0383,-0.0318,-0.0019,0.0022,0.0167,-0.0311,-0.0035],
        ["Mar-16","YTD",-0.0038,0.0030,0.0488,-0.0383,-0.0318,-0.0019,0.0022,0.0167,-0.0311,-0.0035],
        ["Apr-16","MTD",0.0015,0.0003,-0.0014,0.0076,0.0068,-0.0010,0.0007,0.0068,0.0047,0.0015],
        ["Apr-16","QTD",0.0015,0.0003,-0.0014,0.0076,0.0068,-0.0010,0.0007,0.0068,0.0047,0.0015],
        ["Apr-16","YTD",-0.0023,0.0033,0.0473,-0.0310,-0.0253,-0.0029,0.0028,0.0235,-0.0266,-0.0021],
        ["May-16","MTD",0.0016,0.0003,-0.0052,0.0084,0.0069,-0.0008,0.0001,0.0076,0.0056,0.0012],
        ["May-16","QTD",0.0032,0.0006,-0.0066,0.0160,0.0138,-0.0017,0.0008,0.0144,0.0105,0.0028],
        ["May-16","YTD",-0.0007,0.0036,0.0419,-0.0229,-0.0185,-0.0037,0.0029,0.0313,-0.0210,-0.0008],
        ["Jun-16","MTD",0.0028,0.0037,0.0188,-0.0031,-0.0009,-0.1222,0.0012,0.0062,-0.0055,0],
        ["Jun-16","QTD",0.0059,0.0043,0.0121,0.0129,0.0129,-0.1237,0.0020,0.0207,0.0050,0],
        ["Jun-16","YTD",0.0021,0.0073,0.0615,-0.0259,-0.0194,-0.1254,0.0041,0.0377,-0.0263,0],
        ["Jul-16","MTD",0.0013,0.0011,-0.0091,0.0040,0.0023,-0.0010,0.0004,0,0.0146,0],
        ["Jul-16","QTD",0.0013,0.0011,-0.0091,0.0040,0.0023,-0.0010,0.0004,0,0.0146,0],
        ["Jul-16","YTD",0.0034,0.0084,0.0519,-0.0220,-0.0172,-0.1263,0.0045,0,-0.0121,0],
        ["Aug-16","MTD",0.0027,0.0012,0.0062,0.0084,0.0082,-0.0008,0.0001,0,0.0044,0],
        ["Aug-16","QTD",0.0040,0.0022,-0.0029,0.0124,0.0105,-0.0018,0.0004,0,0.0196,0],
        ["Aug-16","YTD",0.0060,0.0095,0.0584,-0.0138,-0.0091,-0.1270,0.0046,0.0380,-0.0066,0],
        ["Sep-16","MTD",0.0016,0.0007,0.0176,0.0012,0.0049,-0.0010,0.0009,0,0.0056,0],
        ["Sep-16","QTD",0.0056,0.0029,0.0146,0.0136,0.0155,-0.0028,0.0013,0,0.0253,0],
        ["Sep-16","YTD",0.0116,0.0102,0.0770,-0.0126,-0.0042,-0.1279,0.0054,0.0380,-0.0011,0]
    ];

    private publicMarketsPerformance = [
            ["Aug-15",-0.0626,-0.0681,-0.0221,0.0010],
            ["Sep-15",-0.0264,-0.0386,-0.0207,0.0079],
            ["Oct-15",0.0830,0.0783,0.0146,0.0004],
            ["Nov-15",0.0005,-0.0067,-0.0072,-0.0088],
            ["Dec-15",-0.0175,-0.0187,-0.0133,0.0016],
            ["Jan-16",-0.0507,-0.0605,-0.0276,0.0116],
            ["Feb-16",-0.0041,-0.0096,-0.0032,0.0169],
            ["Mar-16",0.0660,0.0652,0.0124,0.0163],
            ["Apr-16",0.0027,0.0138,0.0041,0.0100],
            ["May-16",0.0153,0.0023,0.0046,-0.0052],
            ["Jun-16",0.0009,-0.0128,0.0020,0.0256],
            ["Jul-16",0.0356,0.0415,0.0145,0.0061],
            ["Aug-16",-0.0012,-0.0013,0.0016,-0.0033],
            ["Sep-16",-0.0012,0.0036,0.0055,0.0026]
    ];





    //private setStaticColumns(tableChart){
    //
    //    tableChart.columns = [];
    //
    //    var types = ["number", "number","number"];
    //    var names = ["Day", "Name 1", "Name 2"];
    //    for(var i = 0; i < names.length; i++){
    //        var column = new TableColumnDto();
    //        column.type = types[i];
    //        column.name = names[i];
    //        tableChart.columns.push(column);
    //    }
    //}
    //
    //private setStaticRows(tableChart){
    //    tableChart.rows =[
    //        [1,  37.8, 80.8],
    //        [2,  30.9, 69.5],
    //        [3,  25.4,   57],
    //        [4,  11.7, 18.8],
    //        [5,  11.9, 17.6],
    //        [6,   8.8, 13.6],
    //        [7,   7.6, 12.3],
    //        [8,  12.3, 29.2],
    //        [9,  16.9, 42.9],
    //        [10, 12.8, 30.9],
    //        [11,  5.3,  7.9],
    //        [12,  6.6,  8.4],
    //        [13,  4.8,  6.3],
    //        [14,  4.2,  6.2  ]
    //    ];
    //}
    //getLineChartLAternativePerformance(){
    //    this.tableChart_alt_perf= new TableChartDto();
    //    this.setStaticColumns(this.tableChart_alt_perf);
    //    this.setStaticRows(this.tableChart_alt_perf);
    //}
    //getLineChartLPublicPerformance(){
    //    this.tableChart_public_perf= new TableChartDto();
    //    this.setStaticColumns(this.tableChart_public_perf);
    //    this.setStaticRows(this.tableChart_public_perf);
    //}
}