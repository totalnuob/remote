import { Component, NgModule } from '@angular/core';
import { } from '@angular/common';

import { Router } from '@angular/router';
import {} from '@angular/http';

import './rxjs-operators';

import {NewsService} from "./news/news.service";
import {LookupService} from "./common/lookup.service";
import {EmployeeService} from "./employee/employee.service";
import {MemoService} from "./m2s2/memo.service";
import {TripMemoService} from "./tripMemo/trip-memo.service";

import {AuthGuard} from "./auth.guard.service";
import {FileUploadService} from "./upload/file.upload.service";

// TODO: move per component?
// TODO: importing in component makes available for other components
import '../../public/css/ng2-select.css';
import '../../public/js/chart.min.js';
import '../../public/js/google-charts/loader.js';
import '../../public/js/bootstrap-datetimepicker.min.js';
import '../../public/js/viz_v1.js';

import {TextareaAutosize} from "./common/textarea-autosize.directive";
import {RiskManagementReportService} from "./riskmanagement/riskmanagement.report.service";

//import '../../public/js/jquery.ns-autogrow.min.js';

@Component({
    selector: 'app-main',
    templateUrl: './app.component.html',
    styleUrls: [
        '../../public/css/header.css', '../../public/css/footer.css',
        '../../public/css/common.css'
    ],
    providers: [
        //FORM_PROVIDERS, // fixes 'No provider for RadioControlRegistry!'
        //HTTP_PROVIDERS,
        AuthGuard,
        MemoService,
        TripMemoService,
        NewsService,
        LookupService,
        EmployeeService,
        FileUploadService,
        RiskManagementReportService
    ]
})
@NgModule({
    imports: [TextareaAutosize]
})
export class AppComponent {

    constructor(
        private _router: Router
    ){}

    logout() {
        localStorage.removeItem("user");
        this._router.navigate(['login']);
    }

    // TODO: refactor
    showMenu(){
        return localStorage.getItem("user") != null;
    }
}

