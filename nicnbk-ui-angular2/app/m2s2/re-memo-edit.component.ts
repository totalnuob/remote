import { Component, OnInit, ViewChild, AfterViewInit  } from '@angular/core';
import {LookupService} from "../common/lookup.service";
import {REMemo} from "./model/re-memo";
import {SELECT_DIRECTIVES, SelectComponent} from "ng2-select";
import {MemoService} from "./memo.service";
import {ActivatedRoute, ROUTER_DIRECTIVES} from '@angular/router';
//import {SelectItem} from "ng2-select/ng2-select";
import {CommonComponent} from "../common/common.component";
import {EmployeeService} from "../employee/employee.service";
import {MemoAttachmentDownloaderComponent} from "./memo-attachment-downloader.component";

declare var $:any
declare var Chart: any;

@Component({
    selector: 're-memo-edit',
    templateUrl: `app/m2s2/view/re-memo-edit.component.html`,
    styleUrls: [],
    directives: [SELECT_DIRECTIVES, ROUTER_DIRECTIVES, MemoAttachmentDownloaderComponent],
    providers: [],
})
export class RealEstateMemoEditComponent extends CommonComponent implements OnInit{

    private sub: any;
    private memoIdParam: number;

    public uploadFiles: Array<any> = [];

    private visible = false;

    memo = new REMemo;

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

        // loadLookups
        this.loadLookups();

        // parse params and load data
        this.sub = this.route
            .params
            .subscribe(params => {
                this.memoIdParam = +params['id'];
                if(this.memoIdParam > 0) {
                    this.memoService.get(4, this.memoIdParam)
                        .subscribe(
                            memo => {
                                // TODO: check response memo
                                this.memo = memo;
                                //console.log(this.memo);

                                // preselect memo strategies
                                this.preselectStrategies();

                                // preselect memo geographies
                                this.preselectGeographies();

                                // untoggle funds details if fundname is not empty
                                if(this.memo.fundName != null && this.memo.fundName != "") {
                                    this.visible = true;
                                }

                                // preselect memo attendees
                                this.preselectAttendeesNIC();
                            },
                            error => this.errorMessage = "Error loading memo"
                        );
                }
            });
    }

    preselectStrategies(){
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
    }

    preselectAttendeesNIC(){
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
    }

    preselectGeographies(){
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

    toggle() {
        this.visible = !this.visible;
    }

    save(){
        // TODO: ngModel date
        this.memo.meetingDate = $('#meetingDateValue').val();

        //console.log(this.memo);

        //TODO: refactor ?
        this.memo.strategies = this.convertToServiceModel(this.memo.strategies);
        this.memo.geographies = this.convertToServiceModel(this.memo.geographies);

        this.memoService.saveRE(this.memo)
            .subscribe(
                response  => {
                    this.memo.id = response.entityId;

                    if(this.uploadFiles.length > 0) {

                        // TODO: refactor
                        this.memoService.postFiles(this.memo.id, [], this.uploadFiles).subscribe(
                            res => {
                                // clear upload files list on view
                                this.uploadFiles.length = 0;

                                // update files list with new files
                                if(!this.memo.files){ // no files existed
                                    this.memo.files = [];
                                }
                                for (var i = 0; i < res.length; i++) {
                                    this.memo.files.push(res[i]);
                                }

                                this.postAction("Successfully saved.", null);
                            },
                            error => {
                                // TODO: don't save memo?

                                this.postAction(null, "Error uploading attachments.");
                            });
                    }else{
                        this.postAction("Successfully saved.", null);
                    }
                },
                error =>  {
                    this.postAction(null, "Error saving memo.");
                }
            );
    }

    postAction(successMessage, errorMessage){
        this.successMessage = successMessage;
        this.errorMessage = errorMessage;

        // TODO: non jQuery
        $('html, body').animate({ scrollTop: 0 }, 'fast');
    }

    deleteAttachment(fileId){
        var confirmed = window.confirm("Are you sure want to delete");
        if(confirmed) {
            this.memoService.deleteAttachment(this.memo.id, fileId)
                .subscribe(
                    response => {
                        for(var i = this.memo.files.length - 1; i >= 0; i--) {
                            if(this.memo.files[i].id === fileId) {
                                this.memo.files.splice(i, 1);
                            }
                        }

                        this.postAction("Attachment deleted.", null);
                    },
                    error => {
                        this.postAction(null, "Failed to delete attachment");
                    }
                );
        }
    }

    onFileChange(event) {
        var target = event.target || event.srcElement;
        var files = target.files;
        for (let i = 0; i < files.length; i++) {
            this.uploadFiles.push(files[i]);
        }
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

        // load strategies
        this.lookupService.getREStrategies()
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.strategyList.push({ id: element.code, text: element.nameEn});
                    });
                },
                error =>  this.errorMessage = <any>error
            );
        // load geographies
        this.lookupService.getGeographies()
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.geographyList.push({ id: element.code, text: element.nameEn});
                    });
                },
                error =>  this.errorMessage = <any>error
        );
        // load currencies
        this.lookupService.getCurrencyList()
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.currencyList.push({ id: element.code, text: element.nameEn});
                    });
                },
                error =>  this.errorMessage = <any>error
        );

        // load employees
        this.employeeService.findAll()
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.attendeesList.push({ id: element.id, text: element.firstName + " " + element.lastName[0] + "."});

                    });
                },
                error =>  this.errorMessage = <any>error);
    }

}