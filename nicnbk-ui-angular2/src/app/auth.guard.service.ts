import { Injectable }     from '@angular/core';
import { CanActivate }    from '@angular/router';
import {AuthenticationService} from "./authentication/authentication.service";

@Injectable()
export class AuthGuard implements CanActivate {

    constructor(
        private authenticationService: AuthenticationService
    ){}

    canActivate() {
        // authentication
        this.authenticationService.checkCredentials();
        return true;
    }
}