import {Component, OnInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';

import {LookupService} from "../common/lookup.service";
import {CommonFormViewComponent} from "../common/common.component";
import {Subscription} from 'rxjs';
import {ErrorResponse} from "../common/error-response";
import {CorpMeetingService} from "./corp-meetings.service";
import {MemoSearchParamsExtended} from "../m2s2/model/memo-search-params-extended";
import {CorpMeetingSearchParams} from "./model/corp-meeting-search-params";
import {CorpMeetingSearchResults} from "./model/corp-meeting-search-results";

declare var $:any

@Component({
    selector: 'corp-meetings-list',
    templateUrl: 'view/corp-meetings-list.component.html',
    styleUrls: [],
    providers: [],
})
export class CorpMeetingsListComponent extends CommonFormViewComponent implements OnInit {
    busy: Subscription;
    public sub: any;

    searchParams = new CorpMeetingSearchParams();
    corpMeetings = [];
    searchResult: CorpMeetingSearchResults;

    constructor(
        private lookupService: LookupService,
        private corpMeetingService: CorpMeetingService,
        private router: Router,
        private route: ActivatedRoute
    ){
        super(router);

        this.sub = this.route
            .params
            .subscribe(params => {
                if(params['params'] != null){
                    this.searchParams = JSON.parse(params['params']);
                    this.busy = this.corpMeetingService.search(this.searchParams)
                        .subscribe(
                            (searchResult: CorpMeetingSearchResults) => {
                                this.corpMeetings = searchResult.corpMeetings;
                                this.searchResult = searchResult;
                            },
                            error =>  this.errorMessage = "Failed to search corp meetings."
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

    public canEdit(){
        return true;
    }

    search(page){
        this.searchParams.pageSize = 20;

        this.searchParams.page = page;

        this.searchParams.dateFrom = $('#fromDate').val();
        this.searchParams.dateTo = $('#toDate').val();

        console.log(this.searchParams);
        this.busy = this.corpMeetingService.search(this.searchParams)
            .subscribe(
                (searchResult: CorpMeetingSearchResults)  => {
                    this.corpMeetings = searchResult.corpMeetings;
                    this.searchResult = searchResult;
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error searching corp meetings";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null,  this.errorMessage);
                }
            );
    }
}