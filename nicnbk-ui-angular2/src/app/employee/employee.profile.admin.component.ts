import {Component} from "@angular/core";
import {CommonFormViewComponent} from "../common/common.component";
import {ActivatedRoute, Router} from "@angular/router";
import {EmployeeService} from "./employee.service";
import {Employee} from "./model/employee";
import {ErrorResponse} from "../common/error-response";
import {Observable} from "rxjs/Observable";
import {Subscription} from "../../../node_modules/rxjs";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";

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
    private moduleAccessChecker: ModuleAccessCheckerService;

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

    newPassword = '';
    newPasswordConfirm = '';

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private employeeService: EmployeeService
    ){
        super(router);

        this.moduleAccessChecker = new ModuleAccessCheckerService;

        if(!this.moduleAccessChecker.checkAccessAdmin()) {
            this.router.navigate(['accessDenied']);
        }

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
                                this.employeeService.getFullEmployeeByUsername(this.username)
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
                                this.employee.locked = false;
                                this.employee.failedLoginAttempts = 0;
                                this.employee.mfaEnabled = false;

                                console.log('New User creation');
                            }
                        });
                },
                (error: ErrorResponse) => {
                    this.errorMessage = "Error getting lookups";
                    console.log("Error getting lookups");
                    if(error && !error.isEmpty()) {
                        this.processErrorMessage(error);
                        console.log(error);
                    }
                    this.postAction(null, this.errorMessage);
                }
            );
    }

    hasRole(value) {
        if(this.employee == null || this.employee.roles == null || this.employee.roles.length == 0) {
            return false;
        }
        for(var i = 0; i < this.employee.roles.length; i++) {
            if(this.employee.roles[i].code == value) {
                return true;
            }
        }
        return false;
    }

    deleteRole(role) {
        if(this.employee == null || this.employee.roles == null || this.employee.roles.length == 0) {
            return;
        }
        for(var i = 0; i < this.employee.roles.length; i++) {
            if(this.employee.roles[i].code === role.code) {
                this.employee.roles.splice(i, 1)
                break;
            }
        }
    }

    addRole(role) {
        if(this.employee == null) {
            return;
        }
        if(this.employee.roles == null) {
            this.employee.roles = [];
        }
        this.employee.roles.push(role);
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

        if(this.newPassword == this.newPasswordConfirm) {
            if(this.newPassword == '') {
                this.busy = this.employeeService.saveAdmin(this.employee)
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
            } else if(this.checkPassword()) {
                this.busy = this.employeeService.saveAndChangePassword(this.employee, this.newPassword)
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
                this.newPassword = '';
                this.newPasswordConfirm = '';
            }
        } else {
            this.postAction(null, "Passwords do not match");
        }
    }

    public checkPassword(){
        if (this.newPassword.length < 8) {
            this.errorMessage = "Password too short (min 8)";
            return false;
        } else if (this.newPassword.length > 50) {
            this.errorMessage = "Password too long (max 50)";
            return false;
        } else if (this.newPassword.search(/\d/) == -1) {
            this.errorMessage = "Password must contain at least 1 digit";
            return false;
        } else if (this.newPassword.search(/[a-zA-Z]/) == -1) {
            this.errorMessage = "Password must contain at least 1 character";
            return false;
        } else if (this.newPassword.search(/[A-Z]/) == -1) {
            this.errorMessage = "Password must contain at least 1 capital letter";
            return false;
        }
        return true;
    }
}
