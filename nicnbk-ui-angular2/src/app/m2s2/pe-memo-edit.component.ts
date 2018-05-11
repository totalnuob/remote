import { Component,NgModule, OnInit, ViewChild  } from '@angular/core';
import {LookupService} from "../common/lookup.service";
import {PEMemo} from "./model/pe-memo";
import {MemoService} from "./memo.service";
import {ActivatedRoute, Router} from '@angular/router';
import {Lookup} from "../common/lookup";
import {EmployeeService} from "../employee/employee.service";

import {FileUploadService} from "../upload/file.upload.service";
import {CommonFormViewComponent} from "../common/common.component";
import {SaveResponse} from "../common/save-response";
import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";
import {PEFirm} from "../pe/model/pe.firm";
import {PEFirmService} from "../pe/pe.firm.service";
import {PEFundService} from "../pe/pe.fund.service";
import {PESearchParams} from "../pe/model/pe.search-params";
import {PEFund} from "../pe/model/pe.fund";
import {MemoSearchParams} from "./model/memo-search-params";

declare var $:any;
declare var Chart: any;

@Component({
    selector: 'pe-memo-edit',
    templateUrl: './view/pe-memo-edit.component.html',
    styleUrls: [],
    providers: [PEFirmService, PEFundService],
})
@NgModule({
    imports: []
})
export class PrivateEquityMemoEditComponent extends CommonFormViewComponent implements OnInit{

    public sub: any;
    public memoIdParam: number;
    busy: Subscription;

    public uploadFiles: Array<any> = [];

    private visible = false;
    private showFundDetails = false;
    private showFirmDetails = false;
    submitted = false;

    public memo = new PEMemo;

    @ViewChild('attendeesSelect')
    private attendeesSelect;

    @ViewChild('strategySelect')
    private strategySelect;

    @ViewChild('geographySelect')
    private geographySelect;

    public strategyList: Array<any> = [];
    public geographyList: Array<any> = [];
    public attendeesList: Array<any> = [];

    public firmStrategyList: Array<any> = [];
    public firmIndustryList: Array<any> = [];
    public firmGeographyList: Array<any> = [];
    public fundStrategyList: Array<any> = [];
    public fundGeographyList: Array<any> = [];



    public firmList: Array<any> = [];
    public fundList: Array<any> = [];
    public foundFundsList: Array<any> = [];
    private peSearchParams = new PESearchParams();

    private breadcrumbParams: string;
    private searchParams = new MemoSearchParams();

    closingScheduleList = [];
    openingScheduleList = [];
    currencyList = [];

    options = {
        placeholder: "+ tag",
        secondaryPlaceholder: "Enter a new tag",
        separatorKeys: [188, 191], // exclude coma from tag content
        maxItems: 10
    }

    private moduleAccessChecler: ModuleAccessCheckerService;

    public onItemAdded(item) {
        this.memo.tags.push(item);
    }

    public onItemRemoved(item) {
        for(var i = this.memo.tags.length - 1; i >= 0; i--) {
            if(this.memo.tags[i] === item) {
                this.memo.tags.splice(i, 1);
            }
        }
    }


    constructor(
        private lookupService: LookupService,
        private employeeService: EmployeeService,
        private memoService: MemoService,
        private route: ActivatedRoute,
        private router: Router,
        private peFirmService: PEFirmService,
        private peFundService: PEFundService
    ){
        super(router);

        this.moduleAccessChecler = new ModuleAccessCheckerService;

        // loadLookups
        this.sub = this.loadLookups();

        // load all firms for dropdown
        this.sub = this.loadFirms();

        // parse params and load data
        this.sub = this.route
            .params
            .subscribe(params => {
                this.memoIdParam = +params['id'];
                this.breadcrumbParams = params['params'];
                //console.log(this.breadcrumbParams);
                if(this.breadcrumbParams != null) {
                    this.searchParams = JSON.parse(this.breadcrumbParams);
                }
                this.memo.firm = new PEFirm();
                this.memo.fund = new PEFund();
                if(this.memoIdParam > 0) {
                    this.busy = this.memoService.get(2, this.memoIdParam)
                        .subscribe(
                            memo => {
                                // TODO: check response memo

                                this.memo = memo;

                                //this.initRadarChart();

                                this.preselectFirmStrategyGeographyIndustry(this.memo.firm);
                                this.preselectFundStrategyGeographyIndustry(this.memo.fund);

                                if(this.memo.firm == null){
                                    this.memo.firm = new PEFirm();
                                }

                                if(this.memo.tags == null) {
                                    this.memo.tags = [];
                                }

                                // untoggle funds details if fundname is not empty
                                if(this.memo.fundName != null && this.memo.fundName != "") {
                                    this.visible = true;
                                }

                                // preselect memo attendees
                                this.preselectAttendeesNIC();

                                if(this.memo.fund != null){
                                    this.showFundDetails = true;
                                }

                            },
                            (error: ErrorResponse) => {
                                this.errorMessage = "Error loading memo";
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, null);
                                console.log("Error loading memo");
                            }
                        );
                }else{
                    // TODO: default value for radio buttons?
                    this.memo.meetingType = "MEETING";
                    this.memo.fund.suitable = true;
                    this.memo.suitable = true;
                    this.memo.currentlyFundRaising = true;
                    this.memo.openingSoon = false;
                    this.memo.tags = [];
                }
            }
            );
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

