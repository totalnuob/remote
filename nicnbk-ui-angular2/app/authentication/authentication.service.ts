import { Injectable } from '@angular/core';
import {Router} from '@angular/router';

export class User {
    constructor(
        public username: string,
        public password: string) { }
}

@Injectable()
export class AuthenticationService {

    constructor(
        private _router: Router){}


    login(user){
        var authenticatedUser = users.find(u => u.username === user.username);
        if (authenticatedUser && authenticatedUser.password === user.password){

            // TODO: set user object?
            localStorage.setItem("user", authenticatedUser.username);

            // TODO: navigate to requested url
            this._router.navigate(['/']);

            return true;
        }
        return false;

    }

    checkCredentials(){
        if (localStorage.getItem("user") === null){
            this._router.navigate(['login']);
        }
    }
}

var users = [
    new User('nician','123')
];