
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
import {HFResearchComponent} from "./hf/hf.research.component";
import {HFResearchFormComponent} from "./hf/hf.research-form.component";

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
import {MMEditComponent} from "./macromonitor/macromonitor.edit.component";
import {MMViewComponent} from "./macromonitor/macromonitor.view.component";
import {NotFoundComponent} from "./page-not-found.component";
import {MainNBReportingComponent} from "./reporting/main.nb.reporting.component";
import {InputFileUploadNBReportingComponent} from "./reporting/input.file.upload.nb.reporting.component";
import {ScheduleInvestmentsNBReportingComponent} from "./reporting/schedule.investments.nb.reporting.component";
import {SOIReportNBReportingComponent} from "./reporting/soi.report.nb.reporting.component";
import {StatementBalanceOperationsNBReportingComponent} from "./reporting/statement.balance.operations.nb.reporting.component";
import {StatementCashflowsNBReportingComponent} from "./reporting/statement.cashflows.nb.reporting.component";
import {StatementChangesNBReportingComponent} from "./reporting/statement.changes.nb.reporting.component";
import {SingularityGeneralLedgerBalanceNBReportingComponent} from "./reporting/singularity.general.ledger.balance.nb.reporting.component";
import {SingularityNOALTrancheANBReportingComponent} from "./reporting/singularity.noal.tranchea.nb.reporting.component";
import {SingularityNOALTrancheBNBReportingComponent} from "./reporting/singularity.noal.trancheb.nb.reporting.component";
import {OtherInfoInputNBReportingComponent} from "./reporting/other.info.input.nb.reporting.component";
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
import {TerraGeneralLedgerFormNBReportingComponent} from "./reporting/terra.general.ledger.form.nb.reporting.component";
import {PreviousYearInputViewNBReportingComponent} from "./reporting/previous.year.input.view.nb.reporting.component";
import {TerraGeneratedFormViewNBReportingComponent} from "./reporting/terra.generated.form.view.nb.reporting.component";
import {LookupValuesNBReportingComponent} from "./reporting/lookup.values.nb.reporting.component";
import {CurrencyRatesLookupValuesNBReportingComponent} from "./reporting/currency.rates.lookup.values.nb.reporting.component";
import {TypedLookupValuesNBReportingComponent} from "./reporting/typed.lookup.values.nb.reporting.component";
import {PeriodicDataNBReportingComponent} from "./reporting/periodic.data.nb.reporting.component";
import {MatchingLookupValuesNBReportingComponent} from "./reporting/matching.lookup.values.nb.reporting.component";
import {HFScreeningEditComponent} from "./hf/hf-screening-edit.component";
import {HFScreeningListComponent} from "./hf/hf.screening.list.component";
import {HFScreeningFilteredResultsComponent} from "./hf/hf.screening.filters.component";
import {HFScreeningFilteredResultsEditComponent} from "./hf/hf.screening.filters.edit.component";
import {LookupValuesComponent} from "./lookup/lookup.values.component";
import {CurrencyRatesLookupValuesComponent} from "./lookup/currency.rates.lookup.values.component";
import {HFScoringListComponent} from "./hf/hf.scoring.list.component";
import {HFScoringEditComponent} from "./hf/hf-scoring-edit.component";
import {BenchmarkLookupValuesComponent} from "./lookup/benchmark.lookup.values.component";
import {HFResearchPageComponent} from "./hf/hf.research-page.component";
import {MonitoringHFListComponent} from "./monitoring/monitoring-hedge-funds-list.component";
import {MonitoringHFResearchComponent} from "./monitoring/monitoring-hedge-funds-research.component";
import {HRNewsListComponent} from "./hr/hr-news-list.component";
import {HREmployeesListComponent} from "./hr/hr-employees-list.component";
import {HRDocsListComponent} from "./hr/hr-docs-list.component";
import {EmployeeProfileEditComponent} from "./employee/employee.profile.edit.component";
import {HRNewsEditComponent} from "./hr/hr-news-edit.component";


