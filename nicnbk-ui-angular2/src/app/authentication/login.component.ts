import {Component, ElementRef, Compiler, OnInit  } from '@angular/core';
import { RuntimeCompiler} from '@angular/compiler';
import {Router, ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs';
import {AuthenticationService, User} from './authentication.service'
import lastIndexOf = require("core-js/fn/array/last-index-of");

@Component({
    selector: 'login-form',
    providers: [AuthenticationService, RuntimeCompiler],
    templateUrl: './view/login.component.html',
    styleUrls: ['../../../public/css/footer.css']
})

export class LoginComponent implements OnInit{
    ngOnInit():void {
        if(localStorage.getItem("authenticatedUser") != null){
            this.router.navigate(['/']);
        }else{

        }
    }

    public user = new User('','', '');
    public errorMsg = '';

    //busy: Subscription;

    constructor(
        private router: Router,
        //private route: ActivatedRoute,
        private authenticationService: AuthenticationService) {
    }

    //ngOnInit() {
    //    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
    //
    //    if(this.returnUrl != '/') {
    //        this.url = this.returnUrl.substring(0, this.returnUrl.indexOf(';'));
    //        if(this.url == '/news') {
    //            this.params = this.returnUrl.substring(this.returnUrl.indexOf('=') + 1);
    //        }
    //    } else {
    //        this.url = this.returnUrl;
    //    }
    //}

    login() {
        this.authenticationService.login(this.user)
            .subscribe(
                response => {
                    //this.runtimeCompiler.clearCache();

                    localStorage.setItem("authenticatedUserPosition",  JSON.stringify(response.position));
                    localStorage.setItem("authenticatedUser", this.user.username);
                    localStorage.setItem("authenticatedUserRoles", JSON.stringify(response.roles));

                    localStorage.removeItem("activeBlock");
                    localStorage.removeItem("activeMenu");

                    location.reload();
                },
                error =>  {
                    this.errorMsg = 'Failed to login! (Account will be blocked after three unsuccessful attempts)';
                }
            );
    }
}