import { Component, NgModule, OnInit } from '@angular/core';
import { } from '@angular/common';

import { Router, NavigationEnd } from '@angular/router';
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
import {LegalService} from "./legal/legal.service";
import {MonitoringHedgeFundService} from "./monitoring/monitoring-hf.service";
import {Subscription} from "../../node_modules/rxjs";
import {VersionService} from "./common/version.service";
import {NotificationService} from "./notification/notification.service";
import {Version} from "./version/model/version";


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
        HRService,
        LegalService,
        HedgeFundScoringService,
        MonitoringHedgeFundService,
        VersionService,
        NotificationService
    ]
})
@NgModule({
    imports: [TextareaAutosize]
})
export class AppComponent implements OnInit{

    versionLatestVersion;
    versionList: Array<Version> = [];
    busy: Subscription;
    notificationsBusy: Subscription;
    activeMenu;
    activeBlock = 'corp';
    //snow = true;

    authenticatedUserNotifications = [];
    pauseNotifications = false;

    private moduleAccessChecker: ModuleAccessCheckerService;

    constructor(
        private authenticationService: AuthenticationService,
        private _router: Router,
        private notificationService: NotificationService,
        private versionService: VersionService,
    ){
        this.moduleAccessChecker = new ModuleAccessCheckerService;
        this.getVersion();

        this.loadUserNotification();

/*
// TODO: prepend component urls woth 'corp', 'invest', 'admin'
        this._router.events.subscribe((e) => {
          if (e instanceof NavigationEnd) {
            //console.log(e.url);
            this.initMenu(e.url);
          }
        });
*/
    }

    ngOnInit(){
        //setInterval(this.loadUserNotification(), 60000, "Load notifications every minute");
        setInterval(()=> { this.loadUserNotification() }, 60000);

        // Active Block
        var activeBlockSelected = localStorage.getItem("activeBlock");
        if(activeBlockSelected){
            this.activeBlock = activeBlockSelected;
        }else{
            this.activeBlock ='corp';
        }

        // Active Menu
        var activeMenuSelected = localStorage.getItem("activeMenu");
        if(activeMenuSelected){
            this.activeMenu = activeMenuSelected;
        }
    }

    initMenu(url){
        this.activeBlock = 'corp';
        if(url.startsWith('/invest')){
            this.activeBlock = 'invest';
        }else if(url.startsWith('/admin')){
            this.activeBlock = 'admin';
        }
    }

    loadUserNotification(){
        if(!this.pauseNotifications){
            this.busy = this.notificationService.getUserNotifications()
                .subscribe(
                    (response) => {
                        //console.log(response);
                        this.authenticatedUserNotifications = response;
                        localStorage.setItem("notificationErrorResponse", 0);
                    },
                    error => {
                        if(error && error.status == 401){
                            var notificationErrorResponse = localStorage.getItem("notificationErrorResponse");
                            if(notificationErrorResponse){
                                var count = Number(notificationErrorResponse);
                                if(count < 3){
                                    localStorage.setItem("notificationErrorResponse", (count + 1));
                                }else{
                                    this.pauseNotifications = true;
                                }
                            }else{
                                localStorage.setItem("notificationErrorResponse", 1);
                            }
                        }
                        console.log('Error loading user notifications');
                    }
                )
        }
    }

    getVersion() {
        this.busy = this.versionService.getVersion()
            .subscribe(
                (response) => {
                    this.versionList = this.addNumeration(response.versionDtoList);

                    for (var i = 0; i < this.versionList.length; i++) {
                        this.versionList[i].collapsed = (i != 0);
                    }

                    //console.log(this.versionList);

                    if(this.versionList.length > 0) {
                        var a = this.versionList[0].description[0].split(" ");
                        this.versionLatestVersion = a[a.length - 1];
                        //console.log('Version: ' + this.versionLatestVersion);
                    } else {
                        this.versionLatestVersion = '0.0.0';
                        console.log('Get version: Error');
                    }
                },
                error => {
                    this.versionLatestVersion = '0.0.0';
                    console.log('Get version: Error');
                }
            )
    }

