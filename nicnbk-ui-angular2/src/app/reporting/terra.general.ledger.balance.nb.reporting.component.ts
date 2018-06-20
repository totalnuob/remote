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
    selector: 'terra.general.ledger.balance.nb.reporting',
    templateUrl: 'view/terra.general.ledger.balance.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class TerraGeneralLedgerBalanceNBReportingComponent extends CommonNBReportingComponent {

    private sub: any;
    private reportId;

    records: PeriodicReportRecordHolder;
    report;

    prevPageSource;

    busy, busyCreate: Subscription;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService
    ){
        super(router, route, periodicReportService);

        this.records = new PeriodicReportRecordHolder;
        this.sub = this.route
            .params
            .subscribe(params => {
                this.reportId = +params['id'];
                this.prevPageSource = params['prevPageSource'];

                if(this.reportId > 0){
                    // load report data
                    this.busy = this.periodicReportService.getRealEstateGeneralLedgerBalance(this.reportId)
                        .subscribe(
                            response  => {
                                console.log(response);
                                this.records = response;
                                this.report = response.report;

                            },
                            (error: ErrorResponse) => {
                                this.successMessage = null;
                                this.errorMessage = "Error loading Terra General Ledger Balance";
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


}