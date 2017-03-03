import {Component, ViewChild} from '@angular/core';
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
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";

declare var $:any

@Component({
    selector: 'pe-firm-profile',
    templateUrl: 'view/pe.firm-profile.component.html',
    styleUrls: [],
    providers: [PEFirmService, PEFundService]
})
export class PEFirmProfileComponent extends CommonFormViewComponent {

    private firm = new PEFirm();

    @ViewChild('strategySelect')
    private strategySelect;

    @ViewChild('industrySelect')
    private industrySelect;

    @ViewChild('geographySelect')
    private geographySelect;

    public strategyList: Array<any> = [];
    public industryList: Array<any> = [];
    public geographyList: Array<any> = [];

    private sub: any;
    public firmIdParam: number;

    foundFundsList: PEFund[];
    private searchParams = new PESearchParams();

    busy: Subscription;

    private moduleAccessChecker: ModuleAccessCheckerService;

    constructor(
        private lookupService: LookupService,
        private firmService: PEFirmService,
        private fundService: PEFundService,
        private route: ActivatedRoute,
        private router: Router
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

                    this.busy = this.fundService.search(this.searchParams)
                        .subscribe(
                            searchResult => {
                                this.foundFundsList = searchResult;
                            },
                            (error: ErrorResponse) => {
                                this.errorMessage = "Error loading GP funds";
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
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

        this.firmService.save(this.firm)
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

    // TODO: Move to a common component
    loadLookups(){
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
}