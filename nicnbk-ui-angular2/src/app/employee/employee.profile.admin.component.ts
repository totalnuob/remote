import {Component} from "@angular/core";
import {CommonFormViewComponent} from "../common/common.component";
import {ActivatedRoute, Router} from "@angular/router";
import {EmployeeService} from "./employee.service";
import {Employee} from "./model/employee";
import {ErrorResponse} from "../common/error-response";
import {Observable} from "rxjs/Observable";

@Component({
    selector: 'employee-profile-admin',
    templateUrl: './view/employee.profile.admin.component.html',
    styleUrls: [],
    providers: []
})
export class EmployeeProfileAdminComponent extends CommonFormViewComponent{

    private sub: any;

    private username: string;
    private breadcrumbParams: string;

    private employee = new Employee();

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private employeeService: EmployeeService
    ){
        super(router);

        Observable.forkJoin(
            this.employeeService.getAllPositions(),
            this.employeeService.getAllRoles(),
        )
            .subscribe(
                ([data1, data2]) => {
                    console.log('All positions');
                    console.log(data1);
                    console.log('All roles');
                    console.log(data2);

                    this.sub = this.route
                        .params
                        .subscribe(params => {
                                this.username = params['username'];
                                this.breadcrumbParams = params['params'];
                                if(this.username != 'newUser') {
                                    this.employeeService.getEmployeeByUsername(this.username)
                                        .subscribe( response => {
                                                this.employee = response;
                                                console.log(this.employee);




                                            },
                                            (error: ErrorResponse) => {
                                                this.errorMessage = "Error searching employee";
                                                console.log("Error searching employee");
                                                if(error && !error.isEmpty()) {
                                                    this.processErrorMessage(error);
                                                    console.log(error);
                                                }
                                                this.postAction(null, this.errorMessage);
                                            }
                                        );
                                } else {
                                    console.log('New User creation');
                                }
                            }
                        );
                }
            );
    }
}
