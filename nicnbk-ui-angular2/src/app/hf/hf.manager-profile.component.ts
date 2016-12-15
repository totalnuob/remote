import { Component } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

import {HFManager} from "./model/hf.manager";
import {LookupService} from "../common/lookup.service";
import {CommonFormViewComponent} from "../common/common.component";
import {HFManagerService} from "./hf.manager.service";
import {SaveResponse} from "../common/save-response.";
import {HedgeFund} from "./model/hf.fund";

@Component({
    selector: 'hf-manager-profile',
    templateUrl: 'view/hf.manager-profile.component.html',
    styleUrls: [
        //'../../../public/css/...',
    ],
    providers: []
})
export class HFManagerProfileComponent extends CommonFormViewComponent{

    private manager = new HFManager();

    public sub: any;
    public managerIdParam: number;

    managerTypeLookup = [];
    strategyLookup = [];
    statusLookup = [];
    currencyLookup = [];
    legalStructureLookup = [];
    domicileCountryLookup = [];

    constructor(
        private lookupService: LookupService,
        private managerService: HFManagerService,
        private route: ActivatedRoute,
        private router: Router
    ) {

        super();

        // loadLookups
        this.loadLookups();

        // parse params and load data
        this.sub = this.route
            .params
            .subscribe(params => {
                this.managerIdParam = +params['id'];
                if(this.managerIdParam > 0) {
                    this.managerService.get(this.managerIdParam)
                        .subscribe(
                            data => {
                                console.log(data);
                                // TODO: check response memo
                                this.manager = data;

                                //this.manager.funds = [];
                                //var fund = new HedgeFund();
                                //fund.name = "ABC";
                                //this.manager.funds.push(fund);
                            },
                            error => this.errorMessage = "Error loading manager profile"
                        );
                }else{

                }
            });

    }

    save(){
        console.log(this.manager);

        this.managerService.save(this.manager)
            .subscribe(
                (response: SaveResponse)  => {
                    this.manager.id = response.entityId;
                    this.manager.creationDate = response.creationDate;

                    this.postAction("Successfully saved.", null);
                },
                error =>  {
                    this.postAction(null, "Error saving manager profile.");
                }
            );
    }

    createFund(){
        // TODO: navigate to component
        this.router.navigate(['/hf/fundProfile/0/' + this.manager.id], { relativeTo: this.route });
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
                error =>  this.errorMessage = <any>error
            );

        this.lookupService.getCurrencyList()
            .subscribe(
                data => {
                    //data.forEach(element => {
                    //    this.strategyLookup.push({ id: element.code, value: element.nameEn});
                    //});
                    this.currencyLookup = data;
                },
                error =>  this.errorMessage = <any>error
            );

        // status
        this.lookupService.getManagerStatuses().then(data => this.statusLookup = data);

        // legal structure
        this.lookupService.getLegalStructures().then(data => this.legalStructureLookup = data);

        // domicile country
        this.lookupService.getDomicileCountries().then(data => this.domicileCountryLookup = data);


    }
}