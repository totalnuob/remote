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

    textInput: String;
    successMessageIsRequested: String;
    errorMessageIsRequested: String;
    errorMessageIsValidEmail: String;
    isRequested: boolean;
    isValidEmail: boolean;

    constructor(
        private router : Router,
        private route: ActivatedRoute,
        private authenticationService: AuthenticationService
    ) {
        super(router);
    }

    reset() {
        this.isValidEmail = this.validateEmail(this.fullEmail(this.textInput));
        if (this.isValidEmail) {
            this.authenticationService.reset(this.fullEmail(this.textInput))
                .subscribe(
                    response => {
                        if (response == true){
                            this.isRequested = true;
                            this.successMessageIsRequested = "Request to reset password was send successfully. Please, check your email for password reset link"
                        }else{
                            if (response == null) {
                                this.isRequested = false;
                                this.errorMessageIsRequested = "Request to reset password failed, unauthorized attempt or no such user exists in the system"
                            }
                        }
                    },
                    (error: ErrorResponse) => {
                        this.isRequested = false;
                        this.errorMessageIsRequested = "Request to reset password failed, unauthorized attempt or no such user exists in the system"
                    }
                );
        } else {
            this.isValidEmail = false;
            this.errorMessageIsValidEmail = "Given email is not valid"
        }
    }

    validateEmail(email) {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email) && email.endsWith("@nicnbk.kz");
    }

    fullEmail(email) {
        email = email.concat("@nicnbk.kz")
        return email;
    }

}