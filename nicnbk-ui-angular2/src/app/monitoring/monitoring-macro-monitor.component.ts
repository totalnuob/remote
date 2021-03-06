import {Component, AfterViewInit, ViewChild} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {CommonFormViewComponent} from "../common/common.component";
import {CommonTableau} from "./common-tableau.component";

import '../../../public/js/viz_v1.js';
import {GoogleChartComponent} from "../google-chart/google-chart.component";

declare var google:any;
declare var $: any;

@Component({
    selector: 'monitoring-macro-monitor',
    templateUrl: 'view/monitoring-macro-monitor.component.html',
    styleUrls: [],
    providers: [],
})
export class MonitoringMacroMonitorComponent extends GoogleChartComponent {
    //ngAfterViewInit():void {
    //    this.tableau_func()
    //}

    constructor(
    ) {
        super();
    }

    drawGraph(){
        this.drawValuesTable('table', this.data, this.dates);
        this.drawLineChart('line_chart', this.data, this.dates, 'MSCI WORLD');

        this.drawValuesTable('table_us', this.dataUs, this.datesUs);
        this.drawLineChart('line_chart_us', this.dataUs, this.datesUs, 'S&P 500');

        this.drawValuesTable('table_china', this.dataChina, this.datesChina);
        this.drawLineChart('line_chart_china', this.dataChina, this.datesChina, 'Shanghai SE composite');

        this.drawValuesTable('table_eu', this.dataEu, this.datesEu);
        this.drawLineChart('line_chart_eu', this.dataEu, this.datesEu, 'STXE 600 Index');
    }


    drawValuesTable(id, input, dates){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "");
        this.setColumnData(data, dates);
        this.setRowData(data, input);

        var options = {
            showRowNumber: false,
            width: '100%',
            height: '100%',
            'allowHtml': true,
            cssClassNames: {
                tableCell: '',
            }
        };

        var colorFormatter = new google.visualization.ColorFormat();
        colorFormatter.addRange(-5, 1, 'white', '#9C0824');
        colorFormatter.addRange(1, 2, 'white', '#B41F27');
        colorFormatter.addRange(2, 3, 'white', '#CC312B');
        colorFormatter.addRange(3, 4, 'white', '#E86753');
        colorFormatter.addRange(4, 5, 'black', '#FCB4A5'); // pink
        colorFormatter.addRange(5, 6, 'black', '#B9D7B7'); // green
        colorFormatter.addRange(6, 7, 'black', '#74AF72');
        colorFormatter.addRange(7, 8, 'white', '#428F4A');
        colorFormatter.addRange(8, 9, 'white', '#297839');
        colorFormatter.addRange(9, 10, 'white', '#09622A');
        colorFormatter.addRange(10, 15, 'white', '#09622A');
        for(var i = 1; i <= dates.length; i++){
            colorFormatter.format(data, i);
        }

        //// SET FORMAT FOR GDP ROW (index =10)
        //for(var i = 1; i <= dates.length; i++){
        //    var bgColor = '#9C0824';
        //    var textColor = 'white';
        //    var value = data.getValue(10, i);
        //    if(value >= 0 && value < 0.3){
        //        bgColor = '#B41F27';
        //        textColor = 'white';
        //    }else if(value >= 0.3 && value < 0.6){
        //        bgColor = '#CC312B';
        //        textColor = 'white';
        //    }else if(value >= 0.6 && value < 0.9){
        //        bgColor = '#E86753';
        //        textColor = 'white';
        //    }else if(value >= 1.2 && value < 1.5){
        //        bgColor = '#FCB4A5';
        //        textColor = 'black';
        //    }else if(value >= 1.5 && value < 1.8){
        //        bgColor = '#B9D7B7';
        //        textColor = 'black';
        //    }else if(value >= 1.8 && value < 2.1){
        //        bgColor = '#74AF72';
        //        textColor = 'black';
        //    }else if(value >= 2.1 && value < 2.4){
        //        bgColor = '#428F4A';
        //        textColor = 'white';
        //    }else if(value >= 2.4 && value < 2.7){
        //        bgColor = '#297839';
        //        textColor = 'white';
        //    }
        //    else if(value >= 2.7 ){
        //        bgColor = '#09622A';
        //        textColor = 'white';
        //    }
        //    data.setProperty(10, i, 'style', 'background-color:' + bgColor + '; color: ' + textColor);
        //}

