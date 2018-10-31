import { NgModule, CUSTOM_ELEMENTS_SCHEMA, Type } from '@angular/core';
import { BrowserModule }  from '@angular/platform-browser';
import { LocationStrategy, HashLocationStrategy} from '@angular/common';
import { HttpModule } from '@angular/http';
import { FormsModule } from '@angular/forms';
import { COMPILER_PROVIDERS } from "@angular/compiler";

import {SelectModule} from 'ng2-select';
import { TagInputModule } from 'ng2-tag-input';
import {ElasticModule} from 'angular2-elastic';

import {RouterModule} from '@angular/router';

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
import {MMEditComponent} from "./macromonitor/macromonitor.edit.component";
import {MMViewComponent} from "./macromonitor/macromonitor.view.component";
import {NotFoundComponent} from "./page-not-found.component";
import {MainNBReportingComponent} from "./reporting/main.nb.reporting.component";
import {InputFileUploadNBReportingComponent} from "./reporting/input.file.upload.nb.reporting.component";
import {ScheduleInvestmentsNBReportingComponent} from "./reporting/schedule.investments.nb.reporting.component";
import {StatementBalanceOperationsNBReportingComponent} from "./reporting/statement.balance.operations.nb.reporting.component";
import {StatementCashflowsNBReportingComponent} from "./reporting/statement.cashflows.nb.reporting.component";
import {StatementChangesNBReportingComponent} from "./reporting/statement.changes.nb.reporting.component";
import {SingularityGeneralLedgerBalanceNBReportingComponent} from "./reporting/singularity.general.ledger.balance.nb.reporting.component";
import {SingularityNOALTrancheANBReportingComponent} from "./reporting/singularity.noal.tranchea.nb.reporting.component";
import {SingularityNOALTrancheBNBReportingComponent} from "./reporting/singularity.noal.trancheb.nb.reporting.component";
import {OtherInfoInputNBReportingComponent} from "./reporting/other.info.input.nb.reporting.component";
import {FileAttachmentDownloaderComponent} from "./common/file-attachment-downloader.component";
//import {GenerateReportsNBReportingComponent} from "./reporting/generate.reports.nb.reporting.component";
import {NICKMFInputNBReportingComponent} from "./reporting/nick.mf.input.nb.reporting.component";
import {SingularGeneratedFormNBReportingComponent} from "./reporting/singular.generated.form.nb.reporting.component";
import {TarragonGeneratedFormNBReportingComponent} from "./reporting/tarragon.generated.form.nb.reporting.component";
import {GeneratedReportsNBReportingComponent} from "./reporting/generated.reports.nb.reporting.component";
import {ConsolidatedBalanceUSDFormNBReportingComponent} from "./reporting/consolidated.balance.usd.form.nb.reporting.component";
import {PreviousYearInputNBReportingComponent} from "./reporting/previous.year.input.nb.reporting.component";
import {ConsolidatedIncomeExpenseUSDFormNBReportingComponent} from "./reporting/consolidated.income.expense.usd.form.nb.reporting.component";
import {ConsolidatedTotalIncomeUSDFormNBReportingComponent} from "./reporting/consolidated.total.income.usd.form.nb.reporting.component";
import {NICKMFInputViewNBReportingComponent} from "./reporting/nick.mf.input.view.nb.reporting.component";
import {TarragonGeneratedFormViewNBReportingComponent} from "./reporting/tarragon.generated.form.view.nb.reporting.component";
import {ConsolidatedKZTForm8NBReportingComponent} from "./reporting/consolidated.kzt.form.8.nb.reporting.component";
import {ConsolidatedKZTForm7NBReportingComponent} from "./reporting/consolidated.kzt.form.7.nb.reporting.component";
import {ConsolidatedKZTForm10NBReportingComponent} from "./reporting/consolidated.kzt.form.10.nb.reporting.component";
import {ConsolidatedKZTForm14NBReportingComponent} from "./reporting/consolidated.kzt.form.14.nb.reporting.component";
import {ConsolidatedKZTForm13NBReportingComponent} from "./reporting/consolidated.kzt.form.13.nb.reporting.component";
import {ConsolidatedKZTForm1NBReportingComponent} from "./reporting/consolidated.balance.kzt.form.1.nb.reporting.component";
import {ReserveCalculationFormNBReportingComponent} from "./reporting/reserve.calculation.form.nb.reporting.component";
import {ConsolidatedKZTForm2NBReportingComponent} from "./reporting/consolidated.kzt.form.2.nb.reporting.component";
import {ConsolidatedKZTForm19NBReportingComponent} from "./reporting/consolidated.kzt.form.19.nb.reporting.component";
import {ConsolidatedKZTForm22NBReportingComponent} from "./reporting/consolidated.kzt.form.22.nb.reporting.component";
import {ConsolidatedKZTForm3NBReportingComponent} from "./reporting/consolidated.kzt.form.3.nb.reporting.component";
import {ConsolidatedKZTForm6NBReportingComponent} from "./reporting/consolidated.kzt.form.6.nb.reporting.component";
import {MonitoringHedgeFunds2PrintComponent} from "./monitoring/monitoring-hedge-funds-2-print.component";
import {CorpMeetingsListComponent} from "./corpmeetings/corp-meetings-list.component";
import {CorpMeetingEditComponent} from "./corpmeetings/corp-meetings-edit.component";
import {TerraGeneralLedgerBalanceNBReportingComponent} from "./reporting/terra.general.ledger.balance.nb.reporting.component";
import {TerraGeneratedFormNBReportingComponent} from "./reporting/terra.generated.form.nb.reporting.component";
import {TerraCombinedNBReportingComponent} from "./reporting/terra.combined.nb.reporting.component";
import {PreviousYearInputViewNBReportingComponent} from "./reporting/previous.year.input.view.nb.reporting.component";
import {TerraGeneratedFormViewNBReportingComponent} from "./reporting/terra.generated.form.view.nb.reporting.component";
import {LookupValuesNBReportingComponent} from "./reporting/lookup.values.nb.reporting.component";
import {CurrencyRatesLookupValuesNBReportingComponent} from "./reporting/currency.rates.lookup.values.nb.reporting.component";
import {TypedLookupValuesNBReportingComponent} from "./reporting/typed.lookup.values.nb.reporting.component";
import {PeriodicDataNBReportingComponent} from "./reporting/periodic.data.nb.reporting.component";
import {MatchingLookupValuesNBReportingComponent} from "./reporting/matching.lookup.values.nb.reporting.component";
//import {CorpMeetingEditComponent} from "./corpmeetings/ic-meeting-topic-edit.component";

