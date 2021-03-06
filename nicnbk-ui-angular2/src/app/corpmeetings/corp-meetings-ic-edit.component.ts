import {Component, OnInit, ViewChild} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import { SelectComponent} from "ng2-select";
import {DATA_APP_URL} from "../common/common.service.constants";

import {LookupService} from "../common/lookup.service";
import {CommonFormViewComponent} from "../common/common.component";
import {Subscription} from 'rxjs';
import {ErrorResponse} from "../common/error-response";
import {CorpMeeting} from "./model/corp-meeting";
import {EmployeeService} from "../employee/employee.service";

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/forkJoin';
import {CorpMeetingService} from "./corp-meetings.service";
import {SaveResponse} from "../common/save-response";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {CorpMeetingSearchParams} from "./model/corp-meeting-search-params";
import {ICMeeting} from "./model/ic-meeting";
import {BaseDictionary} from "../common/model/base-dictionary";

//import { TagInputModule } from 'ng2-tag-input';

declare var $:any

@Component({
    selector: 'corp-meeting-ic-edit',
    templateUrl: 'view/corp-meeting-ic-edit.component.html',
    styleUrls: [],
    providers: [],
})
export class CorpMeetingICEditComponent extends CommonFormViewComponent implements OnInit {

    busy: Subscription;
    public sub: any;

    voteSuccessMessage:
    voteErrorMessage

    //icList: ICMeeting[];
    icMeeting: ICMeeting;

    public attendeesList = [];
    ceoSubList = [];

    public attendeesListMB = [];
    ceoSublistMB = [];

    public finalAttendeesList = [];
    finalCeoSublist = [];

    attendeesListPlaceholder = [];
    ceoSublistPlaceholder = [];

    @ViewChild('inviteesSelect')
    private inviteesSelect;

    @ViewChild('corpMeetingTypeSelect')
    private corpMeetingTypeSelect;

    public inviteesList = [];

    public absenceTypes = [];

    public placeTypes = [];

    public types = [];

    public voteTypes = [];

    private breadcrumbParams: string;
    //private searchParams = new ICMeetingTopicSearchParams();

    tagOptions = {
        placeholder: "+ tag",
        secondaryPlaceholder: "Enter a new tag",
        separatorKeys: [188, 191], // exclude coma from tag content
        maxItems: 20
    };

    public corpMeetingTypes = ['IC', 'EXEC'];

    uploadAgendaFile = [];
    uploadProtocolFile = [];
    uploadBulletinFile = [];

    //icMeetingTopicTypes: BaseDictionary[];

    constructor(
        private employeeService: EmployeeService,
        private lookupService: LookupService,
        private corpMeetingService: CorpMeetingService,
        private router: Router,
        private route: ActivatedRoute,
        private moduleAccessChecker: ModuleAccessCheckerService
    ){
        super(router);

        this.icMeeting = new ICMeeting();
        this.icMeeting.corpMeetingType = 'IC';

        Observable.forkJoin(
            // Load lookups
            this.lookupService.getICMeetingAbsenceTypes(),
            this.employeeService.findICMembers(),
            this.employeeService.findAll(),
            this.lookupService.getICMeetingPlaceTypes(),
            this.lookupService.getICMeetingVoteTypes(),
            this.employeeService.findMBMembers(),
            )
            .subscribe(
                ([data1, data2, data3, data4, data5, data6]) => {
                    data1.forEach(element => {
                        this.absenceTypes.push(element);
                    });
                    data2.forEach(element => {
                        if(element.position != null && element.position.code != null && element.position.code === 'CEO'){
                            element.isCeo = true;
                        }
                        this.attendeesList.push({"employee" : element, present: true});

                        if(element.position != null && element.position.code != null && element.position.code === 'DEP_CEO'){
                            this.ceoSubList.push({id: element.id, text: element.firstName + " " + element.lastName,
                                                            firstName: element.firstName, lastName: element.lastName});
                        }
                    });
                    data3.forEach(element => {
                        this.inviteesList.push({id: element.id, text: element.firstName + " " + element.lastName});
                    });
                    data4.forEach(element => {
                        this.placeTypes.push(element);
                    });
                    data5.forEach(element => {
                        this.voteTypes.push(element);
                    });
                    data6.forEach(element => {
                        if(element.position != null && element.position.code != null && element.position.code === 'CEO'){
                            element.isCeo = true;
                        }
                        this.attendeesListMB.push({"employee" : element, present: true});

                        if(element.position != null && element.position.code != null && element.position.code === 'DEP_CEO'){
                            this.ceoSublistMB.push({id: element.id, text: element.firstName + " " + element.lastName,
                                firstName: element.firstName, lastName: element.lastName});
                        }
                    });

                    this.sub = this.route
                        .params
                        .subscribe(params => {
                            this.icMeeting.id = +params['id'];
                            this.breadcrumbParams = params['params'];
                            if(this.breadcrumbParams != null) {
                                this.searchParams = JSON.parse(this.breadcrumbParams);
                            }
                            if(this.icMeeting.id > 0) {
                               this.getICMeeting(this.icMeeting.id, null, null);
                            }else{
                                this.icMeeting.id = null;
                                this.icMeeting.ceoSubEmployee = {};
                            }
                        });
                });
    }

