import {Component, OnInit, ViewChild} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import { SelectComponent} from "ng2-select";


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
import {ICMeetingTopic} from "./model/ic-meeting-topic";
import {ICMeetingTopicSearchParams} from "./model/ic-meeting-topic-search-params";
import {ICMeeting} from "./model/ic-meeting";

declare var $:any

@Component({
    selector: 'corp-meeting-edit',
    templateUrl: 'view/corp-meeting-edit.component.html',
    styleUrls: [],
    providers: [],
})
export class CorpMeetingEditComponent extends CommonFormViewComponent implements OnInit {

    busy: Subscription;
    public sub: any;

    icList: ICMeeting[];

    public uploadMaterialsFiles: Array<any> = [];
    //public uploadProtocolsFiles: Array<any> = [];

    icMeetingTopic: ICMeetingTopic;

    private breadcrumbParams: string;
    private searchParams = new ICMeetingTopicSearchParams();

    //private visible = false;
    modalErrorMessage;
    modalSuccessMessage;
    showNewICModal = false;
    newICMeeting = new ICMeeting();

    constructor(
        private employeeService: EmployeeService,
        private corpMeetingService: CorpMeetingService,
        private router: Router,
        private route: ActivatedRoute,
        private moduleAccessChecker: ModuleAccessCheckerService
    ){
        super(router);

        this.icMeetingTopic = new ICMeetingTopic();

        Observable.forkJoin(
            // Load lookups
            this.corpMeetingService.getAllICMeetings()
            )
            .subscribe(
                ([data]) => {

                    this.icList = [];
                    data.forEach(element => {
                        this.icList.push(element);
                    });
                    this.sub = this.route
                        .params
                        .subscribe(params => {
                            this.icMeetingTopic.id = +params['id'];
                            this.breadcrumbParams = params['params'];
                            if(this.breadcrumbParams != null) {
                                this.searchParams = JSON.parse(this.breadcrumbParams);
                            }
                            if(this.icMeetingTopic.id > 0) {
                                this.busy = this.corpMeetingService.getICMeetingTopic(this.icMeetingTopic.id)
                                    .subscribe(
                                        (topic: ICMeetingTopic )=> {
                                            this.icMeetingTopic = topic;
                                            if(!this.icMeetingTopic.icMeeting){
                                                this.icMeetingTopic.icMeeting = new ICMeeting();
                                            }
                                        },
                                        (error: ErrorResponse) => {
                                            this.errorMessage = "Error loading IC Meeting topic";
                                            if(error && !error.isEmpty()){
                                                this.processErrorMessage(error);
                                            }
                                            this.postAction(null, null);
                                        }
                                    );
                            }else{
                                this.icMeetingTopic.id = null;
                            }
                        });
                });
    }

