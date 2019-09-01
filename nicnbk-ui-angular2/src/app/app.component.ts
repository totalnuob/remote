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
import {HRService} from "./hr/hr.service";



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
        HRService
    ]
})
@NgModule({
    imports: [TextareaAutosize]
})
export class AppComponent {

    activeMenu;

    activeBlock = 'corp';

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

    checkAvailable(menu){
        if(menu === "HR"){
            return this.activeBlock === 'corp';
        }else if(menu === "TRAININGS"){
            return this.activeBlock === 'corp';
        }else if(menu === "MONITORING"){
            return this.activeBlock === 'invest' && this.showMonitoring();
        }else if(menu === "MACROMONITOR"){
            return this.activeBlock === 'invest' && this.showMacromonitor();;
        }else if(menu === "M2S2"){
            return this.activeBlock === 'invest' && this.showM2S2();
        }else if(menu === "PRIVATE_EQUITY"){
            return this.activeBlock === 'invest' && this.showPrivateEquity();
        }else if(menu === "HEDGE_FUNDS"){
            return this.activeBlock === 'invest' && this.showHedgeFunds();
        }else if(menu === "REPORTING"){
            return this.activeBlock === 'invest' && this.showReporting();
        }else if(menu === "CORP_MEETINGS"){
            return this.activeBlock === 'invest' && this.showCorpMeeting();
        }else if(menu === "LOOKUPS"){
            return this.activeBlock === 'invest' && this.showLookups();;
        }
    }


    getUsername(){
        return localStorage.getItem("authenticatedUser") != null ? "," + localStorage.getItem("authenticatedUser") : "";
    }

    showM2S2(){
        return this.moduleAccessChecker.checkAccessM2S2();
    }

    showInvestNews(){
        return this.moduleAccessChecker.checkAccessNews();
    }

    showMonitoring(){
        return this.moduleAccessChecker.checkAccessMonitoring();
    }

    showMacromonitor(){
        return this.moduleAccessChecker.checkAccessMacroMonitor();
    }

    showLookups(){
        return this.moduleAccessChecker.checkAccessLookups();
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

    getCurrentEmployeeId(){
        return localStorage.getItem('authenticatedUser');
    }

    getHomePageURLByType(type){
        if(type === 'corp'){
            return 'hr/news';
        }else if(type === 'invest'){
            return "news";
        }
        return null;
    }
}

