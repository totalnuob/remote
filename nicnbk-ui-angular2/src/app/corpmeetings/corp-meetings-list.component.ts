import {Component, OnInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/forkJoin';

import {LookupService} from "../common/lookup.service";
import {CommonFormViewComponent} from "../common/common.component";
import {Subscription} from 'rxjs';
import {ErrorResponse} from "../common/error-response";
import {CorpMeetingService} from "./corp-meetings.service";
import {MemoSearchParamsExtended} from "../m2s2/model/memo-search-params-extended";
import {CorpMeetingSearchParams} from "./model/corp-meeting-search-params";
import {CorpMeetingSearchResults} from "./model/corp-meeting-search-results";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {BaseDictionary} from "../common/model/base-dictionary";
import {ICMeetingSearchResults} from "./model/ic-meeting-search-results";
import {ICMeeting} from "./model/ic-meeting";
import {SaveResponse} from "../common/save-response";
import {ICMeetingSearchParams} from "./model/ic-meeting-search-params";
import {ICMeetingTopicSearchParams} from "./model/ic-meeting-topic-search-params";
import {ICMeetingTopicSearchResults} from "./model/ic-meeting-topic-search-results";

declare var $:any

@Component({
    selector: 'corp-meetings-list',
    templateUrl: 'view/corp-meetings-list.component.html',
    styleUrls: [],
    providers: [],
})
export class CorpMeetingsListComponent extends CommonFormViewComponent implements OnInit {
    modalSuccessMessage: string;
    modalErrorMessage: string;

    busy: Subscription;
    public sub: any;

    icTopicSearchParams = new ICMeetingTopicSearchParams();
    icTopics = [];
    icTopicSearchResult: ICMeetingTopicSearchResults;

    icMeetings = [];
    icMeetingsSearchParams = new ICMeetingSearchParams();
    icMeetingsSearchResult: ICMeetingSearchResults

    icMeeting = new ICMeeting();

    constructor(
        private corpMeetingService: CorpMeetingService,
        private moduleAccessChecker: ModuleAccessCheckerService,
        private router: Router,
        private route: ActivatedRoute
    ){
        super(router);

        this.sub = this.route
            .params
            .subscribe(params => {
                if (params['params'] != null) {
                    this.icTopicSearchParams = JSON.parse(params['params']);

                    $('#fromDate').val(this.icTopicSearchParams.dateFrom);
                    $('#toDate').val(this.icTopicSearchParams.dateTo);

                    this.busy = this.corpMeetingService.searchICMeetingTopics(this.icTopicSearchParams)
                        .subscribe(
                            (searchResult:ICMeetingTopicSearchResults) => {
                                this.icTopics = searchResult.icMeetingTopics;
                                this.icTopicSearchResult = searchResult;
                            },
                            error => this.errorMessage = "Failed to search IC meeting topics"
                        );
                } else {
                    this.searchICMeetingTopics(0);
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

        $('#ICDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
    }

    public canEdit(){
        return this.moduleAccessChecker.checkAccessCorpMeetingsEditor();
    }

    searchICMeetingTopics(page){
        this.icTopicSearchParams.pageSize = 20;

        this.icTopicSearchParams.page = page;

        this.icTopicSearchParams.dateFrom = $('#fromDate').val();
        this.icTopicSearchParams.dateTo = $('#toDate').val();

        //console.log(this.searchParams);
        this.busy = this.corpMeetingService.searchICMeetingTopics(this.icTopicSearchParams)
            .subscribe(
                (searchResult: ICMeetingTopicSearchResults)  => {
                    this.icTopics = searchResult.icMeetingTopics;
                    this.icTopicSearchResult = searchResult;
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error searching IC meeting topic";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null,  this.errorMessage);
                }
            );
    }

    navigate(meetingId){
        this.icTopicSearchParams.path = '/corpMeetings';
        let params = JSON.stringify(this.icTopicSearchParams);
        this.router.navigate(['/corpMeetings/edit/', meetingId, { params }]);
    }

    clearSearchForm(){
        this.icTopicSearchParams.dateFrom = null;
        this.icTopicSearchParams.dateTo = null;
        this.icTopicSearchParams.searchText = null;
    }


    searchIC(page){
        this.icMeetingsSearchParams.pageSize = 20;

        this.icMeetingsSearchParams.page = 0;
        if(page) {
            this.icMeetingsSearchParams.page = page;
        }

        this.busy = this.corpMeetingService.searchICMeetings(this.icMeetingsSearchParams)
            .subscribe(
                (searchResult:ICMeetingSearchResults) => {
                    this.icMeetings = searchResult.icMeetings;
                    this.icMeetingsSearchResult = searchResult;
                },
                error => this.errorMessage = "Failed to search IC meetings."
            );
    }

    closeICSaveModal(){
        this.icMeeting = new ICMeeting();
        this.modalErrorMessage = null;
        this.modalSuccessMessage = null;
        this.searchIC(this.icMeetingsSearchParams.page);
    }

    saveICMeeting(){
        this.icMeeting.date = $('#ICDate').val();
        console.log(this.icMeeting.date);
        if(this.icMeeting.date == null || this.icMeeting.date.trim() === ''){
            this.modalErrorMessage = "Date required"
            this.modalSuccessMessage = null;
            return;
        }
        if(this.icMeeting.number == null || this.icMeeting.number.trim() === ''){
            this.modalErrorMessage = "Number required"
            this.modalSuccessMessage = null;
            return;
        }
        this.busy = this.corpMeetingService.saveICMeeting(this.icMeeting)
            .subscribe(
                (resposne: SaveResponse)  => {
                    this.icMeeting.id = resposne.entityId;
                    this.modalSuccessMessage = "Successfully saved IC meeting."
                    this.modalErrorMessage = null;
                },
                (error: ErrorResponse) => {
                    this.modalErrorMessage = error.message;
                    this.modalSuccessMessage = null;
                }
            );
    }

    editICMeeting(icMeeting){
        this.icMeeting = icMeeting;
    }
}