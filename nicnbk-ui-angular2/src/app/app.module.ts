import { NgModule, CUSTOM_ELEMENTS_SCHEMA, Type } from '@angular/core';
import { BrowserModule }  from '@angular/platform-browser';
import { LocationStrategy, HashLocationStrategy} from '@angular/common';
import { HttpModule } from '@angular/http';
import { FormsModule } from '@angular/forms';
import { COMPILER_PROVIDERS } from "@angular/compiler";

import {SelectModule} from 'ng2-select';
//import { TagInputModule } from 'ng2-tag-input';

import { appRouterProviders } from './app.routes';
import {AuthGuard} from "./auth.guard.service";
import {AuthenticationService} from "./authentication/authentication.service";

import { AppComponent } from './app.component';
import {LoginComponent} from "./authentication/login.component";
import {NewsListComponent} from "./news/news-list.component";
import {NewsEditComponent} from "./news/news-edit.component";
import {NewsViewComponent} from "./news/news-view.component";
import {MemoListComponent} from "./m2s2/memo-list.component";
import {GeneralMemoEditComponent} from "./m2s2/general-memo-edit.component";
import {PrivateEquityMemoEditComponent} from "./m2s2/pe-memo-edit.component";
import {HedgeFundsMemoEditComponent} from "./m2s2/hf-memo-edit.component";
import {RealEstateMemoEditComponent} from "./m2s2/re-memo-edit.component";
import {TripMemoListComponent} from "./tripMemo/trip-memo-list.component";
import {TripMemoEditComponent} from "./tripMemo/trip-memo-edit.component";
import {MemoAttachmentDownloaderComponent} from "./m2s2/memo-attachment-downloader.component";

import {MonitoringPortfolioComponent} from "./monitoring/monitoring-portfolio.component";
import {MonitoringHedgeFundsComponent} from "./monitoring/monitoring-hedge-funds.component";
import {MonitoringLiquidPortfolioComponent} from "./monitoring/monitoring-liquid-portfolio.component";
import {MonitoringPrivateEquityComponent} from "./monitoring/monitoring-private-equity.component";
import {MonitoringMacroMonitorComponent} from "./monitoring/monitoring-macro-monitor.component";
import {RiskManagementAxiomaReportingComponent} from "./riskmanagement/riskmanagement-axioma-reporting.component";
import {HFDashboardComponent} from "./hf/hf.dashboard.component";
import {HFFundSearchComponent} from "./hf/hf.fund-search.component";
import {HFFundSelectionComponent} from "./hf/hf.fund-selection.component";
import {HFPortfolioComponent} from "./hf/hf.portfolio.component";
import {HFReportComponent} from "./hf/hf.report.component";
import {HFFundProfileComponent} from "./hf/hf.fund-profile.component";
import {HFManagerProfileComponent} from "./hf/hf.manager-profile.component.ts";
import {PEFundProfileComponent} from "./pe/pe.fund-profile.component";
import {PEFirmProfileComponent} from "./pe/pe.firm-profile.component";
import {PEFirmSearchComponent} from "./pe/pe.firm-search.component";
import {PEFundReportComponent} from "./pe/pe.fund-report.component";
import {MonitoringPrivateEquityFundComponent} from "./monitoring/monitoring-private-equity-fund.component";
import {MonitoringHedgeFunds2Component} from "./monitoring/monitoring-hedge-funds-2.component";
import {BusyModule} from "angular2-busy/index"
import {HFManagerSearchComponent} from "./hf/hf.manager-search.component";
import {AccessDeniedComponent} from "./access.denied.component";
import {EmployeeProfileComponent} from "./employee/employee.profile.component";

@NgModule({
    imports: [
        BrowserModule,
        FormsModule, HttpModule,
        SelectModule,
        //TagInputModule,
        appRouterProviders,
        BusyModule
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA], // for custom elements like ng-select
    providers: [
        {provide: LocationStrategy, useClass: HashLocationStrategy}, // make pages available by URL on apache
        AuthGuard,
        AuthenticationService,
        COMPILER_PROVIDERS
        ],
    declarations: [
        AppComponent,
        LoginComponent, EmployeeProfileComponent,
        NewsListComponent, NewsEditComponent, NewsViewComponent,
        MemoListComponent,
        MemoAttachmentDownloaderComponent,
        GeneralMemoEditComponent, PrivateEquityMemoEditComponent, HedgeFundsMemoEditComponent, RealEstateMemoEditComponent,
        TripMemoListComponent, TripMemoEditComponent, RiskManagementAxiomaReportingComponent, MonitoringHedgeFundsComponent, MonitoringLiquidPortfolioComponent,
        MonitoringMacroMonitorComponent, MonitoringPortfolioComponent, MonitoringPrivateEquityComponent, MonitoringPrivateEquityFundComponent,
        MonitoringHedgeFunds2Component,

        HFDashboardComponent, HFFundSearchComponent, HFFundSelectionComponent, HFPortfolioComponent, HFReportComponent,
        HFFundProfileComponent, HFManagerProfileComponent, HFManagerSearchComponent,
        PEFundProfileComponent, PEFirmProfileComponent, PEFirmSearchComponent, PEFundReportComponent,
        AccessDeniedComponent
    ],
    bootstrap: [ AppComponent ]
})
export class AppModule extends Type { }
