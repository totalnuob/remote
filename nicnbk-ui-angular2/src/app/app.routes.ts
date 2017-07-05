//import { provideRouter, RouterConfig }  from '@angular/router';
import { Routes, RouterModule } from '@angular/router';

import {NewsListComponent} from './news/news-list.component';
import {NewsEditComponent} from "./news/news-edit.component";
import {NewsViewComponent} from "./news/news-view.component";

import {MemoListComponent} from "./m2s2/memo-list.component";
import {PrivateEquityMemoEditComponent} from "./m2s2/pe-memo-edit.component";
import {HedgeFundsMemoEditComponent} from "./m2s2/hf-memo-edit.component";
import {RealEstateMemoEditComponent} from "./m2s2/re-memo-edit.component";
import {GeneralMemoEditComponent} from "./m2s2/general-memo-edit.component";

import {TripMemoListComponent} from "./tripMemo/trip-memo-list.component";
import {TripMemoEditComponent} from "./tripMemo/trip-memo-edit.component";
import {MonitoringPortfolioComponent} from "./monitoring/monitoring-portfolio.component";
import {MonitoringHedgeFundsComponent} from "./monitoring/monitoring-hedge-funds.component";
import {MonitoringLiquidPortfolioComponent} from "./monitoring/monitoring-liquid-portfolio.component";
import {MonitoringPrivateEquityComponent} from "./monitoring/monitoring-private-equity.component";
import {MonitoringMacroMonitorComponent} from "./monitoring/monitoring-macro-monitor.component";
import {RiskManagementAxiomaReportingComponent} from "./riskmanagement/riskmanagement-axioma-reporting.component";

import {LoginComponent} from "./authentication/login.component";

import {AuthGuard} from "./auth.guard.service";
import {HFDashboardComponent} from "./hf/hf.dashboard.component";
import {HFFundSearchComponent} from "./hf/hf.fund-search.component";
import {HFFundSelectionComponent} from "./hf/hf.fund-selection.component";
import {HFPortfolioComponent} from "./hf/hf.portfolio.component";
import {HFReportComponent} from "./hf/hf.report.component";
import {HFFundProfileComponent} from "./hf/hf.fund-profile.component";
import {HFManagerProfileComponent} from "./hf/hf.manager-profile.component.ts";
import {MonitoringPrivateEquityFundComponent} from "./monitoring/monitoring-private-equity-fund.component";
import {MonitoringHedgeFunds2Component} from "./monitoring/monitoring-hedge-funds-2.component";
import {PEFundProfileComponent} from "./pe/pe.fund-profile.component";
import {PEFirmProfileComponent} from "./pe/pe.firm-profile.component";
import {PEFirmSearchComponent} from "./pe/pe.firm-search.component";
import {PEFundReportComponent} from "./pe/pe.fund-report.component";
import {HFManagerSearchComponent} from "./hf/hf.manager-search.component";
import {AccessDeniedComponent} from "./access.denied.component";
import {EmployeeProfileComponent} from "./employee/employee.profile.component";
import {NotFoundComponent} from "./page-not-found.component";
import {MainNBReportingComponent} from "./reporting/main.nb.reporting.component";
import {InputFileUploadNBReportingComponent} from "./reporting/input.file.upload.nb.reporting.component";
import {ScheduleInvestmentsNBReportingComponent} from "./reporting/schedule.investments.nb.reporting.component";
import {StatementBalanceOperationsNBReportingComponent} from "./reporting/statement.balance.operations.nb.reporting.component";


