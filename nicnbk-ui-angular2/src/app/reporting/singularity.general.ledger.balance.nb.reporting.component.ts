import {Component} from "@angular/core";
import {CommonFormViewComponent} from "../common/common.component";
import {Router, ActivatedRoute} from '@angular/router';
import {InputFilesNBReport} from "./model/input.files.nb.report";
import {Subscription} from "rxjs/Subscription";
import {PeriodicReportService} from "./periodic.report.service";
import {ErrorResponse} from "../common/error-response";
import {DATA_APP_URL} from "../common/common.service.constants";
import {PeriodicReportRecord} from "./model/periodic.report.record";
import {PeriodicReportRecordHolder} from "./model/periodic.report.record.holder";
import {CommonNBReportingComponent} from "./common.nb.reporting.component";
import {PeriodicReport} from "./model/periodic.report";
import {SingularityAdjustments} from "./model/singularity.adjustments.list";
import {SingularityAdjustedRedemption} from "./model/singularity.adjusted.redemption";

@Component({
    selector: 'singularity.general.ledger.balance.nb.reporting',
    templateUrl: 'view/singularity.general.ledger.balance.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class SingularityGeneralLedgerBalanceNBReportingComponent extends CommonNBReportingComponent {

    private sub: any;
    private reportId;

    records: PeriodicReportRecordHolder;
    report;

    prevPageSource;
    showAdjustments: boolean;

    busy: Subscription;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService
    ){
        super(router, route, periodicReportService);

        this.showAdjustments = false;

        this.records = new PeriodicReportRecordHolder;
        this.sub = this.route
            .params
            .subscribe(params => {
                this.reportId = +params['id'];
                this.prevPageSource = params['prevPageSource'];

                if(this.reportId > 0){
                    // load report data
                    this.busy = this.periodicReportService.getGeneralLedgerBalance(this.reportId)
                        .subscribe(
                            response  => {
                                this.records = response;
                                this.report = response.report;
                                for(var i = 0; i < this.records.generalLedgerBalanceList.length; i++){
                                    if(this.records.generalLedgerBalanceList[i].adjustedRedemption != null){
                                        this.showAdjustments = true;
                                        break;
                                    }else if(this.records.generalLedgerBalanceList[i].interestRate != null){
                                        this.showAdjustments = true;
                                        break;
                                    }

                                }

                            },
                            (error: ErrorResponse) => {
                                this.successMessage = null;
                                this.errorMessage = "Error loading General Ledger Balance";
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, null);
                            }
                        );
                }else{
                    // TODO: ??
                    console.log("NO report id")
                }
            });
    }


    public toggleAdjustments(){
        this.showAdjustments = !this.showAdjustments;
    }

    public showAdjustmentsRedemption(record){
        return this.showAdjustments && (record == null ||
            (record.chartAccountsLongDescription != null &&
            record.chartAccountsLongDescription.trim().startsWith('Investment in Portfolio Fund') && record.shortName != null));
    }

    public showAdjustmentsInterestRate(record) {
        return this.showAdjustments && (record == null ||
            (record != null && record.chartAccountsLongDescription != null && record.chartAccountsLongDescription.trim() === 'Bank Loans - Accrued Interest'));
    }

    public saveAdjustments(){
        var adjustments = new SingularityAdjustments();
        if(adjustments.adjustedRedemptions == null){
            adjustments.adjustedRedemptions = [];
        }

        adjustments.reportId = this.report.id;
        if(this.records != null && this.records.generalLedgerBalanceList != null){
            for(var i = 0; i < this.records.generalLedgerBalanceList.length; i++){
                var record = this.records.generalLedgerBalanceList[i];
                var redemption = new SingularityAdjustedRedemption();
                redemption.recordId = record.id;

                var adjusted = false;
                if(record != null && record.chartAccountsLongDescription != null && record.chartAccountsLongDescription.trim() === 'Bank Loans - Accrued Interest'){
                    redemption.interestRate = record.interestRate;
                    adjusted = true;
                }
                if(record != null && record.chartAccountsLongDescription != null && record.chartAccountsLongDescription.startsWith('Investment in Portfolio Fund')
                    && record.shortName != null){
                    redemption.adjustedRedemption = record.adjustedRedemption;
                    adjusted = true;
                }

                if(adjusted) {
                    adjustments.adjustedRedemptions.push(redemption);
                }
            }


            this.busy = this.periodicReportService.saveSingularityAdjustments(adjustments)
                .subscribe(
                    response  => {
                        console.log(response);
                        if(response){
                            this.postAction("Successfully saving Singularity adjustments", null);
                        }else{
                            this.postAction(null, "Error saving Singularity adjustments");
                        }
                    },
                    (error: ErrorResponse) => {
                        this.successMessage = null;
                        this.errorMessage = "Error saving Singularity adjustments";
                        if(error && !error.isEmpty()){
                            this.processErrorMessage(error);
                        }
                        this.postAction(null, null);
                    }
                );
        }


    }

}