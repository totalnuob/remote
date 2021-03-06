import {Component, Injectable }     from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot }    from '@angular/router';
import {AuthenticationService} from "./authentication/authentication.service";

@Injectable()


export class AuthGuard implements CanActivate {

    constructor(
        private authenticationService: AuthenticationService
    ){}

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot){
        // authentication
        // this.authenticationService.checkCredentials(state.url);
        // this.authenticationService.checkResetToken(state.root.params)
        // if (this.authenticationService.isValid(state.url)) {
        //     this.authenticationService.checkResetToken(state.root.params.username)
        //     return true;
        // }
        this.authenticationService.checkCredentials(state.url);
        return true;
    }

}