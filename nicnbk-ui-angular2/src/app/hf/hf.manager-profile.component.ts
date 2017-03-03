import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

import {HFManager} from "./model/hf.manager";
import {LookupService} from "../common/lookup.service";
import {CommonFormViewComponent} from "../common/common.component";
import {HFManagerService} from "./hf.manager.service";
import {SaveResponse} from "../common/save-response";
import {HedgeFund} from "./model/hf.fund";
import {MemoSearchParams} from "../m2s2/model/memo-search-params";
import {MemoService} from "../m2s2/memo.service";
import {Memo} from "../m2s2/model/memo";
import {MemoSearchResults} from "../m2s2/model/memo-search-results";
import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";

declare var $:any;
declare var Chart: any;

@Component({
    selector: 'hf-manager-profile',
    templateUrl: 'view/hf.manager-profile.component.html',
    styleUrls: [
        //'../../../public/css/...',
    ],
    providers: []
})
export class HFManagerProfileComponent extends CommonFormViewComponent implements OnInit{

    memoSearchParams = new MemoSearchParams();
    memoList: Memo[];
    memoSearchResult: MemoSearchResults;
    meetingTypes = [];
    busy: Subscription;

    private manager = new HFManager();

    public sub: any;
    public managerIdParam: number;

    managerTypeLookup = [];
    strategyLookup = [];
    statusLookup = [];
    currencyLookup = [];
    legalStructureLookup = [];
    domicileCountryLookup = [];

    private breadcrumbParams: string;

    private moduleAccessChecker: ModuleAccessCheckerService;

    constructor(
        private lookupService: LookupService,
        private managerService: HFManagerService,
        private route: ActivatedRoute,
        private memoService: MemoService,
        private router: Router
    ) {

        super();

        this.moduleAccessChecker = new ModuleAccessCheckerService;

        if(!this.moduleAccessChecker.checkAccessHedgeFunds()){
            this.router.navigate(['accessDenied']);
        }

        // loadLookups
        this.loadLookups();

        // TODO: wait/sync on lookup loading
        // TODO: sync on subscribe results
        //this.waitSleep(700);

        // parse params and load data
        this.sub = this.route
            .params
            .subscribe(params => {
                this.managerIdParam = +params['id'];
                this.breadcrumbParams = params['params'];
                if(this.managerIdParam > 0) {
                    this.busy = this.managerService.get(this.managerIdParam)
                        .subscribe(
                            data => {
                                console.log(this.breadcrumbParams);
                                // TODO: check response memo
                                this.manager = data;

                                this.initRadarChart();

                                // memo search params init
                                this.memoSearchParams.memoType = "3";
                                this.memoSearchParams.firmId = this.manager.id;
                            },
                            (error: ErrorResponse) => {
                                this.errorMessage = "Error loading manager profile";
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, null);
                            }
                        );
                }else{
                }
            });

    }

    save(){
        this.errorMessage = null;
        this.successMessage = null;

        this.manager.inceptionDate = $('#inceptionDateValue').val();

        //console.log(this.manager);

        if(!this.validate()){
            return;
        }

        this.managerService.save(this.manager)
            .subscribe(
                (response: SaveResponse)  => {
                    this.manager.id = response.entityId;
                    if(this.manager.id == null) {
                        this.manager.creationDate = response.creationDate;
                    }

                    this.postAction("Successfully saved.", null);
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error saving manager profile";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            );
    }

    validate(){
        if(this.manager.name == null || this.manager.name.trim() == ''){
            this.errorMessage = "Manager name required.";
            return false;
        }

        return true;
    }

    createFund(){
        // TODO: navigate to component
        this.router.navigate(['/hf/fundProfile/0/' + this.manager.id], { relativeTo: this.route });
    }

    ngOnInit():any {

        this.postAction(null, null);

        // TODO: exclude jQuery
        // datetimepicker
        $('#inceptionDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        $('#fromDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        $('#toDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        // init chart
        this.initRadarChart();
    }

    search(page){
        console.log(this.memoSearchParams);

        this.memoSearchParams.pageSize = 20;
        this.memoSearchParams.page = page;
        this.memoSearchParams.fromDate = $('#fromDate').val();
        this.memoSearchParams.toDate = $('#toDate').val();

        this.busy = this.memoService.searchHF(this.memoSearchParams)
            .subscribe(
                searchResult => {
                    this.memoList = searchResult.memos;
                    this.memoSearchResult = searchResult;
                },
                error => this.errorMessage = "Failed to search memos."
            );
    }

    loadLookups(){
        // manager types
        this.lookupService.getManagerTypes().then(data => this.managerTypeLookup = data);

        //strategy
        this.lookupService.getHFStrategies()
            .subscribe(
                data => {
                    //data.forEach(element => {
                    //    this.strategyLookup.push({ id: element.code, value: element.nameEn});
                    //});
                    this.strategyLookup = data;
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error loading lookups";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            );

        this.lookupService.getCurrencyList()
            .subscribe(
                data => {
                    this.currencyLookup = data;

                    // TODO: wait/sync on lookup loading
                    // TODO: sync on subscribe results
                    if(this.manager.id == null){
                        this.manager.aumCurrency = 'USD';
                    }
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error loading lookups";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            );

        // status
        this.lookupService.getManagerStatuses().then(data => this.statusLookup = data);

        // memo types
        this.lookupService.getMeetingTypes().then(meetingTypes => this.meetingTypes = meetingTypes);
    }

    getStrategyName(code){
        for(var i = 0; i < this.strategyLookup.length; i++){
            if(this.strategyLookup[i].code === code){
                return this.strategyLookup[i].nameEn;
            }
        }
    }

    setUpRadarChart(ctx, scores){

        var labels = ["Management and Team", "Portfolio", "Strategy"];

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

    initRadarChart(){
        var scores = new Array();
        scores.push(Number(this.manager.managementAndTeamScore));
        scores.push(Number(this.manager.portfolioScore));
        scores.push(Number(this.manager.strategyScore));

        //initializing average score for memos. if new memo skip
        if(this.manager.managementAndTeamScore != null) {
            var totalScore = Number(this.manager.portfolioScore) + Number(this.manager.strategyScore) + Number(this.manager.managementAndTeamScore);
            var rounded = Math.round((totalScore / 3) * 10) / 10;
            $("#averageScore").text(rounded);
        }

        this.setUpRadarChart($('#myChart'), scores);
    }

    updateScore(){

        // TODO: fix select bind - ngModel
        this.manager.managementAndTeamScore = $('#managementAndTeamScore').val();
        this.manager.portfolioScore = $('#portfolioScore').val();
        this.manager.strategyScore = $('#strategyScore').val();

        var totalScore = Number(this.manager.managementAndTeamScore) + Number(this.manager.portfolioScore) + Number(this.manager.strategyScore);
        var rounded = Math.round( (totalScore/3) * 10 ) / 10;
        $("#averageScore").text(rounded);

        var scores = new Array();
        scores.push(Number(this.manager.managementAndTeamScore));
        scores.push(Number(this.manager.portfolioScore));
        scores.push(Number(this.manager.strategyScore));
        this.setUpRadarChart($('#myChart'), scores);

    }

    navigate(memoType, memoId){
        this.memoSearchParams.path = '/hf/managerProfile/' + this.manager.id;
        let params = JSON.stringify(this.memoSearchParams);
        this.router.navigate(['/m2s2/edit/', memoType, memoId, { params }]);
    }

}