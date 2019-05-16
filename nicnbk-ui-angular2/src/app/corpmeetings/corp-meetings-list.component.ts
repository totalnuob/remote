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

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/forkJoin';

declare var $:any

@Component({
    selector: 'corp-meetings-list',
    templateUrl: 'view/corp-meetings-list.component.html',
    styleUrls: [],
    providers: [],
})
export class CorpMeetingsListComponent extends CommonFormViewComponent implements OnInit {
    activeTab = "IC_TOPICS";
    modalSuccessMessage: string;
    modalErrorMessage: string;

    modalProtocolSuccessMessage: string;
    modalProtocolErrorMessage: string;

    busy: Subscription;
    busyCreate: Subscription;
    public sub: any;

    icTopicSearchParams = new ICMeetingTopicSearchParams();
    icTopics = [];
    icTopicSearchResult: ICMeetingTopicSearchResults;

    icMeetings = [];
    icMeetingsSearchParams = new ICMeetingSearchParams();
    icMeetingsSearchResult: ICMeetingSearchResults

    icMeeting = new ICMeeting();
    uploadProtocolFile: any;

    icMeetingTopicTypes: BaseDictionary[];

    constructor(
        private corpMeetingService: CorpMeetingService,
        private lookupService: LookupService,
        private moduleAccessChecker: ModuleAccessCheckerService,
        private router: Router,
        private route: ActivatedRoute
    ){
        super(router);

        if(this.route.params["_value"] != null && this.route.params["_value"].successMessage != null){
            this.successMessage = this.route.params["_value"].successMessage;
        }

        Observable.forkJoin(
            // Load lookups
            this.lookupService.getICMeetingTopicTypes()
            )
            .subscribe(
                ([data1]) => {
                    this.icMeetingTopicTypes = [];
                    // Check rights
                    for(var i = 0; i < data1.length; i++){
                        var element = data1[i];
                        if(this.moduleAccessChecker.checkAccessAdmin() || this.moduleAccessChecker.checkAccessICMember()){
                            this.icMeetingTopicTypes.push(element);
                        }else if(element.code === "PE"){
                            if(this.moduleAccessChecker.checkAccessPrivateEquityEditor()){
                                this.icMeetingTopicTypes.push(element);
                            }
                        }else if(element.code === "HF"){
                            if(this.moduleAccessChecker.checkAccessHedgeFundsEditor()){
                                this.icMeetingTopicTypes.push(element);
                            }
                        }else if(element.code === "RE"){
                            if(this.moduleAccessChecker.checkAccessRealEstateEditor()){
                                this.icMeetingTopicTypes.push(element);
                            }
                        }else if(element.code === "SRM"){
                            if(this.moduleAccessChecker.checkAccessStrategyRisksEditor()){
                                this.icMeetingTopicTypes.push(element);
                            }
                        }else if(element.code === "REP"){
                            if(this.moduleAccessChecker.checkAccessReportingEditor()){
                                this.icMeetingTopicTypes.push(element);
                            }
                        }else{

                        }
                    }
                    //data1.forEach(element => {
                    //    this.icMeetingTopicTypes.push(element);
                    //});

                    this.sub = this.route
                        .params
                        .subscribe(params => {
                            var page = 0;
                            if (params['params'] != null) {
                                this.icTopicSearchParams = JSON.parse(params['params']);

                                $('#fromDate').val(this.icTopicSearchParams.dateFrom);
                                $('#toDate').val(this.icTopicSearchParams.dateTo);

                                page = this.icTopicSearchParams.page > 0 ? this.icTopicSearchParams.page : 0;
                                //this.busy = this.corpMeetingService.searchICMeetingTopics(this.icTopicSearchParams)
                                //    .subscribe(
                                //        (searchResult:ICMeetingTopicSearchResults) => {
                                //            this.icTopics = searchResult.icMeetingTopics;
                                //            this.icTopicSearchResult = searchResult;
                                //        },
                                //        error => this.errorMessage = "Failed to search IC meeting topics"
                                //    );
                            } else {

                            }
                            if(this.icMeetingTopicTypes != null && this.icMeetingTopicTypes.length == 1){
                                for(var i = 0; i < this.icMeetingTopicTypes.length; i++){
                                    if(this.icMeetingTopicTypes[i].code != 'NONE'){
                                        this.icTopicSearchParams.type = this.icMeetingTopicTypes[i].code;
                                    }
                                }
                            }
                            this.searchICMeetingTopics(page);
                        });
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

        $('#fromDateDTPickeerIC').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
        $('#untilDateDTPickeerIC').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        $('#ICDateDTPickeer').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
    }

    searchICMeetingTopics(page){
        this.activeTab = "IC_TOPICS";

        this.successMessage = null;
        this.errorMessage = null;

        this.icTopicSearchParams.pageSize = 20;
        this.icTopicSearchParams.page = page;

        if(this.icTopicSearchParams.type === 'NONE'){
            this.icTopicSearchParams.type = null;
        }
        this.icTopicSearchParams.dateFrom = $('#fromDate').val();
        this.icTopicSearchParams.dateTo = $('#toDate').val();

        //console.log(this.icTopicSearchParams);
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
        this.icTopicSearchParams = new ICMeetingTopicSearchParams();
    }

    clearSearchFormIC(){
        this.icMeetingsSearchParams.dateFrom = null;
        this.icMeetingsSearchParams.dateTo = null;
        this.icMeetingsSearchParams.number = null;

        $('#fromDateIC').val(null);
        $('#toDateIC').val(null);

    }

    searchIC(page){
        this.activeTab = "IC_LIST";

        this.successMessage = null;
        this.errorMessage = null;

        this.icMeetingsSearchParams.pageSize = 20;
        this.icMeetingsSearchParams.page = 0;

        if(page) {
            this.icMeetingsSearchParams.page = page;
        }

        this.icMeetingsSearchParams.dateFrom = $('#fromDateIC').val();
        this.icMeetingsSearchParams.dateTo = $('#toDateIC').val();
        //console.log(this.icMeetingsSearchParams);
        this.busy = this.corpMeetingService.searchICMeetings(this.icMeetingsSearchParams)
            .subscribe(
                (searchResult:ICMeetingSearchResults) => {
                    this.icMeetings = searchResult.icMeetings;
                    this.icMeetingsSearchResult = searchResult;
                    //console.log(this.icMeetingsSearchResult);
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
        //console.log(this.icMeeting.date);
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
        if(this.icMeeting.closed && this.uploadProtocolFile == null){
            this.modalErrorMessage = "Protocol file required for IC Meeting with status CLOSED";
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
        this.modalErrorMessage = null;
        this.modalSuccessMessage = null;
        if(icMeeting == null){
            this.icMeeting = new ICMeeting();
        }else {
            this.icMeeting = icMeeting;
        }
    }

    deleteICMeeting(icMeeting){
        if(confirm("Are you sure want to delete?")) {
            this.busy = this.corpMeetingService.deleteICMeeting(icMeeting.id)
                .subscribe(
                    (resposne) => {
                        this.searchIC(this.icMeetingsSearchParams.page);
                        this.postAction("Successfully deleted IC meeting.", null);
                    },
                    (error:ErrorResponse) => {
                        this.errorMessage = error.message != null && error.message.trim() != "" ? error.message : "Error deleting IC Meeting";
                        this.successMessage = null;
                        this.postAction(this.successMessage, this.errorMessage)
                    }
                );
        }
    }

    showAddProtocolModal(icMeeting){
        this.icMeeting = icMeeting;
    }

    onFileChangeProtocol(event){
        var target = event.target || event.srcElement;
        var file = target.files;
        this.uploadProtocolFile = file;

    }

    deleteUnsavedAttachment(){
        this.uploadProtocolFile = null;
        $('#attachmentFile').val("");
    }

    saveProtocolAttachment(){
        //console.log(this.icMeeting);
        //console.log(this.uploadProtocolFile);
        this.busyCreate = this.corpMeetingService.postICMeetingProtocolFiles(this.icMeeting.id, [], this.uploadProtocolFile).subscribe(
            res => {
                // clear upload files list on view
                this.uploadProtocolFile = null;

                // update files list with new files
                if(res.length > 0) {
                    this.icMeeting.closed = true;
                    this.icMeeting.protocolFileId = res[0].id;
                    this.icMeeting.protocolFileName = res[0].fileName;
                    this.modalProtocolSuccessMessage = "Successfully saved IC meeting Protocol";
                    this.modalProtocolErrorMessage = null;
                }else{
                    this.modalProtocolErrorMessage = "Error saving IC meeting Protocol: received no return from server";
                    this.modalProtocolSuccessMessage  = null;
                }

            },
            error => {

                // TODO: ?????????

            });
    }

    deleteICMeetingProtocol(icMeeting){
        if(confirm("Are you sure want to delete?")) {
            this.corpMeetingService.deleteICMeetingProtocolAttachment(icMeeting.id, icMeeting.protocolFileId)
                .subscribe(
                    response => {
                        icMeeting.protocolFileId = null;
                        icMeeting.protocolFileName = null;
                        icMeeting.closed = false;

                        this.postAction("Attachment deleted.", null);
                    },
                    (error:ErrorResponse) => {
                        this.errorMessage = "Error deleting attachment";
                        if (error && !error.isEmpty()) {
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, null);
                    }
                );
        }
    }
    closeProtocolModal(){
        this.modalProtocolErrorMessage = null;
        this.modalProtocolSuccessMessage = null;
    }

    canViewProtocol(icMeeting){
        return icMeeting != null && icMeeting.closed &&  this.moduleAccessChecker.checkAccessICMember();
    }

    canAddProtocol(icMeeting){
        return icMeeting != null && !icMeeting.closed &&  this.moduleAccessChecker.checkAccessICMember();
    }

    canCreateNewTopic(){
        return this.moduleAccessChecker.checkAccessHedgeFundsEditor() ||
            this.moduleAccessChecker.checkAccessPrivateEquityEditor() || this.moduleAccessChecker.checkAccessRealEstateEditor() ||
            this.moduleAccessChecker.checkAccessStrategyRisksEditor() || this.moduleAccessChecker.checkAccessReportingEditor();
    }

}