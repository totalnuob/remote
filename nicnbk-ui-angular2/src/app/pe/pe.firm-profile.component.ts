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
import {PESearchParams} from "./model/pe.search-params";

import {Subscription} from 'rxjs';
import {MemoSearchParams} from "../m2s2/model/memo-search-params";
import {Memo} from "../m2s2/model/memo";
import {MemoSearchResults} from "../m2s2/model/memo-search-results";
import {MemoService} from "../m2s2/memo.service";

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

    foundFundsList: PEFund[];
    private searchParams = new PESearchParams();

    busy: Subscription;

    //For memo loading
    memoSearchParams = new MemoSearchParams;
    meetingTypes = [];
    memoList: Memo[];
    memoSearchResult: MemoSearchResults;

    constructor(
        private lookupService: LookupService,
        private firmService: PEFirmService,
        private fundService: PEFundService,
        private route: ActivatedRoute,
        private router: Router,
        private memoService: MemoService
    ) {
        super();

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
                    this.searchParams['id'] = this.firmIdParam;
                    this.busy = this.firmService.get(this.firmIdParam)
                        .subscribe(
                            data => {
                                //console.log(data);

                                //TODO: check response memo
                                this.firm = data;

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
                            error => this.errorMessage = "Error loading firm profile"
                        );
                    this.busy = this.fundService.search(this.searchParams)
                        .subscribe(
                            searchResult => {
                                this.foundFundsList = searchResult;
                            },
                            error => this.errorMessage = "Failed to load GP's funds"
                        );
                }
            })
    }

    save(){
        console.log(this.firm);

        this.firm.strategy = this.convertToServiceModel(this.firm.strategy);
        this.firm.industryFocus = this.convertToServiceModel(this.firm.industryFocus);
        this.firm.geographyFocus = this.convertToServiceModel(this.firm.geographyFocus);

        this.firmService.save(this.firm)
            .subscribe(
                (response: SaveResponse) => {
                    this.firm.id = response.entityId;
                    this.firm.creationDate = response.creationDate;

                    this.postAction("Successfully saved.", null);
                },
                error => {
                    this.postAction(null, "Error saving firm profile.");
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

        this.loadLookups();
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
                error =>  this.errorMessage = "Failed to search memos."
            );
    }

    postAction(successMessage, errorMessage) {
        this.successMessage = successMessage;
        this.errorMessage = errorMessage;

        // TODO: non jQuery
        $('html, body').animate({scrollTop: 0}, 'fast');
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
                error => this.errorMessage = <any>error
            );

        //load PE_Industry_Focus
        this.lookupService.getPEIndustryFocus()
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.industryList.push({id: element.code, text: element.nameEn});
                    });
                },
                error => this.errorMessage = <any>error
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
}