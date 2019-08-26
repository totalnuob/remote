import {Component, OnInit, ElementRef, Compiler } from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {User} from "../authentication/authentication.service";
import {EmployeeService} from "./employee.service";
import {ErrorResponse} from "../common/error-response";
import {CommonFormViewComponent} from "../common/common.component";
import {Employee} from "../hr/model/employee";
import {cursorTo} from "readline";


export class ChangePasswordCredentials {
    constructor(
        public currentPassword: string,
        public newPassword: string) { }
}

declare var $:any

@Component({
    selector: 'employee-profile-edit',
    providers: [],
    templateUrl: './view/employee.profile.edit.component.html',
    styleUrls: ['../../../public/css/footer.css']
})
export class EmployeeProfileEditComponent extends CommonFormViewComponent implements OnInit{

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

    private employee = new Employee();

    ngOnInit(){
        $('#birthDatePicker').datetimepicker({
            format: 'DD-MM-YYYY'
        });
    }

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private employeeService: EmployeeService) {

        super(router);

        this.sub = this.route
            .params
            .subscribe(params => {
                //this.employeeId = +params['id'];
                this.username = params['username'];
                this.breadcrumbParams = params['params'];
                //console.log(this.breadcrumbParams);
                if(this.username != null){
                    this.employeeService.getEmployeeByUsername(this.username)
                        .subscribe(
                            employee => {
                                this.employee = employee;
                                $('#birthDatePicker').datetimepicker({
                                    format: 'DD-MM-YYYY'
                                });
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
                    $('#birthDatePicker').datetimepicker({
                        format: 'DD-MM-YYYY'
                    });
                }
            });
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

    userOwned(){
        var currentUser = localStorage.getItem("authenticatedUser");
        if(this.employee != null && currentUser != null && this.employee.username === currentUser){
            return true;
        }
        return false;
    }

    saveEmployeeProfile(){
        this.employee.birthDate= $('#birthDate').val();
        //console.log(this.employee);
        this.employeeService.save(this.employee)
            .subscribe(
                response => {
                    this.postAction("Successfully saved profile", null);
                },
                (error:ErrorResponse) => {
                    if (error && !error.isEmpty()) {
                        this.processErrorMessage(error);
                    }else {
                        this.postAction(null, "Error saving profile");
                    }
                }
            );
    }

}