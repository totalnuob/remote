import {Component, OnInit} from "@angular/core";
import {AuthenticationService, UserPassword, UserToken} from "./authentication.service";
import {RuntimeCompiler} from "@angular/compiler";
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {ErrorResponse} from "../common/error-response";
import {Router, ActivatedRoute} from '@angular/router';
import {async} from "rxjs/scheduler/async";

const delay = require('core-js/core/delay');

@Component({
    selector: 'password-reset-confirm',
    providers: [AuthenticationService, RuntimeCompiler],
    templateUrl: './view/password.reset.confirm.component.html',
    styleUrls: ['../../../public/css/footer.css']
})


export class PasswordResetConfirmComponent implements OnInit{
    errorMessage: string;
    successMessage: string;
    token: string;
    username: string;
    isValid: boolean;
    isReset: boolean;
    passwordNotConfirmed: boolean;
    passwordNotValid: boolean;

    confirmNewPassword = '';
    passwordFieldType: boolean;
    confirmPasswordFieldType: boolean;
    successMessageResetPassword: String;
    errorMessageResetPassword: String;
    errorNotValidPassword: String;

    public userToken = new UserToken('', '');
    public userPassword = new UserPassword('', '', '');

    constructor(
        private authenticationService: AuthenticationService,
        private router: Router,
        private route: ActivatedRoute,
        private fb: FormBuilder ) {
    }

    ngOnInit():void {
        this.route.params.subscribe(params => {
            console.log(JSON.stringify(params))
            this.userToken.username = params['username'];
            this.userToken.token = params['token'];
            this.userPassword.token = params['token'];
        });
        this.verifyToken();
    }

    verifyToken() {
        this.authenticationService.validateToken(this.userToken).subscribe(
            response => {
                console.log(response);
                if (response == true) {
                    this.isValid = true;
                    this.userPassword.username = this.userToken.username;
                    console.log(this.isValid);
                } else {
                    this.isValid = false;
                    console.log(this.isValid);
                }
            } , (error: ErrorResponse) => {
                console.log(this.isValid);
                this.isValid = false;
            }
        );
    }

    resetPassword() {
        if (this.isPasswordValid()) {
            this.passwordNotValid = false;
            if (this.isNewPasswordConfirmed()) {
                this.passwordNotConfirmed = false;
                this.authenticationService.changePassword(this.userPassword)
                    .subscribe(
                        response => {
                            (async () => {
                                if (response == true){
                                    this.isReset = true;
                                    this.successMessageResetPassword = "Successfully reset password. Redirecting to login page.";
                                    console.log(this.isReset);
                                    await delay(5000);
                                    this.router.navigate(['/login']);
                                } else {
                                    this.isReset = false;
                                    this.errorMessageResetPassword = "Failed to reset password. Token has expired, please" +
                                        " contact system administrator or request new password reset link";
                                    console.log(this.isReset);
                                }
                            })();
                        },
                        (error: ErrorResponse) => {
                            this.isValid = false;
                        }
                    );
            } else {
                this.passwordNotConfirmed = true;
            }
        } else {
            this.passwordNotValid = true;
        }
    }

    isNewPasswordConfirmed(): boolean {
        return this.userPassword.newPassword == this.confirmNewPassword;
    }

    togglePasswordTextType() {
        this.passwordFieldType = !this.passwordFieldType;
    }

    toggleConfirmPasswordTextType() {
        this.confirmPasswordFieldType = !this.confirmPasswordFieldType;
    }

    delay(ms: number) {
        return new Promise( resolve => setTimeout(resolve, ms) );
    }

    isPasswordValid(): boolean {
        if (this.userPassword.newPassword.length < 8) {
            this.errorNotValidPassword = "Password too short (min 8)";
            return false;
        } else if (this.userPassword.newPassword.length > 50) {
            this.errorNotValidPassword = "Password too long (max 50)";
            return false;
        } else if (this.userPassword.newPassword.search(/\d/) == -1) {
            this.errorNotValidPassword = "Password must contain at least 1 digit";
            return false;
        } else if (this.userPassword.newPassword.search(/[a-zA-Z]/) == -1) {
            this.errorNotValidPassword = "Password must contain at least 1 character";
            return false;
        } else if (this.userPassword.newPassword.search(/[A-Z]/) == -1) {
            this.errorNotValidPassword = "Password must contain at least 1 capital letter";
            return false;
        }
        return true;
    }

}