        this.postAction(null, null);

        // TODO: exclude jQuery
        // datetimepicker
        $('#meetingDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        $('#timePicker').datetimepicker({
            format: 'LT'
        })

        // init chart also moved to constructor
        // due to that scores array is still empty when ngOnInit called
        //this.initRadarChart();
    }

    toggle() {
        this.visible = !this.visible;
    }

    save(){

        // TODO: ngModel date
        this.memo.meetingDate = $('#meetingDateValue').val();
        this.memo.meetingTime = $('#meetingTimeValue').val();

        //console.log(this.memo);

        //TODO: refactor ?
        this.memo.strategies = this.convertToServiceModel(this.memo.strategies);
        this.memo.geographies = this.convertToServiceModel(this.memo.geographies);

        //if(this.memo.fund.id == null) {
        //    this.memo.fund = null;
        //}

        this.memoService.savePE(this.memo)
            .subscribe(
                (response: SaveResponse)  => {
                    this.memo.id = response.entityId;
                    this.memo.creationDate = response.creationDate;
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
                                this.submitted = true;
                            },
                            (error: ErrorResponse) => {
                                this.errorMessage = "Error uploading attachments";
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, null);
                                this.submitted = false;
                            });
                    }else{
                        this.postAction("Successfully saved.", null);
                        this.submitted = true;
                    }
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error saving memo";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                    this.submitted = false;
                }
            );

        if(this.memo.fund && this.memo.fund.id != null){
            //console.log("here");
            this.peFundService.save(this.memo.fund)
                .subscribe(
                    (response: SaveResponse) => {
                        this.postAction("Succesfully saved fund", null);
                        this.submitted = true;
                    },
                    (error: ErrorResponse) => {
                        this.errorMessage = "Error saving fund";
                        if(error && !error.isEmpty()){
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, null);
                        this.submitted = false;
                    }
                )
        }
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
                        this.submitted = false;
                    },
                    (error: ErrorResponse) => {
                        this.errorMessage = "Error deleting attachments";
                        if(error && !error.isEmpty()){
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, null);
                        this.submitted = false;
                    }
                );
        }
    }

    onFileChange(event) {
        var target = event.target || event.srcElement;
        var files = target.files;
        this.uploadFiles.length = 0;
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
        //this.setUpRadarChart($('#myChart'), scores);

    }

    //initRadarChart(){
    //    var scores = new Array();
    //    scores.push(Number(this.memo.teamScore));
    //    scores.push(Number(this.memo.trackRecordScore));
    //    scores.push(Number(this.memo.strategyScore));
    //
    //    //initializing average score for memos. if new memo skip
    //    if(this.memo.teamScore != null) {
    //        var totalScore = Number(this.memo.teamScore) + Number(this.memo.trackRecordScore) + Number(this.memo.strategyScore);
    //        var rounded = Math.round((totalScore / 3) * 10) / 10;
    //        $("#averageScore").text(rounded);
    //    }
    //
    //    this.setUpRadarChart($('#myChart'), scores );
    //}

    //setUpRadarChart(ctx, scores){
    //
    //    var labels = ["GP and Team", "Track Record", "Strategy"];
    //
    //    var data = {
    //        labels: labels,
    //        datasets: [
    //            {
    //                label: "",
    //                backgroundColor: "rgba(255,99,132,0.2)",
    //                borderColor: "rgba(255,99,132,1)",
    //                pointBackgroundColor: "rgba(255,99,132,1)",
    //                pointBorderColor: "#fff",
    //                pointHoverBackgroundColor: "#fff",
    //                pointHoverBorderColor: "rgba(255,99,132,1)",
    //                // playing with lines
    //                borderDash: [15, 2],
    //                data: scores //[4, 3, 2]
    //            }
    //        ]
    //    };
    //    var myRadarChart = new Chart(ctx, {
    //        type: 'radar',
    //        data: data,
    //        options: {
    //            legend:{
    //                display: false
    //            },
    //            scale: {
    //                ticks: {
    //                    //beginAtZero: true,
    //                    min: 0,
    //                    max: 5
    //                },
    //                gridLines: {
    //                    color: "#8A9396"
    //                }
    //            }
    //        }
    //    });
    //}

    loadLookups(){
        this.lookupService.getClosingSchedules().then(data => this.closingScheduleList = data);
        this.lookupService.getOpeningScheduleList().then(data => this.openingScheduleList = data);

        // load strategies
        this.lookupService.getPEStrategies()
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.strategyList.push({ id: element.code, text: element.nameEn});
                    });
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error loading lookups";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }

            );

        // load geographies
        this.lookupService.getGeographies()
            .subscribe(
            data => {
                data.forEach(element => {
                    this.geographyList.push({ id: element.code, text: element.nameEn});
                });
            },
            (error: ErrorResponse) => {
                this.errorMessage = "Error loading lookups";
                if(error && !error.isEmpty()){
                    this.processErrorMessage(error);
                }
                this.postAction(null, null);
            }
        );

        // load currencies
        this.lookupService.getCurrencyList()
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.currencyList.push(element);
                    });
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error loading lookups";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
        );

        // load employees
        this.employeeService.findAll()
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.attendeesList.push({ id: element.id, text: element.firstName + " " + element.lastName});

                    });
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

    loadFirms(){
        this.peFirmService.getFirms()
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.firmList.push({id: element.id, name: element.firmName});
                    });
                },
                //error => {
                //    this.postAction(null, "Error loading firms list for dropdown.");
                //}
                (error: ErrorResponse) => {
                this.errorMessage = "Error loading firms list for dropdown.";
                if(error && !error.isEmpty()){
                    this.processErrorMessage(error);
                }
                this.postAction(null, null);
        }
            )
        //console.log(this.firmList);
    }

    getFirmDataOnChange(id){
        //this.getFundData(null);
        this.getFirmData(id);
        if(this.memo.fund) {
            this.memo.fund.suitable = true;
        }
    }

    getFirmData(id){
        this.fundList = [];
        this.peFirmService.get(id)
            .subscribe(
                (data: PEFirm) => {
                    if(data && data.id > 0) {
                        this.memo.firm = data;
                        this.preselectFirmStrategyGeographyIndustry(this.memo.firm);
                        this.preselectFundStrategyGeographyIndustry(null);
                        this.memo.fund = null;
                    } else {
                        this.errorMessage = "Error loading fund manager info.";
                    }
                },
                error => this.errorMessage = "Error loading manager profile"
            );

        //this.peSearchParams['id'] = id;
        //this.peFundService.search(this.peSearchParams)
        //    .subscribe(
        //        searchResult => {
        //            this.foundFundsList = searchResult;
        //            searchResult.forEach(element => {
        //                this.fundList.push({id: element.id, name: element.fundName});
        //            });
        //        },
        //        error => this.errorMessage = "Failed to load GP's funds"
        //    );
    }

    getFundData(id){
        //if(id == null) {
        //    return this.memo.fund = new PEFund();
        //}
        for(var i = 0; i < this.memo.firm.funds.length; i++){
            if(this.memo.firm.funds[i].id == id) {
                this.preselectFundStrategyGeographyIndustry(this.memo.firm.funds[i]);
                return this.memo.fund = this.memo.firm.funds[i];
            }
        }
    }

    toggleFund(){
        if(this.memo.fund == null){
            this.memo.fund = new PEFund();
            this.memo.fund.suitable = true;
        }
        this.showFundDetails = !this.showFundDetails;
    }

    toggleFirm(){
        this.showFirmDetails = !this.showFirmDetails;
    }

    //TODO: bind ngModel - boolean
    setSuitable(){
        this.memo.fund.suitable = true;
        this.memo.fund.nonsuitableReason = '';
    }

    setNonSuitable(){
        this.memo.fund.suitable = false;
    }

    setSuitableTemp(){
        this.memo.suitable = true;
        this.memo.nonsuitableReason = '';
    }

    setNonSuitableTemp(){
        this.memo.suitable = false;
    }

    setCurrentlyFundRaising() {
        this.memo.currentlyFundRaising = true;
        this.memo.openingSoon = false;
        this.memo.openingSchedule = null;
    }

    setOpeningSoon() {
        this.memo.openingSoon = true;
        this.memo.currentlyFundRaising = false;
        this.memo.closingSchedule = null;
    }

    public canEdit(){
        return this.moduleAccessChecler.checkAccessPrivateEquityEditor();
    }

    preselectFirmStrategyGeographyIndustry(firm){
        this.firmGeographyList = [];
        this.firmStrategyList = [];
        this.firmIndustryList = [];

        if(firm && firm.id && firm.strategy) {
            firm.strategy.forEach(element => {
                this.firmStrategyList.push(element.nameEn.toString());
            });
        }
        if(firm && firm.id && firm.industryFocus) {
            this.memo.firm.industryFocus.forEach(element => {
                this.firmIndustryList.push(element.nameEn.toString());
            });
        }
        if(firm && firm.id && firm.geographyFocus) {
            firm.geographyFocus.forEach(element => {
                this.firmGeographyList.push(element.nameEn.toString());
            });
        }

        console.log(this.firmStrategyList);

    }

    preselectFundStrategyGeographyIndustry(fund){
        this.fundStrategyList = [];
        this.fundGeographyList = [];

        if(fund && fund.id && fund.strategy) {
            fund.strategy.forEach(element => {
                this.fundStrategyList.push(element.nameEn.toString());
            });
        }
        if(fund && fund.id && fund.geography) {
            fund.geography.forEach(element => {
                this.fundGeographyList.push(element.nameEn.toString());
            });
        }
    }
}