    toggleVersion(i) {
        this.versionList[i].collapsed = !this.versionList[i].collapsed;
    }

    addNumeration(versionList: Array<Version>) {
        for (let version of versionList) {
            var numeration = 0;
            for (var i = 0; i < version.description.length; i++) {
                if (version.numFmt[i] == null) {
                    numeration = 0;
                } else if (version.numFmt[i] == "decimal") {
                    // version.numFmt[i] = (++numeration).toString() + '.';
                    version.description[i] = (++numeration) + ". " + version.description[i];
                } else if (version.numFmt[i] == "bullet") {
                    // version.numFmt[i] = "\u2022";
                        version.description[i] = "\u2022 " + version.description[i];
                }
            }
        }
        return versionList;
    }

    logout() {
        console.log("logging out...");
        localStorage.removeItem("activeBlock");
        localStorage.removeItem("activeMenu");
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

//    showSnow(){
//        return this.snow;
//   }

//    changeSnow(){
//        this.snow = !this.snow;
//    }

    checkAvailable(menu){
        if(menu === "HR"){
            return this.activeBlock === 'corp';
        }else if(menu === "TRAININGS"){
            return this.activeBlock === 'corp';
        }else if(menu === "MONITORING"){
            return this.activeBlock === 'invest' && this.showMonitoring();
        }else if(menu === "MACROMONITOR"){
            return this.activeBlock === 'invest' && this.showMacromonitor();
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
            return this.activeBlock === 'invest' && this.showLookups();
        }else if(menu === "ADMIN"){
            return this.activeBlock === 'admin' && this.showAdmin();
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

    showAdmin(){
        return this.moduleAccessChecker.checkAccessAdmin();
    }

    getCurrentEmployeeId(){
        return localStorage.getItem('authenticatedUser');
    }

    getHomePageURLByType(type){
        if(type === 'corp'){
            return 'hr/news';
        }else if(type === 'invest'){
            return "news";
        }else if(type === 'admin'){
            return "admin/main";
        }
        return 'hr/news';
    }
    setActiveBlock(selected){
        localStorage.setItem("activeBlock", selected);
        this.activeBlock = selected;
        if(this.activeBlock === 'invest'){
            this.activeMenu = 'NEWS';
        }else if(this.activeBlock === 'corp'){
            this.activeMenu = 'HR_NEWS';
        }else if(this.activeBlock === 'admin'){
            this.activeMenu = 'USER_MANAGEMENT';
        }
    }
    setActiveMenu(selected){
        localStorage.setItem("activeMenu", selected);
        this.activeMenu = selected;
    }

    showNotifications(){
        //alert("Danger!");
    }

    getNotifications(){
        //return [];
        //return [{'id': 1, 'name': 'Notification 1', 'status': 'NEW'}];
        return this.authenticatedUserNotifications;
    }

    closeNotification(notification){
        if(notification != null && notification.id > 0){
            this.notificationsBusy = this.notificationService.closeUserNotification(notification.id)
                .subscribe(
                    (response) => {
                        for(var i = 0; this.authenticatedUserNotifications != null && i < this.authenticatedUserNotifications.length; i++) {
                            if(this.authenticatedUserNotifications[i] === notification) {
                                this.authenticatedUserNotifications.splice(i, 1);
                            }
                        }
                    },
                    error => {
                        console.log(response);
                        console.log('Error closing user notification');
                    }
                )
        }
    }

    closeNotificationsModal(){
    }

    closeAllNotifications(){
        this.notificationsBusy = this.notificationService.closeUserNotificationsAll()
            .subscribe(
                (response) => {
                    this.authenticatedUserNotifications = [];
                },
                error => {
                    console.log(response);
                    console.log('Error closing user notifications (ALL)');
                }
            )
    }
}

