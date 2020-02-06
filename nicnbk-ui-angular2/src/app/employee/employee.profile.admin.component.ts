import {Component} from "@angular/core";
import {CommonFormViewComponent} from "../common/common.component";
import {ActivatedRoute, Router} from "@angular/router";
import {EmployeeService} from "./employee.service";
import {Employee} from "./model/employee";
import {ErrorResponse} from "../common/error-response";
import {Observable} from "rxjs/Observable";
import {Subscription} from "../../../node_modules/rxjs";

declare var $:any;

@Component({
    selector: 'employee-profile-admin',
    templateUrl: './view/employee.profile.admin.component.html',
    styleUrls: [],
    providers: []
})
export class EmployeeProfileAdminComponent extends CommonFormViewComponent{

    busy: Subscription;
    private sub: any;

    private username: string;
    private breadcrumbParams: string;
    private breadcrumbParamsPath: string;

    private employee = new Employee();

    private departments;
    private positions;
    private roles;

    private chosenDepartment;
    private chosenPosition;
    private departmentPositions;

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
                    this.departments.push({"code": "NONE", "nameEn": "-- no department or unknown --", "nameRu": "-- без департамента или неизвестен --"});
                    console.log('All departments');
                    console.log(this.departments);

                    this.positions = data2;
                    this.positions.push({"code": "NONE", "nameEn": "-- no position or unknown --", "nameRu": "-- без позиции или неизвестна --"});
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

                            if(this.username != 'newEmployee') {
                                this.employeeService.getEmployeeByUsername(this.username)
                                    .subscribe( response => {
                                            this.employee = response;
                                            console.log(this.employee);

                                            if(this.employee.position == null) {
                                                this.chosenDepartment = "NONE";
                                                this.departmentChanged(this.chosenDepartment);
                                                this.chosenPosition = "NONE";
                                            } else {
                                                if(this.employee.position.department == null) {
                                                    this.chosenDepartment = "NONE";
                                                } else {
                                                    this.chosenDepartment = this.employee.position.department.code;
                                                }
                                                this.departmentChanged(this.chosenDepartment);
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
                                this.chosenDepartment = null;
                                this.chosenPosition = null;
                                this.employee.active = true;

                                console.log('New User creation');
                            }
                        });
                }
            );
    }

    departmentChanged(value) {
        console.log(value);
        this.departmentPositions = [];

        if(this.positions != null) {
            for(var i = 0; i < this.positions.length; i++) {
                if(value === "NONE" && this.positions[i].department == null) {
                    this.departmentPositions.push(this.positions[i]);
                } else if(this.positions[i].department != null && this.positions[i].department.code === value) {
                    this.departmentPositions.push(this.positions[i]);
                }
            }
        }

        this.chosenPosition = null;
    }

    saveEmployeeProfile() {
        this.employee.birthDate= $('#birthDate').val();
        console.log(this.chosenPosition);

        this.employee.position = {};
        this.employee.position.code = this.chosenPosition;
        this.busy = this.employeeService.save(this.employee)
            .subscribe(
                response => {
                    this.employee.id = response.entityId;
                    this.postAction("Successfully saved profile", null);
                },
                (error:ErrorResponse) => {
                    if (error && !error.isEmpty()) {
                        this.processErrorMessage(error);
                        console.log(error);
                    }else {
                        this.postAction(null, "Error saving profile");
                    }
                }
            );
    }
}
