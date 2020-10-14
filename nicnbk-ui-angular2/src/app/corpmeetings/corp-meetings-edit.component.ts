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
import {BaseDictionary} from "../common/model/base-dictionary";
import {FileEntity} from "../common/model/file-entity";

//import { TagInputModule } from 'ng2-tag-input';

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

    uploadExplanatoryNoteFile = [];

    public uploadMaterialsFiles: Array<any> = [];

    icMeetingTopic: ICMeetingTopic;

    editableDecisionApproveList = [];

    private breadcrumbParams: string;
    private searchParams = new ICMeetingTopicSearchParams();

    modalErrorMessage;
    modalSuccessMessage;

    @ViewChild('decisionApproveListSelect')
    private decisionApproveListSelect;

    public employeeList = [];

    tagOptions = {
        placeholder: "+ tag",
        secondaryPlaceholder: "Enter a new tag",
        separatorKeys: [188, 191], // exclude coma from tag content
        maxItems: 20
    }

    constructor(
        private employeeService: EmployeeService,
        private lookupService: LookupService,
        private corpMeetingService: CorpMeetingService,
        private router: Router,
        private route: ActivatedRoute,
        private moduleAccessChecker: ModuleAccessCheckerService
    ){
        super(router);

        this.icMeetingTopic = new ICMeetingTopic();

        Observable.forkJoin(
            // Load lookups
            // TODO: only open IC meetings
            this.corpMeetingService.getAllICMeetings(),
            this.employeeService.findAll()
            )
            .subscribe(
                ([data1, data2]) => {

                    this.icList = [];
                    data1.forEach(element => {
                        this.icList.push(element);
                    });

                    data2.forEach(element => {
                        this.employeeList.push({id: element.id, text: element.firstName + " " + element.lastName,
                                                    firstName: element.firstName, lastName: element.lastName});
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

                                            // Check allowed types
                                            if(this.canEdit()) {
                                                // When IC Member and access to edit some types
                                                var allowedTypes = [];
                                                for (var i = 0; i < this.icMeetingTopicTypes.length; i++) {
                                                    if ((this.icMeetingTopic != null && this.icMeetingTopic.type == this.icMeetingTopicTypes[i].code) ||
                                                        this.corpMeetingService.checkICMeetingTopicEditAccess(this.icMeetingTopicTypes[i].code)) {
                                                        allowedTypes.push(this.icMeetingTopicTypes[i]);
                                                    }
                                                }
                                                this.icMeetingTopicTypes = allowedTypes;
                                            }

                                            if(this.icMeetingTopicTypes != null && this.icMeetingTopicTypes.length == 1){
                                                this.icMeetingTopic.type = this.icMeetingTopicTypes[0].code;
                                            }

                                            this.postAction(null, null);
                                        },
                                        (error: ErrorResponse) => {
                                            this.errorMessage = "Error loading IC Meeting topic";
                                            if(error && !error.isEmpty()){
                                                this.processErrorMessage(error);
                                            }
                                            this.postAction(null, this.errorMessage);
                                        }
                                    );
                            }else{
                                this.icMeetingTopic.id = null;// Set type
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

    addDecisionApproveListEmployee(){
        //alert("addApproveListEmployee");
    }

    public canEdit(){
        if(this.icMeetingTopic.icMeeting != null && this.icMeetingTopic.icMeeting.closed){
            return false;
        }
        // TODO: check rights
        var accessOk = this.moduleAccessChecker.checkAccessCorpMeetingsEdit(this.icMeetingTopic.type);
        return accessOk;
    }

    onFileChangeMaterials(event) {
        var target = event.target || event.srcElement;
        var files = target.files;
        this.uploadMaterialsFiles.length = 0;
        for (let i = 0; i < files.length; i++) {
            var material = new FileEntity();
            material.file = files[i];
            material.name = files[i].protocolName;
            this.uploadMaterialsFiles.push(material);
        }
    }


    onFileChangeExplanatoryNote(event) {
        var target = event.target || event.srcElement;
        var files = target.files;
        this.uploadExplanatoryNoteFile.length = 0;
        for (var i = 0; i < files.length; i++) {
            this.uploadExplanatoryNoteFile.push(files[i]);
        }
    }

    save(){
        if(this.uploadMaterialsFiles != null && this.uploadMaterialsFiles.length > 0){
            this.icMeetingTopic.materials = this.icMeetingTopic.materials == null ? [] : this.icMeetingTopic.materials;

        }
        this.busy = this.corpMeetingService.saveICMeetingTopicWithFiles(this.icMeetingTopic, this.uploadMaterialsFiles)
            .subscribe(
                (saveResponse: SaveResponse) => {
                    if(saveResponse.status === 'SUCCESS' ){
                        this.icMeetingTopic.id = saveResponse.entityId;
                        this.postAction("Successfully saved IC meeting topic.", null);
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

    deleteUnsavedAttachment(newFile){
        for(var i = this.uploadMaterialsFiles.length - 1; i >= 0; i--) {
            if(this.uploadMaterialsFiles[i] == newFile) {
                this.uploadMaterialsFiles.splice(i, 1);
            }
        }

    }

    deleteUnsavedExplanatoryNote(aFile){
        for(var i = this.uploadExplanatoryNoteFile.length - 1; i >= 0; i--) {
            if(this.uploadExplanatoryNoteFile[i] == aFile) {
                this.uploadExplanatoryNoteFile.splice(i, 1);
            }
        }
    }

    deleteAttachment(fileId){
        alert("TODO");
        /*var confirmed = window.confirm("Are you sure want to delete");
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
        */
    }

    deleteExplanatoryNote(){
        alert("TODO");
    }

    public deleteICMeetingTopic(){
        if(confirm("Are you sure want to delete?")) {
            this.busy = this.corpMeetingService.deleteICMeetingTopic(this.icMeetingTopic.id)
                .subscribe(
                    response => {
                        if (response) {
                            this.router.navigate(['/corpMeetings/', {"successMessage": "Successfully deleted IC Topic."}]);
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

    icListChanged(){

    /*
        var value = this.icMeetingTopic.icMeeting.id;
        if(Number(value) == -1){
            this.showNewICModal = true;
            this.newICMeeting = new ICMeeting();
        }else if(Number(value) == 0) {
            this.showNewICModal = false;
            this.icMeetingTopic.icMeeting = new ICMeeting();
        }else{
            for(var i = 0; this.icList != null && i < this.icList.length; i++){
                if(this.icList[i].id == Number(value)) {
                    if (this.icList[i] != null && this.icList[i].closed) {
                        if (confirm("'№ " + this.icList[i].number + " (" + this.icList[i].date + ")" +
                                "'" + " IC is CLOSED, selecting it will make this topic non-editable after save operation. Confirm?")) {
                            //select none
                        }else {
                            this.icMeetingTopic.icMeeting = new ICMeeting();
                            this.icMeetingTopic.icMeeting.id = 0;
                            break;
                        }
                    }

                    break;
                }
            }
            this.showNewICModal = false;
        }
    */

    }

    public onTagRemoved(item) {
        for(var i = this.icMeetingTopic.tags.length - 1; i >= 0; i--) {
            if(this.icMeetingTopic.tags[i] === item) {
                this.icMeetingTopic.tags.splice(i, 1);
            }
        }
    }

    public onTagAdded(item) {
        if(this.icMeetingTopic.tags == null) {
            this.icMeetingTopic.tags = [];
        }
        this.icMeetingTopic.tags.push(item);
    }

    public selectedApproveList(value:any):void {
        //console.log('Selected value is: ', value);
    }

    public removedApproveList(value:any):void {
        //console.log('Removed value is: ', value);
    }

    public refreshApproveList(value:any):void {
        this.editableDecisionApproveList = value;
    }

    updateApproveListModal(){
        if(this.editableDecisionApproveList != null && this.editableDecisionApproveList.length > 0){
            var newDecisionApproveList = [];
            for(var i = 0; i < this.editableDecisionApproveList.length; i++){
                var item = this.editableDecisionApproveList[i];
                if(this.icMeetingTopic.decisionApproveList != null && this.icMeetingTopic.decisionApproveList.length > 0){
                    var found = false;
                    for(var j = 0; j < this.icMeetingTopic.decisionApproveList; j++){
                        if(this.icMeetingTopic.decisionApproveList[j].id === item.id){
                            newDecisionApproveList.push(this.icMeetingTopic.decisionApproveList[j]);
                            found = true;
                            break;
                        }
                    }
                    if(!found){
                        newDecisionApproveList.push({"employeeDto": {"id": item.id, "fullName": item.text}, "approved": false});
                    }
                }else{
                    // add all
                    newDecisionApproveList.push({"employeeDto": {"id": item.id, "fullName": item.text}, "approved": false});
                }
            }
            this.icMeetingTopic.decisionApproveList = newDecisionApproveList;
        }
    }

}
