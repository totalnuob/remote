import {Component} from "@angular/core";
import {CommonFormViewComponent} from "../common/common.component";
import {ActivatedRoute, Router} from "@angular/router";
import {EmployeeService} from "./employee.service";
import {Employee} from "./model/employee";
import {ErrorResponse} from "../common/error-response";
import {Observable} from "rxjs/Observable";

declare var $:any;

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
    private breadcrumbParamsPath: string;

    private employee = new Employee();

    departments;
    positions;
    roles;

    chosenDepartment;
    chosenPosition;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private employeeService: EmployeeService
    ){
        super(router);

        Observable.forkJoin(
            this.employeeService.getAllDepartments(),
            this.employeeService.getAllPositions(),
            this.employeeService.getAllRoles(),
        )
            .subscribe(
                ([data1, data2, data3]) => {

                    this.departments = data1;
                    this.departments.push({"code": "NONE", "nameEn": "-- no dept --", "nameRu": "-- без департамента --"});
                    console.log('All departments');
                    console.log(this.departments);

                    this.positions = data2;
                    console.log('All positions');
                    console.log(this.positions);

                    this.roles = data3;
                    console.log('All roles');
                    console.log(this.roles);

                    $('#birthDatePicker').datetimepicker({
                        format: 'DD-MM-YYYY'
                    });

                    this.sub = this.route
                        .params
                        .subscribe(params => {
                            this.username = params['username'];
                            this.breadcrumbParams = params['params'];
                            this.breadcrumbParamsPath = JSON.parse(this.breadcrumbParams)['path'];
                            if(this.username != 'newUser') {
                                this.employeeService.getEmployeeByUsername(this.username)
                                    .subscribe( response => {
                                            this.employee = response;
                                            console.log(this.employee);

                                            if(this.employee.position) {
                                                if(this.employee.position.department != null) {
                                                    this.chosenDepartment = this.employee.position.department.code;
                                                } else {
                                                    this.chosenDepartment = "NONE";
                                                }

                                                this.chosenPosition = this.employee.position.code;
                                            }
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
                        });
                }
            );
    }
}
