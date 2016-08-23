import {Component, ElementRef} from '@angular/core';
import { ROUTER_DIRECTIVES} from '@angular/router';
import {AuthenticationService, User} from './authentication.service'

@Component({
    selector: 'login-form',
    providers: [AuthenticationService],
    directives: [ROUTER_DIRECTIVES],
    templateUrl: './view/login.component.html',
    styleUrls: ['../../../public/css/footer.css']
})

export class LoginComponent {

    public user = new User('','');
    public errorMsg = '';

    constructor(
        private _service: AuthenticationService) {}

    login() {
        if(!this._service.login(this.user)){
            this.errorMsg = 'Failed to login';
        }
    }
}