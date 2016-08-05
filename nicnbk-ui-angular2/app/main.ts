import { bootstrap }    from '@angular/platform-browser-dynamic';

import { AppComponent } from './app.component';
import { appRouterProviders } from './app.routes';
import { HTTP_PROVIDERS } from '@angular/http';
import {AuthGuard} from "./auth.guard.service";
import {AuthenticationService} from "./authentication/authentication.service";

bootstrap(AppComponent, [
    appRouterProviders,
    HTTP_PROVIDERS,
    AuthGuard,
    AuthenticationService
]);
