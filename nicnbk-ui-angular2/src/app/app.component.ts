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
import {AuthenticationService} from "./authentication/authentication.service";
import {MacroMonitorService} from "./macromonitor/macromonitor.service";
import {FileDownloadService} from "./common/file.download.service";
import {CorpMeetingService} from "./corpmeetings/corp-meetings.service";
import {HedgeFundScreeningService} from "./hf/hf.fund.screening.service";
import {HedgeFundScoringService} from "./hf/hf.fund.scoring.service";
import {MonitoringHedgeFundService} from "./monitoring/monitoring-hf.service";



@Component({
    selector: 'app-main',
    templateUrl: './app.component.html',
    styleUrls: [
        '../../public/css/header.css', '../../public/css/footer.css',
        '../../public/css/common.css','../../public/css/forms.css',
        '../../node_modules/angular2-busy/build/style/busy.css',
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
        FileDownloadService,
        HFManagerService,
        HedgeFundService,
        RiskManagementReportService,
        AlbourneService,
        ModuleAccessCheckerService,
        MacroMonitorService,
        PeriodicReportService,
        CorpMeetingService,
        HedgeFundScreeningService,
        HedgeFundScoringService,
        MonitoringHedgeFundService
    ]
})
@NgModule({
    imports: [TextareaAutosize]
})
export class AppComponent {

    activeMenu;

    private moduleAccessChecker: ModuleAccessCheckerService;

    constructor(
        private authenticationService: AuthenticationService,
        private _router: Router
    ){
        this.moduleAccessChecker = new ModuleAccessCheckerService;
    }

    logout() {
        console.log("logging out...");
        this.authenticationService.logout()
            .subscribe(
                res => {
                    localStorage.removeItem("authenticatedUser");
                    localStorage.removeItem("authenticatedUserRoles");
                    //location.reload();
                    this._router.navigate(['login']);

                },
                error =>  {
                    console.log("Logout request error");
                    // TODO: error message

                    //this.errorMsg = 'Failed to login';
                }
            );
    }

    // TODO: refactor
    showMenu(){
        return localStorage.getItem("authenticatedUser") != null;
    }

    showTrainings(){
        return true;
        //return !this.moduleAccessChecker.checkAccessMemoRestricted();
    }

    showM2S2(){
        return true;
        //return !this.moduleAccessChecker.checkAccessMemoRestricted();
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

    showCorpMeeting(){
        return this.moduleAccessChecker.checkAccessCorpMeetingsView();
    }
}