const routes: Routes  = [
    {
        path: '',
        redirectTo: '/hr/news',
        pathMatch: 'full'
    },
    /* LOGIN ***************************************/
    {
        path: 'login',
        component: LoginComponent
    },
    /* USER PROFILE ***************************************/
    {
        path: 'profile/:username',
        component: EmployeeProfileComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'profile/edit/:username',
        component: EmployeeProfileEditComponent,
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

    {
        path: 'monitoring/hf2Print',
        component: MonitoringHedgeFunds2PrintComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'monitoring/hflist',
        component: MonitoringHFListComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'monitoring/hfresearch/:id',
        component: MonitoringHFResearchComponent,
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
        path: 'hf/research',
        component: HFResearchComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'hf/research/edit/:id',
        component: HFResearchFormComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'hf/research/edit/page/:managerId/:pageId',
        component: HFResearchPageComponent,
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
    {
        path: 'hf/screening',
        component: HFScreeningListComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'hf/screening/edit/:id',
        component: HFScreeningEditComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'hf/screening/filteredResults/:id',
        component: HFScreeningFilteredResultsComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'hf/screening/filteredResults/edit/:screeningId/:id',
        component: HFScreeningFilteredResultsEditComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'hf/scoring',
        component: HFScoringListComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'hf/scoring/edit/:id',
        component: HFScoringEditComponent,
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
    {
        path: 'macromonitor',
        component: MMViewComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'macromonitor/:id',
        component: MMEditComponent,
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
        path: 'reporting/NBReporting/pe/scheduleInvestments/:id/:prevPageSource',
        component: ScheduleInvestmentsNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/pe/soiReport/:id/:prevPageSource',
              component: SOIReportNBReportingComponent,
              canActivate: [AuthGuard]
          },
    {
        path: 'reporting/NBReporting/pe/statementBalanceOperations/:id/:prevPageSource',
        component: StatementBalanceOperationsNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/pe/statementCashflows/:id/:prevPageSource',
        component: StatementCashflowsNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/pe/statementChanges/:id/:prevPageSource',
        component: StatementChangesNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/hf/generalLedgerBalance/:id/:prevPageSource',
        component: SingularityGeneralLedgerBalanceNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/re/generalLedgerBalance/:id/:prevPageSource',
        component: TerraGeneralLedgerBalanceNBReportingComponent,
        canActivate: [AuthGuard]
    },

    {
        path: 'reporting/NBReporting/re/terraCombined/:id/:prevPageSource',
        component: TerraCombinedNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/re/terraGeneralLedger/:id/:prevPageSource',
        component: TerraGeneralLedgerFormNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/hf/noalA/:id/:prevPageSource',
        component: SingularityNOALTrancheANBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/hf/noalB/:id/:prevPageSource',
        component: SingularityNOALTrancheBNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/otherInfo/:id',
        component: OtherInfoInputNBReportingComponent,
        canActivate: [AuthGuard]
    },
    //{
    //    path: 'reporting/NBReporting/generateReports/:id',
    //    component: GenerateReportsNBReportingComponent,
    //    canActivate: [AuthGuard]
    //},
    {
        path: 'reporting/NBReporting/NICKMFInput/:id',
        component: NICKMFInputNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/SingularGeneratedForm/:id',
        component: SingularGeneratedFormNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/TarragonGeneratedForm/:id',
        component: TarragonGeneratedFormNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/TerraGeneratedForm/:id',
        component: TerraGeneratedFormNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/TerraGeneratedFormView/:id',
        component: TerraGeneratedFormViewNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/GeneratedReports/:id/:showInputList',
        component: GeneratedReportsNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/consolidatedBalanceUSDForm/:id',
        component: ConsolidatedBalanceUSDFormNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/previousYearInput/:id',
        component: PreviousYearInputNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/previousYearInputView/:id',
        component: PreviousYearInputViewNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/consolidatedIncomeExpenseUSDForm/:id',
        component: ConsolidatedIncomeExpenseUSDFormNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/consolidatedTotalIncomeUSDForm/:id',
        component: ConsolidatedTotalIncomeUSDFormNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/nickMFInputView/:id',
        component: NICKMFInputViewNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/TarragonGeneratedFormView/:id',
        component: TarragonGeneratedFormViewNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/consolidatedKZTForm8/:id',
        component: ConsolidatedKZTForm8NBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/consolidatedKZTForm7/:id',
        component: ConsolidatedKZTForm7NBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/consolidatedKZTForm10/:id',
        component: ConsolidatedKZTForm10NBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/consolidatedKZTForm14/:id',
        component: ConsolidatedKZTForm14NBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/consolidatedKZTForm13/:id',
        component: ConsolidatedKZTForm13NBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/consolidatedKZTForm1/:id',
        component: ConsolidatedKZTForm1NBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/consolidatedKZTForm2/:id',
        component: ConsolidatedKZTForm2NBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/consolidatedKZTForm19/:id',
        component: ConsolidatedKZTForm19NBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/consolidatedKZTForm22/:id',
        component: ConsolidatedKZTForm22NBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/consolidatedKZTForm3/:id',
        component: ConsolidatedKZTForm3NBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/consolidatedKZTForm6/:id',
        component: ConsolidatedKZTForm6NBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/reserveCalculation',
        component: ReserveCalculationFormNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/lookupValues',
        component: LookupValuesNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/lookupValues/CurrencyRates',
        component: CurrencyRatesLookupValuesNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/typedLookupValues',
        component: TypedLookupValuesNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/periodicData',
        component: PeriodicDataNBReportingComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'reporting/NBReporting/matchingLookupValues',
        component: MatchingLookupValuesNBReportingComponent,
        canActivate: [AuthGuard]
    },
    /* Corp Meetings ******************************************/
    {
        path: 'corpMeetings',
        component: CorpMeetingsListComponent,
        canActivate: [AuthGuard]
    },

    {
        path: 'corpMeetings/edit/:id',
        component: CorpMeetingEditComponent,
        canActivate: [AuthGuard]
    },

    /* Lookup values ******************************************/
    {
        path: 'lookups',
        component: LookupValuesComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'lookups/currency',
        component: CurrencyRatesLookupValuesComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'lookups/benchmarks',
        component: BenchmarkLookupValuesComponent,
        canActivate: [AuthGuard]
    },
    /* HR ******************************************/
    {
        path: 'hr/news',
        component: HRNewsListComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'hr/news/edit/:id',
        component: HRNewsEditComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'hr/employees',
        component: HREmployeesListComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'hr/docs',
        component: HRDocsListComponent,
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
