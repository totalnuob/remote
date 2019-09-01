import {Component, OnInit, ElementRef, Compiler } from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {User} from "../authentication/authentication.service";
import {EmployeeService} from "./employee.service";
import {ErrorResponse} from "../common/error-response";
import {CommonFormViewComponent} from "../common/common.component";
import {Employee} from "../hr/model/employee";
import {cursorTo} from "readline";
import {ModuleAccessCheckerService} from "../authentication/module.access.checker.service";
import {EmployeesSearchParams} from "../hr/model/employees-search-params";


export class ChangePasswordCredentials {
    constructor(
        public currentPassword: string,
        public newPassword: string) { }
}

declare var $:any

@Component({
    selector: 'employee-profile',
    providers: [],
    templateUrl: './view/employee.profile.component.html',
    styleUrls: ['../../../public/css/footer.css']
})
export class EmployeeProfileComponent extends CommonFormViewComponent implements OnInit{

    activeTab = "PROFILE";
    public errorMessage;
    public successMessage;

    currentPassword: string;
    newPassword: string;
    newPasswordConfirm: string;

    private sub: any;
    //private employeeId: number;
    private username: string;
    private breadcrumbParams: string;

    private employee: Employee;

    private moduleAccessChecker: ModuleAccessCheckerService;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private employeeService: EmployeeService) {

        super(router);

        this.moduleAccessChecker = new ModuleAccessCheckerService;

        this.sub = this.route
            .params
            .subscribe(params => {
                //this.employeeId = +params['id'];
                this.username = params['username'];
                this.breadcrumbParams = params['params'];
                console.log(this.breadcrumbParams);
                //if(this.employeeId > 0) {
                //    this.employeeService.getEmployeeById(this.employeeId)
                //        .subscribe(
                //            employee => {
                //                this.employee = employee;
                //            },
                //            (error:ErrorResponse) => {
                //                this.errorMessage = "Error loading news";
                //                if (error && !error.isEmpty()) {
                //                    this.processErrorMessage(error);
                //                }
                //                this.postAction(null, null);
                //            }
                //        );
                //}
                if(this.username != null){
                    this.employeeService.getEmployeeByUsername(this.username)
                        .subscribe(
                            employee => {
                                this.employee = employee;
                            },
                            (error:ErrorResponse) => {
                                this.errorMessage = "Error loading news";
                                if (error && !error.isEmpty()) {
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, null);
                            }
                        );
                } else {
                    this.employee = new Employee();
                }
            });
    }

    ngOnInit(){
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

    public changePassword(){
        // TODO: check input fields
        if(this.newPassword === this.newPasswordConfirm){
            if(!this.checkPassword()){
                return;
            }
            this.employeeService.changeSelfPassword(new ChangePasswordCredentials(this.currentPassword, this.newPassword))
                .subscribe(
                    response => {
                        this.successMessage = "Password successfully changed. Please log in with your new password";
                        this.errorMessage = null;
                        //alert("Password successfully changed. Please login");

                        //$('#passwordChangeModal').open();
                    },
                    error => {
                        this.errorMessage = "Error changing password. Please check your password";
                        this.successMessage = null;
                    }
                );
        }else{
            this.errorMessage = "New passwords do not match";
        }
    }

    public finishChangePassword(){
        localStorage.removeItem("authenticatedUser");
        localStorage.removeItem("authenticatedUserRoles");

        location.reload();

        this.router.navigate(['/login']);
    }

    getFirstname(){
        return "";
    }

    getLastname(){
        return "";
    }

    getUsername(){
    }

    userOwned(){
        var currentUser = localStorage.getItem("authenticatedUser");
        if(this.employee != null && currentUser != null && this.employee.username === currentUser){
            return true;
        }
        return false;
    }

    public canEditEmployeeProfile(){
        return this.moduleAccessChecker.checkAccessEmployeeProfileEditor();
    }

    editEmployeeProfile(){
        let params = this.breadcrumbParams;
        this.router.navigate(['/profile/edit/', this.username, { params }]);
    }

    getFullPositionNameEn(){
        if(this.employee && this.employee.position){
            return this.employee.position.nameEn +
                (this.employee.position.department != null  && this.employee.position.department.nameEn != null ?
                " of " + this.employee.position.department.nameEn : "");

        }
    }
}