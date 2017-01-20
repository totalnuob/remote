import { Component, OnInit} from '@angular/core';
import {CommonFormViewComponent} from "../common/common.component";
declare var google:any;
@Component({
    selector: 'chart'
})
export class GoogleChartComponent extends CommonFormViewComponent implements OnInit{
    private static googleLoaded:any;

    constructor(){
        //console.log("GoogleChartComponent")
        super();
    }

    getGoogle() {
        return google;
    }
    ngOnInit() {
        if(!GoogleChartComponent.googleLoaded) {
            GoogleChartComponent.googleLoaded = true;
            google.charts.load('current',  {packages: ['corechart', 'bar', 'table', 'line', 'geochart']});
        }
        //setTimeout(() => this.drawGraph(), 1000);
        google.charts.setOnLoadCallback(() => this.drawGraph());
    }

    drawGraph(){
        console.log("DrawGraph base");
    }

    createBarChart(element:any):any {
        return new google.visualization.BarChart(element);
    }

    createTableChart(element:any):any {
        return new google.visualization.Table(element);
    }

    createLineChart(element:any):any {
        return new google.visualization.LineChart(element);
    }

    createDataTable(array:any[]):any {
        return google.visualization.arrayToDataTable(array);
    }

    // TODO: merge with CommonComponent
    waitSleep(milliseconds) {
        var start = new Date().getTime();
        for (var i = 0; i < 1e7; i++) {
            if ((new Date().getTime() - start) > milliseconds) {
                break;
            }
        }
    }
}