import { Component, OnInit  } from '@angular/core';
import { ROUTER_DIRECTIVES } from '@angular/router';
import {LookupService} from "../common/lookup.service";
import {MemoSearchParams} from "./model/memo-search-params";
import {MemoService} from "./memo.service";
import {CommonComponent} from "../common/view.component";
import {Memo} from "./model/memo";
import {MemoSearchResults} from "./model/memo-search-results";

declare var $:any

@Component({
    selector: 'm2s2-list',
    templateUrl: `app/m2s2/view/memo-list.component.html`,
    styleUrls: [],
    directives: [ROUTER_DIRECTIVES],
    providers: [],
})
export class MemoListComponent  extends CommonComponent implements OnInit{

    searchParams = new MemoSearchParams;

    memoTypes = [];
    meetingTypes = [];

    memoList: Memo[];

    memoSearchResult: MemoSearchResults;


    constructor(
        private lookupService: LookupService,
        private memoService: MemoService
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
        this.loadLookups();

        // find all
        this.search(0);
    }

    loadLookups(){
        // memo types
        this.lookupService.getMemoTypes().then(memoTypes => this.memoTypes = memoTypes);

        //meeting types
        this.lookupService.getMeetingTypes().then(meetingTypes => this.meetingTypes = meetingTypes);
    }

    search(page){

        // TODO: as parameter?
        this.searchParams.pageSize = 10;

        if(page > 0) {
            this.searchParams.page = page;
        }

        this.searchParams.fromDate = $('#fromDate').val();
        this.searchParams.toDate = $('#toDate').val();

        this.memoService.search(this.searchParams)
            .subscribe(
                searchResult  => {
                    this.memoList = searchResult.memos;
                    this.memoSearchResult = searchResult;
                },
                error =>  this.errorMessage = <any>error
            );
    }

}