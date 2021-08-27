import {Component} from "@angular/core";
import {CommonFormViewComponent} from "../common/common.component";
import {ActivatedRoute, Router} from "@angular/router";
import {EmployeeService} from "./employee.service";
import {Employee} from "./model/employee";
import {ErrorResponse} from "../common/error-response";
import {Observable} from "rxjs/Observable";
import {Subscription} from "../../../node_modules/rxjs";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {el} from "@angular/platform-browser/testing/browser_util";

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

    private employee = new Employee;

    private departments;
    private positions;
    private roles;
    private activeEmployees;

    private chosenDepartment;
    private chosenPosition;
    private departmentPositions;
    private chosenSubstitutedEmployee;

    newPassword = '';
    newPasswordConfirm = '';
    generatedPassword = '';
    passwordFieldType: boolean;
    confirmPasswordFieldType: boolean;
    emailCheckbox = false;
    isActingAs: boolean;
    actingEmployee: number;

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
            this.employeeService.findActiveAll(),
        )
            .subscribe(
                ([data1, data2, data3, data4]) => {

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

                    this.activeEmployees = data4;
                    // this.activeEmployees.removeAt(55);
                    // this.activeEmployees.removeAt(61);
                    // this.activeEmployees.removeAt(62);
                    // this.activeEmployees.removeAt(67);
                    console.log(this.activeEmployees);

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
                                            console.log(response);
                                            // this.employee.isActing = response.isActing;
                                            // this.employee.actingEmployee = response.actingEmployee;
                                            console.log(this.employee);
                                            if (this.employee.isActing != null  && this.employee.actingEmployee != null) {
                                                if (this.employee.isActing) {
                                                    this.isActingAs = this.employee.isActing;
                                                    this.actingEmployee = this.employee.actingEmployee;
                                                    this.chosenSubstitutedEmployee = this.employee.actingEmployee;
                                                }
                                            } else {
                                                this.employee.isActing = false;
                                                this.employee.actingEmployee = null;
                                            }

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
                                this.employee.isActing = false;
                                this.employee.actingEmployee = null;

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

    toggleIsEmployeeActingAsButton() {
        this.isActingAs = !this.isActingAs;
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

    selectedActingEmployee(value: number) {
        console.log(value);
        // for (let i = 0; i < this.activeEmployees.length; i++) {
        //     if (value === this.activeEmployees[i].id) {
        //         this.actingEmployee = this.activeEmployees[i].id;
        //         this.isActingAs = true;
        //     }
        // }
        this.actingEmployee = value;
        this.isActingAs = true;
    }

    saveEmployeeProfile() {
        this.employee.birthDate= $('#birthDate').val();

        console.log(this.chosenPosition);
        this.employee.position = {};
        this.employee.position.code = this.chosenPosition;
        this.employee.isActing = this.isActingAs;
        console.log(this.employee.isActing);
        this.employee.actingEmployee = this.actingEmployee;

        if(this.newPassword == this.newPasswordConfirm) {
            if(this.newPassword == '') {
                this.busy = this.employeeService.saveAdmin(this.employee)
                    .subscribe(
                        response => {
                            this.employee.id = response.entityId;
                            this.successMessage = "Successfully saved profile"
                            this.postAction(this.successMessage, null);
                        },
                        (error:ErrorResponse) => {
                            if (error && !error.isEmpty()) {
                                this.processErrorMessage(error);
                                console.log(error);
                                this.errorMessage = "Error saving profile";
                            }else {
                                this.postAction(null, this.errorMessage);
                            }
                        }
                    );
            } else if(this.checkPassword()) {
                this.busy = this.employeeService.saveAndChangePassword(this.employee, this.newPassword, this.emailCheckbox)
                    .subscribe(
                        response => {
                            this.employee.id = response.entityId;
                            this.successMessage = "Successfully saved profile";
                            this.postAction(this.successMessage, null);
                        },
                        (error:ErrorResponse) => {
                            if (error && !error.isEmpty()) {
                                this.processErrorMessage(error);
                                console.log(error);
                                this.errorMessage = "Error saving profile";
                            }else {
                                this.postAction(null, this.errorMessage);
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

    generatePassword(passwordLength) {
        var numberChars = "0123456789";
        var upperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        var lowerChars = "abcdefghijklmnopqrstuvwxyz";
        var allChars = numberChars + upperChars + lowerChars;
        var randPasswordArray = Array(passwordLength);
        randPasswordArray[0] = numberChars;
        randPasswordArray[1] = upperChars;
        randPasswordArray[2] = lowerChars;
        randPasswordArray = randPasswordArray.fill(allChars, 3);
        this.generatedPassword = this.shuffleArray(randPasswordArray.map(function(x) { return x[Math.floor(Math.random() * x.length)] })).join('');
        this.emailCheckbox = true;
    }

    shuffleArray(array) {
        for (var i = array.length - 1; i > 0; i--) {
            var j = Math.floor(Math.random() * (i + 1));
            var temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
        return array;
    }

    togglePasswordTextType() {
        this.passwordFieldType = !this.passwordFieldType;
    }

    toggleConfirmPasswordTextType() {
        this.confirmPasswordFieldType = !this.confirmPasswordFieldType;
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
