import {Component, OnInit} from "@angular/core";
import {Router, ActivatedRoute} from '@angular/router';
import {Subscription} from "rxjs/Subscription";
import {ErrorResponse} from "../common/error-response";
import {PeriodicReportService} from "./periodic.report.service";
import {CommonNBReportingComponent} from "./common.nb.reporting.component";
import {DATA_APP_URL} from "../common/common.service.constants";
import {LookupService} from "../common/lookup.service";
import {BaseDictionary} from "../common/model/base-dictionary";
import {PeriodicReport} from "./model/periodic.report";
import {isNumeric} from "rxjs/util/isNumeric";
import {TerraGeneralLedgerRecordHolder} from "./model/terra.general.ledger.record.holder";

declare var $:any

@Component({
    selector: 'terra-general-ledger-form-nb-reporting',
    templateUrl: 'view/terra.general.ledger.form.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class TerraGeneralLedgerFormNBReportingComponent extends CommonNBReportingComponent implements OnInit {

    private sub: any;
    private reportId;

    recordsHolder: TerraGeneralLedgerRecordHolder;
    report;
    prevPageSource;


    busy: Subscription;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService
    ){
        super(router, route, periodicReportService);

        this.recordsHolder = new TerraGeneralLedgerRecordHolder;

        this.sub = this.route
            .params
            .subscribe(params => {
                this.reportId = +params['id'];
                this.prevPageSource = params['prevPageSource'];

                if(this.reportId > 0){
                    // load report data

                    this.busy = this.periodicReportService.getTerraGeneralLedger(this.reportId)
                        .subscribe(
                            response  => {
                                this.recordsHolder = response;
                                console.log(response);
                                this.report = response.report;
                            },
                            (error: ErrorResponse) => {
                                this.successMessage = null;
                                this.errorMessage = "Error loading Terra General Ledger records";
                                if(error && !error.isEmpty()){
                                    this.processErrorMessage(error);
                                }
                                this.postAction(null, null);
                            }
                        );
                }else{
                    // TODO: ??
                }
            });
    }
}