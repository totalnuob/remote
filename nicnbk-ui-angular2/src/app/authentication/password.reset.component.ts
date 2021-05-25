import {Component} from "@angular/core";
import {AuthenticationService} from "./authentication.service";
import {RuntimeCompiler} from "@angular/compiler";
import {CommonFormViewComponent} from "../common/common.component";
import {Subscription} from "rxjs";
import {ModuleAccessCheckerService} from "./module.access.checker.service";
import {ErrorResponse} from "../common/error-response";
import {Router, ActivatedRoute} from '@angular/router';

@Component({
    selector: 'password-reset',
    providers: [AuthenticationService, RuntimeCompiler],
    templateUrl: './view/password.reset.component.html',
    styleUrls: ['../../../public/css/footer.css']
})

export class PasswordResetComponent extends CommonFormViewComponent {

    busy: Subscription;
    private sub: any;

    emailAddress = '';
    successMessageResetPassword: String;
    errorMessageResetPassword: String;
    isRequested: boolean;

    constructor(
        private router : Router,
        private route: ActivatedRoute,
        private authenticationService: AuthenticationService
    ) {
        super(router);
    }

    reset() {
        this.authenticationService.reset(this.emailAddress)
            .subscribe(
                response => {
                    if (response == true){
                        this.isRequested = true;
                        this.successMessageResetPassword = "Request to reset password was send successfully. Please, check your email for password reset link"
                    }else{
                        if (response == null) {
                            this.isRequested = false;
                            this.errorMessageResetPassword = "Request to reset password failed, unauthorized attempt or no such user exists in the system"
                        }
                    }
                },
                (error: ErrorResponse) => {
                    this.isRequested = false;
                    this.errorMessageResetPassword = "Request to reset password failed, unauthorized attempt or no such user exists in the system"
                }
            );
    }

}