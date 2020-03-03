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

@Injectable()
export class AuthenticationService extends CommonService{

    constructor(
        private router: Router,
        private http: Http){
        super();
    }

    private LOGIN_URL = DATA_APP_URL + "authenticate";
    private LOGOUT_URL = DATA_APP_URL + "signout";
    //private CHECK_TOKEN_URL = DATA_APP_URL + "checkToken";

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

    logout(){

        return this.http.post(this.LOGOUT_URL, null, this.getOptionsWithCredentials())
            // TODO: parse json with extractData()
            .map(this.extractData)
            .catch(this.handleError);

    }

    //checkToken(){
    //    return this.http.post(this.CHECK_TOKEN_URL, null, this.getOptionsWithCredentials())
    //        .map(this.extractData)
    //        .catch(this.handleError);
    //
    //}

    checkCredentials(state){
        if (localStorage.getItem("authenticatedUser") === null){
            console.log("Check credentials: forward to login");
            this.router.navigate(['/login']);
        }
    }
}

//var users = [
//    new User('nician','VeKMazVa#@!'),
//    new User('birtanov', '123456'),
//];