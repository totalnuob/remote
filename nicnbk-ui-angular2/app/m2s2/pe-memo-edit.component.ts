import { Component, OnInit, ViewChild, AfterViewInit  } from '@angular/core';
import {LookupService} from "../common/lookup.service";
import {PEMemo} from "./model/pe-memo";
import {SELECT_DIRECTIVES, SelectComponent} from "ng2-select";
import {MemoService} from "./memo.service";
import {ActivatedRoute, ROUTER_DIRECTIVES} from '@angular/router';
import {Lookup} from "../common/lookup";
//import {SelectItem} from "ng2-select/ng2-select";
import {MemoComponent} from "../common/view.component";
import {EmployeeService} from "../employee/employee.service";

declare var $:any
declare var Chart: any;

@Component({
    selector: 'pe-memo-edit',
    templateUrl: `app/m2s2/view/pe-memo-edit.component.html`,
    styleUrls: [],
    directives: [SELECT_DIRECTIVES, ROUTER_DIRECTIVES],
    providers: [],
})
export class PrivateEquityMemoEditComponent extends MemoComponent implements OnInit{

    private sub: any;
    private memoIdParam: number;
    memo = new PEMemo;

    @ViewChild('attendeesSelect')
    private attendeesSelect: SelectComponent;

    @ViewChild('strategySelect')
    private strategySelect: SelectComponent;

    @ViewChild('geographySelect')
    private geographySelect: SelectComponent;

    public strategyList: Array<any> = [];
    public geographyList: Array<any> = [];
    public attendeesList: Array<any> = [];

    closingScheduleList = [];
    openingScheduleList = [];
    currencyList = [];

    constructor(
        private lookupService: LookupService,
        private employeeService: EmployeeService,
        private memoService: MemoService,
        private route: ActivatedRoute
    ){
        super();

        // load strategies
        this.lookupService.getPEStrategies().then(
            data => {
                data.forEach(element => {
                    this.strategyList.push({ id: element.code, text: element.nameEn});
                });
            }
        );
        // load geographies
        this.lookupService.getGeographies().then(
            data => {
                data.forEach(element => {
                    this.geographyList.push({ id: element.code, text: element.nameEn});
                });
            }
        );


        // TODO: refactor as findAll ??? or load cash
        this.employeeService.getAll().then(
            data => {
                data.forEach(element => {
                    this.attendeesList.push({ id: element.id, text: element.lastName});
                });
                //console.log(this.strategyList);
            }
        );
        //this.employeeService.findAll()
        //    .subscribe(
        //        data => {
        //            data.forEach(element => {
        //                this.attendeesList.push({ id: element.id, text: element.lastName});
        //
        //            });
        //            console.log(this.attendeesList);
        //        },
        //        error =>  this.errorMessage = <any>error);

        this.sub = this.route
            .params
            .subscribe(params => {
                this.memoIdParam = +params['id'];
                if(this.memoIdParam > 0) {
                    this.memoService.get(2, this.memoIdParam)
                        .subscribe(
                            memo => {
                                // TODO: check response memo
                                this.memo = memo;
                                console.log(this.memo);

                                // preselect memo strategies
                                if(this.memo.strategies) {
                                    this.memo.strategies.forEach(element => {
                                        for (var i = 0; i < this.strategyList.length; i++) {
                                            var option = this.strategyList[i];
                                            if (element.code === option.id) {
                                                this.strategySelect.active.push(option);
                                            }
                                        }
                                    });
                                }
                                // preselect memo geographies
                                if(this.memo.geographies) {
                                    this.memo.geographies.forEach(element => {
                                        for (var i = 0; i < this.geographyList.length; i++) {
                                            var option = this.geographyList[i];
                                            if (element.code === option.id) {
                                                this.geographySelect.active.push(option);
                                            }
                                        }
                                    });
                                }
                                // preselect memo attendees
                                if(this.memo.attendeesNIC) {
                                    this.memo.attendeesNIC.forEach(element => {
                                        for (var i = 0; i < this.attendeesList.length; i++) {
                                            var option = this.attendeesList[i];
                                            if (element.id === option.id) {
                                                this.attendeesSelect.active.push(option);
                                            }
                                        }
                                    });
                                }
                            },
                            error => this.errorMessage = <any>error
                        );
                }
            });
    }

