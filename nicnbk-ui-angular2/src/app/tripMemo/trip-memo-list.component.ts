import {Component, OnInit} from '@angular/core';
import {} from '@angular/router';
import { } from '@angular/common';

import {LookupService} from "../common/lookup.service";
import {TripMemoSearchParams} from "./model/trip-memo-search-params";
import {TripMemoService} from "./trip-memo.service";
import {CommonFormViewComponent} from "../common/common.component";
import {TripMemo} from "./model/trip-memo";
import {TripMemoSearchResults} from "./model/trip-memo-search-results";
import {Subscription} from 'rxjs';

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

    tripMemoSearchResult: TripMemoSearchResults;
    tripMemoList: TripMemo[];

    constructor(
        private lookupService: LookupService,
        private tripMemoService: TripMemoService
    ){
        super();
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
                error =>  this.errorMessage = "Failed to search trip memos."
            );
    }
}