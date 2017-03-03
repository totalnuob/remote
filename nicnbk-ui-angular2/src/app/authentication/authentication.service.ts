import { Injectable } from '@angular/core';
import {Router} from '@angular/router';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import {CommonService} from "../common/common.service";
import {DATA_APP_URL} from "../common/common.service.constants";

export class User {
    constructor(
        public username: string,
        public password: string) { }
}

@Injectable()
export class AuthenticationService extends CommonService{

    constructor(
        private _router: Router,
        private http: Http){
        super();
    }

    private LOGIN_URL = DATA_APP_URL + "authenticate";

    login(user){

        let body = JSON.stringify(user);
        return this.http.post(this.LOGIN_URL, body, this.getOptionsWithCredentials())
        // TODO: parse json with extractData()
            .map(this.extractData)
            .catch(this.handleError);

    }

    checkCredentials(){
        if (localStorage.getItem("authenticatedUser") === null){
            this._router.navigate(['login']);
        }
    }
}

//var users = [
//    new User('nician','VeKMazVa#@!'),
//    new User('birtanov', '123456'),
//];