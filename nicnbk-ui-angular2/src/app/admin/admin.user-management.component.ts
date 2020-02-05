import {Component} from '@angular/core';
import {CommonFormViewComponent} from "../common/common.component";
import {ActivatedRoute, Router} from "@angular/router";
import {EmployeesSearchParams} from "../employee/model/employees-search-params";
import {Subscription} from "rxjs";
import {EmployeeService} from "../employee/employee.service";
import {EmployeesSearchResult} from "../employee/model/employees-search-result";
import {ErrorResponse} from "../common/error-response";

@Component({
    selector: 'admin-user-management',
    templateUrl: 'view/admin.user-management.component.html',
    styleUrls: [],
    providers: []
})
export class AdminUserManagementComponent extends CommonFormViewComponent{

    private sub: any;
    private searchParams = new EmployeesSearchParams();
    private searchResult: EmployeesSearchResult;

    busy: Subscription;

    constructor(
        private employeeService: EmployeeService,
        private router: Router,
        private route: ActivatedRoute
    ){
        super(router);

        this.sub = this.route
            .params
            .subscribe(params => {
                if(params['params'] != null) {
                    this.searchParams = JSON.parse(params['params']);
                } else {
                    this.searchParams.pageSize = 20;
                    this.searchParams.page = 0;
                }
                console.log(this.searchParams);
                this.search(this.searchParams.page);
            });
    }

    search(page){
        this.searchParams.page = page;

        this.busy = this.employeeService.search(this.searchParams)
            .subscribe(
                response => {
                    this.searchResult = response;
                    console.log(response);
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error searching employees";
                    console.log("Error searching employees");
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                        console.log(error);
                    }
                    this.postAction(null, this.errorMessage);
                }
            );
    }

    navigate(username){
        console.log(this.searchParams);
        this.searchParams.path = '/admin/userManagement';
        let params = JSON.stringify(this.searchParams);
        this.router.navigate(['/profile/admin', username, { params }]);
    }
}