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
                    if (response.status === 'SUCCESS'){
                        this.successMessageResetPassword = response.message.nameEn;
                        this.errorMessageResetPassword = null;
                    }else{
                        if(response.message != null){
                            var message = response.message.nameEn != null ? response.message.nameEn :
                                response.message.nameRu != null ? response.message.nameRu : response.message.nameKz;
                            if(message != null && message != ''){
                                this.postAction(null, message);
                            }else{
                                this.postAction(null, "Error resetting password");
                            }
                        }
                    }
                },
                (error: ErrorResponse) => {
                    this.successMessageResetPassword = null;
                    this.errorMessageResetPassword = error && error.message ? error.message : "Error resetting password";
                    //this.processErrorMessage(error);
                }
            );
    }

}