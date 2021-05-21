import {Component, OnInit} from "@angular/core";
import {AuthenticationService} from "./authentication.service";
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
    token: null;
    username: null;
    CurrentState: any;
    IsResetFormValid = true;

    newPassword = '';
    confirmNewPassword = '';
    passwordFieldType: boolean;
    confirmPasswordFieldType: boolean;
    successMessageResetPassword: String;
    errorMessageResetPassword: String;

    constructor(
        private authenticationService: AuthenticationService,
        private router: Router,
        private route: ActivatedRoute,
        private fb: FormBuilder ) {

        this.CurrentState = 'Wait';
        this.route.params.subscribe(params => {
            console.log(params)
            this.username = params['username'];
            console.log(this.username);
            this.verifyToken();
        });
    }

    ngOnInit() {
        // this.Init();
        this.CurrentState = 'Wait';
        this.route.queryParams.subscribe(params => {
            console.log(params)
            this.username = params['username'];
            console.log(this.username);
            this.verifyToken();
        });
    }

    verifyToken() {
        this.authenticationService.validateToken(this.username).subscribe(
            response => {
                if (response.status === 'SUCCESS') {
                    this.CurrentState = 'Verified';
                    this.successMessageResetPassword = response.message.nameEn;
                    this.errorMessageResetPassword = null;
                }
            } , (error: ErrorResponse) => {
                this.CurrentState = 'NotVerified';
                this.successMessageResetPassword = null;
                this.errorMessageResetPassword = error && error.message ? error.message : "Error resetting password";
            }
        );
    }

    // VerifyToken() {
    //     this.authenticationService.ValidPasswordToken({ resettoken: this.resetToken }).subscribe(
    //         data => {
    //             this.CurrentState = 'Verified';
    //         },
    //         err => {
    //             this.CurrentState = 'NotVerified';
    //         }
    //     );
    // }

    Init() {
        this.ResponseResetForm = this.fb.group(
            {
                resettoken: [this.token],
                newPassword: ['', [Validators.required, Validators.minLength(8)]],
                confirmPassword: ['', [Validators.required, Validators.minLength(8)]]
            }
        );
    }

    Validate(passwordFormGroup: FormGroup) {
        const new_password = passwordFormGroup.controls.newPassword.value;
        const confirm_password = passwordFormGroup.controls.confirmPassword.value;

        if (confirm_password.length <= 0) {
            return null;
        }

        if (confirm_password !== new_password) {
            return {
                doesNotMatch: true
            };
        }

        return null;
    }

    // ResetPassword(form) {
    //     console.log(form.get('confirmPassword'));
    //     if (form.valid) {
    //         this.IsResetFormValid = true;
    //         this.authenticationService.newPassword(this.ResponseResetForm.value).subscribe(
    //             data => {
    //                 this.ResponseResetForm.reset();
    //                 this.successMessage = data.message;
    //                 setTimeout(() => {
    //                     this.successMessage = null;
    //                     this.router.navigate(['sign-in']);
    //                 }, 3000);
    //             },
    //             err => {
    //                 if (err.error.message) {
    //                     this.errorMessage = err.error.message;
    //                 }
    //             }
    //         );
    //     } else { this.IsResetFormValid = false; }
    // }

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