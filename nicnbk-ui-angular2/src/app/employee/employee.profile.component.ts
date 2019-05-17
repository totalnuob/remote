import {Component, ElementRef, Compiler } from '@angular/core';
import {Router} from '@angular/router';
import {User} from "../authentication/authentication.service";
import {EmployeeService} from "./employee.service";


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
export class EmployeeProfileComponent {

    activeTab = "PROFILE";
    public errorMessage;
    public successMessage;

    currentPassword: string;
    newPassword: string;
    newPasswordConfirm: string;


    constructor(
        private router: Router,
        private employeeService: EmployeeService) {
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
        return localStorage.getItem("authenticatedUser");
    }


}