        var chart = this.createTableChart(document.getElementById(id));
        chart.draw(data, options);
    }

    private setColumnData(data, dates){
        for(var i = 0; i < dates.length; i++){
            data.addColumn("number", dates[i]);
        }
    };

    private setRowData(data, input){
        data.addRows(input);
    }

    drawLineChart(id, input, dates, str){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "Date");
        data.addColumn("number", "Macro Score");
        data.addColumn("number", str);

        data.addRows(this.getLineChartData(input, dates));

        var options = {
            showRowNumber: false,
            width: 1100,
            height: 400,
            'allowHtml': true,
            cssClassNames: {},
            hAxis: {
                showTextEvery: 6
            }
        };

        var chart = this.createLineChart(document.getElementById(id));
        chart.draw(data, options);
    }

    private getLineChartData(input, dates){
        var rows = [];
        for(var i = dates.length - 1; i >= 0; i--){
            var row = [dates[i], input[9][1+i], input[10][1+i]];
            rows.push(row);
        }
        //console.log(rows);
        return rows;
    }

    private dates = ["Apr-17","Mar-17","Feb-17","Jan-17","Dec-16", "Nov-16", "Oct-16", "Sep-16",
        "Aug-16", "Jul-16","Jun-16","May-16", "Apr-16","Mar-16","Feb-16","Jan-16", "Dec-15","Nov-15", "Oct-15","Sep-15","Aug-15","Jul-15","Jun-15", "May-15", "Apr-15",
        "Mar-15", "Feb-15", "Jan-15", "Dec-14", "Nov-14", "Oct-14", "Sep-14", "Aug-14", "Jul-14", "Jun-14", "May-14", "Apr-14", "Mar-14", "Feb-14", "Jan-14", "Dec-13",
        "Nov-13", "Oct-13", "Sep-13", "Aug-13", "Jul-13", "Jun-13", "May-13", "Apr-13", "Mar-13", "Feb-13", "Jan-13", "Dec-12", "Nov-12", "Oct-12", "Sep-12", "Aug-12",
        "Jul-12", "Jun-12", "May-12", "Apr-12", "Mar-12", "Feb-12", "Jan-12", "Dec-11", "Nov-11", "Oct-11", "Sep-11", "Aug-11", "Jul-11", "Jun-11", "May-11", "Apr-11",
        "Mar-11", "Feb-11", "Jan-11", "Dec-10", "Nov-10", "Oct-10", "Sep-10", "Aug-10", "Jul-10", "Jun-10", "May-10", "Apr-10", "Mar-10", "Feb-10", "Jan-10", "Dec-09",
        "Nov-09", "Oct-09", "Sep-09", "Aug-09", "Jul-09", "Jun-09", "May-09", "Apr-09", "Mar-09", "Feb-09", "Jan-09", "Dec-08", "Nov-08", "Oct-08", "Sep-08", "Aug-08",
        "Jul-08", "Jun-08", "May-08", "Apr-08", "Mar-08", "Feb-08", "Jan-08", "Dec-07", "Nov-07", "Oct-07", "Sep-07", "Aug-07", "Jul-07", "Jun-07", "May-07", "Apr-07",
        "Mar-07", "Feb-07", "Jan-07", "Dec-06", "Nov-06", "Oct-06", "Sep-06", "Aug-06", "Jul-06", "Jun-06", "May-06", "Apr-06", "Mar-06", "Feb-06", "Jan-06", "Dec-05"
    ];
    private data = [
        ["Global Leading Indicators",6.7,6.7,6.6,6.6,5.9,5.9,5.6,5.0,4.1,4.8,4.4,3.5,4.2,3.6,3.5,4.2,4.0,5.1,5.1,4.3,5.1,4.8,5.4,5.2,5.7,5.4,4.4,4.2,4.5,5.1,5.3,6.1,6.2,6.7,7.3,7.0,6.0,7.2,6.4,7.0,6.6,7.8,7.1,8.2,8.1,7.6,6.7,6.4,6.1,5.3,5.8,5.7,5.2,4.5,4.7,4.6,4.0,4.3,3.9,4.1,4.2,5.0,4.8,3.9,4.1,3.8,3.9,3.0,3.2,5.4,5.3,5.9,5.7,6.9,6.6,6.4,6.8,6.4,6.0,6.0,6.3,6.7,6.7,7.0,7.2,7.5,6.9,7.2,7.2,7.2,6.9,6.7,6.6,6.1,5.9,5.3,4.8,3.3,3.1,2.7,1.7,1.9,2.1,3.2,3.7,3.1,4.3,4.3,4.5,4.0,4.3,4.9,5.1,5.2,5.3,5.8,5.8,6.5,6.2,5.9,5.7,6.2,5.3,5.2,6.0,5.3,5.4,5.7,5.4,5.4,5.6,5.5,5.5,6.0,5.4,6.3,5.6],
        ["Global Inflation",6.9,6.8,6.8,6.7,5.6,5.9,5.8,5.5,5.3,4.8,4.5,4.3,4.0,4.1,3.8,4.4,4.4,4.2,4.1,4.5,4.4,4.5,4.5,4.2,4.0,3.8,3.9,3.6,4.0,4.4,4.3,4.7,4.9,5.2,5.2,5.0,5.0,4.9,4.7,4.7,4.6,4.3,4.0,4.4,4.3,3.9,3.6,3.4,2.8,3.4,3.4,3.6,3.3,3.5,3.8,3.7,3.6,3.5,3.6,3.9,4.4,4.6,4.6,4.7,4.8,5.7,5.9,6.1,6.4,6.2,6.8,6.7,6.8,6.8,6.8,6.8,6.1,5.7,5.6,5.4,5.0,5.0,4.7,4.5,4.5,4.5,4.2,3.9,4.0,3.4,3.3,3.1,2.7,2.5,2.5,2.3,2.3,1.9,2.4,2.4,2.3,3.9,4.6,5.5,6.3,6.9,7.1,6.7,6.6,6.7,6.9,7.0,7.1,5.9,5.8,5.5,5.5,5.6,5.9,5.7,5.6,5.4,5.1,4.9,5.0,5.2,5.4,5.8,6.9,7.1,7.4,7.0,7.0,6.8,6.3,6.5,6.3],
        ["Global Manufacturing",7.1,7.1,7.0,7.0,6.4,6.3,6.3,5.1,4.7,5.5,4.0,3.4,3.6,4.1,2.9,4.2,3.5,4.0,4.5,3.1,3.0,3.4,4.4,3.8,3.6,4.7,4.8,4.4,4.5,4.6,5.3,5.2,4.4,7.4,5.9,4.7,4.9,4.5,5.7,4.7,5.5,6.0,5.7,7.3,6.8,3.9,6.9,6.0,5.3,4.1,7.3,4.6,6.1,4.3,5.8,5.9,3.3,5.6,4.2,5.0,4.6,5.1,5.1,4.4,5.6,4.7,4.6,3.3,4.8,4.7,4.3,5.5,4.7,6.6,4.6,7.2,5.5,6.6,5.6,6.4,6.4,5.9,5.9,5.6,6.4,6.6,5.2,7.9,5.3,5.9,6.0,5.9,5.8,6.5,5.0,5.7,4.1,4.1,2.7,0.6,0.7,0.4,-0.5,0.3,3.0,3.5,3.8,3.7,4.5,3.9,3.9,4.0,5.1,5.8,6.2,4.6,4.5,4.9,6.2,5.7,5.5,5.5,6.2,3.8,5.6,5.3,3.0,6.4,5.5,4.5,4.9,5.3,4.3,5.8,6.0,4.1,4.6],
        ["Global Service",6.0,6.0,6.1,6.1,5.7,4.7,4.7,3.6,2.9,3.7,3.9,3.0,3.6,3.5,2.9,4.4,4.4,5.3,5.8,4.7,6.4,6.7,5.5,6.1,6.6,6.0,5.5,5.2,5.2,5.7,5.2,7.0,7.2,6.6,7.3,6.3,5.1,5.6,3.6,5.1,4.0,4.8,5.1,3.9,8.9,6.3,4.8,4.6,4.2,6.4,7.2,6.1,7.1,5.8,4.5,5.1,2.9,2.2,3.8,4.6,4.6,5.9,6.0,6.4,3.8,4.3,4.1,3.9,5.0,5.1,5.7,6.2,6.5,6.8,7.5,7.6,7.7,7.7,7.5,7.0,6.7,7.6,7.8,8.3,8.5,7.8,6.8,6.2,6.5,6.1,6.8,6.5,5.8,4.8,4.7,3.5,3.1,1.6,1.7,2.1,0.4,-1.7,0.1,2.8,3.6,3.0,1.5,3.7,3.9,2.0,2.1,-2.1,2.5,2.3,2.9,2.2,2.2,2.6,4.2,3.4,2.4,1.5,2.8,3.5,1.5,2.1,2.1,2.0,0.5,1.6,1.4,3.8,5.5,4.3,3.9,1.5,5.5],
        ["Global Trade",5.9,6.3,6.3,6.3,5.6,5.0,5.0,5.1,5.1,3.2,4.6,4.2,4.4,4.8,5.8,5.5,6.0,6.8,6.9,6.8,6.8,7.0,7.0,6.7,7.0,6.9,7.4,7.7,7.7,7.5,7.5,7.7,7.3,7.4,7.2,7.3,7.4,7.3,7.5,7.9,7.3,6.9,6.8,6.3,6.8,6.7,6.3,6.5,6.5,6.5,6.1,6.8,6.0,6.2,5.9,6.5,6.1,6.1,6.3,6.6,5.7,6.1,5.8,5.7,6.6,6.9,6.9,6.9,7.0,6.9,6.9,7.1,7.0,7.2,7.2,7.4,7.6,6.5,6.5,6.2,6.1,6.0,5.9,5.4,5.2,5.0,4.6,4.5,4.5,3.9,3.8,3.7,2.9,2.8,2.6,1.9,1.8,1.6,2.0,1.6,1.5,3.6,5.2,5.4,6.0,6.2,5.9,6.4,6.5,6.3,6.6,6.8,6.6,7.5,7.5,7.3,7.4,7.3,7.3,7.3,7.3,7.4,7.4,7.4,7.0,6.5,6.4,6.5,6.5,6.4,6.7,6.8,6.8,7.0,6.8,6.8,7.0],
        ["Global Liquidity",4.9,4.9,4.9,5.1,5.2,5.4,5.9,5.4,5.7,5.6,6.8,5.8,6.4,6.2,8.7,8.3,6.4,6.2,6.7,8.7,9.6,7.5,8.1,6.7,6.3,5.8,3.9,4.4,4.2,3.3,3.3,2.9,2.6,2.4,1.6,2.1,1.6,1.0,2.0,2.6,1.8,2.8,2.7,3.2,3.8,3.1,3.7,3.5,3.1,3.7,4.1,4.0,4.6,4.7,5.1,5.2,6.2,6.1,6.6,8.1,7.4,7.5,7.2,7.6,8.6,8.3,7.4,8.8,7.5,5.3,3.8,3.7,3.0,3.1,3.0,3.2,3.9,3.7,3.2,3.1,3.8,3.8,6.7,6.3,5.1,4.3,4.9,5.0,4.8,4.1,4.3,4.3,5.4,6.1,7.7,5.4,5.1,5.7,7.1,7.5,9.8,10.1,12.1,13.4,5.8,5.5,6.9,5.4,5.1,9.0,8.2,6.4,12.5,8.3,5.2,7.1,1.8,7.9,6.5,1.3,4.4,5.6,0.3,-1.3,3.1,0.5,2.2,4.0,1.9,4.2,3.1,4.2,2.1,3.2,1.1,3.2,4.5],
        ["Global Consumer Spending",7.7,7.4,7.4,7.4,6.1,5.7,5.7,5.6,4.7,5.6,5.5,5.8,8.3,5.9,5.1,5.0,6.6,5.5,5.3,5.4,5.8,5.8,5.1,6.7,6.2,7.5,5.3,4.8,6.3,6.0,6.9,5.3,7.0,5.5,6.4,5.4,6.6,8.9,6.1,5.5,7.5,6.0,5.7,5.7,4.4,4.4,4.2,3.8,3.8,3.9,3.9,4.3,5.3,3.2,3.5,4.2,3.6,3.9,2.1,3.0,4.5,4.1,4.7,5.7,5.0,3.6,5.1,5.0,4.6,5.1,4.2,5.1,5.9,6.5,5.2,6.1,7.4,6.2,6.5,5.2,6.2,5.5,4.9,4.7,5.1,6.2,3.8,4.6,6.8,4.1,4.5,2.7,5.3,4.0,4.0,3.7,4.2,3.9,2.9,4.5,4.2,2.7,2.8,2.6,4.1,4.6,5.6,5.9,6.4,6.7,4.0,5.7,7.0,6.6,5.3,5.4,6.0,5.7,4.7,5.7,6.4,6.4,5.0,5.8,8.1,4.9,5.3,4.7,5.0,6.3,5.1,5.3,6.5,6.1,4.7,6.2,7.4],
        ["Global Housing Prices",5.5,5.5,5.5,5.5,4.7,4.8,4.8,4.8,5.5,4.8,3.6,3.1,2.7,5.3,3.3,3.3,3.6,4.1,3.6,4.7,2.9,5.3,3.3,4.1,5.5,3.3,4.6,2.1,5.5,5.1,6.4,5.2,4.8,7.0,5.5,5.2,6.7,5.8,4.7,6.2,6.1,5.1,5.3,6.4,5.5,5.1,6.6,6.9,5.0,7.8,7.2,6.5,6.6,6.3,6.0,5.8,5.7,5.3,5.4,5.3,5.9,6.3,5.7,3.8,5.7,5.1,4.8,6.5,5.8,6.6,6.8,5.8,7.6,5.5,5.9,6.3,6.1,6.5,6.9,6.0,6.2,6.3,4.7,6.6,6.6,6.6,4.6,6.2,4.8,4.8,5.2,5.3,4.8,5.0,5.3,5.0,4.7,4.1,4.8,5.2,3.4,3.2,4.1,4.9,4.7,4.8,5.8,6.9,6.2,6.0,6.5,6.3,6.4,6.5,6.1,7.2,6.0,6.1,5.6,5.6,4.6,5.7,4.5,6.0,4.9,4.2,4.3,4.5,4.4,4.5,3.8,5.0,4.7,4.8,4.4,4.7,5.0],
        ["Global Investor Sentiment",5.3,5.3,5.3,4.3,5.7,6.2,4.4,5.0,5.2,6.7,7.5,4.0,5.0,5.4,6.3,4.2,5.1,5.8,6.6,5.3,5.2,3.4,4.5,3.1,2.7,4.9,6.4,6.3,6.6,7.4,6.8,4.7,5.9,2.0,2.9,3.5,3.0,3.3,4.8,4.5,6.8,6.5,5.6,5.4,4.3,6.7,2.5,2.9,2.6,3.8,2.8,5.5,5.5,5.8,4.4,5.8,5.7,3.8,3.4,3.6,4.0,5.6,6.5,7.3,5.3,4.9,6.8,5.4,4.1,3.5,3.5,1.5,3.9,3.7,4.0,5.2,6.6,7.7,8.8,6.7,3.3,5.0,4.3,3.3,3.6,3.3,3.3,3.0,5.8,5.8,3.6,4.8,4.6,6.8,6.2,5.2,6.9,7.9,5.3,2.6,3.4,5.5,6.4,5.2,5.0,5.7,3.9,4.4,6.2,6.2,5.1,3.3,4.2,3.4,3.8,6.6,6.1,6.7,7.9,6.7,6.3,5.6,8.7,6.4,7.1,7.3,7.6,7.6,6.6,6.1,5.3,5.9,6.9,5.1,6.0,3.7,5.7],
        ["Macro Score",7.6,7.9,7.8,7.7,5.5,5.5,5.4,5.0,4.8,5.0,5.0,4.1,4.7,4.7,4.7,4.8,4.9,5.2,5.4,5.3,5.5,5.4,5.3,5.2,5.3,5.4,5.1,4.8,5.4,5.4,5.7,5.4,5.6,5.6,5.5,5.2,5.1,5.4,5.1,5.4,5.6,5.6,5.3,5.6,5.9,5.3,5.0,4.9,4.4,5.0,5.3,5.2,5.5,4.9,4.8,5.2,4.6,4.5,4.4,4.9,5.0,5.6,5.6,5.5,5.5,5.2,5.5,5.4,5.4,5.4,5.3,5.3,5.7,5.9,5.6,6.2,6.4,6.3,6.3,5.8,5.6,5.8,5.7,5.7,5.8,5.8,4.9,5.4,5.5,5.1,4.9,4.8,4.9,5.0,4.9,4.2,4.1,3.8,3.6,3.2,3.1,3.3,4.1,4.8,4.7,4.8,5.0,5.3,5.6,5.6,5.3,4.7,6.3,5.7,5.3,5.8,5.0,5.9,6.1,5.3,5.4,5.5,5.0,4.6,5.4,4.6,4.6,5.3,4.7,5.1,4.8,5.4,5.5,5.5,5.0,4.8,5.7],
        ["MSCI WORLD",9.3,9.8,9.3,8.1,7.2,5.3,5.0,6.1,5.9,6.0,3.9,4.4,4.3,3.6,0.5,0.3,3.4,4.6,5.2,1.4,3.6,7.1,6.5,7.5,7.7,7.1,7.9,6.3,7.0,7.6,7.2,7.1,8.1,7.8,8.5,8.3,8.2,8.3,8.7,7.9,9.3,9.2,9.2,8.7,7.8,8.8,7.8,8.8,9.5,8.9,8.5,8.7,7.1,6.6,6.2,6.7,5.9,5.2,5.0,3.7,6.5,7.0,6.8,5.6,4.3,4.4,5.2,2.6,5.1,7.2,7.8,8.4,9.2,8.3,8.6,8.1,8.0,7.0,7.6,7.1,5.6,6.0,4.7,5.0,6.0,5.9,5.2,4.9,5.2,4.9,4.5,4.5,4.1,3.7,3.0,2.9,2.2,1.3,0.3,0.4,0.3,-1.1,-2.2,-1.4,1.5,2.0,2.9,5.6,5.4,4.2,4.6,4.9,7.1,7.6,8.8,8.4,7.5,7.7,8.7,9.3,9.2,8.5,8.5,9.1,9.4,9.4,9.2,8.4,8.2,7.7,7.7,8.0,9.4,9.1,9.1,9.7,8.9],
    ];
    private datesUs = ["Apr-17","Mar-17","Feb-17","Jan-17","Dec-16", "Nov-16", "Oct-16", "Sep-16",
        "Aug-16", "Jul-16","Jun-16","May-16", "Apr-16","Mar-16","Feb-16","Jan-16", "Dec-15","Nov-15", "Oct-15","Sep-15","Aug-15","Jul-15","Jun-15", "May-15", "Apr-15",
        "Mar-15", "Feb-15", "Jan-15", "Dec-14", "Nov-14", "Oct-14", "Sep-14", "Aug-14", "Jul-14", "Jun-14", "May-14", "Apr-14", "Mar-14", "Feb-14", "Jan-14", "Dec-13",
        "Nov-13", "Oct-13", "Sep-13", "Aug-13", "Jul-13", "Jun-13", "May-13", "Apr-13", "Mar-13", "Feb-13", "Jan-13", "Dec-12", "Nov-12", "Oct-12", "Sep-12", "Aug-12",
        "Jul-12", "Jun-12", "May-12", "Apr-12", "Mar-12", "Feb-12", "Jan-12", "Dec-11", "Nov-11", "Oct-11", "Sep-11", "Aug-11", "Jul-11", "Jun-11", "May-11", "Apr-11",
        "Mar-11", "Feb-11", "Jan-11", "Dec-10", "Nov-10", "Oct-10", "Sep-10", "Aug-10", "Jul-10", "Jun-10", "May-10", "Apr-10", "Mar-10", "Feb-10", "Jan-10", "Dec-09",
        "Nov-09", "Oct-09", "Sep-09", "Aug-09", "Jul-09", "Jun-09", "May-09", "Apr-09", "Mar-09", "Feb-09", "Jan-09", "Dec-08", "Nov-08", "Oct-08", "Sep-08", "Aug-08",
        "Jul-08", "Jun-08", "May-08", "Apr-08", "Mar-08", "Feb-08", "Jan-08", "Dec-07", "Nov-07", "Oct-07", "Sep-07", "Aug-07", "Jul-07", "Jun-07", "May-07", "Apr-07",
        "Mar-07", "Feb-07", "Jan-07", "Dec-06", "Nov-06", "Oct-06", "Sep-06", "Aug-06", "Jul-06", "Jun-06", "May-06", "Apr-06", "Mar-06", "Feb-06", "Jan-06", "Dec-05"
    ];
    private dataUs = [
        ["Leading Indicators",6.3,6.3,6.4,6.4,6.3,6.3,6.4,6.4,4.9,7.2,6,4.6,6.7,5.1,5.1,4.4,4.3,6.3,6.6,4.6,5.2,4.9,7,6.7,7.2,6.1,4.8,5.6,6.9,7,6.7,7.3,6.6,7.5,8.1,7.7,6.8,8.9,7.6,7.3,6.1,9.7,6,9.1,8,7.3,6.2,7.1,7.8,5.4,7.2,7.4,6.5,5.4,6.3,6.6,4.8,5.9,4.6,6,5.2,6.8,7,6.1,6.4,5.7,6.5,4.4,4,6.8,5,7,5.2,7.5,6.9,6.2,7.5,7.2,6.4,6.7,6.4,5.9,5.8,5.7,5.9,6.5,5.2,5.6,5.8,5.9,5.4,5.6,5.7,5.7,5.5,5,4.9,2.6,2.7,2,0.8,0.5,-0.1,0.4,1.7,-0.2,2.8,2,2.5,-0.1,0.4,0.5,0.5,0.6,1.3,1.2,1.5,3.8,3.1,3.7,3.5,5.1,3.6,3.6,5.4,3.9,4.2,4.6,4.4,3.9,4.9,3.9,4,6.1,5.3,6.9,5.4],
        ["Inflation",3.3,3.6,3.6,4.4,3.6,3.6,3.6,4.4,4.5,4.3,4.5,4.4,4.8,3.7,4.5,4,3.8,4,5,3.9,3.3,4.3,4.4,5.4,6.9,6,5.8,5,5.1,5.5,5.6,4.9,5,5.9,7.3,7.7,7.5,6.9,6.2,6.1,6.8,6.1,4.6,5.3,5.9,4.6,5.1,4.3,3.6,4.4,5.5,2.7,6.3,5.2,4.8,5.2,4.2,4.2,5.4,4.8,5.8,5.5,6.1,6.7,6.8,5.3,5.5,5.1,6.9,7.5,7.1,6.5,5.7,6.1,6.8,7.1,5.8,5,5.3,5.4,6.1,5.7,5.2,4.6,4.2,4,3.4,3.1,4,3.5,5,4.7,3.9,4.2,3.2,5,4.9,4.9,4.6,3.2,1.1,2.1,2.4,3.1,4,3.8,3.1,5.9,1.9,5.3,4.7,6.2,6.8,6.2,5,6,4.6,5,4.4,4.7,4.9,5.7,6.4,5.3,5.3,3,5,4.2,5.3,5.9,6.3,6.1,6,6.3,5.7,6.1,5],
        ["Manufacturing",4.4,4.4,4.5,4.3,4.4,4.4,4.5,4.3,3.8,5.4,4.1,3.3,3.7,3.4,3.1,3.9,2.7,3.2,4.3,3.6,3.9,4.4,5,4.5,4.3,5.4,5,5.8,6.1,7,6.9,6.6,5.7,9.2,7.3,6.9,7.2,7.4,7.3,5.2,6.7,7.9,6.7,7.8,7.6,5,6.5,5.8,5.9,4.7,7.2,5,6,4.8,5.3,6.2,3.3,5.3,5.1,5.3,5.6,5,5.3,5,5.5,5,4.3,3.9,4.9,5.2,5.1,5.6,5.3,7.6,5.4,7.1,6.3,6.5,6,6.9,6.5,6.3,6,6,6.4,6.2,5.2,7.7,5.4,5.5,6.2,5.4,5.5,6,3.6,4.4,3.2,2.8,1.6,-0.1,0.2,0.4,-1,0.1,2.2,3.3,3.9,3.7,3.8,3.9,3.7,4.7,5.3,5.4,5.4,4.8,4.8,6.2,6,5.6,6.3,5.8,5.9,3.9,5.5,5,3.6,6.8,5.4,4.4,5.4,5.4,4.6,6.2,6.6,4.4,5.3],
        ["Service",4.2,4.4,4.6,4.7,4.4,4.4,4.6,4.7,1.2,3.1,3.6,1.4,3.2,1.9,0.4,1.8,3.5,4.9,5.2,4.5,5.8,6.5,4.3,4.8,6.2,6.7,6,4.8,4.4,7,5.7,7.6,8.2,8.6,8.9,8.3,5.5,4.4,1.8,6,3.1,4.6,5.1,3.9,8.9,6.3,4.8,4.6,4.2,6.4,7.2,6.1,7.1,5.8,4.5,5.1,2.9,2.2,3.8,4.6,4.6,5.9,6,6.4,3.8,4.3,4.1,3.9,5,5.1,5.7,6.2,6.5,6.8,7.5,7.6,7.7,7.7,7.5,7,6.7,7.6,7.8,8.3,8.5,7.8,6.8,6.2,6.5,6.1,6.8,6.5,5.8,4.8,4.7,3.5,3.1,1.6,1.7,2.1,0.4,-1.7,0.1,2.8,3.6,3,1.5,3.7,3.9,2,2.1,-2.1,2.5,2.3,2.9,2.2,2.2,2.6,4.2,3.4,2.4,1.5,2.8,3.5,1.5,2.1,2.1,2,0.5,1.6,1.4,3.8,5.5,4.3,3.9,1.5,5.5],
        ["Trade",5.9,6.3,6.8,6.8,6.3,6.3,6.8,6.8,4.5,4.8,3.5,3.1,4,4.4,4.4,4.9,5.2,5.6,5.5,5.7,4.8,5.8,5.2,5.5,5.3,3.9,7.1,5.7,5.3,5.4,5.1,5,6.2,6.1,5.8,5.1,4.6,5.3,7.6,7.9,8.5,7.7,7.4,6.9,6.6,6.7,7.1,5,5.7,6.3,6.5,6.8,7.5,5.8,6.5,7,6.9,7,7,5.8,5.6,5,6.9,5.5,5.7,6.1,6.4,6.6,6.2,6.1,5.6,4.7,5.2,5,4.9,4.5,5.2,5,4.9,4.7,4.8,5.1,4.8,4.7,4.7,4.9,5.5,5.7,5.7,5.5,5.6,5.7,5.4,5.4,5.7,5.9,5.9,6.2,8.3,8.2,8.3,9.1,6.7,6.6,6.6,5.7,7.1,6.9,6.6,7.4,5.9,6.6,7.4,7,7.7,7.5,7.6,7,7.2,7.1,6.8,6.6,7.5,7.6,7.1,7,6.9,6.2,6.2,6.3,6.7,6.7,6.9,6.9,5.3,4.8,5],
        ["Liquidity",3.0,3.0,3.0,2.1,3,3,3,2.1,2.6,2.6,5,2.7,4,3.5,8.4,7.3,2.8,3.1,3.7,7.3,8.2,3.9,4.8,4.2,3.7,3.9,3.6,4.7,3.2,2.9,3,2.2,1.8,1.6,-0.2,1.4,0.3,-0.2,2,3,0.5,3.6,3.5,4,5,3.5,4.2,3.7,2.3,3,3.4,2.8,2.9,2.8,3.1,2.7,4.3,3.9,4.3,6.7,4.3,3.7,4.6,4.6,6.4,7.7,7.6,10.5,10.1,6.9,4.4,4.5,2.8,3,3.5,3.7,3.2,4.4,3.6,3.8,3.8,3.3,4.2,4.2,2.6,1.6,2.7,3,2.3,3.4,3.8,3.4,4.5,4.5,4.8,5.6,5.7,6.7,7.9,7.4,7.7,9.7,8.8,7.9,7.6,7.4,7.7,7.2,7.5,9.7,11.8,10.7,10,10.8,8.7,9.4,11.5,12.4,6,2.8,3.8,4.1,3.4,1.3,1.6,2,2.2,3.1,2.7,3.1,2.6,3,1.9,1.8,1.6,2.1,2.5],
        ["Consumer Spending",5.7,5.7,5.6,6.4,5.7,5.7,5.6,6.4,6.2,4.9,4.7,4.1,4.4,4.4,4.5,5.2,4.6,4.3,4.7,5.3,5.2,4.3,5.2,4.9,5.2,5.8,6,6.1,6.2,6.4,7.2,7.1,7.5,7.7,7.1,6.8,6.6,6.3,5.3,5.4,3.8,3.5,3.5,4.4,4.6,4.3,4.3,3.7,3.1,2.9,3.8,3.1,7,6.7,6.3,5.3,4.2,4.8,5.3,5.7,6.6,6.9,7.1,6.7,7.3,6.4,5.4,5.8,5.7,7,6.6,6.9,7.1,6.4,7,6.3,6,5.7,5.2,5.5,6.1,5.8,5.7,6,5.7,5.5,4.9,5.4,4.7,4.1,3.7,3.3,3,2.4,2.2,2,1.8,0.8,0.4,0.1,0,0.7,1,2.6,3.3,3.7,4.5,5.5,2.2,1.9,1.4,2.4,3.3,2.6,3,3.3,3.3,4.1,3.2,4.1,4.2,5.4,6.5,5.9,5.5,4.6,5.8,6.8,6.7,7.2,7.7,6.7,7.8,7.2,7.2,7.9,4.6],
        ["Housing Market",5.6,5.7,5.7,5.8,5.7,5.7,5.7,5.8,6.2,6.2,6.4,5.6,5.5,7.2,6.1,6.3,6.9,6.2,5.7,8.8,5.8,7.4,7.3,6.8,7.4,5.4,5,4.8,7.1,5.7,7,5.1,5.1,6.1,4.9,4.9,4.7,5,4.6,4.9,5.9,5.8,5.9,6.1,6.5,7.1,6.7,7.6,6.6,8.8,8.2,7.9,8.3,8,8.4,8.1,7.8,6.8,7.2,7.5,7.9,7.8,7.3,6.8,7.3,7.8,5,7.2,5.1,6.2,6.2,4.5,5.3,4.6,2.9,5.9,4,4.2,4.5,3.8,4.4,3,3.9,6.1,6.6,5.8,4.8,5.3,4.3,7.5,7.3,6.4,5.8,5.3,4.8,4.2,3.4,2.5,4.1,4.7,3.5,1.3,2.6,2.8,2.5,2.5,3,1.9,1.8,1.8,2.3,1.5,1.9,1.7,1.3,1.5,1.7,1.6,1.7,1.1,1.6,2.6,1.5,1.6,2.4,2.2,1.4,1.4,1.3,0.7,0.8,2,1.4,3,3,5.2,3.8],
        ["Investor Sentiment",2.8,3.3,3.3,3.0,3.3,3.3,3.3,3,4.1,4.5,3.9,1.3,3.3,3.2,4.1,3.7,2.2,3.9,5.8,3.7,3.7,0.7,4.1,1.7,2.5,4.7,6.6,6.3,8.1,8.5,8.1,6,9.1,3,4.9,4.8,4.2,3.3,5.6,3.5,9.5,7.6,7,4.6,4,7.3,3.1,4.8,2.8,5.5,2.6,8.2,7,5.9,2.9,4.4,4,2.6,2.7,2.4,2.2,5.6,6,7.2,5.4,3.4,5.9,3.4,4.2,4.5,4.7,1.5,4.4,5.4,4.1,5.6,8,7.4,8.6,6.6,0.8,6,4.5,3.2,6.2,3.9,4.7,4.7,8.3,6.6,4.6,5.9,4.7,7.9,3.2,6.1,5.1,5.8,2.1,2.2,2.9,3.4,4.7,3.9,2.9,5.2,3,3.1,6.8,5.6,3.6,2.3,2,1.5,2,7.1,4.6,5.7,4.2,2.7,4.2,5.2,7.8,4.6,6.2,4.8,7.1,7,5,3.6,4.2,2.9,4.9,3.7,5,2.5,3.6],
        ["Macro Score",7.3,7.4,7.6,8.4,7.4,7.4,7.6,8.4,7.5,7.9,5.7,5.3,6.1,5.9,5.4,6,5.3,6,6.6,5.3,5,5.4,7.2,6.7,6.6,6.6,7.6,8,8.7,8.8,9.2,8.9,9,10.7,9.8,8.9,9.4,10.6,10.8,8.5,9.4,9.8,7.7,9.8,7.8,7.1,7.9,6.8,7.3,5.3,7.4,6.7,9,7.6,8.3,8.5,6.1,8.1,7.3,7.1,7.7,7.8,8.7,7.4,8.5,7.3,7,5.7,5.9,8,7.3,8,7.8,9.1,8,7.8,8.2,7.7,7.1,7.8,7.8,7.6,6.9,6.6,7,7.5,6.6,8.4,7.1,6.4,6.1,5.8,5.6,5.9,4.6,5,4.4,3.2,2.7,1.5,1.7,2.8,0.8,1.8,3.5,3.6,6.2,5.8,4.1,3.5,2.2,4.8,4.9,4.3,5.3,5.2,4.9,6.4,6.3,7.5,7.9,8.8,9.3,8.1,9.5,8,8.1,10.1,9.7,9,10.3,8.6,8.8,10.1,9.6,9.6,6.7],
        ["S&P500",10.0,10.1,10.3,9.5,10.1,9.1,7.5,8.7,9,9.6,8,8,7.2,7.1,4.4,4.6,6.6,7.3,7.3,5,5.8,7.6,7.3,7.9,7.9,7.9,8.5,7.4,8.3,8.6,8.2,7.9,8.5,8,8.6,8.5,8.4,8.5,8.8,8.2,9.3,9.2,9,8.4,8,9,8.4,9.2,9.4,9.5,9,9.1,8,8,8,8.9,8.4,7.9,7.6,6.7,8.5,9,8.6,7.7,6.7,6.5,6.7,4.4,6.1,7.5,8.1,8.6,9.1,8.7,8.8,8.4,8.3,7.5,7.8,7.3,5.9,6.5,5.4,6,6.9,6.6,5.8,5.3,5.6,5.2,4.5,4.6,4.2,3.7,3,2.8,2.2,1.3,0,0.2,0.3,-1,-1.7,-0.4,2.3,2,2.4,5.4,5.2,3.9,4.1,5.1,6.8,7.2,8.5,8.4,7.7,7.5,8.7,9.7,9.3,8.4,8.4,9.6,9.7,10,9.9,9,8.2,7.5,7.4,7.5,8.8,8.7,8.7,9,8.4],
    ];
    private datesChina = [
        "Apr-17","Mar-17","Feb-17","Jan-17",
        "Dec-16","Nov-16","Oct-16","Sep-16","Aug-16","Jul-16","Jun-16","May-16","Apr-16","Mar-16","Feb-16","Jan-16","Dec-15","Nov-15","Oct-15","Sep-15","Aug-15",
        "Jul-15","Jun-15","May-15","Apr-15","Mar-15","Feb-15","Jan-15","Dec-14","Nov-14","Oct-14","Sep-14","Aug-14","Jul-14","Jun-14","May-14","Apr-14","Mar-14","Feb-14","Jan-14",
        "Dec-13","Nov-13","Oct-13","Sep-13","Aug-13","Jul-13","Jun-13","May-13","Apr-13","Mar-13","Feb-13","Jan-13","Dec-12","Nov-12","Oct-12","Sep-12","Aug-12","Jul-12","Jun-12",
        "May-12","Apr-12","Mar-12","Feb-12","Jan-12","Dec-11","Nov-11","Oct-11","Sep-11","Aug-11","Jul-11","Jun-11","May-11","Apr-11","Mar-11","Feb-11","Jan-11","Dec-10","Nov-10",
        "Oct-10","Sep-10","Aug-10","Jul-10","Jun-10","May-10","Apr-10","Mar-10","Feb-10","Jan-10","Dec-09","Nov-09","Oct-09","Sep-09","Aug-09","Jul-09","Jun-09","May-09"
    ];

    private dataChina = [
        ["Leading indicators",5.1,5.4,5.6,6.2,5.4,5.6,6.2,5.1,4.0,3.0,3.7,4.0,3.2,3.5,3.1,2.8,2.5,2.6,2.5,2.9,3.3,3.4,3.2,3.5,4.4,3.8,4.2,4.0,4.4,4.6,5.2,5.9,7.3,6.8,5.9,4.9,4.4,5.4,4.2,4.2,4.2,4.9,5.7,5.7,4.6,4.4,4.7,4.8,6.3,6.0,5.7,5.3,5.3,4.6,3.8,3.7,3.4,2.9,2.6,3.4,3.1,4.5,3.7,2.0,1.8,2.1,2.8,3.4,3.8,4.0,3.2,4.1,3.3,5.4,5.1,5.4,5.2,5.5,5.5,5.4,5.8,6.1,5.9,6.1,6.3,6.2,6.5,6.6,7.1,7.1,6.6,6.5,6.5,6.5,5.1,4.2],
        ["Inflation",8.6,8.4,8.2,7.2,8.4,8.2,7.2,7.5,8.3,7.7,7.8,7.4,7.6,5.2,5.8,5.6,4.7,4.8,5.7,4.7,4.8,4.7,4.4,4.2,3.1,1.4,0.7,0.6,2.2,2.2,3.0,4.3,4.5,4.9,4.3,4.9,4.9,5.5,6.0,6.1,5.8,5.9,4.4,3.1,3.2,2.9,3.3,3.3,3.7,3.6,3.0,2.7,2.8,3.0,3.0,3.1,3.4,3.2,4.3,4.6,4.4,5.3,5.0,4.8,5.9,6.7,7.2,7.4,7.4,7.5,7.2,7.4,7.5,8.0,8.6,8.3,7.2,6.7,6.1,5.9,6.1,6.2,5.8,5.5,5.4,4.7,4.6,4.1,3.4,3.1,2.7,2.3,2.1,2.0,1.6,1.3],
        ["Manufacturing",6.8,7.0,8.3,6.9,7.0,8.3,6.9,6.0,5.3,4.5,4.8,4.4,5.7,2.1,3.5,4.1,3.3,3.2,2.9,2.5,3.6,3.4,3.5,3.9,2.6,2.6,4.2,4.7,3.5,4.3,6.5,5.4,6.7,5.7,5.3,4.2,4.8,2.7,3.4,5.5,5.3,4.2,6.3,5.3,5.1,3.4,4.8,5.4,5.4,4.2,6.8,5.5,3.5,3.8,4.2,3.3,4.1,3.0,3.6,5.4,5.6,4.7,1.0,5.0,2.2,3.4,4.6,3.7,4.0,3.6,3.9,5.2,5.7,3.4,5.6,5.8,5.3,5.3,5.7,5.3,5.4,5.0,6.1,6.8,6.2,4.8,5.9,7.2,6.2,6.1,5.9,5.1,5.1,4.5,4.6,4.5],
        ["Service",6.0,5.9,6.4,5.1,5.9,6.4,5.1,4.6,5.1,5.5,2.9,4.1,4.9,2.3,4.5,4.1,3.5,3.6,2.6,3.3,5.8,4.0,4.7,4.4,4.2,4.1,3.7,5.3,4.8,4.5,5.3,6.3,2.7,6.7,4.6,4.7,5.0,4.4,2.7,3.5,5.4,6.4,4.8,2.1,2.2,1.5,1.9,2.2,3.9,2.1,4.5,4.3,3.4,3.1,0.3,3.8,2.5,4.2,1.5,2.7,6.0,4.8,1.8,2.6,1.6,0.5,0.4,2.9,3.4,2.3,6.7,5.9,7.4,4.6,5.1,6.8,7.0,5.9,6.4,6.6,5.9,7.3,6.8,6.6,6.1,5.7,6.6,7.0,6.6,7.3,6.1,5.5,5.4,4.2,3.8,2.9],
        ["Trade",4.2,4.3,4.5,4.4,4.3,4.5,4.4,5.5,5.7,5.3,5.7,5.4,4.9,5.6,7.5,6.6,6.5,6.7,6.3,6.3,5.9,6.4,7.0,6.4,5.6,7.5,8.2,8.0,8.4,7.8,7.3,7.3,7.7,6.3,6.5,5.1,4.0,3.0,5.6,4.6,5.3,5.3,4.4,4.1,4.3,5.7,5.0,4.9,3.5,4.6,5.0,5.7,4.9,5.7,5.1,4.3,4.6,5.1,4.4,3.5,2.9,2.3,5.6,5.4,5.1,5.1,5.5,4.9,5.6,5.1,4.5,4.0,3.2,2.5,3.5,4.1,5.2,5.4,4.9,5.3,5.8,5.5,5.7,4.4,3.8,4.4,5.0,5.1,4.8,5.2,4.6,4.2,3.9,3.7,3.6,3.9],
        ["Liquidity",4.2,4.3,4.4,4.4,4.3,4.4,4.4,6.6,6.6,6.6,6.7,6.7,6.7,7.0,7.0,6.9,4.4,4.4,5.0,5.0,5.0,5.3,6.6,6.6,6.7,5.9,5.9,5.9,8.1,8.1,8.3,8.2,8.2,8.0,6.9,6.9,6.9,3.6,3.6,3.6,2.1,2.1,2.1,3.4,3.4,3.4,5.9,5.9,5.9,6.7,6.7,6.7,6.6,6.6,6.6,8.6,8.6,8.6,9.5,9.5,9.5,8.0,8.0,7.5,6.9,6.9,7.1,5.0,5.0,4.7,2.8,2.8,2.8,5.1,5.1,5.1,4.8,4.8,4.8,5.5,5.5,5.5,2.6,2.6,2.6,7.1,7.1,7.1,3.7,3.7,3.7,6.4,6.4,6.4,5.0,5.0],
        ["Consumer Spending",4.4,4.7,5.1,4.8,4.7,5.1,4.8,4.2,4.8,4.5,3.6,4.2,4.0,4.8,5.2,4.9,4.7,4.3,5.3,4.0,3.9,3.8,3.9,3.7,4.0,4.4,3.9,3.9,4.1,3.8,4.2,3.9,4.1,3.9,4.5,4.4,4.8,5.4,5.6,6.2,5.0,5.6,4.6,3.3,2.8,3.9,3.2,3.9,3.7,6.1,5.5,5.6,5.7,5.5,4.2,3.7,3.5,4.8,4.4,4.6,4.3,6.5,5.9,5.2,3.9,4.5,4.7,4.7,4.9,5.7,5.1,5.1,5.1,5.2,5.1,4.6,4.9,6.0,6.3,5.3,5.9,6.1,4.9,4.6,5.1,6.2,5.4,5.6,5.2,4.9,5.0,3.9,4.3,3.6,2.7,2.6],
        ["Housing Market",5.7,6.7,7.9,8.3,6.7,7.9,8.3,7.8,7.4,7.3,7.2,7.7,7.6,5.4,6.5,6.2,5.9,5.5,5.4,5.2,5.4,5.0,4.5,3.5,2.7,2.5,5.3,5.4,3.6,3.8,3.6,3.7,3.5,4.0,3.7,3.8,4.1,4.7,7.6,7.7,7.1,7.2,7.5,7.0,7.1,7.2,7.3,7.6,7.1,5.2,5.5,5.3,5.8,5.5,4.6,5.0,4.9,4.1,4.2,3.9,3.6,3.6,1.0,1.5,3.1,3.1,3.4,3.8,3.9,3.9,3.8,2.8,3.7,3.6,4.4,4.6,4.4,4.6,4.8,4.8,5.0,5.1,5.2,5.7,5.8,5.6,7.3,6.8,7.2,7.0,6.5,6.7,6.5,6.2,5.7,4.8],
        ["Investor Sentiment",4.7,4.7,4.6,4.5,4.7,4.6,4.5,3.8,3.4,2.8,2.4,2.0,1.7,1.5,1.3,1.3,1.3,1.3,1.3,1.4,1.6,1.7,1.6,1.4,1.1,0.6,0.3,0.1,0.1,0.3,1.1,2.1,3.1,3.9,4.5,5.1,5.5,6.0,6.4,6.9,7.3,7.7,8.1,8.4,8.6,8.4,8.1,7.8,7.4,7.0,6.6,6.0,5.4,4.8,4.3,4.0,3.7,3.3,2.9,2.5,2.1,1.7,1.6,1.6,1.8,2.2,3.1,4.3,5.3,5.7,5.9,6.0,6.0,6.1,6.1,6.1,6.2,6.2,6.3,6.3,6.5,6.6,6.8,7.1,7.2,7.4,7.6,7.6,7.4,7.1,6.7,6.2,5.6,4.9,4.3,3.6],
        ["Macro Score",4.7,4.7,5.1,5.1,4.7,5.1,5.1,4.6,4.4,4.5,4.1,4.9,4.6,4.7,6.8,6.0,6.9,7.2,5.6,6.6,7.6,7.0,7.3,7.0,7.3,9.4,10.0,10.7,9.8,9.1,8.5,8.7,7.1,7.3,6.1,4.2,2.9,3.4,3.3,2.5,4.7,5.3,4.3,2.5,2.1,2.8,1.6,1.8,2.6,1.9,3.7,4.3,3.8,4.2,2.4,2.9,2.4,3.6,1.2,1.5,2.9,2.9,4.2,3.6,3.1,2.0,1.7,3.1,3.4,2.3,4.7,4.3,3.8,1.9,2.4,4.1,5.2,4.9,4.8,5.1,5.0,5.5,6.7,5.6,4.9,3.1,4.0,4.4,6.1,7.1,6.1,4.8,4.8,4.7,5.0,4.8],
        ["SHANGHAI SE COMPOSITE",4.6,4.6,4.6,4.3,4.6,4.6,4.2,4.0,4.4,4.2,4.3,4.4,4.5,4.8,4.1,4.3,6.3,6.2,6.2,5.6,6.0,7.2,9.0,11.0,12.1,11.6,10.9,11.2,14.2,12.6,9.5,8.9,6.5,6.4,3.4,3.0,2.7,2.7,2.9,2.5,3.6,5.2,3.8,4.2,3.0,1.6,1.4,4.8,3.4,3.9,4.7,4.7,3.8,1.7,2.0,1.9,1.3,1.3,1.9,2.9,3.0,1.7,2.9,1.6,0.5,1.2,2.0,0.8,2.3,3.3,3.7,3.5,5.1,5.4,5.4,4.8,5.1,5.3,6.3,4.9,4.9,5.0,3.9,4.6,5.6,6.5,6.0,5.5,6.0,5.5,4.9,4.3,4.0,5.1,4.3,3.7],
    ];

    private datesEu = [
        "Apr-17","Mar-17","Feb-17","Jan-17",
        "Dec-16","Nov-16","Oct-16","Sep-16","Aug-16","Jul-16","Jun-16","May-16","Apr-16","Mar-16","Feb-16","Jan-16",
        "Dec-15","Nov-15","Oct-15","Sep-15","Aug-15","Jul-15","Jun-15","May-15","Apr-15","Mar-15","Feb-15","Jan-15",
        "Dec-14","Nov-14","Oct-14","Sep-14","Aug-14","Jul-14","Jun-14","May-14","Apr-14","Mar-14","Feb-14","Jan-14"
    ];

    private dataEu = [
        ["Leading Indicators",5.4,5.4,5.4,5.4,5.4,5.4,4.1,2.8,1.7,0.8,0.1,-0.3,-0.3,0.4,1.6,2.8,3.5,3.8,3.9,4.0,4.4,4.9,5.3,5.6,5.7,5.8,5.8,5.8,5.8,5.9,6.0,6.2,6.5,6.8,7.1,7.5,7.9,8.3,8.7,9.0],
        ["Inflation",8.9,8.9,8.9,7.6,8.9,7.6,7.5,7.5,6.9,6.3,5.5,6.3,5.1,6.0,4.5,5.4,6.9,6.8,6.3,6.6,6.6,6.3,6.8,7.0,6.7,6.1,5.5,5.1,6.8,6.8,6.7,7.2,6.8,6.5,6.9,7.0,6.7,6.7,5.6,5.1],
        ["Manufacturing",9.0,9.1,9.1,8.3,9.1,8.3,6.9,6.0,6.3,4.1,5.6,4.0,6.4,4.2,4.8,7.8,5.9,6.9,7.0,6.3,7.3,7.7,7.2,6.7,6.2,7.1,6.8,6.6,5.7,4.5,5.4,4.5,4.5,5.6,5.4,5.0,7.7,6.2,6.8,7.3],
        ["Service",5.4,5.5,5.5,6.0,5.5,6.0,3.8,2.5,3.8,4.0,3.7,4.8,4.4,4.4,4.9,5.7,6.9,7.0,6.9,6.2,7.6,7.0,7.9,7.0,7.7,8.3,7.6,5.4,2.8,1.4,3.7,3.9,5.7,8.4,5.7,7.0,7.3,5.3,6.4,5.0],
        ["Trade",4.5,4.4,4.4,4.3,4.4,4.3,4.5,5.7,5.5,4.9,5.4,5.6,6.0,6.2,5.6,5.7,6.1,5.9,5.7,5.2,5.3,6.1,5.5,5.9,5.6,4.8,6.1,6.7,8.8,8.9,8.4,7.7,6.1,6.7,4.9,5.8,5.6,5.8,6.1,5.8],
        ["Liquidity",9.9,10.0,10.1,10.1,10.1,10.1,10.2,9.9,9.0,9.8,9.6,8.9,8.5,10.1,9.9,9.5,8.6,11.1,10.2,10.2,6.0,7.2,6.1,5.5,4.7,4.4,4.8,6.7,5.4,5.8,5.4,4.7,4.9,5.1,6.2,6.7,6.5,5.4,6.0,6.2],
        ["Consumer Spending",4.3,4.3,4.3,4.1,4.3,4.1,3.5,3.5,6.0,6.2,6.4,5.6,4.9,4.8,6.3,7.1,7.4,7.6,7.3,7.6,7.6,7.6,7.9,7.8,7.9,8.0,7.6,7.4,7.3,6.9,7.0,7.1,7.4,7.6,7.8,7.9,7.8,7.6,7.2,7.4],
        ["Housing Market",7.5,7.5,7.5,7.6,7.5,7.6,7.7,8.3,8.1,8.6,8.2,6.9,7.7,7.1,8.1,7.6,6.6,6.9,7.6,6.4,6.5,5.5,5.4,5.4,4.0,5.4,4.9,5.5,5.9,6.0,6.1,5.4,4.8,4.4,4.2,3.1,4.1,4.1,4.4,4.4],
        ["Investor Sentiment",6.7,4.8,4.8,5.7,4.8,5.7,4.8,4.5,4.3,3.8,5.2,4.5,4.4,4.2,4.6,5.0,6.2,6.0,5.8,5.9,7.0,6.8,7.2,7.6,7.7,7.9,7.2,4.9,4.8,3.3,2.8,3.3,5.7,6.5,6.7,7.5,7.9,8.3,8.4,8.3],
        ["EU Macro Score",5.3,5.0,5.0,4.9,5.0,4.9,4.0,3.2,4.0,2.9,3.5,3.1,4.2,3.2,4.4,5.9,5.3,5.1,5.3,5.5,6.7,7.5,7.4,7.3,8.2,8.1,8.7,8.7,7.9,7.6,8.2,8.1,7.8,8.6,8.0,8.3,9.0,8.7,9.3,9.8],
        ["STXE 600 Index",5.3,5.0,5.0,4.9,5.2,3.7,3.4,3.8,3.9,3.8,2.8,4.1,3.6,3.4,3.1,3.7,5.6,7.1,6.6,4.7,5.9,8.3,7.6,9.1,9.4,10.4,11.1,9.4,7.3,7.9,7.0,7.7,7.8,7.5,8.2,8.5,8.3,8.3,9.0,8.1],
    ];
}