import { Component, OnInit, ViewChild, ChangeDetectionStrategy } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Location} from '@angular/common';
import {AuthenticationService} from "../authentication/authentication.service";

//import '../../../public/js/star-rating/star-rating.min.js';
import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {ErrorResponse} from "../common/error-response";
import {CommonFormViewComponent} from "../common/common.component";
import {EmployeeService} from "../employee/employee.service";
import {EmployeesSearchParams} from "../employee/model/employees-search-params";
import {EmployeesSearchResult} from "../employee/model/employees-search-result";


declare var $: any

@Component({
    selector: 'hr-employees-list',
    templateUrl: 'view/hr-employees-list.component.html',
    styleUrls: [
    ],
    providers: [AuthenticationService],
    changeDetection: ChangeDetectionStrategy.Default // TODO: change to OnPush ??
})
export class HREmployeesListComponent extends CommonFormViewComponent implements OnInit{
    busy: Subscription;

    private moduleAccessChecker: ModuleAccessCheckerService;
    private sub: any;
    private searchParams = new EmployeesSearchParams;
    private searchResult: EmployeesSearchResult;

    ngOnInit():void {
    }

    constructor(
        private employeeService: EmployeeService,
        private router: Router,
        private route: ActivatedRoute
    ){
        super(router);
        this.moduleAccessChecker = new ModuleAccessCheckerService;


        this.sub = this.route
            .params
            .subscribe(params => {
                if(params['params'] != null){
                    this.searchParams = JSON.parse(params['params']);
                    console.log(this.searchParams);
                    this.busy = this.employeeService.search(this.searchParams)
                        .subscribe(
                            response  => {
                                this.searchResult = response;
                                console.log(response);
                            },
                            (error: ErrorResponse) => {
                                this.errorMessage = "Error searching employees";
                                console.log("Error searching employees");
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, null);
                            }
                        );
                } else {
                    this.search(0);
                }
            });

    }


    canEdit(){
        return false;
    }

    search(page){

        // TODO: as parameter?
        this.searchParams.pageSize = 20;
        this.searchParams.page = page;

        this.busy = this.employeeService.search(this.searchParams)
            .subscribe(
                response  => {
                    this.searchResult = response;
                    console.log(response);
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error searching employees";
                    console.log("Error searching employees");
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                    this.postAction(null, null);
                }
            );
    }

    navigate(username){
        console.log(this.searchParams);
        this.searchParams.path = '/hr/employees';
        let params = JSON.stringify(this.searchParams);
        this.router.navigate(['/profile', username, { params }]);
    }

}