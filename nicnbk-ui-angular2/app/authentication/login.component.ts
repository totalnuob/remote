import {Component, ElementRef} from '@angular/core';
import {AuthenticationService, User} from './authentication.service'

@Component({
    selector: 'login-form',
    providers: [AuthenticationService],
    templateUrl: `app/authentication/view/login.component.html`,
    styleUrls: ['app/static/css/footer.css']
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