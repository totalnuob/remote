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
import {UpcomingEvent} from "./model/upcoming.event";
import {ICMeeting} from "./model/ic-meeting";
import {SaveResponse} from "../common/save-response";
import {ICMeetingSearchParams} from "./model/ic-meeting-search-params";

import {ICAssignmentSearchParams} from "./model/ic-assignment-search-params";
import {ICAssignmentSearchResults} from "./model/ic-assignment-search-results";

import {ICMeetingTopicSearchParams} from "./model/ic-meeting-topic-search-params";
import {ICMeetingTopicSearchResults} from "./model/ic-meeting-topic-search-results";
import {DATA_APP_URL} from "../common/common.service.constants";

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/forkJoin';

declare var $:any;

var moment = require("moment");

@Component({
    selector: 'corp-meetings-list',
    templateUrl: 'view/corp-meetings-list.component.html',
    styleUrls: [],
    providers: [],
})
export class CorpMeetingsListComponent extends CommonFormViewComponent implements OnInit {
    activeTab = "UPCOMING";
    modalSuccessMessage: string;
    modalErrorMessage: string;

    modalProtocolSuccessMessage: string;
    modalProtocolErrorMessage: string;

    busy: Subscription;
    busyCreate: Subscription;
    public sub: any;

    upcomingEventMonth;
    upcomingEvents = [];
    upcomingEventsCalendar = [];
    selectedEvent = {};

    icTopicSearchParams = new ICMeetingTopicSearchParams();
    icTopics = [];
    icTopicSearchResult: ICMeetingTopicSearchResults;

    mbTopicsSearchParams = new ICMeetingTopicSearchParams();
    mbTopics = [];
    mbTopicsSearchResult: ICMeetingTopicSearchResults;

    icMeetings = [];
    icMeetingsSearchParams = new ICMeetingSearchParams();
    icMeetingsSearchResult: ICMeetingSearchResults;

    corpMeetings = [];
    corpMeetingsSearchParams = new ICMeetingSearchParams();
    corpMeetingsSearchResult: ICMeetingSearchResults;

    assignmentsSearchParams = new ICAssignmentSearchParams();
    assignmentsSearchResult = new ICAssignmentSearchResults();
    assignments = [];

    icMeeting = new ICMeeting();
    uploadProtocolFile: any;

    corpMeeting = new ICMeeting();
    uploadMBProtocolFile: any;

    icMeetingTopicTypes: BaseDictionary[];
    corpMeetingTopicTypes: BaseDictionary[];

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
                            console.log(params);
                            var page = 0;
                            this.activeTab = params['activeTab'];

