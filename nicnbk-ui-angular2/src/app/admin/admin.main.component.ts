import { Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthenticationService} from "../authentication/authentication.service";
import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {CommonFormViewComponent} from "../common/common.component";

declare var $: any

@Component({
    selector: 'admin-main',
    templateUrl: 'view/admin.main.component.html',
    styleUrls: [
    ],
    providers: [AuthenticationService]
})
export class AdminMainComponent extends CommonFormViewComponent implements OnInit{
    busy: Subscription;

    private moduleAccessChecker: ModuleAccessCheckerService;
    private sub: any;

    ngOnInit():void {
    }

    constructor(
        private router: Router,
        private route: ActivatedRoute
    ){
        super(router);
        this.moduleAccessChecker = new ModuleAccessCheckerService;
/*
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
*/
    }
}