    public selected(value:any):void {
        //console.log('Selected value is: ', value);
    }

    public removed(value:any):void {
        //console.log('Removed value is: ', value);
    }

    public refreshAttendeesNIC(value:any):void {
        this.memo.attendeesNIC = value;
    }

    public refreshGeography(value:any):void {
        this.memo.geographies = value;
    }
    public refreshStrategy(value:any):void {
        this.memo.strategies = value;
    }

    ngOnInit() {


        // TODO: exclude jQuery
        // datetimepicker
        $('#meetingDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        // load lookups
        this.loadLookups();

        // init chart
        this.initRadarChart();

    }

    save(){
        // TODO: ngModel date
        this.memo.meetingDate = $('#meetingDateValue').val();

        //console.log(this.memo);

        //TODO: refactor ?
        this.memo.strategies = this.convertToServiceModel(this.memo.strategies);
        this.memo.geographies = this.convertToServiceModel(this.memo.geographies);

        this.memoService.savePE(this.memo)
            .subscribe(
                response  => {
                    this.successMessage = "Successfully saved.";
                    this.errorMessage = null;
                },
                //error =>  this.errorMessage = <any>error
                error =>  {
                    this.errorMessage = "Error saving memo";
                    this.successMessage = null;
                }
            );
    }

    changeArrangedBy(){
        if(this.memo.arrangedBy != 'OTHER'){
            this.memo.arrangedByDescription = '';
        }
    }

    updateScore(){

        // TODO: fix select bind - ngModel
        this.memo.teamScore = $('#teamScore').val();
        this.memo.trackRecordScore = $('#trackRecordScore').val();
        this.memo.strategyScore = $('#strategyScore').val();

        var totalScore = Number(this.memo.teamScore) + Number(this.memo.trackRecordScore) + Number(this.memo.strategyScore);
        var rounded = Math.round( (totalScore/3) * 10 ) / 10;
        $("#averageScore").text(rounded);

        var scores = new Array();
        scores.push(Number(this.memo.teamScore));
        scores.push(Number(this.memo.trackRecordScore));
        scores.push(Number(this.memo.strategyScore));
        this.setUpRadarChart($('#myChart'), scores);

    }

    initRadarChart(){
        var scores = new Array();
        scores.push(Number(this.memo.teamScore));
        scores.push(Number(this.memo.trackRecordScore));
        scores.push(Number(this.memo.strategyScore));
        this.setUpRadarChart($('#myChart'), scores);
    }

    setUpRadarChart(ctx, scores){

        var labels = ["GP and Team", "Track Record", "Strategy"];

        var data = {
            labels: labels,
            datasets: [
                {
                    label: "",
                    backgroundColor: "rgba(255,99,132,0.2)",
                    borderColor: "rgba(255,99,132,1)",
                    pointBackgroundColor: "rgba(255,99,132,1)",
                    pointBorderColor: "#fff",
                    pointHoverBackgroundColor: "#fff",
                    pointHoverBorderColor: "rgba(255,99,132,1)",
                    // playing with lines
                    borderDash: [15, 2],
                    data: scores //[4, 3, 2]
                }
            ]
        };
        var myRadarChart = new Chart(ctx, {
            type: 'radar',
            data: data,
            options: {
                legend:{
                    display: false
                },
                scale: {
                    ticks: {
                        //beginAtZero: true,
                        min: 0,
                        max: 5
                    },
                    gridLines: {
                        color: "#8A9396"
                    }
                }
            }
        });
    }

    loadLookups(){
        this.lookupService.getClosingSchedules().then(data => this.closingScheduleList = data);
        this.lookupService.getOpeningScheduleList().then(data => this.openingScheduleList = data);
        this.lookupService.getCurrencyList().then(data => this.currencyList = data);
    }




}