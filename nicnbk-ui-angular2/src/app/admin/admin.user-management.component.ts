import { Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthenticationService} from "../authentication/authentication.service";
import {Subscription} from 'rxjs';
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {CommonFormViewComponent} from "../common/common.component";

declare var $: any

@Component({
    selector: 'admin-user-management',
    templateUrl: 'view/admin.user-management.component.html',
    styleUrls: [
    ],
    providers: [AuthenticationService]
})
export class AdminUserManagementComponent extends CommonFormViewComponent implements OnInit{
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
    }
}