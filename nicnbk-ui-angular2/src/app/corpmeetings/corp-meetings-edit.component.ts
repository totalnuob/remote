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

    constructor(
        private employeeService: EmployeeService,
        private lookupService: LookupService,
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
                            this.corpMeeting.id = +params['id'];
                            //this.breadcrumbParams = params['params'];
                            if(this.corpMeeting.id > 0) {
                                alert(this.corpMeeting.id);


                                // preselect trip memo attendees
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

    search(page){

        //this.busy = this.tripMemoService.search(this.searchParams)
        //    .subscribe(
        //        searchResult  => {
        //            this.tripMemoList = searchResult.tripMemos;
        //            this.tripMemoSearchResult = searchResult;
        //        },
        //        (error: ErrorResponse) => {
        //            this.errorMessage = "Error searching memos";
        //            console.log("Error searching memos");
        //            if(error && !error.isEmpty()){
        //                this.processErrorMessage(error);
        //            }
        //            this.postAction(null, null);
        //        }
        //    );
    }

    save(){
        if(this.corpMeeting.attendeesNIC == null || this.corpMeeting.attendeesNIC.length == 0){
            this.postAction(null, "Please fill out NIC attendees.");
        }
    }
}
