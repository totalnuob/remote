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
import '../../public/css/bootstrap/bootstrap-datetimepicker.min.css';
import '../../public/js/viz_v1.js';

import {TextareaAutosize} from "./common/textarea-autosize.directive";
import {HFManagerService} from "./hf/hf.manager.service";
import {HedgeFundService} from "./hf/hf.fund.service";
import {RiskManagementReportService} from "./riskmanagement/riskmanagement.report.service";
import {AlbourneService} from "./hf/hf.albourne.service";

import '../../public/js/jquery.ns-autogrow.min.js';
import {ModuleAccessCheckerService} from "./authentication/module.access.checker.service";
import {PeriodicReportService} from "./reporting/periodic.report.service";

@Component({
    selector: 'app-main',
    templateUrl: './app.component.html',
    styleUrls: [
        '../../public/css/header.css', '../../public/css/footer.css',
        '../../public/css/common.css','../../public/css/forms.css',
        '../../node_modules/angular2-busy/build/style/busy.css'
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
        HFManagerService,
        HedgeFundService,
        RiskManagementReportService,
        AlbourneService,
        ModuleAccessCheckerService,
        PeriodicReportService
    ]
})
@NgModule({
    imports: [TextareaAutosize]
})
export class AppComponent {

    private moduleAccessChecker: ModuleAccessCheckerService;

    constructor(
        private _router: Router
    ){
        this.moduleAccessChecker = new ModuleAccessCheckerService;
    }

    logout() {
        localStorage.removeItem("authenticatedUser");
        localStorage.removeItem("authenticatedUserRoles");
        //location.reload();
        this._router.navigate(['login']);
    }

    // TODO: refactor
    showMenu(){
        return localStorage.getItem("authenticatedUser") != null;
    }

    getUsername(){
        return localStorage.getItem("authenticatedUser") != null ? "," + localStorage.getItem("authenticatedUser") : "";
    }

    showPrivateEquity(){
        return this.moduleAccessChecker.checkAccessPrivateEquity();
    }
    showPrivateEquityEditor(){
        return this.moduleAccessChecker.checkAccessPrivateEquityEditor();
    }

    showHedgeFunds(){
        return this.moduleAccessChecker.checkAccessHedgeFunds();
    }

    showHedgeFundsEditor(){
        return this.moduleAccessChecker.checkAccessHedgeFundsEditor();
    }

    showRealEstate(){
        //return false;
        return this.moduleAccessChecker.checkAccessReporting();
    }

    showReporting(){
        //return false;
        return this.moduleAccessChecker.checkAccessReporting();
    }
}