@NgModule({
    imports: [
        BrowserModule,
        FormsModule, HttpModule,
        SelectModule,
        TagInputModule,
        appRouterProviders,
        BusyModule,
        ElasticModule
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
        MemoAttachmentDownloaderComponent, FileAttachmentDownloaderComponent,
        GeneralMemoEditComponent, PrivateEquityMemoEditComponent, HedgeFundsMemoEditComponent, RealEstateMemoEditComponent,
        TripMemoListComponent, TripMemoEditComponent, RiskManagementAxiomaReportingComponent, MonitoringHedgeFundsComponent, MonitoringLiquidPortfolioComponent,
        MonitoringMacroMonitorComponent, MonitoringPortfolioComponent, MonitoringPrivateEquityComponent, MonitoringPrivateEquityFundComponent,
        MonitoringHedgeFunds2Component, MonitoringHedgeFunds2PrintComponent,
        MainNBReportingComponent, InputFileUploadNBReportingComponent,

        HFDashboardComponent, HFFundSearchComponent, HFFundSelectionComponent, HFPortfolioComponent, HFReportComponent,
        HFFundProfileComponent, HFManagerProfileComponent, HFManagerSearchComponent,
        PEFundProfileComponent, PEFirmProfileComponent, PEFirmSearchComponent, PEFundReportComponent,
        AccessDeniedComponent,NotFoundComponent,
        LookupValuesNBReportingComponent, CurrencyRatesLookupValuesNBReportingComponent, TypedLookupValuesNBReportingComponent,PeriodicDataNBReportingComponent,MatchingLookupValuesNBReportingComponent,
        ScheduleInvestmentsNBReportingComponent, StatementBalanceOperationsNBReportingComponent, StatementCashflowsNBReportingComponent, StatementChangesNBReportingComponent,
        SingularityGeneralLedgerBalanceNBReportingComponent, SingularityNOALTrancheANBReportingComponent, SingularityNOALTrancheBNBReportingComponent,TerraGeneralLedgerBalanceNBReportingComponent,TerraCombinedNBReportingComponent,
        OtherInfoInputNBReportingComponent, /*GenerateReportsNBReportingComponent,*/ NICKMFInputNBReportingComponent, SingularGeneratedFormNBReportingComponent, TarragonGeneratedFormNBReportingComponent,
        GeneratedReportsNBReportingComponent, ConsolidatedBalanceUSDFormNBReportingComponent, PreviousYearInputNBReportingComponent, PreviousYearInputViewNBReportingComponent, ConsolidatedIncomeExpenseUSDFormNBReportingComponent,
        ConsolidatedTotalIncomeUSDFormNBReportingComponent, NICKMFInputViewNBReportingComponent, TarragonGeneratedFormViewNBReportingComponent,TerraGeneratedFormNBReportingComponent, TerraGeneratedFormViewNBReportingComponent,
        ConsolidatedKZTForm8NBReportingComponent, ConsolidatedKZTForm7NBReportingComponent, ConsolidatedKZTForm10NBReportingComponent, ConsolidatedKZTForm14NBReportingComponent, ConsolidatedKZTForm13NBReportingComponent,
        ConsolidatedKZTForm1NBReportingComponent, ReserveCalculationFormNBReportingComponent,ConsolidatedKZTForm2NBReportingComponent, ConsolidatedKZTForm19NBReportingComponent, ConsolidatedKZTForm22NBReportingComponent,
        ConsolidatedKZTForm3NBReportingComponent, ConsolidatedKZTForm6NBReportingComponent,
        AccessDeniedComponent, MMEditComponent, MMViewComponent,NotFoundComponent,
        CorpMeetingsListComponent, CorpMeetingEditComponent
    ],
    bootstrap: [ AppComponent ]
})
export class AppModule extends Type { }
