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
import {ICMeetingAssignment} from "./model/ic-meeting-assignment";

import {ICMeetingTopicSearchParams} from "./model/ic-meeting-topic-search-params";
import {ICMeeting} from "./model/ic-meeting";
import {BaseDictionary} from "../common/model/base-dictionary";
import {FileEntity} from "../common/model/file-entity";

import {DATA_APP_URL} from "../common/common.service.constants";

//import { TagInputModule } from 'ng2-tag-input';
declare var $:any

@Component({
    selector: 'corp-meeting-edit',
    templateUrl: 'view/corp-meeting-assignment-edit.component.html',
    styleUrls: [],
    providers: [],
})
export class CorpMeetingAssignmentEditComponent extends CommonFormViewComponent implements OnInit {

    busy: Subscription;
    public sub: any;

    userDepartmentId = 0;

    assignment: ICMeetingAssignment;

    private breadcrumbParams: string;
    private searchParams = new ICMeetingTopicSearchParams();

    modalErrorMessage;
    modalSuccessMessage;

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
        this.userDepartmentId = userPosition != null && userPosition.department != null ? userPosition.department.id : 0;

        this.assignment = new ICMeetingAssignment();
        this.sub = this.route
            .params
            .subscribe(params => {
                this.assignment.id = +params['id'];
                this.breadcrumbParams = params['params'];
                if(this.breadcrumbParams != null) {
                    this.searchParams = JSON.parse(this.breadcrumbParams);
                }
                if(this.assignment.id > 0) {
                   this.getICAssignment(this.assignment.id, null, null);
                }else{
                }
            });
    }

    ngOnInit():any {
        $('#dateDue').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });
    }

    getICAssignment(id, successMessage, errorMessage){

        this.busy = this.corpMeetingService.getICAssignment(id)
                .subscribe(
                    (assignment: any)=> {
                        this.assignment = assignment;
                        //console.log(this.assignment);

                        this.postAction(successMessage, errorMessage);
                    },
                    (error: ErrorResponse) => {
                        this.errorMessage = "Error loading IC Assignment";
                        if(error && !error.isEmpty()){
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, this.errorMessage);
                    }
                );
    }

    public canEdit(){
        if(this.moduleAccessChecker.checkAccessICMeetingAdmin()){
            return true;
        }
        if(this.assignment != null && this.assignment.departments != null && this.assignment.departments.length > 0){
            for(var i = 0; i < this.assignment.departments.length; i ++){
                if(this.assignment.departments[i].id == this.userDepartmentId){
                    return true;
                }
            }
        }
        return false;
    }

    public canDelete(){
        return false;
    }

    checkRequiredFields(){
        if(this.assignment){
            //if(this.assignment.dateDue == null || this.assignment.dateDue.trim() === ''){
            //    this.postAction(null, 'Due Date required');
            //    return false;
            //}
            if(this.assignment.status == null || this.assignment.status.trim() === ''){
                this.postAction(null, 'Status required');
                return false;
            }
            return true;
        }
        return false;
    }

    // checkIsViewableByAll() {
    //     if()
    // }


    save(){
        this.assignment.dateDue = $('#dateDueValue').val();
        if(!this.checkRequiredFields()){
            return false;
        }

        //console.log(this.assignment);
        this.busy = this.corpMeetingService.saveICAssignment(this.assignment)
            .subscribe(
                (saveResponse: SaveResponse) => {
                    if(saveResponse.status === 'SUCCESS' ){
                        this.assignment.id = saveResponse.entityId;
                        this.getICAssignment(saveResponse.entityId,"Successfully saved IC Assignment.", null);
                    }else{
                        this.successMessage = null;
                        this.errorMessage = "Failed to save IC Assignment.";
                        if(result.message != null && result.message.nameEn != null && result.message.nameEn.trim() != ''{
                            this.errorMessage = result.message.nameEn;
                        }
                    }
                },
                (error) => {
                    if(error && error.message){
                        if(error.message.nameEn){
                            this.postAction(null, error.message.nameEn);
                        }else{
                            this.postAction(null, error.message);
                        }
                    }else{
                        this.postAction(null, "Failed to save IC Assignment.");
                    }
                }
            );
    }

    listDepartments(){
        var departments = "";
        if(this.assignment != null && this.assignment.departments != null){
            for(var i = 0; i < this.assignment.departments.length; i++){
                departments += (departments.length > 0 ? ', ': '') + this.assignment.departments[i].shortNameRu;
            }
        }
        return departments;
    }
    listEmployees(){
        var employees = "";
        if(this.assignment != null && this.assignment.employees != null){
            for(var i = 0; i < this.assignment.employees.length; i++){
                employees += (employees.length > 0 ? ', ': '') + this.assignment.employees[i].firstName + ' ' +  this.assignment.employees[i].lastName;
            }
        }
        return employees;
    }
}
