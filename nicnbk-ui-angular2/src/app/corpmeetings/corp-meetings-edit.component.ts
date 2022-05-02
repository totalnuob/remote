import {Component, OnInit, ViewChild} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import { SelectComponent} from "ng2-select";
import {LookupService} from "../common/lookup.service";
import {CommonFormViewComponent} from "../common/common.component";
import {Subscription} from 'rxjs';
import {ErrorResponse} from "../common/error-response";
import {CorpMeeting} from "./model/corp-meeting";
import {EmployeeService} from "../employee/employee.service";
import {Employee} from "../employee/model/employee";

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

import {DATA_APP_URL} from "../common/common.service.constants";

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
    //uploadExplanatoryNoteFileUpd = [];

    public uploadMaterialsFiles: Array<any> = [];
    //public uploadMaterialsFilesUpd: Array<any> = [];

    icMeetingTopic: ICMeetingTopic;

    editableApproveList = [];

    private breadcrumbParams: string;
    private searchParams = new ICMeetingTopicSearchParams();
    fromICMeeting: false;
    backPath;

    modalErrorMessage;
    modalSuccessMessage;

    @ViewChild('approveListSelect')
    private approveListSelect;

    //employeeList = [];
    departmentEmployees = [];
    availableApproveList = [];
    departmentEmployeeList = [];

    icAdminEmployee;
    legalHeadEmployee;

    tagOptions = {
        placeholder: "+ tag",
        secondaryPlaceholder: "Enter a new tag",
        separatorKeys: [188, 191], // exclude coma from tag content
        maxItems: 20
    }

    availableTags = [];

    public departmentList = [];
    public assignmentEmployeeList = [];
    public corpMeetingTypes = ['IC', 'EXEC'];

    constructor(
        private employeeService: EmployeeService,
        private lookupService: LookupService,
        private corpMeetingService: CorpMeetingService,
        private router: Router,
        private route: ActivatedRoute,
        private moduleAccessChecker: ModuleAccessCheckerService
    ){
        super(router);
        var userPosition = JSON.parse(localStorage.getItem("authenticatedUserPosition"));
        var departmentId = userPosition != null && userPosition.department != null ? userPosition.department.id : 0;

        this.icMeetingTopic = new ICMeetingTopic();
        Observable.forkJoin(
            // Load lookups
            this.corpMeetingService.getAllICMeetings(),
            this.corpMeetingService.getAvailableApproveList(),
            this.employeeService.findByDepartmentWithExecutives(departmentId),
            //this.employeeService.findActiveAll(),
            this.lookupService.getAvailableTagsByType("IC"),
            this.employeeService.getAllDepartments()
            )
            .subscribe(
                ([data1, data2, data3, data4,data5]) => {
                    this.icList = [];
                    data1.forEach(element => {
                        this.icList.push(element);
                    });
                    data2.forEach(element => {
                        this.availableApproveList.push({id: element.id, text: element.firstName + " " + element.lastName,
                                                    firstName: element.firstName, lastName: element.lastName});
                        if(element.roles != null && element.roles.length > 0){
                            for(var i = 0; i < element.roles.length; i++){
                                if(element.roles[i].code === 'IC_ADMIN'){
                                    this.icAdminEmployee = element;
                                    break;
                                }
                            }
                        }
                        if(element.position != null && element.position.head && element.position.department != null && element.position.department.code === 'LEGAL'){
                                this.legalHeadEmployee = element;
                        }

                        if(element.position != null && element.position.code != null && element.position.code === 'DEP_CEO'){
                            this.assignmentEmployeeList.push({id: element.id, text: element.firstName + " " + element.lastName,
                                                            firstName: element.firstName, lastName: element.lastName});
                        }
                    });
                    data3.forEach(element => {
                        this.departmentEmployeeList.push({id: element.id, text: element.firstName + " " + element.lastName, active: element.active,
                                        firstName: element.firstName, lastName: element.lastName});
                    });

                    data4.forEach(element => {
                        this.availableTags.push(element.name);
                    });
                    data5.forEach(element => {
                        this.departmentList.push({id: element.id, text: element.shortNameRu});
                    });

                    this.sub = this.route
                        .params
                        .subscribe(params => {
                            this.icMeetingTopic.id = +params['id'];
                            this.breadcrumbParams = params['params'];
                            if(this.breadcrumbParams != null && JSON.parse(this.breadcrumbParams).fromICMeeting != null){
                                this.fromICMeeting = JSON.parse(this.breadcrumbParams).fromICMeeting;
                                this.backPath = JSON.parse(this.breadcrumbParams).backPath;
                            }
                            if(this.breadcrumbParams != null) {
                                this.searchParams = JSON.parse(this.breadcrumbParams);
                            }
                            if(this.icMeetingTopic.id > 0) {
                                this.getICMeetingTopic(this.icMeetingTopic.id, null, null);
                            }else{
                                this.icMeetingTopic.id = null;
                                if(!this.icMeetingTopic.icMeeting){
                                    this.icMeetingTopic.icMeeting = new ICMeeting();
                                }
                                if(!this.icMeetingTopic.speaker){
                                    this.icMeetingTopic.speaker = new Employee();
                                }
                                if(!this.icMeetingTopic.executor){
                                    this.icMeetingTopic.executor = new Employee();
                                }
                                // default approve list
                                this.icMeetingTopic.approveList = [];
                                if(this.icAdminEmployee != null){
                                    this.icMeetingTopic.approveList.push({"employee": {"id": this.icAdminEmployee.id,
                                                        "fullName":  this.icAdminEmployee.firstName + " " + this.icAdminEmployee.lastName},
                                                        "approved": false});
                                }
                                if(this.legalHeadEmployee != null){
                                    this.icMeetingTopic.approveList.push({"employee": {"id": this.legalHeadEmployee.id,
                                                        "fullName":  this.legalHeadEmployee.firstName + " " + this.legalHeadEmployee.lastName},
                                                        "approved": false});
                                }

                                // remove inactive users
                                if(this.departmentEmployeeList != null && this.departmentEmployeeList.length > 0){
                                    var newEmployeeListWithoutInActive = [];
                                    for(var i = 0; i < this.departmentEmployeeList.length; i++){
                                        if(this.departmentEmployeeList[i].active){
                                            newEmployeeListWithoutInActive.push(this.departmentEmployeeList[i]);
                                        }
                                    }
                                    this.departmentEmployeeList = newEmployeeListWithoutInActive;
                                }
                            }
                        });
                });
    }

    getICMeetingTopic(id, successMessage, errorMessage){
        this.uploadExplanatoryNoteFile = [];
        this.uploadMaterialsFiles = [];

        this.busy = this.corpMeetingService.getICMeetingTopic(id)
                .subscribe(
                    (topic: ICMeetingTopic)=> {
                        this.icMeetingTopic = topic;
                        //console.log(this.icMeetingTopic);
                        if(!this.icMeetingTopic.icMeeting){
                            this.icMeetingTopic.icMeeting = new ICMeeting();
                        }
                        if(!this.icMeetingTopic.speaker){
                            this.icMeetingTopic.speaker = new Employee();
                        }
                        if(!this.icMeetingTopic.executor){
                            this.icMeetingTopic.executor = new Employee();
                        }
                        if(this.icMeetingTopic.department != null && this.icMeetingTopic.department.id > 0){
                            this.employeeService.findByDepartmentWithExecutives(this.icMeetingTopic.department.id)
                               .subscribe(
                                    employeeList => {
                                        this.departmentEmployeeList = employeeList;
                                        if(this.icMeetingTopic != null && this.icMeetingTopic.status != null && this.icMeetingTopic.status === 'CLOSED'){
                                             // 1.inactive users should be in the list for historical data
                                         }else{
                                             // remove inactive users
                                             if(this.departmentEmployeeList != null && this.departmentEmployeeList.length > 0){
                                                 var newEmployeeListWithoutInActive = [];
                                                 for(var i = 0; i < this.departmentEmployeeList.length; i++){
                                                     if(this.departmentEmployeeList[i].active){
                                                         newEmployeeListWithoutInActive.push(this.departmentEmployeeList[i]);
                                                     }
                                                 }
                                                 this.departmentEmployeeList = newEmployeeListWithoutInActive;
                                             }
                                         }
                                         // add employees that are set as speaker or executor even if they switched departments
                                          if(this.icMeetingTopic.speaker != null && this.icMeetingTopic.speaker.position != null && this.icMeetingTopic.speaker.position.department != null){
                                              if(this.icMeetingTopic != null && this.icMeetingTopic.department != null &&
                                                          this.icMeetingTopic.speaker.position.department.id != this.icMeetingTopic.department.id){
                                                  this.departmentEmployeeList = this.departmentEmployeeList == null ? []: this.departmentEmployeeList;
                                                  this.departmentEmployeeList.push(this.icMeetingTopic.speaker);
                                              }
                                          }
                                          if(this.icMeetingTopic.executor != null && this.icMeetingTopic.executor.position != null && this.icMeetingTopic.executor.position.department != null){
                                              if(this.icMeetingTopic != null && this.icMeetingTopic.department != null &&
                                                          this.icMeetingTopic.executor.position.department.id != this.icMeetingTopic.department.id){
                                                  this.departmentEmployeeList = this.departmentEmployeeList == null ? []: this.departmentEmployeeList;
                                                  this.departmentEmployeeList.push(this.icMeetingTopic.executor);
                                              }
                                          }
                                     },
                                     error => {
                                        console.log("Error loading department employees");
                                     }
                                );
                         }
                         // preselect decision depts
                         if(this.icMeetingTopic.decisions != null){
                            for(var i = 0; i < this.icMeetingTopic.decisions.length; i++){
                                if(this.icMeetingTopic.decisions[i].departments != null){
                                    for(var j = 0; j < this.icMeetingTopic.decisions[i].departments.length; j++){
                                        this.icMeetingTopic.decisions[i].departments[j].text = this.icMeetingTopic.decisions[i].departments[j].shortNameRu;
                                    }
                                }
                            }
                         }
                         if(this.icMeetingTopic.decisions != null){
                             for(var i = 0; i < this.icMeetingTopic.decisions.length; i++){
                                 if(this.icMeetingTopic.decisions[i].employees != null){
                                     for(var j = 0; j < this.icMeetingTopic.decisions[i].employees.length; j++){
                                         this.icMeetingTopic.decisions[i].employees[j].text =
                                         this.icMeetingTopic.decisions[i].employees[j].firstName + ' ' + this.icMeetingTopic.decisions[i].employees[j].lastName;
                                     }
                                 }
                             }
                          }

                        this.postAction(successMessage, errorMessage);
                    },
                    (error: ErrorResponse) => {
                        this.errorMessage = "Error loading IC Meeting topic";
                        if(error && !error.isEmpty()){
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, this.errorMessage);
                    }
                );
    }

    ngOnInit():any {
        $('#ICDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
    }

    addApproveListEmployee(){
        //alert("addApproveListEmployee");
        this.preselectApproveList();
    }

    public canEdit(){
        if(this.icMeetingTopic == null || this.icMeetingTopic.id == null || this.icMeetingTopic.id == 0){
            return true;
        }
        if(this.icMeetingTopic.closed || this.icMeetingTopic.deleted){
            return false;
        }
        if(this.icMeetingTopic.icMeeting != null && this.icMeetingTopic.icMeeting.closed){
            return false;
        }
        if(this.icMeetingTopic.icMeeting != null && this.icMeetingTopic.icMeeting.deleted){
            return false;
        }

        if(this.icMeetingTopic.icMeeting != null && this.icMeetingTopic.icMeeting.lockedByDeadline){
            if(this.moduleAccessChecker.checkAccessICMeetingAdmin()){
                return true;
            }
            if(this.icMeetingTopic.status === 'TO BE FINALIZED' || this.icMeetingTopic.status === 'FINALIZED' ||
                    this.icMeetingTopic.status === 'UNDER REVIEW'){
                // check deadline
                if(this.icMeetingTopic.icMeeting.updateLockedByDeadline){
                    return false;
                }
            }else{
                return false;
            }
        }
        var departmentId = this.icMeetingTopic != null && this.icMeetingTopic.department != null ? this.icMeetingTopic.department.id : 0;
        var accessOk = this.moduleAccessChecker.checkAccessICMeetingTopicsEdit(departmentId);
        return accessOk;
    }

    public canDelete(){
        if(this.icMeetingTopic.closed || this.icMeetingTopic.deleted){
            return false;
        }
        if(this.icMeetingTopic.icMeeting != null && this.icMeetingTopic.icMeeting.closed){
            return false;
        }
        if(this.icMeetingTopic.icMeeting != null && this.icMeetingTopic.icMeeting.deleted){
            return false;
        }
        if(this.icMeetingTopic.icMeeting != null && this.icMeetingTopic.icMeeting.lockedByDeadline){
            return false;
        }
        return true;
    }

    getClassByStatus(){
        if(this.icMeetingTopic.status != null){
            if(this.icMeetingTopic.status === 'DRAFT'){
                return 'label label-default';
            }
            if(this.icMeetingTopic.status === 'CLOSED'){
                return 'label label-danger';
            }
            if(this.icMeetingTopic.status === 'LOCKED FOR IC'){
                return 'label label-primary';
            }
            if(this.icMeetingTopic.status === 'TO BE FINALIZED'){
                return 'label label-info';
            }
            if(this.icMeetingTopic.status === 'FINALIZED'){
                return 'label label-success';
            }
            if(this.icMeetingTopic.status === 'UNDER REVIEW'){
                return 'label label-warning';
            }
            if(this.icMeetingTopic.status === 'READY' || this.icMeetingTopic.status === 'APPROVED'){
                return 'label label-success';
            }
        }
        return 'label label-default';
    }

    onFileChangeMaterials(event) {
        var target = event.target || event.srcElement;
        var files = target.files;
        //this.uploadMaterialsFiles.length = 0;
        for (let i = 0; i < files.length; i++) {
            var material = new FileEntity();
            material.file = files[i];
            material.name = files[i].protocolName;
            this.uploadMaterialsFiles.push(material);
        }
    }


    getUploadFileMaterialsAsFiles(){
        var filesList = [];
        if(this.uploadMaterialsFiles != null && this.uploadMaterialsFiles.length > 0){
            for (var i = 0; i < this.uploadMaterialsFiles.length; i++) {
                filesList.push(this.uploadMaterialsFiles[i].file);
            }
        }
        return filesList;
    }

    /*getUploadFileMaterialsAsFilesUpd(){
        var filesList = [];
        if(this.uploadMaterialsFilesUpd != null && this.uploadMaterialsFilesUpd.length > 0){
            for (var i = 0; i < this.uploadMaterialsFilesUpd.length; i++) {
                filesList.push(this.uploadMaterialsFilesUpd[i].file);
            }
        }
        return filesList;
    }*/

    onFileChangeExplanatoryNote(event) {
        var target = event.target || event.srcElement;
        var files = target.files;
        this.uploadExplanatoryNoteFile.length = 0;
        for (var i = 0; i < files.length; i++) {
            this.uploadExplanatoryNoteFile.push(files[i]);
        }
    }

    moveTopicMaterialUp(i){
        if(this.icMeetingTopic != null && this.icMeetingTopic.materials != null && this.icMeetingTopic.materials.length > 0){
            if(i == 0 || i >= this.icMeetingTopic.materials.length){
                return;
            }
            var prev = this.icMeetingTopic.materials[i - 1];
            this.icMeetingTopic.materials[i - 1] = this.icMeetingTopic.materials[i];
            this.icMeetingTopic.materials[i] = prev;
        }
    }

    moveTopicMaterialDown(i){
        if(this.icMeetingTopic != null && this.icMeetingTopic.materials != null && this.icMeetingTopic.materials.length > 0){
            if(i >= this.icMeetingTopic.materials.length - 1){
                return;
            }
            var next = this.icMeetingTopic.materials[i + 1];
            this.icMeetingTopic.materials[i + 1] = this.icMeetingTopic.materials[i];
            this.icMeetingTopic.materials[i] = next;
        }
    }

    checkRequiredFields(){
        if(this.icMeetingTopic){
            if(this.icMeetingTopic.name == null || this.icMeetingTopic.name.trim() === ''){
                this.postAction(null, 'Name required');
                return false;
            }
            if(this.icMeetingTopic.decisions == null || this.icMeetingTopic.decisions.length == 0){
                this.postAction(null, 'Decision required');
                return false;
            }else{
                for(var i = 0; i < this.icMeetingTopic.decisions.length; i++){
                    if(this.icMeetingTopic.decisions[i].name == null || this.icMeetingTopic.decisions[i].name.trim() == ''){
                        this.postAction(null, 'Decision name required (decision #' + (i+1));
                        return false;
                    }else if(this.icMeetingTopic.decisions[i].type == null){
                         this.postAction(null, 'Decision type required (decision #' + (i+1));
                         return false;
                     }else if(this.icMeetingTopic.decisions[i].type != null && this.icMeetingTopic.decisions[i].type === 'ASSIGN'){
                        // Assignment
                        if((this.icMeetingTopic.decisions[i].departments == null || this.icMeetingTopic.decisions[i].departments.length == 0) &&
                                (this.icMeetingTopic.decisions[i].employees == null || this.icMeetingTopic.decisions[i].employees.length == 0)){
                            this.postAction(null, 'Decision responsible department or employee required (decision #' + (i+1));
                            return false;
                        }

                    }
                }
            }
            if(this.icMeetingTopic.icMeeting != null && this.icMeetingTopic.icMeeting.id > 0){
                //if((this.icMeetingTopic.explanatoryNote == null || this.icMeetingTopic.explanatoryNote.id == null ||
                //    this.icMeetingTopic.explanatoryNote.id == 0) && this.uploadExplanatoryNoteFile == null){
                //    this.postAction(null, 'IC Meeting selected: Explanatory Note is required');
                //    return false;
                //}
                if(this.icMeetingTopic.speaker == null || this.icMeetingTopic.speaker.id == null || this.icMeetingTopic.speaker.id == 0){
                    this.postAction(null, 'IC Meeting selected: Speaker is required');
                    return false;
                }
            }
            return true;
        }else{
            return false;
        }
    }

    save(){
        if(!this.checkRequiredFields()){
            return false;
        }
        var toPublish = this.icMeetingTopic.icMeeting != null && this.icMeetingTopic.icMeeting.id > 0;
        if(toPublish && !this.icMeetingTopic.published){
            if(!confirm("Selecting IC Meeting will publish your topic and make visible to others. Proceed?")){
                return;
            }
        }else if(!toPublish && this.icMeetingTopic.published){
            if(!confirm("No IC Meeting selected: your topic will be reset and it will not be visible to others. Proceed?")){
                return;
            }
        }
        if(this.uploadMaterialsFiles != null && this.uploadMaterialsFiles.length > 0){
            this.icMeetingTopic.uploadMaterials = this.icMeetingTopic.uploadMaterials == null ? [] : this.icMeetingTopic.uploadMaterials;
            for(var i = 0; i < this.uploadMaterialsFiles.length; i++){
                if(this.uploadMaterialsFiles[i].name == null || this.uploadMaterialsFiles[i].name.trim() === ''){
                   this.postAction(null, "Materials upload: file name for protocol required.");
                   this.icMeetingTopic.uploadMaterials = [];
                   return;
                }
                this.icMeetingTopic.uploadMaterials.push({"file": {"fileName": this.uploadMaterialsFiles[i].file.name},
                "name": this.uploadMaterialsFiles[i].name);
            }
        }

        if(this.icMeetingTopic.materials != null && this.icMeetingTopic.materials.length > 0){
            for(var i = 0; i < this.icMeetingTopic.materials.length; i++){
                if(this.icMeetingTopic.materials[i].name == null || this.icMeetingTopic.materials[i].name.trim() === ''){
                    this.postAction(null, "Materials upload: file name for protocol required.");
                    return;
                }
            }
        }

        if(this.icMeetingTopic.id != null && this.icMeetingTopic.id == 0){
            this.icMeetingTopic.id = null;
        }
        this.icMeetingTopic.toPublish = toPublish;

        //console.log(this.icMeetingTopic);
        this.busy = this.corpMeetingService.saveICMeetingTopicWithFiles(this.icMeetingTopic, this.getUploadFileMaterialsAsFiles(),
                this.uploadExplanatoryNoteFile)
            .subscribe(
                (saveResponse: SaveResponse) => {
                    if(saveResponse.status === 'SUCCESS' ){
                        //this.icMeetingTopic.id = saveResponse.entityId;
                        this.getICMeetingTopic(saveResponse.entityId,"Successfully saved IC meeting topic.", null);
                    }else{
                        this.successMessage = null;
                        this.errorMessage = "Failed to save ic meeting topic";
                        if(result.message != null && result.message.nameEn != null && result.message.nameEn.trim() != ''{
                            this.errorMessage = result.message.nameEn;
                        }
                    }
                },
                (error) => {
                    var errorResponse = JSON.parse(error);
                    if(errorResponse && errorResponse.message && errorResponse.message.nameEn){
                        this.postAction(null, errorResponse.message.nameEn);
                    }else{
                        this.postAction(null, "Failed to save IC Meeting Topic");
                    }
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

    /*deleteUnsavedAttachmentUpd(newFile){
        for(var i = this.uploadMaterialsFilesUpd.length - 1; i >= 0; i--) {
            if(this.uploadMaterialsFilesUpd[i] == newFile) {
                this.uploadMaterialsFilesUpd.splice(i, 1);
            }
        }
    }*/

    deleteUnsavedExplanatoryNote(aFile){
        for(var i = this.uploadExplanatoryNoteFile.length - 1; i >= 0; i--) {
            if(this.uploadExplanatoryNoteFile[i] == aFile) {
                this.uploadExplanatoryNoteFile.splice(i, 1);
            }
        }
    }

    /*deleteUnsavedExplanatoryNoteUpd(aFile){
        for(var i = this.uploadExplanatoryNoteFileUpd.length - 1; i >= 0; i--) {
            if(this.uploadExplanatoryNoteFileUpd[i] == aFile) {
                this.uploadExplanatoryNoteFileUpd.splice(i, 1);
            }
        }
    }*/

    deleteAttachment(fileId, isUpdate){
        var confirmed = window.confirm("Are you sure want to delete");
        if(confirmed) {
            this.corpMeetingService.deleteICMeetingTopicAttachment(this.icMeetingTopic.id, fileId)
                .subscribe(
                    response => {
                        if(isUpdate){
                            for(var i = this.icMeetingTopic.materialsUpd.length - 1; i >= 0; i--) {
                                if(this.icMeetingTopic.materialsUpd[i].file.id === fileId) {
                                    this.icMeetingTopic.materialsUpd.splice(i, 1);
                                }
                            }
                        }else{
                            for(var i = this.icMeetingTopic.materials.length - 1; i >= 0; i--) {
                                if(this.icMeetingTopic.materials[i].file.id === fileId) {
                                    this.icMeetingTopic.materials.splice(i, 1);
                                }
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
    /*deleteAttachmentUpd(fileId){
        if(confirm("Are you sure want to delete")){
            this.corpMeetingService.deleteICMeetingTopicAttachmentUpd(this.icMeetingTopic.id, fileId)
                .subscribe(
                    response => {
                        for(var i = this.icMeetingTopic.materialsUpd.length - 1; i >= 0; i--) {
                            if(this.icMeetingTopic.materialsUpd[i].file.id === fileId) {
                                this.icMeetingTopic.materialsUpd.splice(i, 1);
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
    }*/

    deleteExplanatoryNote(){
        var confirmed = window.confirm("Are you sure want to delete");
        if(confirmed) {
            this.corpMeetingService.deleteICMeetingTopicExplanatoryNote(this.icMeetingTopic.id)
                .subscribe(
                    response => {
                        this.icMeetingTopic.explanatoryNote = null;
                        this.postAction("Explanatory note deleted.", null);
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

    /*deleteExplanatoryNoteUpd(){
        var confirmed = window.confirm("Are you sure want to delete");
        if(confirmed) {
            this.corpMeetingService.deleteICMeetingTopicExplanatoryNoteUpd(this.icMeetingTopic.id)
                .subscribe(
                    response => {
                        this.icMeetingTopic.explanatoryNoteUpd = null;
                        this.postAction("Explanatory note upd deleted.", null);
                    },
                    (error: ErrorResponse) => {
                        this.errorMessage = "Error deleting Explanatory note upd";
                        if(error && !error.isEmpty()){
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, null);
                    }
                );
        }
    }*/

    deleteICMeetingTopic(){
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
        if(this.icMeetingTopic.tags != null){
            for(var i = this.icMeetingTopic.tags.length - 1; i >= 0; i--) {
                if(this.icMeetingTopic.tags[i] === item) {
                    this.icMeetingTopic.tags.splice(i, 1);
                }
            }
        }
    }

    public onTagAdded(item) {
        //console.log(item);
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
        this.editableApproveList = value;
    }


    preselectApproveList(){
        if(this.icMeetingTopic.approveList) {
            this.icMeetingTopic.approveList.forEach(element => {
                for (var i = 0; i < this.availableApproveList.length; i++) {
                    var option = this.availableApproveList[i];
                    if (element.employee.id === option.id) {
                        this.approveListSelect.active.push(option);
                        this.editableApproveList.push(option);
                    }
                }
            });
        }
    }
    updateApproveListModal(){
        if(this.editableApproveList != null && this.editableApproveList.length > 0){
            var newApproveList = [];
            for(var i = 0; i < this.editableApproveList.length; i++){
                var item = this.editableApproveList[i];
                if(this.icMeetingTopic.approveList != null && this.icMeetingTopic.approveList.length > 0){
                    var found = false;
                    for(var j = 0; j < this.icMeetingTopic.approveList.length; j++){
                        if(this.icMeetingTopic.approveList[j].employee.id === item.id){
                            newApproveList.push(this.icMeetingTopic.approveList[j]);
                            found = true;
                            break;
                        }
                    }
                    if(!found){
                        newApproveList.push({"employee": {"id": item.id, "fullName": item.text}, "approved": false});
                    }
                }else{
                    // add all
                    newApproveList.push({"employee": {"id": item.id, "fullName": item.text}, "approved": false});
                }
            }
            this.icMeetingTopic.approveList = newApproveList;

        }else{
            this.icMeetingTopic.approveList = [];
        }
        this.editableApproveList = [];
        this.approveListSelect.active = [];
    }

    canApprove(){
        return this.canChangeApproveStatus(false);
    }

   canCancelApprove(){
        return this.canChangeApproveStatus(true);
    }

    canChangeApproveStatus(approved){
        if(this.icMeetingTopic.closed || this.icMeetingTopic.deleted){
            return false;
        }
        if(this.icMeetingTopic.icMeeting != null && this.icMeetingTopic.icMeeting.closed){
            return false;
        }
        if(this.icMeetingTopic.icMeeting != null && this.icMeetingTopic.icMeeting.deleted){
            return false;
        }
        if(this.icMeetingTopic.icMeeting != null && this.icMeetingTopic.icMeeting.lockedByDeadline){
            if(this.icMeetingTopic.status === 'TO BE FINALIZED' || this.icMeetingTopic.status === 'FINALIZED'
                || this.icMeetingTopic.status === 'UNDER REVIEW'){
                // check deadline
                /*if(this.icMeetingTopic.icMeeting.updateLockedByDeadline){
                    return false;
                }else{
                     // OK
                }*/
            }else{
                return false;
            }
        }
        if(this.icMeetingTopic != null && this.icMeetingTopic.approveList != null && this.icMeetingTopic.approveList.length > 0){
            for(var i = 0; i < this.icMeetingTopic.approveList.length; i++){
                if(this.icMeetingTopic.approveList[i].employee &&
                    this.icMeetingTopic.approveList[i].employee.username === localStorage.getItem("authenticatedUser")){
                    return this.icMeetingTopic.approveList[i].approved == approved;
                }
            }
        }
        return false;
    }


    approve(){
        if(confirm("Are you sure want to approve?")){
            this.busy = this.corpMeetingService.approveICMeetingTopic(this.icMeetingTopic.id)
                .subscribe(
                    response => {
                        //this.icMeetingTopic.explanatoryNote = null;
                        this.getICMeetingTopic(this.icMeetingTopic.id, "Successfully approved topic.", null);
                    },
                    (error: ErrorResponse) => {
                        if(error && !error.isEmpty()){
                            this.processErrorMessage(error);
                            this.postAction(null, this.errorMessage);
                        }else{
                            this.postAction(null, "Error approving topic.");
                        }

                    }
                );
        }
    }

    cancelApprove(){
        if(confirm("Are you sure want to approve?")){
            this.busy = this.corpMeetingService.cancelApproveICMeetingTopic(this.icMeetingTopic.id)
                .subscribe(
                    response => {
                        //this.icMeetingTopic.explanatoryNote = null;
                        this.getICMeetingTopic(this.icMeetingTopic.id, "Successfully dis-approved topic.", null);
                    },
                    (error: ErrorResponse) => {
                        if(error && !error.isEmpty()){
                            this.processErrorMessage(error);
                            this.postAction(null, this.errorMessage);
                        }else{
                            this.postAction(null, "Error dis-approving topic.");
                        }

                    }
                );
        }
    }

    /*showUpdateBlock(){
        if(this.icMeetingTopic != null && this.icMeetingTopic.id > 0 && this.icMeetingTopic.published){
            if(this.icMeetingTopic.icMeeting != null){
                if(this.icMeetingTopic.icMeeting.closed || this.icMeetingTopic.status === 'TO BE FINALIZED'
                 || this.icMeetingTopic.status === 'FINALIZED'){
                    return true;
                }
            }
        }
        return false;
    }*/

    /*canEditUpdateBlock(){
        if(this.icMeetingTopic == null || this.icMeetingTopic.id == null || this.icMeetingTopic.id == 0){
                return true;
        }
        if(this.icMeetingTopic.closed){
            return false;
        }
        if(this.icMeetingTopic.deleted){
            return false;
        }
        if(this.icMeetingTopic.icMeeting != null && this.icMeetingTopic.icMeeting.closed){
            return false;
        }
        if(this.icMeetingTopic.icMeeting != null && this.icMeetingTopic.icMeeting.deleted){
            return false;
        }
        //if(this.icMeetingTopic.icMeeting != null && this.icMeetingTopic.icMeeting.lockedByDeadline){
        //    if(this.moduleAccessChecker.checkAccessICMeetingAdmin()){
        //        return true;
        //    }
        //    return false;
        //}
        if(this.showUpdateBlock() && (this.icMeetingTopic.status === 'TO BE FINALIZED' || this.icMeetingTopic.status === 'FINALIZED')){
            // check deadline
            if(this.icMeetingTopic.icMeeting.updateLockedByDeadline){
                if(this.moduleAccessChecker.checkAccessICMeetingAdmin()){
                    return true;
                }
                return false;
            }
            var departmentId = this.icMeetingTopic != null && this.icMeetingTopic.department != null ? this.icMeetingTopic.department.id : 0;
            if(this.moduleAccessChecker.checkAccessICMeetingTopicsEdit(departmentId)){
                return true;
            }
        }
        return false;
    }*/

    /*saveUpd(){
        //alert("TODO: saveUpd()");
        if(this.uploadMaterialsFilesUpd != null && this.uploadMaterialsFilesUpd.length > 0){
            this.icMeetingTopic.materialsUpd = this.icMeetingTopic.materialsUpd == null ? [] : this.icMeetingTopic.materialsUpd;
            for(var i = 0; i < this.uploadMaterialsFilesUpd.length; i++){
                this.icMeetingTopic.materialsUpd.push({"file": {"fileName": this.uploadMaterialsFilesUpd[i].file.name},
                "name": this.uploadMaterialsFilesUpd[i].name);
            }
        }
        var publishedUpd = false;
        if($('#publishedUpdCheckbox').prop("checked")){
            publishedUpd = true;
        }
        this.busy = this.corpMeetingService.saveICMeetingTopicUpdate({"id": this.icMeetingTopic.id, "publishedUpd": publishedUpd,
            "nameUpd": this.icMeetingTopic.nameUpd, "decisionUpd": this.icMeetingTopic.decisionUpd,
            "uploadMaterialsUpd": this.icMeetingTopic.materialsUpd}, this.getUploadFileMaterialsAsFilesUpd(), this.uploadExplanatoryNoteFileUpd)
        .subscribe(
            (saveResponse: SaveResponse) => {
                if(saveResponse.status === 'SUCCESS' ){
                    //this.icMeetingTopic.id = saveResponse.entityId;
                    this.getICMeetingTopic(saveResponse.entityId,"Successfully saved IC meeting topic.", null);
                }else{
                    this.successMessage = null;
                    this.errorMessage = "Failed to save ic meeting topic";
                    if(result.message != null && result.message.nameEn != null && result.message.nameEn.trim() != ''{
                        this.errorMessage = result.message.nameEn;
                    }
                }
            },
            (error) => {
                var errorResponse = JSON.parse(error);
                if(errorResponse && errorResponse.message && errorResponse.message.nameEn){
                    this.postAction(null, errorResponse.message.nameEn);
                }else{
                    this.postAction(null, "Failed to save IC Meeting Topic");
                }
            }
        );
    }*/

    /*onFileChangeMaterialsUpd(event){
        var target = event.target || event.srcElement;
        var files = target.files;
        this.uploadMaterialsFilesUpd.length = 0;
        for (let i = 0; i < files.length; i++) {
            var material = new FileEntity();
            material.file = files[i];
            material.name = files[i].protocolName;
            this.uploadMaterialsFilesUpd.push(material);
        }
    }*/

    /*onFileChangeExplanatoryNoteUpd(event){
        var target = event.target || event.srcElement;
        var files = target.files;
        this.uploadExplanatoryNoteFileUpd.length = 0;
        for (var i = 0; i < files.length; i++) {
            this.uploadExplanatoryNoteFileUpd.push(files[i]);
        }
    }*/

    exportApproveList(){
        var fileName = "Лист согласования";
            //fileName = fileName.replace(".", ",");
            this.busy = this.corpMeetingService.makeFileRequest(DATA_APP_URL + `corpMeetings/ICMeeting/exportTopicApproveList/${this.icMeetingTopic.id}`,
                fileName, 'POST')
                .subscribe(
                    response  => {
                        //console.log("export topic approve list response ok");
                    },
                    error => {
                        //console.log("fails")
                        this.postAction(null, "Error exporting topic approve list");
                    }
                );
    }
    autoAddedApproveListMember(item){
        return (this.icMeetingTopic.id == null || this.icMeetingTopic.id == 0) &&
                    ((this.icAdminEmployee != null && item.employee != null && item.employee.id == this.icAdminEmployee.id)
                    || (this.legalHeadEmployee != null && item.employee != null && item.employee.id == this.legalHeadEmployee.id));
    }

    addDecision(){
        if(this.icMeetingTopic.decisions == null){
            this.icMeetingTopic.decisions = [];
        }
        this.icMeetingTopic.decisions.push({});
    }

    removeDecision(i){
        if(this.icMeetingTopic.decisions != null && this.icMeetingTopic.decisions.length > i && i >= 0){
            this.icMeetingTopic.decisions.splice(i, 1);
        }
    }

    public selectedDecisionDepartment(value:any):void {
    }
    public selectedDecisionEmployee(value:any):void {
    }

    public removedDecisionDepartment(value:any):void {
    }

    public removedDecisionEmployee(value:any):void {
    }

    public refreshDecisionDepartments(value:any, decision):void {
        //console.log(value);
        //console.log(decision);
        decision.departments = decision.departments == null ? []: decision.departments;
        decision.departments = value;
    }

    public refreshDecisionEmployees(value:any, decision):void {
        decision.employees = decision.employees == null ? []: decision.employees;
        decision.employees = value;
    }

    canEditShare(){
        return this.moduleAccessChecker.checkAccessICMember();
    }

    shareWithDepartment(){
        if(confirm("Are you sure want to share with your department?")){
            this.busy = this.corpMeetingService.shareICMeetingTopic(this.icMeetingTopic.id)
                .subscribe(
                    response => {
                        this.icMeetingTopic.sharedWithDepartment = true;
                        this.postAction("Successfully shared topic.", null);
                    },
                    (error: ErrorResponse) => {
                        if(error && !error.isEmpty()){
                            this.processErrorMessage(error);
                            this.postAction(null, this.errorMessage);
                        }else{
                            this.postAction(null, "Error sharing topic.");
                        }

                    }
                );
        }
    }

    stopShareWithDepartment(){
        if(confirm("Are you sure want to stop share with your department?")){
            this.busy = this.corpMeetingService.stopShareICMeetingTopic(this.icMeetingTopic.id)
                .subscribe(
                    response => {
                        this.icMeetingTopic.sharedWithDepartment = false;
                        this.postAction("Successfully stopped sharing topic.", null);
                    },
                    (error: ErrorResponse) => {
                        if(error && !error.isEmpty()){
                            this.processErrorMessage(error);
                            this.postAction(null, this.errorMessage);
                        }else{
                            this.postAction(null, "Error stopping share topic.");
                        }

                    }
                );
        }
    }

    exportMaterials(){
        if(this.icMeetingTopic.id != null && this.icMeetingTopic.name != null){
            var fileName = (this.icMeetingTopic.icMeeting != null ? 'ИК№' + this.icMeetingTopic.icMeeting.number + ' - ' : '') +
                this.icMeetingTopic.icOrder + ')' + (this.icMeetingTopic.name.length <= 55 ? this.icMeetingTopic.name : this.icMeetingTopic.name.substring(0, 55) + '_');
            console.log(fileName);
            this.busy = this.corpMeetingService.makeFileRequest(DATA_APP_URL + `corpMeetings/icMeetingTopic/exportMaterials/${this.icMeetingTopic.id}/`, fileName)
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

}
