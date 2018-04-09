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

    @ViewChild('attendeesSelect')
    private attendeesSelect: SelectComponent;
    attendeesNICList: any[];

    public uploadMaterialsFiles: Array<any> = [];

    public uploadProtocolsFiles: Array<any> = [];

    corpMeeting: CorpMeeting;

    corpMeetingId: number;

    constructor(
        private employeeService: EmployeeService,
        private corpMeetingService: CorpMeetingService,
        private router: Router,
        private route: ActivatedRoute
    ){
        super(router);

        this.corpMeeting = new CorpMeeting();
        this.attendeesNICList = [];

        Observable.forkJoin(
            // Load lookups
            this.employeeService.findAll()
            )
            .subscribe(
                (data) => {
                    data[0].forEach(element => {
                        this.attendeesNICList.push({ id: element.id, text: element.firstName + " " + element.lastName[0] + "."});
                    });

                    this.sub = this.route
                        .params
                        .subscribe(params => {
                            this.corpMeetingId = +params['id'];
                            //this.breadcrumbParams = params['params'];
                            if(this.corpMeetingId > 0) {
                                this.busy = this.corpMeetingService.get(this.corpMeetingId)
                                    .subscribe(
                                        memo => {
                                            // TODO: check response memo
                                            this.corpMeeting = memo;

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

                                // preselect attendees
                                //TODO: this.preselectAttendees();
                            }
                        });
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error loading lookups";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, this.errorMessage);
                }
            );
    }

    ngOnInit():any {
        // TODO: exclude jQuery
        // datetimepicker
        $('#corpMeetingDateDiv').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        // load lookups
        //this.loadLookups();

        // find all
        //fthis.search(0);
    }

    public canEdit(){
        return true;
    }

    public selected(value:any):void {
        //console.log('Selected value is: ', value);
    }

    public removed(value:any):void {
        //console.log('Removed value is: ', value);
    }

    public refreshAttendees(value:any):void {
        this.corpMeeting.attendeesNIC = value;
    }

    preselectAttendeesNIC(){
        if(this.corpMeeting.attendeesNIC) {
            this.corpMeeting.attendeesNIC.forEach(element => {
                for (var i = 0; i < this.attendeesNICList.length; i++) {
                    var option = this.attendeesNICList[i];
                    if (element.id === option.id) {
                        this.attendeesSelect.active.push(option);
                    }
                }
            });
        }
    }

    onFileChangeProtocols(event) {
        var target = event.target || event.srcElement;
        var files = target.files;
        this.uploadProtocolsFiles.length = 0;
        for (let i = 0; i < files.length; i++) {
            this.uploadProtocolsFiles.push(files[i]);
        }
    }

    onFileChangeMaterials(event) {
        var target = event.target || event.srcElement;
        var files = target.files;
        this.uploadMaterialsFiles.length = 0;
        for (let i = 0; i < files.length; i++) {
            this.uploadMaterialsFiles.push(files[i]);
        }
    }

    save(){
        if(this.corpMeeting != null && (this.corpMeeting.attendeesNIC == null || this.corpMeeting.attendeesNIC.length == 0)){
            this.postAction(null, "Please fill out NIC attendees.");
        }

        this.corpMeeting.date = $('#corpMeetingDate').val();
        this.busy = this.corpMeetingService.save(this.corpMeeting)
            .subscribe(
                (saveResponse: SaveResponse) => {
                    if(saveResponse.status === 'SUCCESS' ){
                        this.corpMeeting.id = saveResponse.entityId;

                        this.postAction("Successfully saved corp meeting", null);
                    }else{
                        if(saveResponse.message != null){
                            var message = saveResponse.message.nameEn != null ? saveResponse.message.nameEn :
                                saveResponse.message.nameRu != null ? saveResponse.message.nameRu : saveResponse.message.nameKz;
                            if(message != null && message != ''){
                                this.postAction(null, message);
                            }else{
                                this.postAction(null, "Error saving corp meeting");
                            }
                        }
                    }
                },
                (error: ErrorResponse) => {
                    this.processErrorMessage(error);
                }
            );

    }
}
