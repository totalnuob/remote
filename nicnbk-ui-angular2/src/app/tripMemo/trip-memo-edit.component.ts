import {Component,NgModule, OnInit, ViewChild} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import { SelectComponent} from "ng2-select";
import {TripMemo} from "./model/trip-memo";
import {CommonFormViewComponent} from "../common/common.component";
import {TripMemoService} from "./trip-memo.service";
import {EmployeeService} from "../employee/employee.service";
import {MemoAttachmentDownloaderComponent} from "../m2s2/memo-attachment-downloader.component";
import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";

declare var $:any
declare var Chart: any;

@Component({
    selector: 'trip-memo-edit',
    templateUrl: 'view/trip-memo-edit.component.html',
    styleUrls: [],
    providers: [],
})
@NgModule({
    imports: [MemoAttachmentDownloaderComponent]
})
export class TripMemoEditComponent extends CommonFormViewComponent implements OnInit{

    private sub: any;
    private tripMemoIdParam: number;
    tripMemo = new TripMemo;
    busy: Subscription;

    public uploadFiles: Array<any> = [];

    @ViewChild('attendeesSelect')
    private attendeesSelect: SelectComponent;

    public attendeesList: Array<any> = [];

    constructor(
        private employeeService: EmployeeService,
        private tripMemoService: TripMemoService,
        private route: ActivatedRoute
    ){
        super();

        //loadLookups
        this.sub = this.loadLookups();

        this.sub = this.route
            .params
            .subscribe(params => {
                this.tripMemoIdParam = +params['id'];
                if(this.tripMemoIdParam > 0) {
                    this.busy = this.tripMemoService.get(this.tripMemoIdParam)
                        .subscribe(
                            tripMemo => {
                                // TODO: check response memo
                                this.tripMemo = tripMemo;

                                // preselect trip memo attendees
                                this.preselectAttendees();

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
                    this.tripMemo.tripType = "BUSINESS TRIP";
                }
            });
    }

    private preselectAttendees() {
        if(this.tripMemo.attendees) {
            this.tripMemo.attendees.forEach(element => {
                for(var i = 0; i < this.attendeesList.length; i++) {
                    var option = this.attendeesList[i];
                    if (element.id === option.id) {
                        this.attendeesSelect.active.push(option);
                    }
                }
            });
        }
    }
    ngOnInit(): any {

        this.postAction(null, null);

        // TODO: exclude jQuery
        // datetimepicker
        $('#meetingDate1').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        $('#meetingDate2').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

    }

    public selected(value:any):void {
        //console.log('Selected value is: ', value);
    }

    public removed(value:any):void {
        //console.log('Removed value is: ', value);
    }

    public refreshAttendees(value:any):void {
        this.tripMemo.attendees = value;
    }

    save() {
        // TODO: ngModel date
        this.tripMemo.meetingDateStart = $('#meetingDateValue').val();
        this.tripMemo.meetingDateEnd = $('#meetingDateValue2').val();

        //console.log(this.tripMemo);
        this.tripMemoService.saveBt(this.tripMemo)
            .subscribe(
                response => {
                    this.tripMemo.id = response.entityId;

                    if (this.uploadFiles.length > 0) {
                        // TODO: refactor
                        this.tripMemoService.postFiles(this.tripMemo.id, [], this.uploadFiles)
                            .subscribe(
                                res => {
                                    // clear upload files list on view
                                    this.uploadFiles.length = 0;

                                    // update files list with new files
                                    if (!this.tripMemo.files) { // no files existed
                                        this.tripMemo.files = [];
                                    }
                                    for (var i = 0; i < res.length; i++) {
                                        this.tripMemo.files.push(res[i]);
                                    }
                                    this.postAction("Successfully saved.", null);
                                },
                                (error: ErrorResponse) => {
                                    this.errorMessage = "Error uploading attachments";
                                    if(error && !error.isEmpty()){
                                        this.processErrorMessage(error);
                                    }
                                    this.postAction(null, null);
                                });
                    } else {
                        this.postAction("Successfully saved.", null);
                    }
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error saving memo";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            );
    }

    deleteAttachment(fileId) {
        var confirmed = window.confirm("Are you sure want to delete attachment?");
        if(confirmed) {
            this.tripMemoService.deleteAttachment(this.tripMemo.id, fileId)
                .subscribe(
                    response => {
                       for(var i = this.tripMemo.files.length - 1; i >= 0; i--) {
                           if(this.tripMemo.files[i].id === fileId) {
                               this.tripMemo.files.splice(i, 1);
                           }
                       }
                       this.postAction("Attachment deleted.", null);
                    },
                    (error: ErrorResponse) => {
                        this.errorMessage = "Error deleting attachments";
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

    loadLookups() {
        // TODO: refactor as findAll ??? or load cash
        this.employeeService.findAll()
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.attendeesList.push({id: element.id, text: element.firstName + " " + element.lastName[0] + "."});
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
        if(this.tripMemo.owner == null || this.tripMemo.owner == "" || this.tripMemo.owner == currentUser){
            //console.log(this.memo.owner);
            return true;
        }
        return false;
    }

}