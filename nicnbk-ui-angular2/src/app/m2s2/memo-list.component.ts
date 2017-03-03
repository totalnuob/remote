import { Component, OnInit  } from '@angular/core';
import { Router} from '@angular/router';
import {LookupService} from "../common/lookup.service";
import {MemoSearchParams} from "./model/memo-search-params";
import {MemoService} from "./memo.service";
import {CommonFormViewComponent} from "../common/common.component";
import {Memo} from "./model/memo";
import {MemoSearchResults} from "./model/memo-search-results";
import {ActivatedRoute} from '@angular/router';

import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";

declare var $:any

var moment = require("moment");

@Component({
    selector: 'm2s2-list',
    templateUrl: './view/memo-list.component.html',
    providers: [],
})
export class MemoListComponent  extends CommonFormViewComponent implements OnInit{

    public sub: any;

    searchParams = new MemoSearchParams;
    busy: Subscription;

    memoTypes = [];
    meetingTypes = [];

    memoList: Memo[];

    memoSearchResult: MemoSearchResults;

    advancedSearch = false;

    options = {
        placeholder: "+ tag",
        secondaryPlaceholder: "Enter search tag",
        separatorKeys: [188, 191], // exclude coma from tag content
        maxItems: 10
    }

    private moduleAccessChecler: ModuleAccessCheckerService

    public onItemAdded(item) {
        this.searchParams.tags.push(item);
    }

    public onItemRemoved(item) {
        for(var i = this.searchParams.tags.length - 1; i >= 0; i--) {
            if(this.searchParams.tags[i] === item) {
                this.searchParams.tags.splice(i, 1);
            }
        }
    }

    constructor(
        private lookupService: LookupService,
        private memoService: MemoService,
        private route: ActivatedRoute,
        private router: Router
    ){
        super();

        this.moduleAccessChecler = new ModuleAccessCheckerService;

        this.sub = this.route
            .params
            .subscribe(params => {
               if(params['params'] != null){
                this.searchParams = JSON.parse(params['params']);
                this.busy = this.memoService.search(this.searchParams)
                    .subscribe(
                        searchResult  => {
                            this.memoList = searchResult.memos;
                            this.memoSearchResult = searchResult;
                        },
                        error =>  this.errorMessage = "Failed to search memos."
                    );
                } else {
                   this.search(0);
               }
            });

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

        // load lookups
        this.loadLookups();

        if(this.searchParams.tags == null) {
            this.searchParams.tags = [];
        }

        // find all
        //this.search(0);
    }

    loadLookups(){
        // memo types
        this.lookupService.getMemoTypes().then(memoTypes => {this.memoTypes = memoTypes});

        console.log(this.memoTypes);

        //meeting types
        this.lookupService.getMeetingTypes().then(meetingTypes => this.meetingTypes = meetingTypes);
    }

    search(page){

        //console.log(this.searchParams);
        // TODO: as parameter?
        this.searchParams.pageSize = 20;

        this.searchParams.page = page;

        this.searchParams.fromDate = $('#fromDate').val();
        this.searchParams.toDate = $('#toDate').val();

        this.busy = this.memoService.search(this.searchParams)
            .subscribe(
                searchResult  => {
                    this.memoList = searchResult.memos;
                    this.memoSearchResult = searchResult;
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error searching memos";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                    //alert(this.errorMessage);
                }
            );
    }

    navigate(memoType, memoId){
        this.searchParams.path = '/m2s2';
        let params = JSON.stringify(this.searchParams);
        this.router.navigate(['/m2s2/edit/', memoType, memoId, { params }]);
    }

    toggle(){
        this.advancedSearch = !this.advancedSearch;
    }

    // TODO: get lookup values from cache
    getMemoTypeName(type){
        if(type == 1){
            return "GENERAL";
        }else if(type == 2 ){
        return "PE";
        }else if(type == 3 ){
            return "HF";
        }else if(type == 4 ){
            return "RE";
        }else{
            return "";
        }
    }

    public showHedgeFunds(){
        return this.moduleAccessChecler.checkAccessHedgeFundsEditor();
    }

    public showPrivateEquity(){
        return this.moduleAccessChecler.checkAccessPrivateEquityEditor();
    }

    public showRealEstate(){
        return this.moduleAccessChecler.checkAccessRealEstateEditor();
    }

}