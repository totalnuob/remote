import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

import {HFManager} from "./model/hf.manager";
import {LookupService} from "../common/lookup.service";
import {CommonFormViewComponent} from "../common/common.component";
import {HFManagerService} from "./hf.manager.service";
import {SaveResponse} from "../common/save-response.";
import {HedgeFund} from "./model/hf.fund";
import {MemoSearchParams} from "../m2s2/model/memo-search-params";
import {MemoService} from "../m2s2/memo.service";
import {Memo} from "../m2s2/model/memo";
import {MemoSearchResults} from "../m2s2/model/memo-search-results";
import {Subscription} from 'rxjs';

declare var $:any

@Component({
    selector: 'hf-manager-profile',
    templateUrl: 'view/hf.manager-profile.component.html',
    styleUrls: [
        //'../../../public/css/...',
    ],
    providers: []
})
export class HFManagerProfileComponent extends CommonFormViewComponent implements OnInit{

    searchParams = new MemoSearchParams();
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

    constructor(
        private lookupService: LookupService,
        private managerService: HFManagerService,
        private route: ActivatedRoute,
        private memoService: MemoService,
        private router: Router
    ) {

        super();

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
                if(this.managerIdParam > 0) {
                    this.busy = this.managerService.get(this.managerIdParam)
                        .subscribe(
                            data => {
                                console.log(data);
                                // TODO: check response memo
                                this.manager = data;
                            },
                            error => this.errorMessage = "Error loading manager profile"
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
                error =>  {
                    this.postAction(null, "Error saving manager profile.");
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

        $('input[type=text], textarea').autogrow({vertical: true, horizontal: false, flickering: false});
    }

    search(page){
        //console.log(this.searchParams);
        //// TODO: as parameter?
        //this.searchParams.pageSize = 20;
        //
        //if(page > 0) {
        //    this.searchParams.page = page;
        //}
        //
        //this.searchParams.fromDate = $('#fromDate').val();
        //this.searchParams.toDate = $('#toDate').val();
        //
        //this.memoService.search(this.searchParams)
        //    .subscribe(
        //        searchResult  => {
        //            this.memoList = searchResult.memos;
        //            this.memoSearchResult = searchResult;
        //        },
        //        error =>  this.errorMessage = "Failed to search memos."
        //    );
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
                    this.currencyLookup = data;


                    // TODO: wait/sync on lookup loading
                    // TODO: sync on subscribe results
                    if(this.manager.id == null){
                        this.manager.aumCurrency = 'USD';
                    }
                },
                error =>  this.errorMessage = <any>error
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
}