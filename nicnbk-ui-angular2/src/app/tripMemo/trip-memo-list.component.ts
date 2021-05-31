import {Component, OnInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';

import {LookupService} from "../common/lookup.service";
import {TripMemoSearchParams} from "./model/trip-memo-search-params";
import {TripMemoService} from "./trip-memo.service";
import {CommonFormViewComponent} from "../common/common.component";
import {TripMemo} from "./model/trip-memo";
import {TripMemoSearchResults} from "./model/trip-memo-search-results";
import {Subscription} from 'rxjs';
import {ErrorResponse} from "../common/error-response";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";

declare var $:any

@Component({
    selector: 'trip-memo-list',
    templateUrl: 'view/trip-memo-list.component.html',
    styleUrls: [],
    providers: [],
})
export class TripMemoListComponent extends CommonFormViewComponent implements OnInit {
    searchParams = new TripMemoSearchParams;
    busy: Subscription;
    public sub: any;

    tripMemoSearchResult: TripMemoSearchResults;
    tripMemoList: TripMemo[];
    tripTypes = [];

    private moduleAccessChecker: ModuleAccessCheckerService;

    constructor(
        private lookupService: LookupService,
        private tripMemoService: TripMemoService,
        private router: Router,
        private route: ActivatedRoute
    ){
        super(router);

        this.moduleAccessChecker = new ModuleAccessCheckerService;

        //loadLookups
        this.sub = this.loadLookups();

        this.sub = this.route
            .params
            .subscribe(params => {
                if(params['params'] != null){
                    this.searchParams = JSON.parse(params['params']);
                    this.busy = this.tripMemoService.search(this.searchParams)
                        .subscribe(
                            searchResult  => {
                                this.tripMemoList = searchResult.tripMemos;
                                this.tripMemoSearchResult = searchResult;
                            },
                            error =>  this.errorMessage = "Failed to search trip memos."
                        );
                } else {
                    this.search(0);
                }
            });

    }

    ngOnInit():any {
        // TODO: exclude jQuery
        // datetimepicker
        $('#fromDateDTPickeer').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
        $('#untilDateDTPickeer').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        // load lookups
        //this.loadLookups();

        // find all
        //fthis.search(0);
    }

    search(page){

        // TODO: as parameter?
        this.searchParams.pageSize = 10;
        this.searchParams.page = page;

        this.searchParams.fromDate = $('#fromDate').val();
        this.searchParams.toDate = $('#toDate').val();

        this.busy = this.tripMemoService.search(this.searchParams)
            .subscribe(
                searchResult  => {
                    this.tripMemoList = searchResult.tripMemos;
                    this.tripMemoSearchResult = searchResult;

                    console.log(searchResult.tripMemos);
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error searching memos";
                    console.log("Error searching memos");
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            );
    }

    loadLookups() {
        //trip types
        this.lookupService.getTripTypes().then(tripTypes => this.tripTypes = tripTypes);
    }

    navigate(tripMemoId){
        this.searchParams.path = '/bt';
        let params = JSON.stringify(this.searchParams);
        this.router.navigate(['/bt/edit/', tripMemoId, { params }]);
    }

    getTripTypeName(type){
        for(var i = 0; i < this.tripTypes.length; i++){
            if(this.tripTypes[i].code == type){
                return this.tripTypes[i].nameEn;
            }
        }
    }

    showMemo(){
        return true;
        //return !this.moduleAccessChecker.checkAccessMemoRestricted();
    }

    export(){
        this.searchParams.pageSize = 20;

        this.searchParams.fromDate = $('#fromDate').val();
        this.searchParams.toDate = $('#toDate').val();

        this.busy = this.tripMemoService.export(this.searchParams)
            .subscribe(
                response  => {
                    //console.log("ok");
                },
                error => {
                    //console.log("fails")
                    console.log(this.searchParams)
                    this.postAction(null, "Error exporting data");
                }
            );
    }
}