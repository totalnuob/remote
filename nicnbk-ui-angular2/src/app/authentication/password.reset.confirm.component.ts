import {Component, OnInit} from "@angular/core";
import {AuthenticationService, UserToken} from "./authentication.service";
import {RuntimeCompiler} from "@angular/compiler";
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {CommonFormViewComponent} from "../common/common.component";
import {Subscription} from "rxjs";
import {ModuleAccessCheckerService} from "./module.access.checker.service";
import {ErrorResponse} from "../common/error-response";
import {Router, ActivatedRoute} from '@angular/router';

@Component({
    selector: 'password-reset-confirm',
    providers: [AuthenticationService, RuntimeCompiler],
    templateUrl: './view/password.reset.confirm.component.html',
    styleUrls: ['../../../public/css/footer.css']
})

export class PasswordResetConfirmComponent implements OnInit{
    ResponseResetForm: FormGroup;
    errorMessage: string;
    successMessage: string;
    token: string;
    username: string;
    isValid: boolean;

    newPassword = '';
    confirmNewPassword = '';
    passwordFieldType: boolean;
    confirmPasswordFieldType: boolean;
    successMessageResetPassword: String;
    errorMessageResetPassword: String;

    public userToken = new UserToken('', '');

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
        });
        this.verifyToken();
    }

    verifyToken() {
        this.authenticationService.validateToken(this.userToken).subscribe(
            response => {
                console.log(response);
                if (response == true) {
                    this.isValid = true;
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

    resetConfirm() {
        this.authenticationService.reset(this.newPassword)
            .subscribe(
                response => {
                    if (response.status === 'SUCCESS'){
                        this.successMessageResetPassword = response.message.nameEn;
                        this.errorMessageResetPassword = null;
                    }
                },
                (error: ErrorResponse) => {
                    this.successMessageResetPassword = null;
                    this.errorMessageResetPassword = error && error.message ? error.message : "Error resetting password";
                    //this.processErrorMessage(error);
                }
            );
    }

    togglePasswordTextType() {
        this.passwordFieldType = !this.passwordFieldType;
    }

    toggleConfirmPasswordTextType() {
        this.confirmPasswordFieldType = !this.confirmPasswordFieldType;
    }

}