    getICMeeting(id, successMessage, errorMessage){
        this.busy = this.corpMeetingService.getICMeeting(id)
            .subscribe(
                (ic: ICMeeting )=> {
                    this.icMeeting = ic;
                    // preselect attendees
                    if(this.icMeeting.attendees != null && this.icMeeting.attendees.length > 0){
                        for(var j = 0; j < this.attendeesList.length; j++){
                            var isPresent = false;
                            for(var i = 0; i < this.icMeeting.attendees.length; i++){
                                if(this.icMeeting.attendees[i].employee.id == this.attendeesList[j].employee.id){
                                    this.attendeesList[j].present = this.icMeeting.attendees[i].present;
                                    if(!this.attendeesList[j].present){
                                        this.attendeesList[j].absenceType = this.icMeeting.attendees[i].absenceType;
                                    }else{
                                        isPresent = true;
                                    }
                                    break;
                                }
                            }
                            if(!isPresent){
                                this.attendeesList[j].present = false;
                            }
                        }

                        // add IC members missing from this.attendeesList, but present in this.icMeeting.attendees
                        for(var i = 0; i < this.icMeeting.attendees.length; i++){
                            var missing = true;
                            for(var j = 0; j < this.attendeesList.length; j++){
                                if(this.icMeeting.attendees[i].employee.id == this.attendeesList[j].employee.id){
                                    missing = false;
                                    break;
                                }
                            }
                            if(missing){
                                // add
                                this.attendeesList.push(this.icMeeting.attendees[i]);
                            }
                        }

                        //
                        if(this.icMeeting.ceoSubEmployee != null && this.icMeeting.ceoSubEmployee.id != null){
                            var missing = true;
                            for(var i = 0; i < this.ceoSubList.length; i++){
                                if(this.ceoSubList[i].id == this.icMeeting.ceoSubEmployee.id){
                                    missing = false;
                                }
                            }
                            if(missing){
                                this.ceoSubList.push(this.icMeeting.ceoSubEmployee);
                            }
                        }else{
                            if(this.icMeeting.ceoSubEmployee == null){
                                this.icMeeting.ceoSubEmployee = {};
                            }
                        }
                    }

                    // preselect invitees
                    if(this.icMeeting.invitees != null){
                        for(var i = 0; i < this.icMeeting.invitees.length; i++){
                           this.icMeeting.invitees[i].text = this.icMeeting.invitees[i].firstName + " " + this.icMeeting.invitees[i].lastName;
                        }
                    }
                    /*this.inviteesSelect.active = [];
                    if(this.icMeeting.invitees) {
                        this.icMeeting.invitees.forEach(element => {
                            for (var i = 0; i < this.inviteesList.length; i++) {
                                var option = this.inviteesList[i];
                                if (element.id === option.id) {
                                    //this.inviteesSelect.active.push(option);
                                }
                            }
                        });
                    }*/

                    // topic votes
                    if(this.icMeeting.topics != null){
                        for(var i = 0; i < this.icMeeting.topics.length; i++){
                            for(var j = 0;  this.icMeeting.topics[i].votes != null && j < this.icMeeting.topics[i].votes.length; j++){
                                 if(this.icMeeting.topics[i].votes[j].employee.username === localStorage.getItem("authenticatedUser")){
                                    this.icMeeting.topics[i].authenticatedUserVote = this.icMeeting.topics[i].votes[j].vote;
                                    this.icMeeting.topics[i].authenticatedUserVoteComment = this.icMeeting.topics[i].votes[j].comment;
                                 }
                            }
                        }
                    }
                    this.postAction(successMessage, errorMessage);
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error loading IC Meeting";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, this.errorMessage);
                }
            );
    }

