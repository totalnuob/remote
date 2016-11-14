import { Component } from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {HedgeFund} from "./model/hf.fund";
import {LookupService} from "../common/lookup.service";
import {CommonFormViewComponent} from "../common/common.component";
import {SaveResponse} from "../common/save-response.";
import {HedgeFundService} from "./hf.fund.service";
import {HFManager} from "./model/hf.manager";
import {HFManagerService} from "./hf.manager.service";

@Component({
    selector: 'hf-fund-profile',
    templateUrl: 'view/hf.fund-profile.component.html',
    styleUrls: [
        //'../../../public/css/...',
    ],
    providers: []
})
export class HFFundProfileComponent extends CommonFormViewComponent {

    private currentYear = new Date().getFullYear();
    private fund = new HedgeFund();

    public sub: any;
    public fundIdParam: number;
    public managerIdParam: number;

    strategyLookup = [];
    geographyLookup = [];
    fundStatusLookup = [];
    currencyLookup = [];
    legalStructureLookup = [];
    domicileCountryLookup = [];

    subscriptionFrequencyTypeLookup = [];
    managementFeeTypeLookup = [];
    performanceFeeTypeLookup = [];
    performanceFeePayFrequencyLookup = [];
    redemptionFrequencyLookup = [];
    redemptionNotificationPeriodLookup = [];


    constructor(
        private lookupService: LookupService,
        private route: ActivatedRoute,
        private fundService: HedgeFundService,
        private managerService: HFManagerService
    ) {

        super();

        // loadLookups
        this.loadLookups();

        // parse params and load data
        this.sub = this.route
            .params
            .subscribe(params => {
                this.fundIdParam = +params['id'];
                this.managerIdParam = +params['managerId'];


                this.fund.manager = new HFManager();

                //this.fund.calculatedValues = [];
                //var item = {'name': 'Return', 'item.year1': '1.01',
                //    'item.year2': '1.01',
                //    'item.year3': '1.01',
                //    'item.year4': '1.01',
                //    'item.year5': '1.01',
                //    'item.year6': '1.01',
                //    'item.year7': '1.01',
                //    'item.year8': '1.01',
                //    'item.year9': '1.01',
                //    'item.year10': '1.01'};
                //this.fund.calculatedValues.push(item);
                //this.fund.calculatedValues.push(item);
                //this.fund.calculatedValues.push(item);
                //this.fund.calculatedValues.push(item);
                //this.fund.calculatedValues.push(item);

                if(this.fundIdParam > 0) {
                    this.fundService.get(this.fundIdParam)
                        .subscribe(
                            (data: HedgeFund) => {
                                if(data && data.id > 0) {
                                    this.fund = data;
                                }else{
                                    // TODO: handle error
                                    this.errorMessage = "Error loading fund profile.";
                                }
                            },
                            error => this.errorMessage = "Error loading manager profile"
                        );
                }else if(this.managerIdParam > 0){
                    this.managerService.get(this.managerIdParam)
                        .subscribe(
                            (data: HFManager) => {
                                if(data && data.id > 0) {
                                    this.fund.manager = data;
                                }else{
                                    // TODO: handle error
                                    this.errorMessage = "Error loading fund manager info.";
                                }
                            },
                            error => this.errorMessage = "Error loading manager profile"
                        );
                }else{
                    // TODO: handle error
                    error => this.errorMessage = "Invalid parameter values";
                    return;
                }
            });

    }

    save(){
        console.log(this.fund);

        this.fundService.save(this.fund)
            .subscribe(
                (response: SaveResponse)  => {
                    this.fund.id = response.entityId;
                    this.fund.creationDate = response.creationDate;

                    this.postAction("Successfully saved.", null);
                },
                error =>  {
                    this.postAction(null, "Error saving manager profile.");
                }
            );
    }

    loadLookups(){

        //strategy
        this.lookupService.getHFStrategies()
            .subscribe(
                data => {
                    //data.forEach(element => {
                    //    this.strategyLookup.push({ id: element.code, value: element.nameEn});
                    //});
                    this.strategyLookup = data;
                },
                error =>  this.errorMessage = <any>error
            );

        // geography
        this.lookupService.getGeographies()
            .subscribe(
                data => {
                    //data.forEach(element => {
                    //    this.strategyLookup.push({ id: element.code, value: element.nameEn});
                    //});
                    this.geographyLookup = data;
                },
                error =>  this.errorMessage = <any>error
            );

        // share class currency
        this.lookupService.getCurrencyList()
            .subscribe(
                data => {
                    this.currencyLookup = data;
                },
                error =>  this.errorMessage = <any>error
            );

        // TODO: load from DB

        // status
        this.lookupService.getManagerStatuses().then(data => this.fundStatusLookup = data);

        // legal structure
        this.lookupService.getLegalStructures().then(data => this.legalStructureLookup = data);

        // domicile country
        this.lookupService.getDomicileCountries().then(data => this.domicileCountryLookup = data);

        // subscription frequency
        this.lookupService.getSubscriptionFrequencyTypes().then(data => this.subscriptionFrequencyTypeLookup = data);

        // management fee type
        this.lookupService.getManagementFeeTypes().then(data => this.managementFeeTypeLookup = data);

        // performance fee type
        this.lookupService.getPerformanceFeeTypes().then(data => this.performanceFeeTypeLookup = data);

        // performance fee pay freq
        this.lookupService.getPerformanceFeePayFrequencyTypes().then(data => this.performanceFeePayFrequencyLookup = data);

        // redemption frequency
        this.lookupService.getRedemptionFrequencyTypes().then(data => this.redemptionFrequencyLookup = data);


        // redemption notification period
        this.lookupService.getRedemptionNotificationPeriodTypes().then(data => this.redemptionNotificationPeriodLookup = data);

    }
}