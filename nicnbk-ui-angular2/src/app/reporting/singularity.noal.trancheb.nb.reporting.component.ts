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

@Component({
    selector: 'singularity.noal.trancheb.nb.reporting',
    templateUrl: 'view/singularity.noal.trancheb.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class SingularityNOALTrancheBNBReportingComponent extends CommonNBReportingComponent {

    private sub: any;
    private reportId;

    records: PeriodicReportRecordHolder;
    report;


    busy: Subscription;

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

                if(this.reportId > 0){
                    // load report data

                    this.busy = this.periodicReportService.getSingularityNOALTrancheB(this.reportId)
                        .subscribe(
                            response  => {
                                console.log(response.noalTrancheBList);
                                this.records = response;
                                this.report = response.report;
                            },
                            (error: ErrorResponse) => {
                                this.successMessage = null;
                                this.errorMessage = "Error loading Singularity NOAL Tranche B";
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