    ngOnInit():any {

        $('#icDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        $('#icTime').datetimepicker({
            //use24hours: true
            format: 'HH:mm'
        })
    }

    public selected(value:any):void {
        //console.log('Selected value is: ', value);
    }

    public removed(value:any):void {
        //console.log('Removed value is: ', value);
    }

    public refreshInvitees(value:any):void {
        this.icMeeting.invitees = value;
    }

    checkICMember(member){
        member.present = !member.present;
        if(member.present){
            member.absenceType = null;
        }
    }

    public canEdit(){
        if(this.icMeeting != null && (this.icMeeting.closed || this.icMeeting.deleted)){
            return false;
        }
        var accessOk = this.moduleAccessChecker.checkAccessICMeetingsEdit();
        return accessOk;
    }

    canDelete(){
        return false;
    }

    public deleteICMeeting(){
        alert("TODO");
        /*if(confirm("Are you sure want to delete?")) {
            this.busy = this.corpMeetingService.deleteICMeeting(this.icMeeting.id)
                .subscribe(
                    response => {
                        if (response) {
                            this.router.navigate(['/corpMeetings/', {"successMessage": "Successfully deleted IC Topic."}]);
                        } else {
                            this.postAction(null, "Failed to delete IC Meeting");
                        }
                    },
                    (error:ErrorResponse) => {
                        this.errorMessage = "Failed to delete IC Meeting";
                        if (error && !error.isEmpty()) {
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, this.errorMessage);
                    }
                );
        }*/
    }

    saveICMeeting(){
        this.icMeeting.date = $('#icDateValue').val();
        if(this.icMeeting.date == null || this.icMeeting.date.trim() === ''){
            this.errorMessage = "Date required";
            this.successMessage = null;
            this.postAction(this.successMessage, this.errorMessage);
            return;
        }
        this.icMeeting.time = $('#icTimeValue').val();
        if(this.icMeeting.number == null || this.icMeeting.number.trim() === ''){
            console.log(this.icMeeting);
            this.errorMessage = "Number required";
            this.successMessage = null;
             this.postAction(this.successMessage, this.errorMessage);
            return;
        }

        // attendees
        this.icMeeting.attendees = [];
        var ceoAbsent = false;
        for(var i = 0; this.attendeesList != null && i < this.attendeesList.length; i++){
            if($('#checkbox_' + i).prop("checked")){
                this.icMeeting.attendees.push({"employee": {"id": this.attendeesList[i].employee.id}, "present": true});
            }else{
                var absentTypeCode = $('#select_' + i).val();
                if(absentTypeCode == null){
                    this.postAction(null, "Reason cannot be empty for absent attendee");
                    return;
                }

                this.icMeeting.attendees.push({"employee": {"id": this.attendeesList[i].employee.id}, "present": false, "absenceType": absentTypeCode});

                if(this.attendeesList[i].employee.isCeo){
                    if(this.icMeeting.ceoSubEmployee == null || this.icMeeting.ceoSubEmployee.id == null){
                        this.postAction(null, "CEO is absent, sub must be specified.");
                        return;
                    }else{
                        ceoAbsent = true;
                    }
                }
            }
        }
        if(!ceoAbsent){
            this.icMeeting.ceoSubEmployee = {};
        }
        //console.log(this.icMeeting);
        this.busy = this.corpMeetingService.saveICMeeting(this.icMeeting,
                                      (this.uploadAgendaFile != null && this.uploadAgendaFile.length > 0 ? this.uploadAgendaFile[0]: null),
                                      (this.uploadProtocolFile != null && this.uploadProtocolFile.length > 0 ? this.uploadProtocolFile[0]: null),
                                      (this.uploadBulletinFile != null && this.uploadBulletinFile.length > 0 ? this.uploadBulletinFile[0]: null))
            .subscribe(
                (response: SaveResponse)  => {
                    this.uploadAgendaFile = [];
                    this.uploadProtocolFile = [];
                    this.uploadBulletinFile = [];
                    this.getICMeeting(response.entityId, "Successfully saved IC meeting.", null);
                },
                (error: ErrorResponse) => {
                    this.postAction(null, error.message != null ? error.message : "Failed to save IC Meeting");
                }
            );
    }

    getICMeetingTopicClassByStatus(topic){
        if(topic.status != null){
            if(topic.status === 'DRAFT'){
                return 'label label-default';
            }
            if(topic.status === 'CLOSED'){
                return 'label label-danger';
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

    navigate(topic){
        if(!this.canViewICTopic(topic)){
            return;
        }
        let params = JSON.stringify({"fromICMeeting": true, "backPath": "/corpMeetings/ic/edit/" + this.icMeeting.id});
        this.router.navigate(['/corpMeetings/edit/', topic.id, { params }]);
    }

    canViewICTopic(topic){
        if(this.moduleAccessChecker.checkAccessICMeetingTopicsViewFull()){
            return true;
        }
        if(this.moduleAccessChecker.checkAccessICMeetingTopicsView()){
            // check same dept
            var userPosition = JSON.parse(localStorage.getItem("authenticatedUserPosition"));
            var sameDept = userPosition != null && topic.department != null &&
                topic.department.id != null && userPosition.department.id == topic.department.id;
            if(sameDept){
                return true;
            }

            // check approvals
            if(topic != null && topic.approveList != null){
                for(var i = 0; i < topic.approveList.length; i++){
                    if(topic.approveList[i].employee.username === localStorage.getItem("authenticatedUser")){
                        return true;
                    }
                }
            }

        }
        return false;
    }

    exportAgenda(){
        var fileName = "???????????????? ???? ???" + this.icMeeting.number + " ???? " + this.icMeeting.date;
        //fileName = fileName.replace(".", ",");
        this.busy = this.corpMeetingService.makeFileRequest(DATA_APP_URL + `corpMeetings/ICMeeting/exportAgenda/${this.icMeeting.id}`,
            fileName, 'POST')
            .subscribe(
                response  => {
                    console.log("export agenda response ok");
                },
                error => {
                    //console.log("fails")
                    this.postAction(null, "Error exporting agenda");
                }
            );
    }

    exportProtocol(){
        var fileName = "???????????????? ???? ???" + this.icMeeting.number + " ???? " + this.icMeeting.date;
        //fileName = fileName.replace(".", ",");
        this.busy = this.corpMeetingService.makeFileRequest(DATA_APP_URL + `corpMeetings/ICMeeting/exportProtocol/${this.icMeeting.id}`,
            fileName, 'POST')
            .subscribe(
                response  => {
                    //console.log("export protocol response ok");
                },
                error => {
                    //console.log("fails")
                    this.postAction(null, "Error exporting protocol");
                }
            );
    }

    exportBulletin(){
        var fileName = "?????????????????? ???? ???" + this.icMeeting.number + " ???? " + this.icMeeting.date;
        //fileName = fileName.replace(".", ",");
        this.busy = this.corpMeetingService.makeFileRequest(DATA_APP_URL + `corpMeetings/ICMeeting/exportBulletin/${this.icMeeting.id}`,
            fileName, 'POST')
            .subscribe(
                response  => {
                    //console.log("export bulletin response ok");
                },
                error => {
                    //console.log("fails")
                    this.postAction(null, "Error exporting bulletin");
                }
            );
    }

    onFileChangeAgenda(event){
        var target = event.target || event.srcElement;
        var files = target.files;
        this.uploadAgendaFile.length = 0;
        for (var i = 0; i < files.length; i++) {
            this.uploadAgendaFile.push(files[i]);
        }
    }

    onFileChangeProtocol(event){
        var target = event.target || event.srcElement;
        var files = target.files;
        this.uploadProtocolFile.length = 0;
        for (var i = 0; i < files.length; i++) {
            this.uploadProtocolFile.push(files[i]);
        }
    }

    onFileChangeBulletin(event){
            var target = event.target || event.srcElement;
            var files = target.files;
            this.uploadBulletinFile.length = 0;
            for (var i = 0; i < files.length; i++) {
                this.uploadBulletinFile.push(files[i]);
            }
        }

    deleteUnsavedAgenda(aFile){
        for(var i = this.uploadAgendaFile.length - 1; i >= 0; i--) {
            if(this.uploadAgendaFile[i] == aFile) {
                this.uploadAgendaFile.splice(i, 1);
            }
        }
    }
    deleteUnsavedProtocol(aFile){
        for(var i = this.uploadProtocolFile.length - 1; i >= 0; i--) {
            if(this.uploadProtocolFile[i] == aFile) {
                this.uploadProtocolFile.splice(i, 1);
            }
        }
    }
    deleteUnsavedBulletin(aFile){
        for(var i = this.uploadBulletinFile.length - 1; i >= 0; i--) {
            if(this.uploadBulletinFile[i] == aFile) {
                this.uploadBulletinFile.splice(i, 1);
            }
        }
    }
    deleteAgenda(){
        if(confirm("Are you sure want to delete")) {
            this.busy = this.corpMeetingService.deleteICMeetingAgenda(this.icMeeting.id)
                .subscribe(
                    response => {
                        this.icMeeting.agenda = null;
                        this.postAction("IC Meeting Agenda deleted.", null);
                    },
                    (error: ErrorResponse) => {
                        this.errorMessage = "Error deleting IC Meeting Agenda";
                        if(error && !error.isEmpty()){
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, null);
                    }
                );
        }
    }
    deleteProtocol(){
        if(confirm("Are you sure want to delete")) {
            this.busy = this.corpMeetingService.deleteICMeetingProtocol(this.icMeeting.id)
                .subscribe(
                    response => {
                        this.icMeeting.protocol = null;
                        this.postAction("IC Meeting Protocol deleted.", null);
                    },
                    (error: ErrorResponse) => {
                        this.errorMessage = "Error deleting IC Meeting Protocol";
                        if(error && !error.isEmpty()){
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, null);
                    }
                );
        }
    }
    deleteBulletin(){
        if(confirm("Are you sure want to delete")) {
            this.busy = this.corpMeetingService.deleteICMeetingBulletin(this.icMeeting.id)
                .subscribe(
                    response => {
                        this.icMeeting.bulletin = null;
                        this.postAction("IC Meeting Bulletin deleted.", null);
                    },
                    (error: ErrorResponse) => {
                        this.errorMessage = "Error deleting IC Meeting Bulletin";
                        if(error && !error.isEmpty()){
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, null);
                    }
                );
        }
    }

    unlockForFinalize(){
        if(confirm("Are you sure want to open for finalize?");) {
            this.busy = this.corpMeetingService.unlockICMeetingForFinalize(this.icMeeting.id)
                .subscribe(
                    response => {
                        //this.postAction("IC Meeting opened for finalize.", null);
                        this.getICMeeting(this.icMeeting.id, "IC Meeting opened for finalize.", null);
                    },
                    (error: ErrorResponse) => {
                        console.log(error);
                        this.errorMessage = "Error opening IC Meeting for finalize";
                        if(error && !error.isEmpty()){
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, errorMessage);
                    }
                );
        }
    }

    canUnlock(){
        return this.moduleAccessChecker.checkAccessICMeetingAdmin() && this.icMeeting.status === 'LOCKED FOR IC';
    }

    getICMeetingClassByStatus(){
        if(this.icMeeting.status === 'CLOSED'){
            return 'label label-danger';
        }else{
            // Check if sent to IC
            if(this.icMeeting.status === 'LOCKED FOR IC'){
                return 'label label-primary';
            }
            // Check if to be finalized after IC
            if(this.icMeeting.status === 'TO BE FINALIZED'){
                return 'label label-info';
            }
            if(this.icMeeting.status === 'FINALIZED'){
                return 'label label-success';
            }
            return 'label label-default';
        }
    }

    canVote(){
        var canVote = false;
        if(this.icMeeting.attendees != null && this.icMeeting.attendees.length > 0){
            for(var i = 0; i < this.icMeeting.attendees.length; i++){
                 if(this.icMeeting.attendees[i].employee.username === localStorage.getItem("authenticatedUser") &&
                        this.icMeeting.attendees[i].present){
                    canVote = true;
                    break;
                 }
            }
        }
        if(canVote){
            if(this.icMeeting.topics != null && this.icMeeting.topics.length > 0){
                for(var i = 0; i < this.icMeeting.topics.length; i++){
                     if(this.icMeeting.topics[i].status != 'FINALIZED'){
                        return false;
                     }
                }
            }
            return true;
        }

        return false;
    }

    showVote(){
        this.voteSuccessMessage = null;
        this.voteErrorMessage = null;
    }

    closeVoteModal(){}

    saveVote(){
        let votes = [];
        console.log(this.icMeeting.topics);
        for(let i = 0; i < this.icMeeting.topics.length; i++){
            votes.push({"icMeetingTopicId": this.icMeeting.topics[i].id, "vote": this.icMeeting.topics[i].authenticatedUserVote,
            "comment": this.icMeeting.topics[i].authenticatedUserVoteComment}
            );
        }

        this.busy = this.corpMeetingService.voteICMeetingTopics({"icMeetingId": this.icMeeting.id, "votes": votes})
                .subscribe(
                    response => {
                        //this.postAction("IC Meeting opened for finalize.", null);
                        //this.getICMeeting(this.icMeeting.id, "IC Meeting opened for finalize.", null);
                        this.voteSuccessMessage = "Votes successfully saved";
                        this.voteErrorMessage = null;
                    },
                    (error: ErrorResponse) => {
                        this.voteSuccessMessage = null;
                        this.voteErrorMessage = "Failed to save votes";
                    }
                );
    }

    moveTopicUp(i){
        if(this.icMeeting != null && this.icMeeting.topics != null && this.icMeeting.topics.length > 0){
            if(i == 0 || i >= this.icMeeting.topics.length){
                return;
            }
            var prev = this.icMeeting.topics[i - 1];
            this.icMeeting.topics[i - 1] = this.icMeeting.topics[i];
            this.icMeeting.topics[i] = prev;
        }
    }

    moveTopicDown(i){
        if(this.icMeeting != null && this.icMeeting.topics != null && this.icMeeting.topics.length > 0){
            if(i >= this.icMeeting.topics.length - 1){
                return;
            }
            var next = this.icMeeting.topics[i + 1];
            this.icMeeting.topics[i + 1] = this.icMeeting.topics[i];
            this.icMeeting.topics[i] = next;
        }
    }

    canClose(){
        var access = this.icMeeting != null && !this.icMeeting.deleted && !this.icMeeting.closed && this.icMeeting.closeable;
        return access && this.moduleAccessChecker.checkAccessICMeetingAdmin();
    }

    closeICMeeting(){
        if(confirm("Are you sure want to close IC Meeting?")){
            this.busy = this.corpMeetingService.closeICMeeting(this.icMeeting.id)
                .subscribe(
                    response => {
                        //this.postAction("IC Meeting successfully closed", null);
                        this.getICMeeting(this.icMeeting.id, "IC Meeting successfully closed", null);
                    },
                    (error: ErrorResponse) => {
                        this.postAction(null, "Failed to close IC Meeting");
                    }
                );
        }
    }

    canReopen(){
        var access = this.icMeeting != null && !this.icMeeting.deleted && this.icMeeting.closed;
        return access && this.moduleAccessChecker.checkAccessICMeetingAdmin();
    }

    reopenICMeeting(){
        if(confirm("Are you sure want to reopen IC Meeting?")){
            this.busy = this.corpMeetingService.reopenICMeeting(this.icMeeting.id)
                .subscribe(
                    response => {
                        this.getICMeeting(this.icMeeting.id, "IC Meeting successfully reopened", null);
                    },
                    (error: ErrorResponse) => {
                        this.postAction(null, "Failed to reopen IC Meeting");
                    }
                );
        }
    }

    exportMaterials(){
        if(this.icMeeting.id != null){
            var fileName = '???????' + this.icMeeting.number + ' - ??????????????????';
            this.busy = this.corpMeetingService.makeFileRequest(DATA_APP_URL + `corpMeetings/icMeeting/exportMaterials/${this.icMeeting.id}/`, fileName)
                .subscribe(
                    response  => {
                        //console.log("ok");
                    },
                    error => {
                        //console.log("fails")
                        this.postAction(null, "Error exporting data");
                    }
                );
        }
    }

    onTypeChange() {
        if (this.icMeeting.corpMeetingType === 'EXEC') {
            this.attendeesListPlaceholder = this.attendeesList;
            this.attendeesList = this.attendeesListMB;
            this.attendeesListMB = this.attendeesListPlaceholder;
        } else if (this.icMeeting.corpMeetingType === 'IC') {
            this.attendeesListPlaceholder = this.attendeesListMB;
            this.attendeesListMB = this.attendeesList;
            this.attendeesList = this.attendeesListPlaceholder;
        }
    }

    changeType() {
        if (this.icMeeting.corpMeetingType === 'IC') {
            this.icMeeting.corpMeetingType = 'EXEC';
        }
        if (this.icMeeting.corpMeetingType === 'EXEC') {
            this.icMeeting.corpMeetingType = 'IC';
        }
    }

    isIC() {
        return this.icMeeting.corpMeetingType === 'IC';
    }
}