    ngOnInit():any {
        $('#ICDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
    }

    public canEdit(){
        return this.moduleAccessChecker.checkAccessCorpMeetingsEditor();
    }

    //
    //onFileChangeProtocols(event) {
    //    var target = event.target || event.srcElement;
    //    var files = target.files;
    //    this.uploadProtocolsFiles.length = 0;
    //    for (let i = 0; i < files.length; i++) {
    //        this.uploadProtocolsFiles.push(files[i]);
    //    }
    //}

    onFileChangeMaterials(event) {
        var target = event.target || event.srcElement;
        var files = target.files;
        this.uploadMaterialsFiles.length = 0;
        for (let i = 0; i < files.length; i++) {
            this.uploadMaterialsFiles.push(files[i]);
        }
    }

    save(){

        if(this.icMeetingTopic.icMeeting != null && (this.icMeetingTopic.icMeeting.id == 0 || this.icMeetingTopic.icMeeting.id == -1)){
            this.icMeetingTopic.icMeeting = new ICMeeting();
        }
        this.busy = this.corpMeetingService.saveICMeetingTopic(this.icMeetingTopic)
            .subscribe(
                (saveResponse: SaveResponse) => {
                    if(saveResponse.status === 'SUCCESS' ){
                        this.icMeetingTopic.id = saveResponse.entityId;
                        if(this.uploadMaterialsFiles.length > 0) {
                            this.busy = this.corpMeetingService.postFiles(this.icMeetingTopic.id, [], this.uploadMaterialsFiles).subscribe(
                                res => {
                                    // clear upload files list on view
                                    this.uploadMaterialsFiles.length = 0;

                                    // update files list with new files
                                    if(!this.icMeetingTopic.materials){ // no files existed
                                        this.icMeetingTopic.materials = [];
                                    }
                                    for (var i = 0; i < res.length; i++) {
                                        this.icMeetingTopic.materials.push(res[i]);
                                    }

                                    this.postAction("Successfully saved IC meeting topic (with attachments)", null);
                                },
                                error => {
                                    // TODO: don't save memo?
                                    this.postAction(null, "Error uploading attachments (materials).");
                                });
                        }else{
                            this.postAction("Successfully saved IC meeting topic.", null);
                        }

                    }else{
                        if(saveResponse.message != null){
                            var message = saveResponse.message.nameEn != null ? saveResponse.message.nameEn :
                                saveResponse.message.nameRu != null ? saveResponse.message.nameRu : saveResponse.message.nameKz;
                            if(message != null && message != ''){
                                this.postAction(null, message);
                            }else{
                                this.postAction(null, "Error saving IC Meeting topic");
                            }
                        }
                    }
                },
                (error: ErrorResponse) => {
                    this.processErrorMessage(error);
                }
            );
    }

    deleteAttachment(fileId){
        var confirmed = window.confirm("Are you sure want to delete");
        if(confirmed) {
            this.corpMeetingService.deleteICMeetingTopicAttachment(this.icMeetingTopic.id, fileId)
                .subscribe(
                    response => {
                        for(var i = this.icMeetingTopic.materials.length - 1; i >= 0; i--) {
                            if(this.icMeetingTopic.materials[i].id === fileId) {
                                this.icMeetingTopic.materials.splice(i, 1);
                            }
                        }

                        this.postAction("Attachment deleted.", null);
                    },
                    (error: ErrorResponse) => {
                        this.errorMessage = "Error deleting attachment";
                        if(error && !error.isEmpty()){
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, null);
                    }
                );
        }
    }

    public deleteICMeetingTopic(){
        if(confirm("Are you sure want to delete?")) {
            this.busy = this.corpMeetingService.deleteICMeetingTopic(this.icMeetingTopic.id)
                .subscribe(
                    response => {
                        if (response) {
                            this.router.navigate(['/corpMeetings/', {}]);
                            this.successMessage = "Successfully deleted IC Topic."
                            this.errorMessage = null;
                        } else {
                            this.postAction(null, "Failed to delete IC Meeting topic");
                        }
                    },
                    (error:ErrorResponse) => {
                        this.errorMessage = "Failed to delete IC Meeting topic";
                        if (error && !error.isEmpty()) {
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, this.errorMessage);
                    }
                );
        }
    }

    icListChanged(value){
        if(Number(value) == -1){
            this.showNewICModal = true;
            this.newICMeeting = new ICMeeting();
        }else if(Number(value) == 0) {
            this.showNewICModal = false;
            this.icMeetingTopic.icMeeting = new ICMeeting();
        }else{
            this.showNewICModal = false;
        }
    }

    doShowNewICModal(){
    }


    saveICMeeting(){
        this.newICMeeting.date = $('#ICDate').val();
        if(this.newICMeeting.date == null){
            this.modalErrorMessage = "Date required";
            this.modalSuccessMessage = null;
            return;
        }
        if(this.newICMeeting.number == null || this.newICMeeting.number.trim() === ''){
            this.modalErrorMessage = "Number required";
            this.modalSuccessMessage = null;
            return;
        }

        this.busy = this.corpMeetingService.saveICMeeting(this.newICMeeting)
            .subscribe(
                (resposne: SaveResponse)  => {
                    this.newICMeeting.id = resposne.entityId;
                    this.modalSuccessMessage = "Successfully saved IC meeting."
                    this.modalErrorMessage = null;

                    this.icList.push(this.newICMeeting);
                    this.icMeetingTopic.icMeeting = this.newICMeeting;

                    this.showNewICModal = false;
                },
                (error: ErrorResponse) => {
                    this.modalErrorMessage = error.message;
                    this.modalSuccessMessage = null;
                }
            );
    }

    closeICSaveModal(){
        if(this.newICMeeting.id != null && this.newICMeeting.id > 0) {
            this.icMeetingTopic.icMeeting = this.newICMeeting;
        }

        this.modalErrorMessage = null;
        this.modalSuccessMessage = null;
    }
}
