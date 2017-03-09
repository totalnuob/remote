import { Component,NgModule, OnInit, ViewChild  } from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {GeneralMemo} from "./model/general-memo";
import {CommonFormViewComponent} from "../common/common.component";
import {MemoService} from "./memo.service";
import {EmployeeService} from "../employee/employee.service";
import {MemoAttachmentDownloaderComponent} from "./memo-attachment-downloader.component";
import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";

declare var $:any
declare var Chart: any;

@Component({
    selector: 'pe-memo-edit',
    templateUrl: './view/general-memo-edit.component.html',
    styleUrls: [],
    providers: [],
})
@NgModule({
    imports: []
})
export class GeneralMemoEditComponent extends CommonFormViewComponent implements OnInit{

    private sub: any;
    private memoIdParam: number;
    busy: Subscription;

    memo = new GeneralMemo;
    submitted = false;

    public uploadFiles: Array<any> = [];

    @ViewChild('attendeesSelect')
    private attendeesSelect;

    public attendeesList: Array<any> = [];

    private breadcrumbParams: string;

    options = {
        placeholder: "+ tag",
        secondaryPlaceholder: "Enter a new tag",
        separatorKeys: [188, 191], // exclude coma from tag content
        maxItems: 10
    }

    public onItemAdded(item) {
        this.memo.tags.push(item);
    }

    public onItemRemoved(item) {
        for(var i = this.memo.tags.length - 1; i >= 0; i--) {
            if(this.memo.tags[i] === item) {
                this.memo.tags.splice(i, 1);
            }
        }
    }

    constructor(
        private employeeService: EmployeeService,
        private memoService: MemoService,
        private route: ActivatedRoute
    ){
        super();

        // loadLookups
        this.sub = this.loadLookups();


        // TODO: wait/sync on lookup loading
        // TODO: sync on subscribe results
        //this.waitSleep(700);

        this.sub = this.route
            .params
            .subscribe(params => {
                this.memoIdParam = +params['id'];
                this.breadcrumbParams = params['params'];
                if(this.memoIdParam > 0) {
                    this.busy = this.memoService.get(1, this.memoIdParam)
                        .subscribe(
                            memo => {
                                // TODO: check response memo
                                this.memo = memo;

                                if(this.memo.tags == null) {
                                    this.memo.tags = [];
                                }

                                // preselect memo attendees
                                this.preselectAttendeesNIC();
                            },
                            (error: ErrorResponse) => {
                                this.errorMessage = "Error loading memo";
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, null);
                            }
                        );
                }else{
                    // TODO: default value for meeting type?
                    this.memo.meetingType = "MEETING";

                    this.memo.tags = [];
                }
            });
    }

    preselectAttendeesNIC(){
        if(this.memo.attendeesNIC) {
            this.memo.attendeesNIC.forEach(element => {
                for (var i = 0; i < this.attendeesList.length; i++) {
                    var option = this.attendeesList[i];
                    if (element.id === option.id) {
                        this.attendeesSelect.active.push(option);
                    }
                }
            });
        }
    }

    ngOnInit():any {

        this.postAction(null, null);

        // TODO: exclude jQuery
        // datetimepicker
        $('#meetingDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        $('#timePicker').datetimepicker({
            format: 'LT'
        })

    }

    public selected(value:any):void {
        //console.log('Selected value is: ', value);
    }

    public removed(value:any):void {
        //console.log('Removed value is: ', value);
    }

    public refreshAttendeesNIC(value:any):void {
        this.memo.attendeesNIC = value;
    }

    save(){

        // TODO: ngModel date
        this.memo.meetingDate = $('#meetingDateValue').val();
        this.memo.meetingTime = $('#meetingTimeValue').val();

        this.memoService.saveGeneral(this.memo)
            .subscribe(
                response  => {
                    this.memo.id = response.entityId;
                    this.memo.creationDate = response.creationDate;

                    if(this.uploadFiles.length > 0) {

                        // TODO: refactor
                        this.memoService.postFiles(this.memo.id, [], this.uploadFiles).subscribe(
                            res => {
                                // clear upload files list on view
                                this.uploadFiles.length = 0;

                                // update files list with new files
                                if(!this.memo.files){ // no files existed
                                    this.memo.files = [];
                                }
                                for (var i = 0; i < res.length; i++) {
                                    this.memo.files.push(res[i]);
                                }

                                this.postAction("Successfully saved.", null);
                                this.submitted = true;
                            },
                            error => {
                                // TODO: don't save memo?

                                this.postAction(null, "Error uploading attachments.");
                                this.submitted = false;
                            });
                    }else{
                        this.postAction("Successfully saved.", null);
                        this.submitted = true;
                    }
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error saving memo";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                    this.submitted = false;
                }
            );

    }

    deleteAttachment(fileId){
        var confirmed = window.confirm("Are you sure want to delete");
        if(confirmed) {
            this.memoService.deleteAttachment(this.memo.id, fileId)
                .subscribe(
                    response => {
                        for(var i = this.memo.files.length - 1; i >= 0; i--) {
                            if(this.memo.files[i].id === fileId) {
                                this.memo.files.splice(i, 1);
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

    onFileChange(event) {
        var target = event.target || event.srcElement;
        var files = target.files;
        this.uploadFiles.length = 0;
        for (let i = 0; i < files.length; i++) {
            this.uploadFiles.push(files[i]);
        }
    }

    changeArrangedBy(){
        if(this.memo.arrangedBy != 'OTHER'){
            this.memo.arrangedByDescription = '';
        }
    }

    loadLookups(){
        // load employees
        this.employeeService.findAll()
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.attendeesList.push({ id: element.id, text: element.firstName + " " + element.lastName[0] + "."});
                    });
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error loading employees";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            );
    }

    public showSaveButton() {
        // only owner can edit
        var moduleAccessChecker = new ModuleAccessCheckerService;
        if(moduleAccessChecker.checkAccessAdmin()){
            return true;
        }
        var currentUser = localStorage.getItem("authenticatedUser");
        //if(this.memo.owner == null  || this.memo.owner == ""){
        //    return true;
        //}
        if(this.memo.owner === currentUser || !this.memo.id){
           return true;
        }
        return false;
    }

}