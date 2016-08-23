import {Component, OnInit} from '@angular/core';
import {ROUTER_DIRECTIVES} from '@angular/router';
import { FORM_DIRECTIVES } from '@angular/common';

import {LookupService} from "../common/lookup.service";
import {TripMemoSearchParams} from "./model/trip-memo-search-params";
import {TripMemoService} from "./trip-memo.service";
import {CommonComponent} from "../common/common.component";
import {TripMemo} from "./model/trip-memo";
import {TripMemoSearchResults} from "./model/trip-memo-search-results";

declare var $:any

@Component({
    selector: 'trip-memo-list',
    templateUrl: 'view/trip-memo-list.component.html',
    styleUrls: [],
    directives: [ROUTER_DIRECTIVES, FORM_DIRECTIVES],
    providers: [],
})
export class TripMemoListComponent extends CommonComponent implements OnInit {
    searchParams = new TripMemoSearchParams;

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

        if(page > 0) {
            this.searchParams.page = page;
        }

        this.searchParams.fromDate = $('#fromDate').val();
        this.searchParams.toDate = $('#toDate').val();

        this.tripMemoService.search(this.searchParams)
            .subscribe(
                searchResult  => {
                    this.tripMemoList = searchResult.tripMemos;
                    this.tripMemoSearchResult = searchResult;
                },
                error =>  this.errorMessage = "Failed to search trip memos."
            );
    }
}