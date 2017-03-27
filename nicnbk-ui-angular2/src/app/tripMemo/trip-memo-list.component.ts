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

    constructor(
        private lookupService: LookupService,
        private tripMemoService: TripMemoService,
        private router: Router,
        private route: ActivatedRoute
    ){
        super();

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
                                this.tripMemoList = searchResult.memos;
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
        $('#fromDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
        $('#toDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        // load lookups
        //this.loadLookups();

        // find all
        this.search(0);
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
}