const routes: Routes  = [
    {
        path: '',
        redirectTo: '/news',
        pathMatch: 'full'
    },
    /* LOGIN ***************************************/
    {
        path: 'login',
        component: LoginComponent
    },
    /* USER PROFILE ***************************************/
    {
        path: 'profile',
        component: EmployeeProfileComponent,
        canActivate: [AuthGuard]
    },

    /* ACCESS DENIED *******************************/
    {
        path: 'accessDenied',
        component: AccessDeniedComponent
    },
    /* NEWS ***************************************/
    {
        path: 'news',
        component: NewsListComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'news/edit/:id',
        component: NewsEditComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'news/view/:id',
        component: NewsViewComponent,
        canActivate: [AuthGuard]
    },

    /* M2S2 ****************************************/
    {
        path: 'm2s2',
        component: MemoListComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'm2s2/edit/1/:id',
        component: GeneralMemoEditComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'm2s2/edit/2/:id',
        component: PrivateEquityMemoEditComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'm2s2/edit/3/:id',
        component: HedgeFundsMemoEditComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'm2s2/edit/4/:id',
        component: RealEstateMemoEditComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'm2s2/edit/1/:id',
        component: GeneralMemoEditComponent,
        canActivate: [AuthGuard]
    },

    /* TripMemo *************************************/
    {
        path: 'bt',
        component: TripMemoListComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'bt/edit',
        component: TripMemoEditComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'bt/edit/:id',
        component: TripMemoEditComponent,
        canActivate: [AuthGuard]
    },

    /* Monitoring ************************************/
    {
        path: 'monitoring/portfolio',
        component: MonitoringPortfolioComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'monitoring/hf',
        component: MonitoringHedgeFundsComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'monitoring/liq',
        component: MonitoringLiquidPortfolioComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'monitoring/pe',
        component: MonitoringPrivateEquityComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'monitoring/mm',
        component: MonitoringMacroMonitorComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'monitoring/axioma',
        component: RiskManagementAxiomaReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'monitoring/peFund',
        component: MonitoringPrivateEquityFundComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'monitoring/hf2',
        component: MonitoringHedgeFunds2Component,
        canActivate: [AuthGuard]
    },
    /* Hedge Funds ************************************/
    {
        path: 'hf/dashboard',
        component: HFDashboardComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'hf/fundSearch',
        component: HFFundSearchComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'hf/managerSearch',
        component: HFManagerSearchComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'hf/fundSelection',
        component: HFFundSelectionComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'hf/portfolio',
        component: HFPortfolioComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'hf/report',
        component: HFReportComponent,
        canActivate: [AuthGuard]
    },
    {
        // existing fund
        path: 'hf/fundProfile/:id',
        component: HFFundProfileComponent,
        canActivate: [AuthGuard]
    },
    {
        // new fund
        path: 'hf/fundProfile/:id/:managerId',
        component: HFFundProfileComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'hf/managerProfile/:id',
        component: HFManagerProfileComponent,
        canActivate: [AuthGuard]
    },
    /* Private Equity ************************************/
    {
        path: 'pe/fundProfile',
        component: PEFundProfileComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'pe/firmProfile',
        component: PEFirmProfileComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'pe/firmProfile/:id',
        component: PEFirmProfileComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'pe/fundProfile/:id/:firmId',
        component: PEFundProfileComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'pe/firmSearch',
        component: PEFirmSearchComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'pe/fundReport/:id',
        component: PEFundReportComponent,
        canActivate: [AuthGuard]
    },
    /* Reporting ************************************/
    {
        path: 'reporting/NBReporting',
        component: MainNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/inputFileUpload/:id',
        component: InputFileUploadNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/pe/scheduleInvestments/:id',
        component: ScheduleInvestmentsNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/pe/statementBalanceOperations/:id',
        component: StatementBalanceOperationsNBReportingComponent,
        canActivate: [AuthGuard]
    },

    /* Page not found. ERROR 404 *********************/
    {
        path: '404',
        component: NotFoundComponent,
        canActivate: [AuthGuard]
    },
    {
        path: '**',
        redirectTo: '/404'
    },

];

export const appRouterProviders = RouterModule.forRoot(routes);

//export const appRouterProviders = [
//    provideRouter(routes)
//];