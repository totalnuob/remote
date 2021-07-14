import {Component, Injectable } from '@angular/core';
import {Router} from '@angular/router';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import {CommonService} from "../common/common.service";
import {DATA_APP_URL} from "../common/common.service.constants";

export class User {
    constructor(
        public username: string,
        public password: string,
        public otp: string) { }
}

export class UserToken {
    constructor(
        public  username: string,
        public token: string) {}
}

export class UserPassword {
    constructor(
        public username: string,
        public newPassword: string,
        public token: string) {}
}

@Injectable()
export class AuthenticationService extends CommonService{

    constructor(
        private router: Router,
        private http: Http){
        super();
    }

    private LOGIN_URL = DATA_APP_URL + "authenticate";
    private LOGOUT_URL = DATA_APP_URL + "signout";
    private RESET_URL = DATA_APP_URL + "requestReset";
    private CHANGE_PASSWORD_URL = DATA_APP_URL + "changePassword";
    private CONFIRM_RESET_URL = DATA_APP_URL + "confirmReset";

    login(user){

        let body = JSON.stringify(user);
        return this.http.post(this.LOGIN_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleError);

    }

    logout(){
        return this.http.post(this.LOGOUT_URL, null, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleError);

    }

    reset(email){
        let body = JSON.stringify(email);
        return this.http.post(this.RESET_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleError);
    }

    validateToken(userToken) {
        let body = JSON.stringify(userToken);
        return this.http.post(this.CONFIRM_RESET_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleError);
    }

    changePassword(userPassword) {
        let body = JSON.stringify(userPassword);
        return this.http.post(this.CHANGE_PASSWORD_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleError);
    }

    // logout(){
    //
    //     return this.http.post(this.LOGOUT_URL, null, this.getOptionsWithCredentials())
    //         // TODO: parse json with extractData()
    //         .map(this.extractData)
    //         .catch(this.handleError);
    //
    // }

    checkCredentials(state){
        if (localStorage.getItem("authenticatedUser") === null){
            console.log("Check credentials: forward to login");
            this.router.navigate(['/login']);
        }
    }

    validateResetToken(isValid){
        if (isValid) {
            console.log(isValid);
            this.router.navigate(['/confirmReset']);
        }
    }

    isValid(url){
        let body = JSON.stringify(url);
        console.log(body);
        return this.http.post(this.CONFIRM_RESET_URL, body, this.getOptionsWithCredentials())
            .map(this.extractData)
            .catch(this.handleError);
    }
}

//var users = [
//    new User('nician','VeKMazVa#@!'),
//    new User('birtanov', '123456'),
//];