                            if(this.activeTab === 'IC_LIST'){
                                if (params['params'] != null) {
                                    this.icMeetingsSearchParams = JSON.parse(params['params']);
                                }
                                $('#fromDateIC').val(this.icMeetingsSearchParams.dateFrom);
                                $('#toDateIC').val(this.icMeetingsSearchParams.dateTo);
                                page = this.icMeetingsSearchParams.page > 0 ? this.icMeetingsSearchParams.page : 0;

                                this.searchIC(page);
                            }else if(this.activeTab === 'IC_TOPICS'){
                                console.log(params['params']);
                                if (params['params'] != null) {
                                    this.icTopicSearchParams = JSON.parse(params['params']);
                                    console.log(this.icTopicSearchParams);
                                }
                                $('#fromDate').val(this.icTopicSearchParams.dateFrom);
                                $('#toDate').val(this.icTopicSearchParams.dateTo);
                                page = this.icTopicSearchParams.page > 0 ? this.icTopicSearchParams.page : 0;

                                this.searchICMeetingTopics(page)
                            }else if(this.activeTab === 'IC_ASSIGNMENTS'){
                                if (params['params'] != null) {
                                    this.assignmentsSearchParams = JSON.parse(params['params']);
                                }
                                $('#fromDateAssignments').val(this.assignmentsSearchParams.dateFrom);
                                $('#toDateAssignments').val(this.assignmentsSearchParams.dateTo);
                                page = this.assignmentsSearchParams.page > 0 ? this.assignmentsSearchParams.page : 0;

                                this.searchICAssignments(page)
                            }else if(this.activeTab === 'MB_LIST'){
                                if (params['params'] != null) {
                                    this.corpMeetingsSearchParams = JSON.parse(params['params']);
                                }
                                $('#fromDateMB').val(this.corpMeetingsSearchParams.dateFrom);
                                $('#toDateMB').val(this.corpMeetingsSearchParams.dateTo);
                                page = this.corpMeetingsSearchParams.page > 0 ? this.corpMeetingsSearchParams.page : 0;

                                this.searchCorpMeetings(page, 'MB');
                            }else if(this.activeTab === 'MB_TOPICS'){
                                if (params['params'] != null) {
                                    this.mbTopicsSearchParams = JSON.parse(params['params']);
                                }
                                $('#fromDateMBTopics').val(this.mbTopicsSearchParams.dateFrom);
                                $('#toDateMBTopics').val(this.mbTopicsSearchParams.dateTo);
                                page = this.mbTopicsSearchParams.page > 0 ? this.mbTopicsSearchParams.page : 0;

                                this.searchCorpMeetings(page, 'MB');
                            }
                            else{
                                this.searchICMeetingUpcomingEvents();
                            }
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
        $('#fromDateAssignmentsPicker').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
        $('#untilDateAssignmentsPicker').datetimepicker({
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

        $('#fromDateCMTopicsPicker').datetimepicker({
            format: 'DD-MM-YYYY'
        });

        $('#untilDateCMTopicsPicker').datetimepicker({
            format: 'DD-MM-YYYY'
        });

        $('#fromDateCMAssignmentsPicker').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
        $('#untilDateCMAssignmentsPicker').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        $('#fromDateDTPickeerCM').datetimepicker({
            format: 'DD-MM-YYYY'
        });

        $('#untilDateDTPickeerCM').datetimepicker({
            format: 'DD-MM-YYYY'
        });

        $('#ICDateDTPickeer').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        $('#CMDateDTPickeer').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        this.assignmentsSearchParams.hideClosed = true;
    }

    searchICMeetingUpcomingEvents(){ // e.g. 09-2020
        this.activeTab = "UPCOMING";

        var now = new Date();
        const month = now.toLocaleString('default', { month: 'long' });
        this.upcomingEventMonth = month + " " + now.getFullYear();
        this.upcomingEventsCalendar = [];

        var daysOfMonth = [];
        var daysOfWeek = [];
        var todayDate = new Date().getDate();
        //console.log(todayDate);
        for (var d = new Date(now.getFullYear(), now.getMonth(), 1); d.getMonth() == now.getMonth(); d.setDate(d.getDate() + 1)) {
            var dayOfWeek = d.getDay() == 0 ? 7: d.getDay();
            if(d.getDate() == 1 && dayOfWeek > 1){
                var date = new Date(d);
                var diff = dayOfWeek;
                while(diff > 1){
                    date.setDate(date.getDate() - (diff - 1));
                    var pushDate = new Date(date);
                    daysOfWeek.push({date: pushDate, day: pushDate.getDate(), isToday: (pushDate.getDate() == todayDate), isCurrentMonth: false, isWeekend: (date.getDay() == 6 || date.getDay() == 0), events: []});
                    diff--;
                    date = new Date(d);
                }
            }
            daysOfWeek.push({date: new Date(d), day: d.getDate(), isToday: (d.getDate() == todayDate), isCurrentMonth: true, isWeekend: (dayOfWeek > 5), events: []});
             if(dayOfWeek == 7){
                daysOfMonth.push(daysOfWeek);
                daysOfWeek = [];
             }
        }
        if(daysOfWeek.length != 7 && daysOfWeek.length > 0){
            daysOfMonth.push(daysOfWeek);
        }
        //console.log(daysOfMonth);

        if(daysOfMonth[daysOfMonth.length-1].length < 7){
            var lastWeek = daysOfMonth[daysOfMonth.length-1];
            var nextMonthDate = new Date(lastWeek[lastWeek.length-1].date);
            var lastDay = nextMonthDate.getDay() == 0 ? 7 : nextMonthDate.getDay();
            while(lastDay < 7){
                nextMonthDate.setDate(nextMonthDate.getDate() + 1);
                var dayOfWeek = nextMonthDate.getDay() == 0 ? 7 : nextMonthDate.getDay();
                lastWeek.push({date: new Date(nextMonthDate), day: nextMonthDate.getDate(),
                                isToday: (nextMonthDate.getDate() == todayDate), isCurrentMonth: false,
                                isWeekend: (dayOfWeek > 5), events: []})
                lastDay++;
            }
        }
        //console.log(daysOfMonth);

        this.upcomingEventsCalendar = daysOfMonth;

        this.busy = this.corpMeetingService.searchUpcomingEvents()
                    .subscribe(
                        events => {
                            if(events != null && events.length > 0){
                                for(var k = 0; k < events.length; k++){
                                    var found = false;
                                    for(var i = 0; !found && i < this.upcomingEventsCalendar.length; i++){
                                        for(var j = 0; !found && j < this.upcomingEventsCalendar[i].length; j++){
                                            var item = this.upcomingEventsCalendar[i][j];
                                            // check date
                                            var eventDate = moment(events[k].date, 'DD-MM-YYYY').toDate();
                                            if(item.date.getTime() === eventDate.getTime()){
                                                item.events.push(events[k]);
                                                found = true;
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        (error: ErrorResponse) => {
                            this.errorMessage = "Error searching IC meeting topic";
                            if(error && !error.isEmpty()){
                                this.processErrorMessage(error);
                            }
                            this.postAction(null,  this.errorMessage);
                        }
                    );
        /*
        this.upcomingEventsCalendar.push([{date: "31-08-2020", day: "31", isCurrentMonth: false, events: []},
        {date: "01-09-2020", day: "01", isCurrentMonth: true, events: []}, {date: "02-09-2020", day: "02", isToday: true, isCurrentMonth: true, events: [
        {name: "IC Meeting question submission due date", description: "This is a test 1 event"},
        {name: "IC Meeting question submission due date", description: "This is a test 1 event"},
        {name: "IC Meeting question submission due date", description: "This is a test 2 event"}]},
        {date: "03-09-2020", day: "03", isCurrentMonth: true, events: []},
        {date: "04-09-2020", day: "04", isCurrentMonth: true, events: []}, {date: "05-09-2020", day: "05", isCurrentMonth: true, isWeekend: true, events: []}, {date: "06-09-2020", day: "06", isCurrentMonth: true, isWeekend: true, events: []}, ]);
        this.upcomingEventsCalendar.push([{date: "07-09-2020", day: "07", isCurrentMonth: true, events: []},
                {date: "08-09-2020", day: "08", isCurrentMonth: true, events: []}, {date: "09-09-2020", day: "09", isCurrentMonth: true, events: []}, {date: "10-09-2020", day: "10", isCurrentMonth: true, events: []},
                {date: "11-09-2020", day: "11", isCurrentMonth: true, events: []}, {date: "12-09-2020", day: "12", isCurrentMonth: true, isWeekend: true, events: []}, {date: "13-09-2020", day: "13", isCurrentMonth: true, isWeekend: true, events: []}, ]);
        this.upcomingEventsCalendar.push([{date: "14-09-2020", day: "14", isCurrentMonth: true, events: []},
                {date: "15-09-2020", day: "15", isCurrentMonth: true, events: []}, {date: "16-09-2020", day: "16", isCurrentMonth: true, events: []}, {date: "17-09-2020", day: "17", isCurrentMonth: true, events: []},
                {date: "18-09-2020", day: "18", isCurrentMonth: true, events: []}, {date: "19-09-2020", day: "19", isCurrentMonth: true, isWeekend: true, events: []}, {date: "20-09-2020", day: "20", isCurrentMonth: true, isWeekend: true, events: []}, ]);
        this.upcomingEventsCalendar.push([{date: "21-09-2020", day: "21", isCurrentMonth: true, events: [{name: "IC Meeting #258", description: "IC MEETINGS IS YO"}]},
                {date: "22-09-2020", day: "22", isCurrentMonth: true, events: []}, {date: "23-09-2020", day: "23", isCurrentMonth: true, events: []}, {date: "24-09-2020", day: "24", isCurrentMonth: true, events: []},
                {date: "25-09-2020", day: "25", isCurrentMonth: true, events: []}, {date: "26-09-2020", day: "26", isCurrentMonth: true, isWeekend: true, events: []}, {date: "27-09-2020", day: "27", isCurrentMonth: true, isWeekend: true, events: []}, ]);
        this.upcomingEventsCalendar.push([{date: "26-09-2020", day: "26", isCurrentMonth: true, events: []},
                {date: "27-09-2020", day: "27", isCurrentMonth: true, events: []}, {date: "28-09-2020", day: "28", isCurrentMonth: true, events: []}, {date: "29-09-2020", day: "29", isCurrentMonth: true, events: []},
                {date: "30-09-2020", day: "30", isCurrentMonth: true, events: []}, {date: "01-10-2020", day: "01", isCurrentMonth: false, isWeekend: true, events: []}, {date: "02-10-2020", day: "02", isCurrentMonth: false, isWeekend: true, events: []}, ]);
        */
        //console.log(this.upcomingEventsCalendar);
    }


    showEvent(event){
        this.selectedEvent = event;
        console.log(event);
    }
    getCalendarDayClass(day){
        let classes = 'col-xs-12';
        classes = !day.isWeekend ? classes + ' calendar-day' : classes + ' calendar-day-weekend';
        classes = !day.isCurrentMonth ? classes + ' calendar-no-current-month' : classes;
        classes = day.isToday ? classes + ' calendar-day-today' : classes;
        //console.log(classes);
        return classes;
    }

    searchICAssignments(page){
        this.activeTab = "IC_ASSIGNMENTS";

        this.successMessage = null;
        this.errorMessage = null;

        this.assignmentsSearchParams.pageSize = 20;
        this.assignmentsSearchParams.page = page;

        this.assignmentsSearchParams.dateFrom = $('#fromDateAssignments').val();
        this.assignmentsSearchParams.dateTo = $('#toDateAssignments').val();

        //console.log(this.icTopicSearchParams);
        this.busy = this.corpMeetingService.searchICAssignments(this.assignmentsSearchParams)
            .subscribe(
                (searchResult: ICAssignmentSearchResults)  => {
                    this.assignmentsSearchResult = searchResult;
                    this.assignments = searchResult.assignments;
                    if(this.assignments != null && this.assignments.length > 0){
                        for(var i = 0; i < this.assignments.length; i++){
                            if(this.assignments[i].employees != null && this.assignments[i].employees.length > 0){
                                this.assignments[i].employeesText = '';
                                for(var j = 0; j < this.assignments[i].employees.length; j++){
                                    if(this.assignments[i].employeesText.length > 0){
                                        this.assignments[i].employeesText += ', ';
                                    }
                                    this.assignments[i].employeesText += this.assignments[i].employees[j].lastName;
                                }
                            }
                        }
                    }
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error searching IC assignments";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null,  this.errorMessage);
                }
            );
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

    searchMBMeetingTopics(page, type){
        this.successMessage = null;
        this.errorMessage = null;

        if (type === 'EXEC') {
            this.activeTab = "MB_TOPICS";

            this.mbTopicsSearchParams.pageSize = 20;
            this.mbTopicsSearchParams.page = page;

            if (this.mbTopicsSearchParams.corpMeetingType === 'NONE'){
                this.mbTopicsSearchParams.corpMeetingType = null;
            }

            this.mbTopicsSearchParams.dateFrom = $('#fromDateMBTopics').val();
            this.mbTopicsSearchParams.dateTo = $('#toDateMBTopics').val();
            this.mbTopicsSearchParams.corpMeetingType = type;

            this.busy = this.corpMeetingService.searchICMeetingTopics(this.mbTopicsSearchParams)
                .subscribe(
                    (searchResult: ICMeetingTopicSearchResults) => {
                        this.mbTopics = searchResult.icMeetingTopics;
                        this.mbTopicsSearchResult = searchResult;
                        console.log(this.mbTopics);
                    },
                    (error : ErrorResponse) => {
                        this.errorMessage = "Error searching EXEC meeting topic";
                        if(error && !error.isEmpty()){
                            this.processErrorMessage(error);
                        }
                        this.postAction(null,  this.errorMessage);
                    }
                );
        } else {
            this.activeTab = "IC_TOPICS";

            this.mbTopicsSearchParams.pageSize = 20;
            this.mbTopicsSearchParams.page = page;

            if (this.icTopicSearchParams.corpMeetingType === 'NONE'){
                this.icTopicSearchParams.corpMeetingType = null;
            }

            this.icTopicSearchParams.dateFrom = $('#fromDateMBTopics').val();
            this.icTopicSearchParams.dateTo = $('#toDateMBTopics').val();
            this.icTopicSearchParams.corpMeetingType = type;

            this.busy = this.corpMeetingService.searchICMeetingTopics(this.icTopicSearchParams)
                .subscribe(
                    (searchResult: ICMeetingTopicSearchResults) => {
                        this.icTopics = searchResult.icMeetingTopics;
                        this.icTopicSearchResult = searchResult;
                        console.log(this.icTopics);
                    },
                    (error : ErrorResponse) => {
                        this.errorMessage = "Error searching IC meeting topic";
                        if(error && !error.isEmpty()){
                            this.processErrorMessage(error);
                        }
                        this.postAction(null,  this.errorMessage);
                    }
                );
        }
    }

    navigate(topicId){
        this.icTopicSearchParams.path = '/corpMeetings';
        let params = JSON.stringify(this.icTopicSearchParams);
        this.router.navigate(['/corpMeetings/edit/', topicId, { params }]);
    }

    canViewICAssignments(){
        return this.canViewICTopics();
    }

    canViewMBAssignments(){
        return this.canViewMBTopics();
    }

    navigateAssignment(assignmentId){
        this.assignmentsSearchParams.path = '/corpMeetings';
        let params = JSON.stringify(this.assignmentsSearchParams);
        console.log(assignmentId);
        this.router.navigate(['/corpMeetings/assignment/edit/', assignmentId, { params }]);
    }

    clearSearchForm(){
        this.icTopicSearchParams = new ICMeetingTopicSearchParams();
    }

    clearAssignmentsSearchForm(){
        this.assignmentsSearchParams = new ICAssignmentSearchParams();
        this.assignmentsSearchParams.hideClosed = true;
    }

    clearSearchFormIC(){
        this.icMeetingsSearchParams.dateFrom = null;
        this.icMeetingsSearchParams.dateTo = null;
        this.icMeetingsSearchParams.number = null;

        $('#fromDateIC').val(null);
        $('#toDateIC').val(null);
    }

    clearSearchFormCM(){
        this.corpMeetingsSearchParams.dateFrom = null;
        this.corpMeetingsSearchParams.dateTo = null;
        this.corpMeetingsSearchParams.number = null;

        $('#fromDateCM').val(null);
        $('#toDateCM').val(null);
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

    searchCorpMeetings(page, type){
        this.successMessage = null;
        this.errorMessage = null;

        this.corpMeetingsSearchParams.pageSize = 20;
        this.corpMeetingsSearchParams.page = 0;

        if(page) {
            this.corpMeetingsSearchParams.page = page;
        }

        if (type === "IC") {
            this.activeTab = "IC_LIST";

            this.icMeetingsSearchParams.dateFrom = $('#fromDateIC').val();
            this.icMeetingsSearchParams.dateTo = $('#toDateIC').val();

            this.icMeetingsSearchParams.type = type;

            this.busy = this.corpMeetingService.searchICMeetings(this.icMeetingsSearchParams)
                .subscribe(
                    (searchResult:ICMeetingSearchResults) => {
                        this.icMeetings = searchResult.icMeetings;
                        this.icMeetingsSearchResult = searchResult;
                        //console.log(this.icMeetingsSearchResult);
                    },
                    error => this.errorMessage = "Failed to search IC meetings."
                );
        } else {
            this.activeTab = "MB_LIST";
            this.corpMeetingsSearchParams.dateFrom = $('#fromDateCM').val();
            this.corpMeetingsSearchParams.dateTo = $('#toDateCM').val();

            this.corpMeetingsSearchParams.type = type;

            this.busy = this.corpMeetingService.searchICMeetings(this.corpMeetingsSearchParams)
                .subscribe(
                    (searchResult:ICMeetingSearchResults) => {
                        this.corpMeetings = searchResult.icMeetings;
                        this.corpMeetingsSearchResult = searchResult;
                    },
                    error => this.errorMessage = "Failed to search corp meetings."
                );
        }
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
        let params = JSON.stringify(this.icMeetingsSearchParams);
        this.router.navigate(['/corpMeetings/ic/edit/', icMeeting.id, { params }]);
    }

    deleteICMeeting(icMeeting){
        alert("TODO");
        /*if(confirm("Are you sure want to delete?")) {
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
        }*/
    }
    canEditICMeeting(icMeeting){
        return this.moduleAccessChecker.checkAccessICMeetingsEdit();
    }
    canDeleteICMeeting(icMeeting){
        return true;
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
        return icMeeting != null && icMeeting.closed && (this.moduleAccessChecker.checkAccessAdmin() || this.moduleAccessChecker.checkAccessCorpMeetingsView());
    }

    canAddProtocol(icMeeting){
        return icMeeting != null && !icMeeting.closed && (this.moduleAccessChecker.checkAccessAdmin() || this.moduleAccessChecker.checkAccessCorpMeetingsEdit());
    }

    canCreateNewTopic(){
        return (this.moduleAccessChecker.checkAccessAdmin() || this.moduleAccessChecker.checkAccessCorpMeetingsView());
    }

    canCreateNewTopicMB(){
        return (this.moduleAccessChecker.checkAccessAdmin() || this.moduleAccessChecker.checkAccessMBMeetingsView());
    }

    canViewIC(){
        return this.moduleAccessChecker.checkAccessICMeetingsView();
    }

    canViewICTopics(){
        return this.moduleAccessChecker.checkAccessICMeetingTopicsView();
    }

    canViewMB(){
        return this.moduleAccessChecker.checkAccessMBMeetingsView();
    }

    canViewMBTopics(){
        return this.moduleAccessChecker.checkAccessMBMeetingTopicsView();
    }

    getTopicClassByStatus(topic){
        //console.log(topic);
        if(topic.status != null){
            if(topic.status === 'DRAFT'){
                return 'label label-default';
            }
            if(topic.status === 'CLOSED'){
                return 'label label-primary';
            }
            if(topic.status === 'LOCKED FOR IC'){
                return 'label label-primary';
            }
            if(topic.status === 'TO BE FINALIZED'){
                return 'label label-info';
            }
            if(topic.status === 'FINALIZED'){
                return 'label label-success';
            }
            if(topic.status === 'UNDER REVIEW'){
                return 'label label-warning';
            }
            if(topic.status === 'READY' || topic.status === 'APPROVED'){
                return 'label label-success';
            }
        }
        return 'label label-default';
    }

    /*getTopicStatus(topic){
        if(!topic.published){
            return 'DRAFT';
        }else if(topic.closed){
            return 'CLOSED';
        }else{
            // Check if sent to IC
            if(topic.icMeeting.lockedByDeadline){
                return 'LOCKED FOR IC';
            }

            // Check if to be finalized after IC
            if(topic.icMeeting.unlockedForFinalize){
                return 'TO BE FINALIZED';
            }

            if(topic.approveList == null || topic.approveList.length == 0){
                return 'READY';
            }else{
                for(var i = 0; i < topic.approveList.length; i++){
                    if(!topic.approveList[i].approved){
                        return 'UNDER REVIEW';
                    }
                }
                // Approved
                return 'APPROVED';
            }
        }
    }*/


     getICClassByStatus(ic){
        //console.log(ic);
        if(ic.status === 'CLOSED'){
            return 'label label-primary';
        }else{
            // Check if sent to IC
            if(ic.status === 'LOCKED FOR IC'){
                return 'label label-primary';
            }
            // Check if to be finalized after IC
            if(ic.status === 'TO BE FINALIZED'){
                return 'label label-info';
            }
            if(ic.status === 'FINALIZED'){
                return 'label label-success';
            }
            return 'label label-default';
        }
    }

    /*getICStatus(ic: ICMeeting){
        if(ic.closed){
            return 'CLOSED';
        }else{
            // Check if sent to IC
            if(ic.lockedByDeadline){
                return 'LOCKED FOR IC';
            }
            // Check if to be finalized after IC
            if(ic.unlockedForFinalize){
                return 'TO BE FINALIZED';
            }
            return 'OPEN';
        }
    }*/


    exportRegistry(){
        var fileName = "???????????? ?????????????????????? ?????????????? ?????????????????????? ????????????????????";
        //fileName = fileName.replace(".", ",");
        this.busy = this.corpMeetingService.makeFileRequest(DATA_APP_URL + `corpMeetings/ICMeeting/exportProtocolRegistry`,
            fileName, 'POST')
            .subscribe(
                response  => {
                    console.log("export protocol registry response ok");
                },
                error => {
                    //console.log("fails")
                    this.postAction(null, "Error exporting protocol registry");
                }
            );
    }

    exportAssignmentRegistry(){
        var fileName = "???????????????????? ???? ???????????????????? ???????????????? ???? 2021";
        this.busy = this.corpMeetingService.makeFileRequest(DATA_APP_URL + `corpMeetings/assignment/exportRegistry`,
            fileName, 'POST')
            .subscribe(
                response  => {
                    console.log("export assignment registry response ok");
                },
                error => {
                    //console.log("fails")
                    this.postAction(null, "Error exporting assignment registry");
                }
            );
    }

}