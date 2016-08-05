import { Component, OnInit, ViewChild  } from '@angular/core';
import {ActivatedRoute, ROUTER_DIRECTIVES} from '@angular/router';
import {SELECT_DIRECTIVES, SelectComponent} from "ng2-select";
//import {SelectItem} from "ng2-select/ng2-select";
import {GeneralMemo} from "./model/general-memo";
import {CommonComponent} from "../common/view.component";
import {MemoService} from "./memo.service";
import {EmployeeService} from "../employee/employee.service";

declare var $:any
declare var Chart: any;

@Component({
    selector: 'pe-memo-edit',
    templateUrl: `app/m2s2/view/general-memo-edit.component.html`,
    styleUrls: [],
    directives: [SELECT_DIRECTIVES, ROUTER_DIRECTIVES],
    providers: [],
})
export class GeneralMemoEditComponent extends CommonComponent implements OnInit{

    private sub: any;
    private memoIdParam: number;
    memo = new GeneralMemo;

    @ViewChild('attendeesSelect')
    private attendeesSelect: SelectComponent;

    public attendeesList: Array<any> = [];

    constructor(
        private employeeService: EmployeeService,
        private memoService: MemoService,
        private route: ActivatedRoute
    ){
        super();
        // TODO: refactor as findAll ??? or load cash
        this.employeeService.getAll().then(
            data => {
                data.forEach(element => {
                    this.attendeesList.push({ id: element.id, text: element.lastName});
                });
                //console.log(this.strategyList);
            }
        );

        this.sub = this.route
            .params
            .subscribe(params => {
                this.memoIdParam = +params['id'];
                if(this.memoIdParam > 0) {
                    this.memoService.get(1, this.memoIdParam)
                        .subscribe(
                            memo => {
                                // TODO: check response memo
                                this.memo = memo;
                                //console.log(this.memo);

                                // preselect memo attendees
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
                            },
                            error => this.errorMessage = "Error loading info"
                        );
                }
            });
    }

    ngOnInit():any {

        // TODO: exclude jQuery
        // datetimepicker
        $('#meetingDate').datetimepicker({
            //defaultDate: new Date(),
            format: 'DD-MM-YYYY'
        });

        // load lookups
        this.loadLookups();
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

        console.log(this.memo);
        this.memoService.saveGeneral(this.memo)
            .subscribe(
                response  => {
                    this.successMessage = "Successfully saved.";
                    this.errorMessage = null;
                    // TODO: rafactor?
                    $('html, body').animate({ scrollTop: 0 }, 'fast');
                },
                //error =>  this.errorMessage = <any>error
                error =>  {
                    this.errorMessage = "Error saving memo";
                    this.successMessage = null;
                    // TODO: rafactor?
                    $('html, body').animate({ scrollTop: 0 }, 'fast');
                }
            );

    }

    changeArrangedBy(){
        if(this.memo.arrangedBy != 'OTHER'){
            this.memo.arrangedByDescription = '';
        }
    }

    loadLookups(){
    }


}