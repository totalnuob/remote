import {Component, OnInit} from "@angular/core";
import {Router, ActivatedRoute} from '@angular/router';
import {Subscription} from "rxjs/Subscription";
import {ErrorResponse} from "../common/error-response";
import {PeriodicReportService} from "./periodic.report.service";
import {OtherInfoNBReporting} from "./model/other.info.nb.reporting";
import {CommonNBReportingComponent} from "./common.nb.reporting.component";
import {DATA_APP_URL} from "../common/common.service.constants";
import {NICKMFReportingInfo} from "./model/nick.mf.reporting.info.nb.reporting";
import {LookupService} from "../common/lookup.service";
import {BaseDictionary} from "../common/model/base-dictionary";
import {NICReportingChartOfAccounts} from "./model/nic.reporting.chart.of.accounts.";
import {NICKMFReportingInfoHolder} from "./model/nick.mf.reporting.info.holder.nb.reporting";
import {PeriodicReport} from "./model/periodic.report";
import {isNumeric} from "rxjs/util/isNumeric";
import {GeneratedGLFormRecord} from "./model/generated.form.record";

declare var $:any

@Component({
    selector: 'singular-generated-form-nb-reporting',
    templateUrl: 'view/singular.generated.form.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class SingularGeneratedFormNBReportingComponent extends CommonNBReportingComponent implements OnInit {

    private sub: any;
    private reportId;

    busy: Subscription;

    private records: GeneratedGLFormRecord[];

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService
    ){
        super(router, route, periodicReportService);

        this.records = [];

        this.sub = this.route
            .params
            .subscribe(params => {
                this.reportId = +params['id'];
                if(this.reportId > 0){
                    // load report data

                    this.busy = this.periodicReportService.getGeneratedSingularForm(this.reportId)
                        .subscribe(
                            response  => {
                                if(response){
                                    this.records = response;
                                    console.log(this.records);
                                }
                },
                (error: ErrorResponse) => {
                    this.successMessage = null;
                    this.errorMessage = "Error loading data";
                    if(error && !error.isEmpty()){
                        this.processErrorMessage(error);
                    }
                                this.postAction(null, null);
                            }
                        );
                }else{
                    // TODO: ??
                    console.log("No report id")
                }
            });
    }

    ngOnInit(): any {
    }
}