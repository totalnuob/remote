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
    /* NEWS ***************************************/
    {
        path: 'news',
        component: NewsListComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'news/edit',
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
];

export const appRouterProviders = RouterModule.forRoot(routes);

//export const appRouterProviders = [
//    provideRouter(routes)
//];
