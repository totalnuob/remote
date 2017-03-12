import {Component, ViewChild, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {PEFirm} from "./model/pe.firm";
import {PEFirmService} from "./pe.firm.service";
import {CommonFormViewComponent} from "../common/common.component";
import {SaveResponse} from "../common/save-response";
import {LookupService} from "../common/lookup.service";
import {error} from "util";
import {PEFundService} from "./pe.fund.service";
import {PEFund} from "./model/pe.fund";

import {Subscription} from 'rxjs';
import {MemoSearchParams} from "../m2s2/model/memo-search-params";
import {Memo} from "../m2s2/model/memo";
import {MemoSearchResults} from "../m2s2/model/memo-search-results";
import {MemoService} from "../m2s2/memo.service";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";

declare var $:any

@Component({
    selector: 'pe-firm-profile',
    templateUrl: 'view/pe.firm-profile.component.html',
    styleUrls: [],
    providers: [PEFirmService, PEFundService]
})
export class PEFirmProfileComponent extends CommonFormViewComponent implements OnInit{

    private firm = new PEFirm();

    @ViewChild('strategySelect')
    private strategySelect;

    @ViewChild('industrySelect')
    private industrySelect;

    @ViewChild('geographySelect')
    private geographySelect;

    private breadcrumbParams: string;

    public strategyList: Array<any> = [];
    public industryList: Array<any> = [];
    public geographyList: Array<any> = [];

    private sub: any;
    public firmIdParam: number;

    busy: Subscription;

    private moduleAccessChecker: ModuleAccessCheckerService;

    //For memo loading
    memoSearchParams = new MemoSearchParams;
    meetingTypes = [];
    memoList: Memo[];
    memoSearchResult: MemoSearchResults;

    constructor(
        private lookupService: LookupService,
        private firmService: PEFirmService,
        //private fundService: PEFundService,
        private route: ActivatedRoute,
        private router: Router,
        private memoService: MemoService
    ) {
        super();

        this.moduleAccessChecker = new ModuleAccessCheckerService;

        if(!this.moduleAccessChecker.checkAccessPrivateEquity()){
            this.router.navigate(['accessDenied']);
        }

        this.loadLookups();

        // TODO: wait/sync on lookup loading
        // TODO: sync on subscribe results
        //this.waitSleep(700);

        //parse params and load data
        this.sub = this.route
            .params
            .subscribe(params => {
                this.firmIdParam = +params['id'];
                this.breadcrumbParams = params['params'];
                if(this.firmIdParam > 0) {
                    this.busy = this.firmService.get(this.firmIdParam)
                        .subscribe(
                            data => {
                                //console.log(data);

                                //TODO: check response memo
                                this.firm = data;
                                console.log(this.firm)

                                // preselect firm strategies
                                this.preselectStrategy();

                                // preselect industry focus
                                this.preselectIndustry();

                                // preselect geography focus
                                this.preselectGeographies();

                                // memo search params init
                                this.memoSearchParams.memoType = "2";
                                this.memoSearchParams.firmId = this.firm.id;

                            },
                            (error: ErrorResponse) => {
                                this.errorMessage = "Error loading firm profile";
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                    console.log(error);
                                }
                                this.postAction(null, null);
                            }
                        );
                }
            })
    }

    save(){
        console.log(this.firm);

        this.firm.strategy = this.convertToServiceModel(this.firm.strategy);
        this.firm.industryFocus = this.convertToServiceModel(this.firm.industryFocus);
        this.firm.geographyFocus = this.convertToServiceModel(this.firm.geographyFocus);

        this.busy = this.firmService.save(this.firm)
            .subscribe(
                (response: SaveResponse) => {
                    this.firm.id = response.entityId;
                    this.firm.creationDate = response.creationDate;

                    this.postAction("Successfully saved.", null);
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error saving firm profile";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            )
    }

    ngOnInit():any {
        // TODO: exclude jQuery
        // datetimepicker
        $('#fromDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
        $('#toDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        //this.loadLookups();
    }


    search(page){

        // TODO: as parameter?
        this.memoSearchParams.pageSize = 20;

        this.memoSearchParams.page = page;

        this.memoSearchParams.fromDate = $('#fromDate').val();
        this.memoSearchParams.toDate = $('#toDate').val();

        this.busy = this.memoService.searchPE(this.memoSearchParams)
            .subscribe(
                searchResult  => {
                    this.memoList = searchResult.memos;
                    this.memoSearchResult = searchResult;
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Failed to search memos";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            );
    }

    // TODO: Move to a common component
    loadLookups(){

        //meeting types
        this.lookupService.getMeetingTypes().then(meetingTypes => this.meetingTypes = meetingTypes);

        //load strategies
        this.lookupService.getPEStrategies()
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.strategyList.push({id: element.code, text: element.nameEn});
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

        //load PE_Industry_Focus
        this.lookupService.getPEIndustryFocus()
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.industryList.push({id: element.code, text: element.nameEn});
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
    }

    // TODO: Move to a common component
    preselectIndustry(){
        if(this.firm.industryFocus) {
            this.firm.industryFocus.forEach(element => {
                for (var i = 0; i < this.industryList.length; i++) {
                    var option = this.industryList[i];
                    if (element.code === option.id) {
                        this.industrySelect.active.push(option);
                    }
                }
            });
        }
    }

    // TODO: Move to a common component
    preselectStrategy(){
        if(this.firm.strategy) {
            this.firm.strategy.forEach(element => {
                for (var i = 0; i < this.strategyList.length; i++) {
                    var option = this.strategyList[i];
                    if (element.code === option.id) {
                        this.strategySelect.active.push(option);
                    }
                }
            });
        }
    }

    // TODO: Move to a common component
    preselectGeographies(){
        if(this.firm.geographyFocus) {
            this.firm.geographyFocus.forEach(element => {
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
    // TODO: Move to a common component
    public refreshStrategy(value:any):void {
        this.firm.strategy = value;
    }
    // TODO: Move to a common component
    public refreshIndustry(value:any):void {
        this.firm.industryFocus = value;
    }
    // TODO: Move to a common component
    public refreshGeography(value:any):void {
        this.firm.geographyFocus = value;
    }

    createFund(){
        this.router.navigate(['/pe/fundProfile/0/' + this.firm.id], {relativeTo: this.route});
    }

    navigate(memoType, memoId){
        this.memoSearchParams.path = '/pe/firmProfile/' + this.firm.id;
        let params = JSON.stringify(this.memoSearchParams);
        this.router.navigate(['/m2s2/edit/', memoType, memoId, { params }]);
    }

    canEdit(){
        return this.moduleAccessChecker.checkAccessPrivateEquityEditor();
    }
}