import {Component, ElementRef, Compiler } from '@angular/core';
import { RuntimeCompiler} from '@angular/compiler';
import {Router} from '@angular/router';
import {AuthenticationService, User} from './authentication.service'

@Component({
    selector: 'login-form',
    providers: [AuthenticationService, RuntimeCompiler],
    templateUrl: './view/login.component.html',
    styleUrls: ['../../../public/css/footer.css']
})

export class LoginComponent {

    public user = new User('','');
    public errorMsg = '';

    constructor(
        private router: Router,
        private authenticationService: AuthenticationService) {}

    login() {
        this.authenticationService.login(this.user)
            .subscribe(
                response => {

                    //this.runtimeCompiler.clearCache();

                    localStorage.setItem("authenticatedUser", this.user.username);
                    localStorage.setItem("authenticatedUserRoles", JSON.stringify(response.roles));

                    location.reload();

                    this.router.navigate(['/']);
                },
                error =>  {
                    this.errorMsg = 'Failed to login';
                }
            );
    }
}