import {GoogleChartComponent} from "../google-chart/google-chart.component";
import { Component, OnInit, AfterViewInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {LookupService} from "../common/lookup.service";
import {MacroMonitorService} from "./macromonitor.service";
import {Subscription} from 'rxjs';
import {MacroMonitorScore} from "./model/macromonitor.score";
import {SaveResponse} from "../common/save-response";
import {ErrorResponse} from "../common/error-response";
import * as moment from "moment";

declare var google:any;

@Component({
    selector: 'mm-view',
    templateUrl: 'view/macromonitor.view.component.html',
    styleUrls: [],
    providers: []
})
export class  MMViewComponent extends GoogleChartComponent {

    public sub: any;
    busy: Subscription;
    fieldsLookup = [];

    constructor (
        private route: ActivatedRoute,
        private lookupService: LookupService,
        private mmService: MacroMonitorService
    ) {
        super();

        //load lookups for fields
        this.sub = this.loadLookups();

    }

    drawGraph(){
        this.getDataByType(1);
    }


    getDataByType(typeId) {
        this.busy = this.mmService.get(typeId)
            .subscribe(
                (data: [MacroMonitorScore]) => {
                    if(data) {
                        this.parseData(data, typeId);
                    } else {
                        this.errorMessage = "Error loading macromonitor data";
                    }
                }
            )
    }

    parseData(dataList, typeId) {
        let valueList = [];
        let dateList = [];

        for(var i = 0; i < this.fieldsLookup.length; i++) {
            valueList.push([this.fieldsLookup[i].nameEn]);
        }

        for(var i = 0; i < dataList.length; i++) {
            if(dateList.indexOf(dataList[i].date) == -1) {
                dateList.push(dataList[i].date);
            }
            for(var j = 0; j < this.fieldsLookup.length; j++) {
                if(dataList[i].field == this.fieldsLookup[j].code) {
                    valueList[j].push(dataList[i].score);
                }
            }
        }

        if(typeId == 1) {
            this.drawValuesTable('table', valueList, dateList);
            this.drawLineChart('line_chart', valueList, dateList, 'MSCI World');
        } else if(typeId == 2) {
            this.drawValuesTable('table_us', valueList, dateList);
            this.drawLineChart('line_chart_us', valueList, dateList, 'S&P 500');
        } else if(typeId == 3) {
            this.drawValuesTable('table_china', valueList, dateList);
            this.drawLineChart('line_chart_china', valueList, dateList, 'Shanghai SE composite');
        } else if(typeId == 4) {
            this.drawValuesTable('table_eu', valueList, dateList)
            this.drawLineChart('line_chart_eu', valueList, dateList, 'STXE 600 Index');
        } else {

        }

    }

    loadLookups(){
        this.lookupService.getMacroMonitorFields()
            .subscribe(
                data => {
                    this.fieldsLookup = data;
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error loading lookups";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            );
    }

    drawValuesTable(id, input, dates) {
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
            },
        };

        var monthYearFormatter = new google.visualization.DateFormat({pattern: "MMM yyyy"});
        monthYearFormatter.format(data, 0);

        var colorFormatter = new google.visualization.ColorFormat();
        colorFormatter.addRange(-100, 1, 'white', '#9C0824');
        colorFormatter.addRange(1, 2, 'white', '#B41F27');
        colorFormatter.addRange(2, 3, 'white', '#CC312B');
        colorFormatter.addRange(3, 4, 'white', '#E86753');
        colorFormatter.addRange(4, 5, 'black', '#FCB4A5'); // pink
        colorFormatter.addRange(5, 6, 'black', '#B9D7B7'); // green
        colorFormatter.addRange(6, 7, 'black', '#74AF72');
        colorFormatter.addRange(7, 8, 'white', '#428F4A');
        colorFormatter.addRange(8, 9, 'white', '#297839');
        colorFormatter.addRange(9, 10, 'white', '#09622A');
        colorFormatter.addRange(10, 100, 'white', '#09622A');

        for(var i = 1; i <= dates.length; i++){
            colorFormatter.format(data, i);
        }

        var chart = this.createTableChart(document.getElementById(id));
        chart.draw(data, options);
    }

    drawLineChart(id, input, dates, str){
        var data = new google.visualization.DataTable();
        data.addColumn("string", "Date");
        data.addColumn("number", str);
        data.addColumn("number", "Macro Score");

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
            var row = [dates[i], input[input.length - 2][1+i], input[input.length - 1][1+i]];
            rows.push(row);
        }
        return rows;
    }

    private setColumnData(data, dates){
        for(var i = 0; i < dates.length; i++){
            data.addColumn("number", moment(dates[i], 'DD-MM-YYYY').format('MMM-YY'));
        }
    };

    private setRowData(data, input){
        data.addRows(input);
    }
}