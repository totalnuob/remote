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

@Component({
    selector: 'schedule.investments.nb.reporting',
    templateUrl: 'view/schedule.investments.nb.reporting.component.html',
    styleUrls: [],
    providers: [],
})
export class ScheduleInvestmentsNBReportingComponent extends CommonFormViewComponent {

    private sub: any;
    private reportId;

    records: PeriodicReportRecordHolder;

    busy: Subscription;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private periodicReportService: PeriodicReportService
    ){
        super(router);


        this.records = new PeriodicReportRecordHolder;

        this.sub = this.route
            .params
            .subscribe(params => {
                this.reportId = +params['id'];

                if(this.reportId > 0){
                    // load report data

                    this.busy = this.periodicReportService.getScheduleInvestments(this.reportId)
                        .subscribe(
                            response  => {
                                this.records = response;
                            },
                            (error: ErrorResponse) => {
                                this.successMessage = null;
                                this.errorMessage = "Error loading schedule of investments";
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